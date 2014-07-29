package ppc.signalize.myvoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;

/**
 * Created by Aron on 7/27/2014.
 */
public class ListAdapter extends BaseAdapter {
    private Context context;
    private static LayoutInflater inflater = null;


    public ListAdapter(Context context){
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean isEnabled(int position) {
        if(AnimatePane.vi != null && AnimatePane.vi.getVisibility() == View.VISIBLE ){
            return false;
        }
        return super.isEnabled(position);
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
        setTextView(svgImageView,textView,context,position);
        return vi;

    }

    protected static void setTextView(SVGImageView svgImageView, TextView textView, Context context, int position){
        switch (position){
            case 0:
                if(svgImageView!=null){
                    svgImageView.setImageResource(R.drawable.request_assistance);
                }
                textView.setText(context.getString(R.string.request_assistance));
                break;
            case 1:
                if(svgImageView!=null){
                    svgImageView.setImageResource(R.drawable.care_schedule);
                }
                textView.setText(context.getString(R.string.care_schedule));
                break;
            case 2:
                if(svgImageView!=null){
                    svgImageView.setImageResource(R.drawable.treatment_plan);
                }
                textView.setText(context.getString(R.string.treatment_plan));
                break;
            case 3:
                if(svgImageView!=null){
                    svgImageView.setImageResource(R.drawable.dining);
                }
                textView.setText(context.getString(R.string.dining));
                break;
            case 4:
                if(svgImageView!=null){
                    svgImageView.setImageResource(R.drawable.entertainment);
                }
                textView.setText(context.getString(R.string.entertainment));
                break;
            case 5:
                if(svgImageView!=null){
                    svgImageView.setImageResource(R.drawable.how_are_we_doing);
                }
                textView.setText(context.getString(R.string.how_are_we_doing));
                break;
        }
    }
}
