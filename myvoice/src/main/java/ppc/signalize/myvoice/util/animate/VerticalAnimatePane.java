package ppc.signalize.myvoice.util.animate;

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
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;

import ppc.signalize.myvoice.MyVoiceActivity;
import ppc.signalize.myvoice.R;
import ppc.signalize.myvoice.model.careteam.CareTeamManager;
import ppc.signalize.myvoice.util.adapter.CareTeamAdapter;
import ppc.signalize.myvoice.util.fling.FlingDownDetector;


public class VerticalAnimatePane implements View.OnClickListener{
    public static View vi = null;
    protected static FrameLayout parent;
    private static MyVoiceActivity my_voice;

    private GestureDetector gestureDetector;

    View.OnTouchListener gestureListener;
    private static boolean queued;
    private static View queue;


    public VerticalAnimatePane(MyVoiceActivity mv){
        my_voice = mv;
        // Gesture detection
        gestureDetector = new GestureDetector(my_voice, new FlingDownDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
    }

    public void animateContentPane(String title_text, int icon_id, FrameLayout parent){


        LayoutInflater inflater = (LayoutInflater)my_voice.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(vi == null){
            vi = inflater.inflate(R.layout.slideup_content,null);
        }

        View inner;
        FrameLayout contented;
        inner = inflater.inflate(R.layout.my_personal_visit_note,null);
        inner.setOnTouchListener(gestureListener);
        TextView title = (TextView)vi.findViewById(R.id.slide_up_title);
        title.setText(title_text);
        SVGImageView icon = (SVGImageView)vi.findViewById(R.id.slide_up_icon);
        icon.setImageResource(icon_id);
        contented = (FrameLayout)vi.findViewById(R.id.content_area);
        contented.removeAllViews();
        contented.addView(inner);



        VerticalAnimatePane.parent = parent;
        //vi.setOnClickListener(this);


        parent.addView(vi);
        revealSlidingContent();
    }

    public static void revealSlidingContent() {
        Animation hide = AnimationUtils.loadAnimation(my_voice, R.anim.subcontent_reveal);
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

    public static void hideSlidingContent() {

        Animation hide = AnimationUtils.loadAnimation(my_voice, R.anim.subcontent_hide);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
               
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

