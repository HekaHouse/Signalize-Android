package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by mukundan on 7/3/14.
 */
public class LazyListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<String> filenames,patterns;
    private static LayoutInflater inflater = null;

    public LazyListAdapter(Activity a, ArrayList<String> filenames, ArrayList<String> patterns){
        this.activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.filenames = filenames;
        this.patterns = patterns;
    }


    @Override
    public int getCount() {
        return patterns.size();
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
            vi = inflater.inflate(R.layout.pattern_file_row,null);
        }
        TextView pattern = (TextView)vi.findViewById(R.id.pattern_row_textview);
        TextView file = (TextView)vi.findViewById(R.id.file_row_textview);
        pattern.setText(patterns.get(position));
        file.setText(filenames.get(position));
        return vi;
    }
}
