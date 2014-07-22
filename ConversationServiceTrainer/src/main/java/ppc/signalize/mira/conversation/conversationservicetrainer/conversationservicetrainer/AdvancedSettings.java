package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer.R;

public class AdvancedSettings extends Activity {

    protected final String TAG = "Advanced Setting Activiy";
    private String currentResponse, strFilename, strPattern;
    EditText currentResponseET;
    Button addTemplateTag, addRandom_LiTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings);
        Bundle extras = getIntent().getExtras();
        currentResponse = extras.getString(IntentStrings.currentResponseIntent);
        strFilename = extras.getString(IntentStrings.fileIntent);
        strPattern = extras.getString(IntentStrings.patternIntent);
        addTemplateTag = (Button) findViewById(R.id.add_template_tag);
        addTemplateTag.setOnClickListener(new AddTagListener(this, TAGTOADD.TEMPLATE));
        addRandom_LiTag = (Button)findViewById(R.id.add_random_tag);
        addRandom_LiTag.setOnClickListener(new AddTagListener(this,TAGTOADD.RANDOM_LI));
        currentResponseET = (EditText) findViewById(R.id.advanced_currentResponse);
        currentResponseET.setText(currentResponse);
        currentResponseET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    Toast.makeText(getApplicationContext(), "" + currentResponseET.getSelectionStart(), Toast.LENGTH_SHORT).show();
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
    protected void validateXML(EditText currentResponse, TAGTOADD tagtoadd){
        try {
            Log.d(TAG,"Current Response " + currentResponse.getText());
            GenericAIMLValidator genericAIMLValidator = new GenericAIMLValidator(currentResponse.getText().toString());
            String []childrenInOrder = {"pattern"};
            switch (tagtoadd){
                case TEMPLATE:
                     childrenInOrder = new String[]{"pattern", "template"};
                     break;
                case RANDOM_LI:
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
}
class AddTagListener implements View.OnClickListener{

    AdvancedSettings activity;
    private String previousText;
    private TAGTOADD tagtoadd;
    public AddTagListener(AdvancedSettings activity, TAGTOADD tagtoadd){
        this.activity = activity;
        this.tagtoadd = tagtoadd;
    }
    @Override
    public void onClick(View v) {
        switch (tagtoadd){
            case TEMPLATE:
                addTemplateTag();
                break;
            case RANDOM_LI:
                addRandom_LiTag();
                break;
        }

    }

    private void addTemplateTag(){
        activity.currentResponseET.clearFocus();
        if(activity.currentResponseET.getText().toString().contains("<template>")){
            AdvancedSettings.showErrorToast(activity.getApplicationContext(),"Response already contains template tag, cannot add one more!!");
        }
        else{
            previousText = activity.currentResponseET.getText().toString();
            String tag = "<template> </template>";
            int start = activity.currentResponseET.getSelectionStart();
            activity.currentResponseET.getText().insert(start,tag);
            activity.currentResponseET.setSelection(start + 10);
            activity.validateXML(activity.currentResponseET, TAGTOADD.TEMPLATE);
            activity.currentResponseET.requestFocus();
        }
    }
    private void addRandom_LiTag(){
        activity.currentResponseET.clearFocus();
        if(activity.currentResponseET.getText().toString().contains("<random>")){
            AdvancedSettings.showErrorToast(activity.getApplicationContext(),"Response already contains random tag, cannot add one more!!");
        }
        else{
            previousText = activity.currentResponseET.getText().toString();
            String tag = "<random><li> </li></random>";
            int start = activity.currentResponseET.getSelectionStart();
            activity.currentResponseET.getText().insert(start,tag);
            activity.currentResponseET.setSelection(start + 12);
            activity.validateXML(activity.currentResponseET, TAGTOADD.RANDOM_LI);
            activity.currentResponseET.requestFocus();
        }
    }

}
enum TAGTOADD{TEMPLATE,RANDOM_LI};