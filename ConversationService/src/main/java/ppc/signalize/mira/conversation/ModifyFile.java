package ppc.signalize.mira.conversation;

import android.app.Activity;
import android.app.ListActivity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;


public class ModifyFile extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String fileName = getIntent().getExtras().getString("fileName");
        Toast.makeText(this,fileName,Toast.LENGTH_SHORT).show();
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();
            String text = new String(buffer);
            ArrayList<ListRow> allRows =XMLParse.XMLParser(text);
            ListRow[] listRow = new ListRow[allRows.size()];
            listRow = allRows.toArray(listRow);
            ModifyFileListAdapter taglist = new ModifyFileListAdapter(this,listRow);
            //ListView listTags = (ListView)findViewById(R.id.file_list_tags);
            this.setListAdapter(taglist);
            /*for(ListRow s:XMLParse.XMLParser(text)){
                taglist.append(s.getTopic());
                taglist.append("  ");
                taglist.append(s.getPattern());
                taglist.append("  ");
                taglist.append(s.getFullXml());
                taglist.append("  ");
                taglist.append("\n");

            }*/

            //TextView textView = (TextView)findViewById(R.id.fileText);
            Toast.makeText(this,"Setting TextView",Toast.LENGTH_SHORT).show();
            //textView.setText(taglist.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ModifyFile","No such file"+fileName);
            Toast.makeText(this,"No such file"+fileName,Toast.LENGTH_SHORT).show();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.modify_file, menu);
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
