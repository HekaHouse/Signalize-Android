package ppc.signalize.mira.conversation.conversationserviceclient.conversationserviceclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ppc.signalize.mira.conversation.IConversation;


public class ConversationClient extends Activity implements ServiceConnection,View.OnClickListener{

    private final String ConversationServiceTAG= "ppc.signalize.mira.conversation.ConversationService";
    private final String ConversationSericePackage = "ppc.signalize.mira.conversation";
    private IConversation service;
    private final String TAG = "ConversationClient";
    private final String StartServiceBroadcast = "ppc.signalize.mira.conversation.startService";
    private EditText input;
    TextView outputPattern, outputFile;
    Button sendText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Intent intent = new Intent();
        intent.setAction(StartServiceBroadcast);
        this.sendBroadcast(intent);
        connectToService();
        sendText = (Button)findViewById(R.id.sendButton);
        input = (EditText)findViewById(R.id.inputText);
        input.setOnFocusChangeListener(new EditTextFocusChangeListener());
        outputPattern = (TextView)findViewById(R.id.responseText);
        sendText.setOnClickListener(this);
        outputFile = (TextView)findViewById(R.id.fileText);

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
            service.process(input.getText().toString());
            outputPattern.setText(service.inputThatTopic());
            outputFile.setText(service.getFilename());
            input.setText("");
            sendText.requestFocus();
        } catch (RemoteException e) {
            Log.e(TAG,"NO SERVICE");
            Toast.makeText(this,"NO SERVICE",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onStop() {
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
        super.onStop();

    }

    private void connectToService() {
        Intent intent;
        intent = new Intent(ConversationServiceTAG);
        intent.setClassName(ConversationSericePackage,ConversationServiceTAG);
        this.bindService(intent,this,BIND_AUTO_CREATE);
        Log.d(TAG, "The Service will be connected soon!");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation_client, menu);
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
