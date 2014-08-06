package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
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
