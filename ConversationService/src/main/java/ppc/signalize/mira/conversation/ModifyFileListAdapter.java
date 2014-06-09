package ppc.signalize.mira.conversation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by mukundan on 6/9/14.
 */
public class ModifyFileListAdapter extends ArrayAdapter<ListRow>{
    private final Context context;
    private final ListRow[] values;
    public ModifyFileListAdapter(Context context, ListRow[] values){
        super(context,R.layout.list_row,values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.list_row,parent,false);
        TextView topicView = (TextView)rowView.findViewById(R.id.topicText);
        TextView patternView = (TextView)rowView.findViewById(R.id.patternText);
        TextView lineView = (TextView)rowView.findViewById(R.id.lineText);
        if(values[position].getTopic()==null){
            topicView.setText("No Topic");
        }
        else{
            topicView.setText(values[position].getTopic());
        }
        patternView.setText(values[position].getPattern());
        lineView.setText(values[position].getFullXml()+"");
        return rowView;
    }
}
