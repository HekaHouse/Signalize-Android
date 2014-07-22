package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Node;



public class FileActivity extends Activity implements View.OnClickListener{
    private TextView pattern;
    private TextView currentResponse;
    private EditText newResponse;
    private Button setResponse;
    private String strFileName,strPattern,strTemplate;
    private final String fileIntent = "fileName";
    private final String patternIntent = "pattern";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FileUtils.setContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        strFileName = extras.getString(fileIntent);
        strPattern = extras.getString(patternIntent);

        TextView fileName = (TextView) findViewById(R.id.fileName);
        pattern = (TextView)findViewById(R.id.pattern);
        currentResponse = (TextView)findViewById(R.id.currentResponse);
        newResponse = (EditText)findViewById(R.id.newResponse);
        setResponse = (Button)findViewById(R.id.setResponse);
        setResponse.setOnClickListener(this);
        fileName.setText(strFileName);
        pattern.setText(strPattern);
        FileUtils.openFile(strFileName);
        Node responseElement = FileUtils.getReqResponse(strPattern);
        FileUtils.changedString = FileUtils.getChangedNodeString(responseElement);
        currentResponse.setText(FileUtils.getChangedNodeString(responseElement));
        Button viewFile = (Button)findViewById(R.id.viewFile);
        viewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),ViewFileActivity.class);
                intent.putExtra("fileName",strFileName);
                startActivity(intent);
            }
        });
    }

    public static void init(Context context){
        FileUtils.setContext(context);
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
        FileUtils.openFile(strFileName);
        Node responseElement = FileUtils.getReqResponse(strPattern);
        FileUtils.setResponseElement(responseElement,newResponse.getText().toString(), strFileName);
        FileUtils.changedString = FileUtils.getChangedNodeString(responseElement);
        currentResponse.setText(FileUtils.changedString);
        FileUtils.changedString = FileUtils.getChangedNodeString(responseElement);
    }
}
