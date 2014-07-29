package ppc.signalize.myvoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.caverock.androidsvg.SVGImageView;

import ppc.signalize.mira.face.MiraAbstractActivity;
import ppc.signalize.mira.nervous.concurrent.AsyncMiraTextResponse;
import ppc.signalize.myvoice.util.SystemUiHider;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MyVoiceActivity extends MiraAbstractActivity implements AdapterView.OnItemClickListener {

    public static final String TAG = "MyVoiceActivity";
    Handler updateConversationHandler;
    static ListView listView;
    SVGImageView mic;
    static int micClick = 0;
    FrameLayout parent;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_menu);
        parent = (FrameLayout)findViewById(R.id.main_frame);
        ListAdapter listAdapter = new ListAdapter(getApplicationContext());

        listView = (ListView)findViewById(R.id.menu_list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        mic = (SVGImageView)findViewById(R.id.svg_mic);
        mic.setClickable(true);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++micClick;
                Toast.makeText(getApplicationContext(),"Clicked mic " + micClick + "time(s)",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Spannable createSpan(String words, int aligned) {
        return null;
    }

    @Override
    public void prependMiraText(String toSpan, int aligned) {

    }

    @Override
    protected void startListening() {

    }

    @Override
    public boolean canListen() {
        return false;
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

    @Override
    public void onBackPressed() {
        if(AnimatePane.vi != null &&AnimatePane.vi.getVisibility() != View.GONE){
            AnimatePane.hideSlidingContent();
        }
        else{
            super.onBackPressed();
        }
    }





}


