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

public class AdvancedSettings extends Activity implements View.OnClickListener{

    protected final String TAG = "Advanced Setting Activity";
    private String currentResponse, strFileName, strPattern;
    private String newFile;
    EditText currentResponseET,newFileET;
    Button setResponse,viewAIMLTags,createNewFile;
    Spinner addSraiTag;
    AddButtons addButtons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        newFile = extras.getString(UtilityStrings.newFileIntent);
        if(newFile==null) {
            setContentView(R.layout.activity_advanced_settings);
            final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.aiml_tag_set);
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
            addSraiTag = (Spinner) findViewById(R.id.add_srai_tag);
            if (!TrainerActivity.listOfPatterns.contains(getString(R.string.select_srai))) {
                Collections.sort(TrainerActivity.listOfPatterns);
                TrainerActivity.listOfPatterns.add(0, getString(R.string.select_srai));
            }
            addSraiTag.setOnItemSelectedListener(new Listeners.SraiSelected(this, UtilityStrings.TAGTOADD.SRAI, currentResponseET));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    TrainerActivity.listOfPatterns);
            addSraiTag.setAdapter(arrayAdapter);
            addButtons.setEditText(currentResponseET);
            currentResponse = extras.getString(UtilityStrings.currentResponseIntent);
            strFileName = extras.getString(UtilityStrings.fileIntent);
            strPattern = extras.getString(UtilityStrings.patternIntent);
            setResponse = (Button) findViewById(R.id.advanced_set_response);
            setResponse.setOnClickListener(this);
            currentResponseET = (EditText) findViewById(R.id.advanced_currentResponse);
            currentResponseET.setText(currentResponse);
            //validateXML(currentResponseET,TAGTOADD.RANDOM_LI);
            currentResponseET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        //Toast.makeText(getApplicationContext(), "" + currentResponseET.getSelectionStart(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
        else if(newFile != null){
            setContentView(R.layout.new_file_activity);
            newFileET = (EditText)findViewById(R.id.newFile);
            final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.new_file_aiml_tag_set);
            addButtons = new AddButtons(this, linearLayout, newFileET);
            viewAIMLTags = (Button) findViewById(R.id.new_file_aiml_tag_button);
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
            addSraiTag = (Spinner) findViewById(R.id.new_file_add_srai_tag);
            if (!TrainerActivity.listOfPatterns.contains(getString(R.string.select_srai))) {
                Collections.sort(TrainerActivity.listOfPatterns);
                TrainerActivity.listOfPatterns.add(0, getString(R.string.select_srai));
            }
            addSraiTag.setOnItemSelectedListener(new Listeners.SraiSelected(this, UtilityStrings.TAGTOADD.SRAI, newFileET));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    TrainerActivity.listOfPatterns);
            addSraiTag.setAdapter(arrayAdapter);
            ((TextView)findViewById(R.id.new_file_name)).setText(newFile);
            createNewFile = (Button)findViewById(R.id.create_new_file);
            createNewFile.setOnClickListener(this);
            newFileET.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (v.getId() == R.id.newFile) {
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
    public static void showErrorToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.RED);
        TextView textView= (TextView) toast.getView().findViewById(android.R.id.message);
        textView.setTextColor(Color.YELLOW);
        toast.show();
    }
    public static void showValidToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.GREEN);
        TextView textView= (TextView) toast.getView().findViewById(android.R.id.message);
        textView.setTextColor(Color.YELLOW);
        toast.show();
    }
    protected void validateXML(EditText currentResponse, UtilityStrings.TAGTOADD tagtoadd){
        try {
            Log.d(TAG,"Current Response " + currentResponse.getText());
            GenericAIMLValidator genericAIMLValidator = new GenericAIMLValidator(currentResponse.getText().toString());
            String []childrenInOrder = {"pattern"};
            switch (tagtoadd){
                case TEMPLATE:
                     childrenInOrder = new String[]{"pattern", "template"};
                     break;
                case RANDOM:
                    childrenInOrder = new String[]{"pattern", "template", "random", "li"};
                    break;
            }

            if(!genericAIMLValidator.isValidChild("category",childrenInOrder)){
                AdvancedSettings.showErrorToast(getApplicationContext(),"AIML Validation Error");
            }
            else{
                AdvancedSettings.showValidToast(getApplicationContext(),"Valid AIML TAGS");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Cannot convert to XML IO Exception");
        } catch (SAXException e) {
            e.printStackTrace();
            Log.e(TAG,"Cannot convert to XML SAX Exception");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            Log.e(TAG,"Cannot convert to XML Parser Config Exception");
        }

    }

    @Override
    public void onClick(View v) {
         /*
        * On click method for the Set Response Button.
        * On click method for the Create New File Button
        * */
        if(newFile == null) {
            FileUtility.openFile(strFileName);
            Node responseElement = FileUtility.getReqResponse(strPattern);
            try {
                FileUtility.setAdvancedResponseElement(responseElement, currentResponseET.getText().toString(), strFileName);
                FileUtility.changedString = currentResponseET.getText().toString();
                Log.d(TAG, "ADDED TO FILE AND WROTE FILE");
                Intent intent;
                intent = new Intent(getApplicationContext(), ViewFileActivity.class);
                intent.putExtra(UtilityStrings.fileIntent, strFileName);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IO EXCEPTION");
            } catch (SAXException e) {
                e.printStackTrace();
                Log.e(TAG, "SAX EXCEPTION");
            }
        }
        else if(newFile!=null){
            AIMLValidate aimlValidate = new AIMLValidate(this.getApplicationContext());
            boolean valid = aimlValidate.aimlValidate(newFileET.getText().toString());
            if(valid){
                valid = FileUtility.createFile(newFile,newFileET.getText().toString());
                if(valid){
                    AdvancedSettings.showValidToast(this,"Created wrote to " + newFile + " successfully.");
                }
                else{
                    AdvancedSettings.showErrorToast(this, newFile + " creation and opening error.");
                }
            }
        }

    }
}

