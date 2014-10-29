package ppc.signalize.myvoice;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.caverock.androidsvg.SVGImageView;

import ppc.signalize.mira.Mira;
import ppc.signalize.mira.face.MiraAbstractActivity;
import ppc.signalize.myvoice.model.menu.MyMenuManager;
import ppc.signalize.myvoice.util.adapter.MyMenuAdapter;
import ppc.signalize.myvoice.util.animate.AnimatePane;
import ppc.signalize.myvoice.util.animate.VerticalAnimatePane;


public class MyVoiceActivity extends MiraAbstractActivity implements RecyclerView.OnItemTouchListener {

    public static final String TAG = "MyVoiceActivity";
    Handler updateConversationHandler;
    static RecyclerView menuView;

    FrameLayout parent;
    private MyMenuAdapter mAdapter;
    private GestureDetectorCompat gesturedetector;
    private AnimatePane animated;
    private VerticalAnimatePane v_animated;
    private FrameLayout mic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_voice_menu);
        init();
        parent = (FrameLayout)findViewById(R.id.main_frame);
        mAdapter = new MyMenuAdapter(new MyMenuManager().getItems(), R.layout.my_voice_menu_row, this);

        menuView = (RecyclerView)findViewById(R.id.menu_list);
        menuView.setLayoutManager(new LinearLayoutManager(this));
        menuView.setItemAnimator(new DefaultItemAnimator());


        menuView.setAdapter(mAdapter);
        menuView.addOnItemTouchListener(this);
        gesturedetector = new GestureDetectorCompat(this, new MyOnGestureListener());
//        menuView.setAdapter(listAdapter);
//        menuView.setOnItemClickListener(this);

        mic = (FrameLayout)findViewById(R.id.mic_box);


    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            parent.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
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
        return true;
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

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        new AnimatePane(getApplicationContext()).animateContentPane( position, this.parent);
//    }

    @Override
    public void onBackPressed() {
        if(AnimatePane.vi != null &&AnimatePane.vi.getVisibility() != View.GONE){
            AnimatePane.hideSlidingContent();
        }
        else{
            super.onBackPressed();
        }
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gesturedetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }



    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = menuView.findChildViewUnder(e.getX(), e.getY());
            clearSelection();
            if(AnimatePane.vi != null &&AnimatePane.vi.getVisibility() != View.GONE){
                mAdapter.clearSelections();
                AnimatePane.hideSlidingContent();
                int idx = menuView.getChildPosition(view);
                animated = new AnimatePane(MyVoiceActivity.this);
                animated.animateContentPaneQueued( idx, MyVoiceActivity.this.parent);
                mAdapter.toggleSelection(idx);
            } else {
                int idx = menuView.getChildPosition(view);
                animated = new AnimatePane(MyVoiceActivity.this);
                animated.animateContentPane( idx, MyVoiceActivity.this.parent);
                mAdapter.toggleSelection(idx);
            }
            
            menuView.findViewHolderForPosition(menuView.getChildPosition(view)).itemView.setSelected(true);

            return super.onSingleTapConfirmed(e);
        }
    }

    public void take_visitation_note(View v) {
        switch (v.getId()){
            case R.id.scheduling_note:
                v_animated = new VerticalAnimatePane(MyVoiceActivity.this);
                v_animated.animateContentPane(getString(R.string.personal_visit_schedule_text), R.layout.my_personal_visit_note, MyVoiceActivity.this.parent);
                break;
            case R.id.dining_note:

                break;

        }
        // Obtain MotionEvent object
//        long downTime = SystemClock.uptimeMillis();
//        long eventTime = SystemClock.uptimeMillis() + 100;
//        float x = mic.getX()+(mic.getWidth()/2);
//        float y = mic.getY()+(mic.getHeight()/2);
//        int metaState = 0;
//        MotionEvent motionEvent = MotionEvent.obtain(
//                downTime,
//                eventTime,
//                MotionEvent.ACTION_DOWN,
//                x,
//                y,
//                metaState
//        );
//
//        mic.dispatchTouchEvent(motionEvent);
        start_mic(v);
    }
    public void close_note(View v) {
        // Obtain MotionEvent object
//        long downTime = SystemClock.uptimeMillis();
//        long eventTime = SystemClock.uptimeMillis() + 100;
//        float x = mic.getX()+(mic.getWidth()/2);
//        float y = mic.getY()+(mic.getHeight()/2);
//        int metaState = 0;
//        MotionEvent motionEvent = MotionEvent.obtain(
//                downTime,
//                eventTime,
//                MotionEvent.ACTION_UP,
//                x,
//                y,
//                metaState
//        );
//
//        mic.dispatchTouchEvent(motionEvent);
        VerticalAnimatePane.hideSlidingContent();
        if (mic.isSelected())
            mic.performClick();
    }
    public void close_content(View v) {
        AnimatePane.hideSlidingContent();
    }
    public void clearSelection() {
        for (int i = 0; i < menuView.getChildCount(); i++) {
            menuView.findViewHolderForPosition(i).itemView.setSelected(false);

        }
    }
    public void start_mic(View v) {

        //if mic is already on
        if (mic.isSelected()) {

            //if clicking the button brought you here
            if (v.getId() == R.id.mic_box) {
                mic.setSelected(false);
                myVoice.getMira().stop_listen("");
            }
            //if mic is off and a note brought you here
            else {
                myVoice.getMira().consider(getTopicForNote(v));
                myVoice.getMira().listen("What would you like to say?");
            }
        //if mic is off and clicking the button brought you here
        } else if (v.getId() == R.id.mic_box) {
            mic.setSelected(true);
            myVoice.getMira().listen("How can I help you?");
        //if mic is off and a note brought you here
        } else {
            mic.setSelected(true);
            myVoice.getMira().consider(getTopicForNote(v));
            myVoice.getMira().listen("What would you like to say?");
        }
    }

    private String getTopicForNote(View v) {
        switch (v.getId()) {
            case R.id.scheduling_note :
                return Mira.buildTopicTag("scheduling note");
        }
        return "";
    }
}


