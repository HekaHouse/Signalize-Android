package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.alicebot.ab.Ghost;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;

import aiml.aimlValidate.AIMLValidate;

/**
 * @author mukundan
 * An activity for Advanced Settings
 */
public class AdvancedSettings extends Activity implements View.OnClickListener{

    protected final String TAG = "Advanced Setting Activity";
    private String currentResponse, strFileName, strPattern;
    private String newFile;
    EditText currentResponseET;
    Button setResponse,viewAIMLTags;
    AutoCompleteTextView addSraiTv;
    AddButtons addButtons;

    /**
     * The layout is set correspondingly and the buttons are added
     * @param savedInstanceState Saved State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        newFile = extras.getString(UtilityStrings.newFileIntent);
        setContentView(R.layout.activity_advanced_settings);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.aiml_tag_set);
        currentResponseET = (EditText) findViewById(R.id.advanced_currentResponse);
        addButtons = new AddButtons(this, linearLayout, currentResponseET);
        viewAIMLTags = (Button) findViewById(R.id.aiml_tag_button);
        viewAIMLTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (linearLayout.getVisibility() == View.GONE) {
                    addButtons.addButtons();
                    viewAIMLTags.setText(getString(R.string.close_aiml_tags));
                    linearLayout.setVisibility(View.VISIBLE);
                } else if (linearLayout.getVisibility() == View.VISIBLE) {
                    viewAIMLTags.setText(getString(R.string.view_aiml_tags));
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });


        if(newFile != null) {
            findViewById(R.id.add_new_aiml_heading).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.name_text_view)).setText(newFile);
        }
        addButtons.setEditText(currentResponseET);
        if(newFile == null) {
            currentResponse = extras.getString(UtilityStrings.currentResponseIntent);
            currentResponseET.setText(currentResponse);
        }
        else{
            currentResponseET.setText(UtilityStrings.XML_PROCESSING_STRING + "\n");
            currentResponseET.setSelection((UtilityStrings.XML_PROCESSING_STRING + "\n").length());
        }
        strFileName = extras.getString(UtilityStrings.fileIntent);
        strPattern = extras.getString(UtilityStrings.patternIntent);
        setResponse = (Button) findViewById(R.id.advanced_set_response);
        setResponse.setOnClickListener(this);
        if(newFile != null){
            setResponse.setText(getString(R.string.create_file));
        }
        addSraiTv = (AutoCompleteTextView)findViewById(R.id.add_srai_tag);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                TrainerActivity.listOfPatterns);
        addSraiTv.setAdapter(arrayAdapter);
        addSraiTv.setOnItemClickListener(new Listeners.SraiClicked(this,UtilityStrings.TAGTOADD.SRAI,currentResponseET));



        currentResponseET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //Toast.makeText(getApplicationContext(), "" + currentResponseET.getSelectionStart(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.setTitle(getString(R.string.add_new_aiml_file));
        currentResponseET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.advanced_currentResponse) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.advanced_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Static method to show error Toast in red
     * @param context The Calling context
     * @param message The required message
     */
    public static void showErrorToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        toast.getView().setBackground(context.getResources().getDrawable(R.drawable.toast_bg_red));
        TextView textView= (TextView) toast.getView().findViewById(android.R.id.message);
        textView.setTextColor(Color.YELLOW);
        toast.show();
    }

    /**
     * Static method to show valid Toast in green
     * @param context The Calling context
     * @param message The required message
     */
    public static void showValidToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        toast.getView().setBackground(context.getResources().getDrawable(R.drawable.toast_bg_green));
        TextView textView= (TextView) toast.getView().findViewById(android.R.id.message);
        textView.setTextColor(Color.YELLOW);
        toast.show();

    }

    @Override
    public void onClick(View v) {
         /*
        * On click method for the Set Response Button.
        * On click method for the Create New File Button
        * */

         if(newFile == null) {
            // Set Response
            FileUtility.openFile(strFileName);
            Node responseElement = FileUtility.getReqResponse(strPattern);
            try {
                AIMLValidate aimlValidate = new AIMLValidate(this.getApplicationContext());
                boolean valid = aimlValidate.aimlValidate("<aiml>" + currentResponseET.getText().toString() + "</aiml>");
                if(valid){
                    AdvancedSettings.showValidToast(this,"Valid AIML TAGS");

                    FileUtility.setAdvancedResponseElement(responseElement, currentResponseET.getText().toString(), strFileName);
                    FileUtility.changedString = currentResponseET.getText().toString();
                    Log.d(TAG, "ADDED TO FILE AND WROTE FILE");
                    AdvancedSettings.showValidToast(this,"Added to file and opening file for view.");
                    Intent intent;
                    intent = new Intent(getApplicationContext(), ViewFileActivity.class);
                    intent.putExtra(UtilityStrings.fileIntent, strFileName);
                    startActivity(intent);
                }
                else{
                    AdvancedSettings.showErrorToast(this,"Invalid AIML");
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IO EXCEPTION");
                AdvancedSettings.showErrorToast(this,"File Exception " +e.getMessage());
            } catch (SAXException e) {
                e.printStackTrace();
                Log.e(TAG, "SAX EXCEPTION");
                AdvancedSettings.showErrorToast(this,"Exception " +e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"AIML Validation Error/ Unknown Error " + e.getMessage() + " " + e.getCause());
                AdvancedSettings.showErrorToast(this, "Error " + e.getMessage() + " " + e.getCause());
            }
        }
        else if(newFile!=null){
             //Create New File
            AIMLValidate aimlValidate = new AIMLValidate(this.getApplicationContext());
            boolean valid = false;
            try {
                valid = aimlValidate.aimlValidate(currentResponseET.getText().toString());
                if(valid){
                    valid = FileUtility.createFile(newFile,currentResponseET.getText().toString());
                    if(valid){
                        AdvancedSettings.showValidToast(this,"Created wrote to " + newFile + " successfully.");
                    }
                    else{
                        AdvancedSettings.showErrorToast(this, newFile + " creation and opening error.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                AdvancedSettings.showErrorToast(this,"Exception at line " + e.getMessage());
            }

        }
        finish();


    }
}

