package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by mukundan on 7/3/14.
 */
public class LazyListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<String> filenames,patterns;
    private static LayoutInflater inflater = null;
    private ImageView delete_button,add_button;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(convertView == null){
            vi = inflater.inflate(R.layout.pattern_file_row,null);
        }
        TextView depth = (TextView)vi.findViewById(R.id.depthText);
        TextView pattern = (TextView)vi.findViewById(R.id.pattern_row_textview);
        TextView file = (TextView)vi.findViewById(R.id.file_row_textview);
        depth.setText(""+(position+1));
        add_button = (ImageView) vi.findViewById(R.id.new_reduction);
        add_button.setFocusable(false);
        add_button.setTag(position);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                Log.d("LazyListAdapter", "position " + position + " " + TrainerActivity.listInpNames.get(position) +
                        ", " + TrainerActivity.listNames.get(position));
                NewReductionDialog nrd = new NewReductionDialog(activity,position);
                nrd.show();

            }
        });
        delete_button = (ImageView) vi.findViewById(R.id.delete_reduction);
        delete_button.setFocusable(false);
        delete_button.setTag(position);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                Toast.makeText(v.getContext(), "position " + position + " " + TrainerActivity.listInpNames.get(position) + ", " + TrainerActivity.listNames.get(position),
                        Toast.LENGTH_SHORT).show();

                DeleteReductionDialog drd = new DeleteReductionDialog(activity,position);
                drd.show();
            }
        });
        pattern.setText(patterns.get(position));
        file.setText(filenames.get(position));
        return vi;
    }
}

