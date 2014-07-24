package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ViewFileActivity extends Activity {
    private final String TAG = "ViewFileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);
        TextView textView = (TextView)findViewById(R.id.textFile);
        String fileName = getIntent().getExtras().getString(IntentStrings.fileIntent);
        ((TextView)findViewById(R.id.viewFileName)).setText(fileName);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(org.alicebot.ab.FileUtils.getStorageDirectory(),"MIRA/aiml/" +fileName));
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();
            String text = new String(buffer);
            Log.d(TAG,"" + text.contains(FileUtility.changedString));
            FileUtility.setColor(textView, text, FileUtility.changedString, Color.GREEN);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_file, menu);
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
