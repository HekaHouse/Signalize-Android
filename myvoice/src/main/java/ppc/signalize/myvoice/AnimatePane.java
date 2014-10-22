package ppc.signalize.myvoice;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.StackView;

import java.util.ArrayList;

import ppc.signalize.myvoice.model.careteam.CareTeamManager;
import ppc.signalize.myvoice.model.careteam.CareTeamMember;
import ppc.signalize.myvoice.util.adapter.CareTeamAdapter;

/**
 * Created by Aron on 7/27/2014.
 */
public class AnimatePane implements View.OnClickListener{
    private static boolean once = false;
    protected static View vi = null;
    protected static FrameLayout parent;
    private static MyVoiceActivity my_voice;

    private GestureDetector gestureDetector;

    View.OnTouchListener gestureListener;
    private static boolean queued;
    private static View queue;


    public AnimatePane(MyVoiceActivity mv){
        my_voice = mv;
        // Gesture detection
        gestureDetector = new GestureDetector(my_voice, new FlingAwayDetector(this));
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
    }
    protected void animateContentPaneQueued(int position, FrameLayout parent) {
        LayoutInflater inflater = (LayoutInflater)my_voice.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(queue == null){
            queue = inflater.inflate(R.layout.sliding_content,null);
        }

        View inner = null;
        FrameLayout contented = null;
        switch (position){
            case 0:
                CareTeamAdapter mAdapter = new CareTeamAdapter(new CareTeamManager().getItems(), R.layout.my_care_card, my_voice);
                inner = inflater.inflate(R.layout.my_care_team,null);
                RecyclerView rv = (RecyclerView)inner.findViewById(R.id.care_team_cards);
                rv.setLayoutManager(new LinearLayoutManager(my_voice));
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(mAdapter);
                rv.setOnTouchListener(gestureListener);

                contented = (FrameLayout)queue.findViewById(R.id.content_area);
                contented.removeAllViews();
                contented.addView(inner);
                break;
            case 1:
                inner = inflater.inflate(R.layout.my_personal_visit_record,null);
                inner.setOnTouchListener(gestureListener);
                contented = (FrameLayout)queue.findViewById(R.id.content_area);
                contented.removeAllViews();
                contented.addView(inner);
                break;
            case 2:
                inner = inflater.inflate(R.layout.my_personal_medical_record,null);
                inner.setOnTouchListener(gestureListener);
                contented = (FrameLayout)queue.findViewById(R.id.content_area);
                contented.removeAllViews();
                contented.addView(inner);
                break;
            case 3:
                inner = inflater.inflate(R.layout.request_assistance,null);
                inner.setOnTouchListener(gestureListener);
                contented = (FrameLayout)queue.findViewById(R.id.content_area);
                contented.removeAllViews();
                contented.addView(inner);
                break;
            default:
                inner = inflater.inflate(R.layout.request_assistance,null);
                inner.setOnTouchListener(gestureListener);
                contented = (FrameLayout)queue.findViewById(R.id.content_area);
                contented.removeAllViews();
                contented.addView(inner);
                break;

        }

        AnimatePane.parent = parent;
        queued = true;
    }
    protected void animateContentPane(int position, FrameLayout parent){


        LayoutInflater inflater = (LayoutInflater)my_voice.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(vi == null){
            vi = inflater.inflate(R.layout.sliding_content,null);
        }

        View inner = null;
        FrameLayout contented = null;
        switch (position){
            case 0:
                CareTeamAdapter mAdapter = new CareTeamAdapter(new CareTeamManager().getItems(), R.layout.my_care_card, my_voice);
                inner = inflater.inflate(R.layout.my_care_team,null);
                RecyclerView rv = (RecyclerView)inner.findViewById(R.id.care_team_cards);
                rv.setLayoutManager(new LinearLayoutManager(my_voice));
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(mAdapter);
                rv.setOnTouchListener(gestureListener);

                contented = (FrameLayout)vi.findViewById(R.id.content_area);
                contented.removeAllViews();
                contented.addView(inner);
                break;
            case 1:
                inner = inflater.inflate(R.layout.my_personal_visit_record,null);
                inner.setOnTouchListener(gestureListener);
                contented = (FrameLayout)vi.findViewById(R.id.content_area);
                contented.removeAllViews();
                contented.addView(inner);
                break;
            case 2:
                inner = inflater.inflate(R.layout.my_personal_medical_record,null);
                inner.setOnTouchListener(gestureListener);
                contented = (FrameLayout)vi.findViewById(R.id.content_area);
                contented.removeAllViews();
                contented.addView(inner);
                break;
            case 3:
                inner = inflater.inflate(R.layout.request_assistance,null);
                inner.setOnTouchListener(gestureListener);
                contented = (FrameLayout)vi.findViewById(R.id.content_area);
                contented.removeAllViews();
                contented.addView(inner);
                break;
            default:
                inner = inflater.inflate(R.layout.request_assistance,null);
                inner.setOnTouchListener(gestureListener);
                contented = (FrameLayout)vi.findViewById(R.id.content_area);
                contented.removeAllViews();
                contented.addView(inner);
                break;

        }


        AnimatePane.parent = parent;
        //vi.setOnClickListener(this);


        parent.addView(vi);
        once = true;
        revealSlidingContent();
    }

    protected static void revealSlidingContent() {
        Animation hide = AnimationUtils.loadAnimation(my_voice, R.anim.content_reveal);
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

    protected static void hideSlidingContent() {

        Animation hide = AnimationUtils.loadAnimation(my_voice, R.anim.content_hide);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                my_voice.clearSelection();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Called when the Animation ended
                // Since we are fading a View out we set the visibility
                // to GONE once the Animation is finished
                vi.setVisibility(View.GONE);
                parent.removeView(vi);
                vi=null;
                if (queued) {
                    vi=queue;
                    queue=null;
                    parent.addView(vi);
                    once = true;
                    revealSlidingContent();
                    queued=false;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // This is called each time the Animation repeats
            }
        });

        vi.startAnimation(hide);
    }

    private void contentTransition() {
        if(vi.getVisibility() != View.GONE){
            hideSlidingContent();
        }
        else{
            revealSlidingContent();
        }
    }
    @Override
    public void onClick(View v) {
        contentTransition();
    }


}

