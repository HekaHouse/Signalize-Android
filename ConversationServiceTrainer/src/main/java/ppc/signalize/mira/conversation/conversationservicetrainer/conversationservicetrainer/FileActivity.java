package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Node;



public class FileActivity extends Activity implements View.OnClickListener{
    private TextView pattern;
    private TextView currentResponse;
    private EditText newResponse;
    private Button setResponse,advancedResponse;
    private String strFileName,strPattern,strTemplate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FileUtility.setContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        strFileName = extras.getString(UtilityStrings.fileIntent);
        strPattern = extras.getString(UtilityStrings.patternIntent);

        TextView fileName = (TextView) findViewById(R.id.fileName);
        pattern = (TextView)findViewById(R.id.pattern);
        currentResponse = (TextView)findViewById(R.id.currentResponse);
        newResponse = (EditText)findViewById(R.id.newResponse);
        setResponse = (Button)findViewById(R.id.setResponse);
        setResponse.setOnClickListener(this);
        advancedResponse = (Button)findViewById(R.id.advancedResponse);
        advancedResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * Onclick method for the Advanced Settings button.
                * */
                Intent intent = new Intent(getApplicationContext(),AdvancedSettings.class);
                intent.putExtra(UtilityStrings.currentResponseIntent,currentResponse.getText());
                intent.putExtra(UtilityStrings.fileIntent,strFileName);
                intent.putExtra(UtilityStrings.patternIntent,strPattern);
                startActivity(intent);
            }
        });
        fileName.setText(strFileName);
        pattern.setText(strPattern);
        FileUtility.openFile(strFileName);
        Node responseElement = FileUtility.getReqResponse(strPattern);
        FileUtility.changedString = FileUtility.getChangedNodeString(responseElement);
        currentResponse.setText(FileUtility.getChangedNodeString(responseElement));
        Button viewFile = (Button)findViewById(R.id.viewFile);
        viewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * On Click method for the View File Button.
                * */
                Intent intent;
                intent = new Intent(getApplicationContext(),ViewFileActivity.class);
                intent.putExtra(UtilityStrings.fileIntent,strFileName);
                startActivity(intent);
            }
        });
        currentResponse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.currentResponse) {
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

    public static void init(Context context){
        FileUtility.setContext(context);
    }

    @Override
    protected void onPause() {
        super.onPause();
        XMLState.setDom();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file, menu);
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
        /*
        * On click method for the Set Response button.
        * */
        FileUtility.openFile(strFileName);
        Node responseElement = FileUtility.getReqResponse(strPattern);
        FileUtility.setResponseElement(responseElement, newResponse.getText().toString(), strFileName);
        FileUtility.changedString = FileUtility.getChangedNodeString(responseElement);
        currentResponse.setText(FileUtility.changedString);
        FileUtility.changedString = FileUtility.getChangedNodeString(responseElement);
    }
}
