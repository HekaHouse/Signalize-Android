package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.alicebot.ab.FileUtils;
import org.alicebot.ab.Ghost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ppc.signalize.mira.conversation.IConversation;


public class TrainerActivity extends Activity implements View.OnClickListener,ServiceConnection,AdapterView.OnItemClickListener{

    private Menu menu;

    protected static ReductionMap map;
    protected static ArrayList<String> listOfPatterns;
    private boolean bound;
    private final String TAG = "ConversationServiceTrainer";
    protected static String inputPattern;
    private LazyListAdapter listAdapter;
    private TopServiceConnectionBarActions bar;
    private IConversation service;
    private EditText input;
    protected static ArrayList<String> listInpNames,listNames;
    ListView listPatternFile;
    String responseTemplate, strFilename, strPattern;
    Button sendText,addReduction;
    TextView response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileUtility.setContext(this);

        setContentView(R.layout.activity_trainer);
        //Ghost.setInternalStorage(true);

        sendText = (Button)findViewById(R.id.sendButton);
        addReduction = (Button) findViewById(R.id.trainer_add_new_reduction);
        addReduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input.getText().length() == 0){
                    AdvancedSettings.showErrorToast(getApplicationContext(),"Specify input pattern first");
                }
                else if(FileUtility.getStorageType() != FileUtility.STORAGE_TYPE.EXTERNAL_STORAGE){
                    AdvancedSettings.showErrorToast(getApplicationContext(),UtilityStrings.cannot_modify_text);
                }
                else{
                    try {
                        final String[] filelist = FileUtility.listFiles(getApplicationContext(),FileUtility.AIMLdir);
                        Log.d(TAG,"FILEIST " + filelist.length);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(TrainerActivity.this);
                        builder.setTitle(getString(R.string.choose_file));
                        Dialog dialog = null;

                        builder.setItems(filelist,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                (new NewReductionDialog(TrainerActivity.this,filelist[which],input.getText().toString())).show();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(getString(R.string.cancel_button),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();


                            }
                        });
                        dialog = builder.create();
                        dialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        AdvancedSettings.showErrorToast(getApplicationContext(),"Cannot load file list!!");
                    }


                }
            }
        });
        input = (EditText)findViewById(R.id.inputText);
        input.setOnFocusChangeListener(new EditTextFocusChangeListener());
        sendText.setOnClickListener(this);
        response = (TextView)findViewById(R.id.responseTextView);
        listPatternFile = (ListView) findViewById(R.id.pattern_file_list);
        listPatternFile.setOnItemClickListener(this);
        Intent intent = new Intent();
        intent.setAction(UtilityStrings.StartServiceBroadcast);
        sendBroadcast(intent);
        bar = new TopServiceConnectionBarActions(this);
        bar.addToRoot(((LinearLayout)findViewById(R.id.top_service_connection_bar_root)));
        bar.notConnected();

        connectToService();

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
        String responseFromService;
        String inputs;
        String filenames;
        try {
            if(service == null) throw new RemoteException();
            input.clearFocus();
            responseTemplate = service.getTemplate();


            inputPattern = input.getText().toString();
            if(inputPattern.length() == 0){
                inputPattern = "*";
                Log.e(TAG,"Setting input Pattern to " + inputPattern + "~" + inputPattern.length());
                input.setText("*");
            }
            responseFromService = service.process(inputPattern);
            inputs = service.getPatterns();
            filenames = service.getFilenames();
            if(inputs == null || filenames == null || responseFromService == null){
                if(inputs == null)
                    inputs = "";
                if(filenames == null)
                    filenames = "";
                if(responseFromService == null)
                   responseFromService = "";

                Toast.makeText(this,"No input or no filename or no response from service",Toast.LENGTH_LONG).show();
            }
            response.setText(responseFromService);
            Toast.makeText(this,"INPUT " + inputs , Toast.LENGTH_SHORT).show();
            String[] inps = inputs.split(FileUtils.delimiter);
            listInpNames = new ArrayList<String>();
            listInpNames.addAll(Arrays.asList(inps));
            map = new ReductionMap(listInpNames);
            String[] names = filenames.split("~");
            listNames = new ArrayList<String>();
            listNames.addAll(Arrays.asList(names));
            Log.i(TAG,"LENGTH " + listInpNames.size());
            Log.i(TAG,listInpNames.toString());
            Log.i(TAG,"LENGTH @ " + listNames.size());
            Log.i(TAG,listNames.toString());
            listAdapter = new LazyListAdapter(this,listNames,listInpNames);
            listPatternFile.setAdapter(listAdapter);
            sendText.requestFocus();
        } catch (RemoteException e) {
            Log.e(TAG, "NO SERVICE");
            Toast.makeText(this, "NO SERVICE", Toast.LENGTH_LONG).show();
        }
        catch (Exception ex){
            Log.e(TAG, "Unknown Exception");
            AdvancedSettings.showErrorToast(this, "Unknown Exception " + ex.getCause());
        }
        catch (Error er){
            Log.e(TAG,"FATAL ERROR");
            AdvancedSettings.showErrorToast(this,"Unknown ERROR " + er.getCause());
        }
    }

    @Override
    protected void onDestroy(){

        try{
            if(bound) {
                this.unbindService(this);
                if(this.service!=null){
                    Log.d(TAG, "The connection to the service was closed.!");
                    Toast.makeText(this, "The connection to the service was closed.!",Toast.LENGTH_SHORT).show();
                }
            }
            else
                Log.e(TAG,"NOT BOUND TO SERVICE");
        }
        catch (IllegalArgumentException e){
            Log.e(TAG, e.getLocalizedMessage());
        }

        super.onDestroy();

    }

    private void connectToService() {
        Intent intent;
        intent = new Intent(UtilityStrings.ConversationServiceTAG);
        intent.setClassName(UtilityStrings.ConversationServicePackage,UtilityStrings.ConversationServiceTAG);
        bound = this.bindService(intent,this,BIND_AUTO_CREATE);
        Log.d(TAG, "The Service will be connected soon! ");
        bar.connectionStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trainer_activity_actions, menu);
        this.menu = menu;
        enableConnectToService(true);
        Log.d(TAG,"Created options menu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.resync_action) {
            //service.writeAIMLOut();

            disableUI();
            new LongResync().execute();

            return true;
        }
        else if(id == R.id.add_new_aiml_file){
            if(FileUtility.getStorageType() == FileUtils.STORAGE_TYPE.EXTERNAL_STORAGE) {
                new CreateFileDialog(this).show();
            }
            else{
                AdvancedSettings.showErrorToast(this,UtilityStrings.cannot_modify_text);
            }

        }
        else if(id == R.id.retry_connection){
            Toast.makeText(this,"Clicked Retry Connection",Toast.LENGTH_SHORT).show();
            connectToService();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void disableUI(){
        input.setEnabled(false);
        sendText.setEnabled(false);
        listPatternFile.setEnabled(false);
        Toast.makeText(getApplicationContext(),"Starting Resync UI Disabled",Toast.LENGTH_LONG).show();

    }

    protected void enableUI(){
        Log.d(TAG,"Enabling UI");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendText = (Button)findViewById(R.id.sendButton);
                input = (EditText)findViewById(R.id.inputText);
                input.setEnabled(true);
                sendText.setEnabled(true);
                listPatternFile.setEnabled(true);
                input.requestFocus();
                Toast.makeText(getApplicationContext(),"Finished Resync Enabling UI",Toast.LENGTH_SHORT).show();
            }
        });
    }



    private class CreateFileDialog extends Dialog implements View.OnClickListener{

        Button ok,cancel;
        EditText filenameET;
        public CreateFileDialog(Context context) {
            super(context);
            this.setContentView(R.layout.custom_create_file_dialog);
            ok = (Button)findViewById(R.id.create_file_dialog_ok_button);
            this.setTitle("Create New File");
            cancel = (Button)findViewById(R.id.create_file_dialog_cancel_button);
            filenameET = (EditText)findViewById(R.id.new_file_name_dialog_et);
            ok.setOnClickListener(this);
            cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.create_file_dialog_ok_button){
                Intent intent = new Intent(getApplicationContext(),AdvancedSettings.class);
                if(filenameET.getText().length() == 0){
                    AdvancedSettings.showErrorToast(getApplicationContext(),"Enter valid filename");
                    return;
                }
                String filename = filenameET.getText().toString();
                if(!filename.contains(".aiml")){
                    filename += ".aiml";
                }
                intent.putExtra(UtilityStrings.newFileIntent,filename);

                this.dismiss();
                startActivity(intent);
            }
            else if(v.getId() == R.id.create_file_dialog_cancel_button){
                this.dismiss();
            }
        }
    }

    private class LongResync extends AsyncTask<Void,Void,Void>{

        private EditText input;
        private Button sendText;
        @Override
        protected Void doInBackground(Void... params) {
            if(service!=null){
                Log.d(TAG,"STARTING RESYNC");

                try {
                    service.reSync();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getStackTrace().toString());
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            enableUI();
            setListOfPatterns();
        }
    }

    protected void setListOfPatterns(){
       listOfPatterns = new ArrayList<String>();
        try {
            ArrayList<String> test = (ArrayList<String>) service.listOfPatterns();
            Log.e(TAG,"ADDING LIST OF PATTERNS");
            Log.e(TAG,test.size() + " ");
            listOfPatterns.addAll(test);
        } catch (RemoteException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "The service is now connected");
        Toast.makeText(this,"The service is now connected",Toast.LENGTH_LONG).show();
        this.service = IConversation.Stub.asInterface(service);
        this.input.setEnabled(true);
        this.sendText.setEnabled(true);
        this.input.requestFocus();
        setListOfPatterns();
        bar.connected();
        enableConnectToService(false);
        try {
            FileUtility.setStorageType(FileUtility.STORAGE_TYPE.valueOf(this.service.getStorageType()));
        } catch (RemoteException e) {
            e.printStackTrace();
            AdvancedSettings.showErrorToast(this,"Not Able to get the defined Storage Type!!!");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG,"Disconnected from the service");
        disableUI();
        bar.notConnected();
        enableConnectToService(true);
    }

    private void enableConnectToService(final boolean flag){
        if(menu == null){
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            enableConnectToService(flag);
                        }
                    });
                }
            },5000);
        }
        else {
            MenuItem menuItem = menu.findItem(R.id.retry_connection);
            Log.d(TAG,"Connect to service " + flag);
            menuItem.setVisible(flag);
            //menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UtilityStrings.DELETE_REDUCTION_RESULT_CODE){
            if(resultCode == RESULT_OK){
                int position = data.getExtras().getInt(UtilityStrings.positionIntent);
                listInpNames.remove(position);
                listNames.remove(position);
                listPatternFile.deferNotifyDataSetChanged();
                disableUI();
                new LongResync().execute();
            }
            else if(resultCode == RESULT_CANCELED){
                AdvancedSettings.showErrorToast(this,"Error in removing the reduction!!");
            }
        }

    }
}
