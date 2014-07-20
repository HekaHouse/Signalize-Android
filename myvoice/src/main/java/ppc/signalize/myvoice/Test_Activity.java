package ppc.signalize.myvoice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;

import ppc.signalize.myvoice.R;

public class Test_Activity extends Activity implements AdapterView.OnItemClickListener{


    FrameLayout parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_menu);
        parent = (FrameLayout)findViewById(R.id.main_frame);
        ListAdapter listAdapter = new ListAdapter(getApplicationContext());
        ListView listView = (ListView)findViewById(R.id.menu_list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        new AnimatePane(getApplicationContext()).animateContentPane( position, this.parent);
    }
}

class AnimatePane implements View.OnClickListener{
    private static boolean once = false;
    protected static View vi = null;
    protected Context context;

    public AnimatePane(Context context){
        this.context = context;
    }

    protected void animateContentPane(int position, FrameLayout parent){

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(R.layout.sliding_content,null);
        vi.setClickable(true);
        vi.setOnClickListener(this);
        TextView contentHeading = (TextView)vi.findViewById(R.id.content_heading);
        ListAdapter.setTextView(null,contentHeading,context,position);
        if(!once)
        {
            parent.addView(vi);
            once = true;
        }
        revealSlidingContent();
    }

    private void revealSlidingContent() {
        Animation hide = AnimationUtils.loadAnimation(context, R.anim.content_reveal);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Called when the Animation starts
                vi.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Called when the Animation ended
                // Since we are fading a View out we set the visibility
                // to GONE once the Animation is finished

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // This is called each time the Animation repeats
            }
        });
        vi.startAnimation(hide);
    }

    private void hideSlidingContent() {
        Animation hide = AnimationUtils.loadAnimation(context, R.anim.content_hide);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Called when the Animation starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Called when the Animation ended
                // Since we are fading a View out we set the visibility
                // to GONE once the Animation is finished
                vi.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // This is called each time the Animation repeats
            }
        });
        vi.startAnimation(hide);
    }


    @Override
    public void onClick(View v) {
        if(vi!=null){
            hideSlidingContent();

        }
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
