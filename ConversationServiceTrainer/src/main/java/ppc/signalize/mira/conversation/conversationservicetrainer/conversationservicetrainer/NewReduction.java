package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Collections;

import aiml.aimlValidate.AIMLValidate;
import ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer.R;

/**
 * @author mukundan
 * An activity for handling the addition of new reduction similar to advanced settings activity
 */

public class NewReduction extends Activity implements View.OnClickListener{
    protected final String newReductionPrefix = "<category><pattern>";
    protected final String newReductionPostPattern = "</pattern><template>";
    protected final String newReductionPostfix = "</template></category>";
    EditText newReductionET;
    Button addReduction,viewAIMLTags;
    AutoCompleteTextView addSraiTag;
    String strFileName,strPattern;
    AddButtons addButtons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        strFileName = extras.getString(UtilityStrings.fileIntent);
        strPattern = extras.getString(UtilityStrings.inputPatternIntent);
        setContentView(R.layout.activity_new_reduction);
        newReductionET = (EditText)findViewById(R.id.advanced_currentResponse);
        String newR = newReductionPrefix + strPattern + newReductionPostPattern;
        newReductionET.setText(newR + " " + newReductionPostfix);
        newReductionET.setSelection(newR.length());
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.aiml_tag_set);
        addButtons = new AddButtons(this, linearLayout, newReductionET);
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
        addSraiTag = (AutoCompleteTextView) findViewById(R.id.add_srai_tag);
        addSraiTag.setOnItemClickListener(new Listeners.SraiClicked(this, UtilityStrings.TAGTOADD.SRAI, newReductionET));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                TrainerActivity.listOfPatterns);
        addSraiTag.setAdapter(arrayAdapter);
        ((TextView)findViewById(R.id.add_new_aiml_heading)).setText(getString(R.string.create_reduction));
        findViewById(R.id.name_text_view).setVisibility(View.GONE);
        addReduction = (Button) findViewById(R.id.advanced_set_response);
        addReduction.setText(getString(R.string.create_reduction));
        addReduction.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_reduction, menu);
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

    @Override
    public void onClick(View v) {
        AIMLValidate aimlValidate = new AIMLValidate(this);
        try {
            aimlValidate.aimlValidate("<aiml>\n" + newReductionET.getText().toString() + "</aiml>");
            AdvancedSettings.showValidToast(this,"Valid AIML tags");
            FileUtility.openFile(strFileName);
            FileUtility.addReduction(strFileName,newReductionET.getText().toString());

        } catch (Exception e) {
            Log.e("Add New Reduction",e.getMessage() + " " +  e.getCause() );
            AdvancedSettings.showErrorToast(this,"Error in AIML validation/Writing to file. " + e.getCause().toString());
            e.printStackTrace();

        }finally {
            finish();
        }
    }
}

/**
 * @author mukundan
 * A class for handling the New Reduction Dialog box
 */
class NewReductionDialog extends Dialog implements View.OnClickListener {
    TrainerActivity activity;
    int position;
    Button ok_button,cancel_button;
    String fileName;
    String inputPattern;

    public NewReductionDialog(Context context) {
        super(context);

    }

    public NewReductionDialog(Activity activity, int position) {
        super(activity);
        this.activity = (activity instanceof TrainerActivity)?(TrainerActivity)activity:null;
        this.position = position;
        this.setContentView(R.layout.reduction_dialog);
        this.setTitle(getContext().getString(R.string.add_new_reduction_question));
        ((TextView)this.findViewById(R.id.reduction_dialog_text)).setText(activity.getString(R.string.new_reduction_dialog_prompt));
        fileName = null;
        inputPattern = null;
        ok_button = (Button) this.findViewById(R.id.reduction_dialog_ok_button);
        cancel_button = (Button) this.findViewById(R.id.reduction_dialog_cancel_button);
        ok_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

    }

    public NewReductionDialog(Activity activity1, String fileName, String inputPattern){
        super(activity1);
        this.activity = (activity1 instanceof TrainerActivity)?(TrainerActivity)activity1:null;
        this.position = -1;
        this.setContentView(R.layout.reduction_dialog);
        this.setTitle(getContext().getString(R.string.add_new_reduction_question));
        ((TextView)this.findViewById(R.id.reduction_dialog_text)).setText(activity.getString(R.string.new_reduction_dialog_prompt));
        this.fileName = fileName;
        this.inputPattern = inputPattern;
        ok_button = (Button) this.findViewById(R.id.reduction_dialog_ok_button);
        cancel_button = (Button) this.findViewById(R.id.reduction_dialog_cancel_button);
        ok_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.reduction_dialog_ok_button){
            if(activity!=null) {
                Intent intent = new Intent(getContext(), NewReduction.class);
                if(fileName == null) {
                    intent.putExtra(UtilityStrings.fileIntent, TrainerActivity.listNames.get(position));
                }
                else{
                    intent.putExtra(UtilityStrings.fileIntent,fileName);
                }
                if(inputPattern == null) {
                    intent.putExtra(UtilityStrings.inputPatternIntent, TrainerActivity.inputPattern);
                }
                else{
                    intent.putExtra(UtilityStrings.inputPatternIntent, inputPattern);
                }
                activity.startActivity(intent);
            }
            this.dismiss();
        }
        else if(v.getId() == R.id.reduction_dialog_cancel_button){
            this.dismiss();
        }

    }
}
