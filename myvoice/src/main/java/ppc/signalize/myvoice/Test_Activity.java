package ppc.signalize.myvoice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;

import ppc.signalize.myvoice.R;

public class Test_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_menu);
        ListAdapter listAdapter = new ListAdapter(getApplicationContext());
        ListView listView = (ListView)findViewById(R.id.menu_list);
        listView.setAdapter(listAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_, menu);
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

class ListAdapter extends BaseAdapter{
    private Context context;
    private static LayoutInflater inflater = null;


    public ListAdapter(Context context){
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(convertView == null){
            vi = inflater.inflate(R.layout.main_list_menu_row,null);
        }

        SVGImageView svgImageView = (SVGImageView) vi.findViewById(R.id.menu_image);
        TextView textView = (TextView)vi.findViewById(R.id.menu_item_name);
        switch (position){
            case 0:
                svgImageView.setImageResource(R.drawable.request_assistance);
                textView.setText(context.getString(R.string.request_assistance));
                break;
            case 1:
                svgImageView.setImageResource(R.drawable.care_schedule);
                textView.setText(context.getString(R.string.care_schedule));
                break;
            case 2:
                svgImageView.setImageResource(R.drawable.treatment_plan);
                textView.setText(context.getString(R.string.treatment_plan));
                break;
            case 3:
                svgImageView.setImageResource(R.drawable.dining);
                textView.setText(context.getString(R.string.dining));
                break;
            case 4:
                svgImageView.setImageResource(R.drawable.entertainment);
                textView.setText(context.getString(R.string.entertainment));
                break;
            case 5:
               svgImageView.setImageResource(R.drawable.how_are_we_doing);
                textView.setText(context.getString(R.string.how_are_we_doing));
                break;
        }
        return vi;

    }
}
