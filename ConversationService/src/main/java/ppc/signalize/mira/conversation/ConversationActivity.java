package ppc.signalize.mira.conversation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.alicebot.ab.FileUtils;
import org.alicebot.ab.Ghost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class ConversationActivity extends Activity {
    Button copyFiles;
    RadioGroup radioStorageGroup;
    RadioButton radioStorageButton;
    ListView fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        FileUtils.setContext(this);
        copyFiles = (Button)findViewById(R.id.copyFilesButton);
        radioStorageGroup = (RadioGroup)findViewById(R.id.radioStorageGroup);
        fileList = (ListView)findViewById(R.id.fileList);
        Ghost.setContext(this);
        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.share_preference_file),MODE_PRIVATE);
        final SharedPreferences.Editor edit = prefs.edit();
        copyFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioStorageButton = (RadioButton)findViewById(radioStorageGroup.getCheckedRadioButtonId());
                String text = radioStorageButton.getText().toString();
                if(text.contains(getString(R.string.radio_external_storage))){
                    Ghost.setExternalStorage();
                    FileUtils.copyAssetsToStorage();
                    Log.d("Conversation Activity","Selected External Storage");
                    Toast.makeText(getApplicationContext(),"Selected External Storage",Toast.LENGTH_LONG).show();
                    Util.setStorageType(FileUtils.STORAGE_TYPE.EXTERNAL_STORAGE);
                    edit.putString(getString(R.string.storage_type_pref),FileUtils.STORAGE_TYPE.EXTERNAL_STORAGE.name());
                    edit.commit();
                }
                else if(text.contains(getString(R.string.radio_internal_storage))){
                    Ghost.setInternalStorage(true);
                    FileUtils.copyAssetsToStorage();
                    Log.d("Conversation Activity","Selected Internal Storage");
                    Toast.makeText(getApplicationContext(),"Selected Internal Storage",Toast.LENGTH_LONG).show();
                    Util.setStorageType(FileUtils.STORAGE_TYPE.INTERNAL_STORAGE);
                    edit.putString(getString(R.string.storage_type_pref),FileUtils.STORAGE_TYPE.INTERNAL_STORAGE.name());
                    edit.commit();
                }
                try {
                    setListAdapter();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Conversation Activity","IO Exception " + e.getMessage()  + " " + e.getCause());
                    Toast.makeText(getApplicationContext(),"IO Exception " + e.getMessage()  + " " + e.getCause(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void setListAdapter() throws IOException {
        Log.d("Conversation Activity","FILE UTILS STORAGE TYPE " + Ghost.getFilesDir());
        Toast.makeText(this,"FILE UTILS STORAGE TYPE " + Ghost.getFilesDir(),Toast.LENGTH_SHORT).show();
        String[] fileL = FileUtils.listFiles(this,FileUtils.AIMLdir);
        ArrayList<String> fileLArrayList = new ArrayList<String>();
        fileLArrayList.addAll(Arrays.asList(fileL));
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fileLArrayList);
        fileList.setAdapter(listAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation, menu);
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
}
