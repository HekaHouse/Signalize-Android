package ppc.signalize.myvoice;

import android.os.Bundle;
import android.os.Handler;
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


import ppc.signalize.mira.Mira;
import ppc.signalize.mira.face.MiraAbstractActivity;
import ppc.signalize.myvoice.model.menu.MyMenuManager;
import ppc.signalize.myvoice.util.adapter.MyMenuAdapter;
import ppc.signalize.myvoice.util.animate.AnimatePane;
import ppc.signalize.myvoice.util.animate.VerticalAnimatePane;
import ppc.signalize.myvoice.views.LinedEditText;


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
        View v = findViewById(R.id.note_text);
        if (v != null) {
            LinedEditText myText = (LinedEditText)v;
            myText.setText(myText.getText()+"\n"+toSpan);
        }
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
            case R.id.admission_note:
                v_animated = new VerticalAnimatePane(MyVoiceActivity.this);
                v_animated.animateContentPane(getString(R.string.admission_note_text), R.drawable.admission, MyVoiceActivity.this.parent);
                break;
            case R.id.room_note:
                v_animated = new VerticalAnimatePane(MyVoiceActivity.this);
                v_animated.animateContentPane(getString(R.string.room_note_text), R.drawable.room, MyVoiceActivity.this.parent);
                break;
            case R.id.meals_note:
                v_animated = new VerticalAnimatePane(MyVoiceActivity.this);
                v_animated.animateContentPane(getString(R.string.meals_note_text), R.drawable.meals, MyVoiceActivity.this.parent);
                break;
            case R.id.nurses_note:
                v_animated = new VerticalAnimatePane(MyVoiceActivity.this);
                v_animated.animateContentPane(getString(R.string.nurses_note_text), R.drawable.nurses, MyVoiceActivity.this.parent);
                break;
            case R.id.visitors_note:
                v_animated = new VerticalAnimatePane(MyVoiceActivity.this);
                v_animated.animateContentPane(getString(R.string.visitors_note_text), R.drawable.visitors, MyVoiceActivity.this.parent);
                break;
            case R.id.special_services_note:
                v_animated = new VerticalAnimatePane(MyVoiceActivity.this);
                v_animated.animateContentPane(getString(R.string.special_services_note_text), R.drawable.special_services, MyVoiceActivity.this.parent);
                break;
            case R.id.phys_note:
                v_animated = new VerticalAnimatePane(MyVoiceActivity.this);
                v_animated.animateContentPane(getString(R.string.phys_note_text), R.drawable.physician, MyVoiceActivity.this.parent);
                break;
            case R.id.rehab_note:
                v_animated = new VerticalAnimatePane(MyVoiceActivity.this);
                v_animated.animateContentPane(getString(R.string.rehab_note_text), R.drawable.rehab, MyVoiceActivity.this.parent);
                break;
            case R.id.personal_note:
                v_animated = new VerticalAnimatePane(MyVoiceActivity.this);
                v_animated.animateContentPane(getString(R.string.personal_note_text), R.drawable.personal, MyVoiceActivity.this.parent);
                break;

        }
        start_mic(v);
    }
    public void close_note(View v) {
        VerticalAnimatePane.hideSlidingContent();
        if (mic.isSelected())
            mic.performClick();
        if(isNoting())
            isNoting(false);
    }

    @Override
    public void close_note() {
        close_note(null);
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
                isNoting(true);
                myVoice.getMira().listen(myVoice.getMira().consider(getTopicForNote(v)).mResponse);
                mic.setSelected(true);
            }
        //if mic is off and clicking the button brought you here
        } else if (v.getId() == R.id.mic_box) {
            myVoice.getMira().listen("How can I help you?");
            mic.setSelected(true);
        } else {
            isNoting(true);
            myVoice.getMira().listen(myVoice.getMira().consider(getTopicForNote(v)).mResponse);
            mic.setSelected(true);
        }
    }

    private String getTopicForNote(View v) {
        switch (v.getId()) {
            case R.id.admission_note:
                return Mira.buildTopicTag("RECORD A YOUR ADMISSION NOTE");
            case R.id.room_note:
                return Mira.buildTopicTag("RECORD A YOUR ROOM NOTE");
            case R.id.meals_note:
                return Mira.buildTopicTag("RECORD A YOUR MEALS NOTE");
            case R.id.nurses_note:
                return Mira.buildTopicTag("RECORD A YOUR NURSES NOTE");
            case R.id.visitors_note:
                return Mira.buildTopicTag("RECORD A YOUR VISITORS NOTE");
            case R.id.special_services_note:
                return Mira.buildTopicTag("RECORD A YOUR SERVICES NOTE");
            case R.id.rehab_note:
                return Mira.buildTopicTag("RECORD A YOUR REHABILITATION NOTE");
            case R.id.phys_note:
                return Mira.buildTopicTag("RECORD A YOUR PHYSICIAN NOTE");
            case R.id.personal_note:
                return Mira.buildTopicTag("RECORD A ANYTHING NOTE");
        }
        return "";
    }
}


