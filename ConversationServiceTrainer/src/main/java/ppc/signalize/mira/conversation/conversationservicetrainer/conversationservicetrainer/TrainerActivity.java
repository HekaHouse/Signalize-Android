package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.alicebot.ab.Ghost;

import java.util.ArrayList;
import java.util.Arrays;

import ppc.signalize.mira.conversation.IConversation;


public class TrainerActivity extends Activity implements View.OnClickListener,ServiceConnection,AdapterView.OnItemClickListener{


    private final String TAG = "ConversationServiceTrainer";
    private LazyListAdapter listAdapter;
    private IConversation service;
    private EditText input;
    private ArrayList<String> listInpNames,listNames;
    ListView listPatternFile;
    String responseTemplate, strFilename, strPattern;
    Button sendText;
    TextView response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileUtility.setContext(this);

        setContentView(R.layout.activity_trainer);
        //Ghost.setInternalStorage(true);
        Ghost.setExternalStorage();
        if(!FileUtility.storeExists()){
        FileUtility.copyAssetsToStorage();
        }


        sendText = (Button)findViewById(R.id.sendButton);
        input = (EditText)findViewById(R.id.inputText);
        input.setOnFocusChangeListener(new EditTextFocusChangeListener());
        sendText.setOnClickListener(this);
        response = (TextView)findViewById(R.id.responseTextView);
        listPatternFile = (ListView) findViewById(R.id.pattern_file_list);
        listPatternFile.setOnItemClickListener(this);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Intent intent = new Intent();
                intent.setAction(UtilityStrings.StartServiceBroadcast);
                sendBroadcast(intent);
                connectToService();
                return null;
            }
        };
        asyncTask.execute("");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        strFilename = listNames.get(position);
        strPattern = listInpNames.get(position);
        Intent intent = new Intent(this, FileActivity.class);
        intent.putExtra(UtilityStrings.fileIntent,strFilename);
        intent.putExtra(UtilityStrings.patternIntent,strPattern);
        startActivity(intent);
    }

    private class EditTextFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if(v.getId() == R.id.inputText && !hasFocus) {

                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        }
    }
    @Override
    public void onClick(View v) {
        try {
            if(service == null) throw new RemoteException();
            input.clearFocus();
            responseTemplate = service.getTemplate();
            response.setText(service.process(input.getText().toString()));
            String inputs = service.getPatterns();
            Toast.makeText(this,"INPUT " + inputs , Toast.LENGTH_SHORT).show();
            String[] inps = inputs.split("~");
            listInpNames = new ArrayList<String>();
            listInpNames.addAll(Arrays.asList(inps));
            String filenames = service.getFilename();
            String[] names = filenames.split("~");
            listNames = new ArrayList<String>();
            listNames.addAll(Arrays.asList(names));
            listAdapter = new LazyListAdapter(this,listNames,listInpNames);
            listPatternFile.setAdapter(listAdapter);
            sendText.requestFocus();
        } catch (RemoteException e) {
            Log.e(TAG, "NO SERVICE");
            Toast.makeText(this, "NO SERVICE", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy(){
        try{
            this.unbindService(this);
        }
        catch (IllegalArgumentException e){
            Log.e(TAG, e.getLocalizedMessage());
        }
        if(this.service!=null){


            Log.d(TAG, "The connection to the service was closed.!");

            Toast.makeText(this, "The connection to the service was closed.!",Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();

    }

    private void connectToService() {
        Intent intent;
        intent = new Intent(UtilityStrings.ConversationServiceTAG);
        intent.setClassName(this, UtilityStrings.ConversationServiceTAG);
        this.bindService(intent,this,BIND_AUTO_CREATE);
        Log.d(TAG, "The Service will be connected soon!");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trainer, menu);
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
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG,"The service is now connected");
        Toast.makeText(this,"The service is now connected",Toast.LENGTH_LONG).show();
        this.service = IConversation.Stub.asInterface(service);
        this.input.setEnabled(true);
        this.sendText.setEnabled(true);
        this.input.requestFocus();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG,"Disconnected from the service");
        this.input.setEnabled(false);
    }

}
