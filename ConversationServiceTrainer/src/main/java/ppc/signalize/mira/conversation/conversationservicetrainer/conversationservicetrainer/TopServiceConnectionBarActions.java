package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer.R.layout.top_service_connection_bar;

/**
 * Created by mukundan on 8/1/14.
 */
public class TopServiceConnectionBarActions {

    private static String NOT_CONNECTED = "Not Connected to Service !!!";
    private static String CONNECTION_START = "Connecting to Service ...";
    private static String CONNECTED = "Connection to service established !";
    private static int delay = 3000;

    private Context context;
    private LinearLayout bar;
    private TextView text;
    private static LayoutInflater inflater;
    public TopServiceConnectionBarActions(LinearLayout layout, TextView textView){
        this.bar = layout;
        this.text = textView;
    }

    public TopServiceConnectionBarActions(Context context){
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bar = (LinearLayout) inflater.inflate(top_service_connection_bar,null);
        this.context = context;
        text = (TextView)bar.findViewById(R.id.top_service_connection_bar_text);
    }

    private void setText(String value){
        text.setText(value);
    }

    private void setBackground(int color){
        bar.setBackgroundColor(color);
    }

    private void setFontColor(int color){
        text.setTextColor(color);
    }

    public void connectionStart(){
        setVisibility(true);
        setBackground(Color.YELLOW);
        setFontColor(Color.BLACK);
        setText(CONNECTION_START);
    }

    public void notConnected(){
        setVisibility(true);
        setBackground(Color.RED);
        setText(TopServiceConnectionBarActions.NOT_CONNECTED);
        setFontColor(Color.WHITE);
    }

    public void connected(){
        setVisibility(true);
        setBackground(Color.GREEN);
        setText(CONNECTED);
        setFontColor(Color.BLACK);
        bar.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(false);
            }
        },delay);
    }

    private void setVisibility(boolean flag){
        if(flag){
            bar.setVisibility(View.VISIBLE);
        }
        else
            bar.setVisibility(View.GONE);
    }

    public void addToRoot(LinearLayout root){
        root.addView(bar);
    }


}
