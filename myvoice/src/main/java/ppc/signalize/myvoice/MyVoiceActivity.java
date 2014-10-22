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
import android.widget.Toast;


import com.caverock.androidsvg.SVGImageView;

import ppc.signalize.mira.face.MiraAbstractActivity;
import ppc.signalize.myvoice.model.menu.MyMenuManager;
import ppc.signalize.myvoice.util.adapter.MyMenuAdapter;


public class MyVoiceActivity extends MiraAbstractActivity implements RecyclerView.OnItemTouchListener {

    public static final String TAG = "MyVoiceActivity";
    Handler updateConversationHandler;
    static RecyclerView menuView;
    SVGImageView mic;
    static int micClick = 0;
    FrameLayout parent;
    private MyMenuAdapter mAdapter;
    private GestureDetectorCompat gesturedetector;
    private AnimatePane animated;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_menu);
        parent = (FrameLayout)findViewById(R.id.main_frame);
        mAdapter = new MyMenuAdapter(new MyMenuManager().getItems(), R.layout.main_list_menu_row, this);

        menuView = (RecyclerView)findViewById(R.id.menu_list);
        menuView.setLayoutManager(new LinearLayoutManager(this));
        menuView.setItemAnimator(new DefaultItemAnimator());


        menuView.setAdapter(mAdapter);
        menuView.addOnItemTouchListener(this);
        gesturedetector = new GestureDetectorCompat(this, new MyOnGestureListener());
//        menuView.setAdapter(listAdapter);
//        menuView.setOnItemClickListener(this);
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

    public void clearSelection() {
        for (int i = 0; i < menuView.getChildCount(); i++) {
            menuView.findViewHolderForPosition(i).itemView.setSelected(false);

        }
    }
}


