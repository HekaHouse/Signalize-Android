package ppc.signalize.mira.conversation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class ConversationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        /*Intent intent = new Intent(this,ConversationService.class);
        startService(intent);*/
        ListView listView=(ListView)findViewById(R.id.listView);
        try {
            String []files = this.getAssets().list("MIRA/aiml");
            ArrayList listFiles;
            listFiles = new ArrayList<String>();
            for(String filename:files){
                listFiles.add(filename);
            }
            ArrayAdapter<String> arrayAdapter;
            arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listFiles);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = (TextView)view;
                    Toast.makeText(getApplicationContext(),"Clicked "+textView.getText().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent;
                    intent = new Intent(getApplication(),ModifyFile.class);
                    intent.putExtra("fileName","MIRA/aiml/"+textView.getText().toString());
                    startActivity(intent);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

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
