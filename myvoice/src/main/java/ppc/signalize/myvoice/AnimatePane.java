package ppc.signalize.myvoice;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Aron on 7/27/2014.
 */
public class AnimatePane implements View.OnClickListener{
    private static boolean once = false;
    protected static View vi = null;
    protected static Context context;
    protected static FrameLayout parent;


    private GestureDetector gestureDetector;

    View.OnTouchListener gestureListener;

    public AnimatePane(Context context){
        this.context = context;
        // Gesture detection
        gestureDetector = new GestureDetector(context, new FlingAwayDetector(this));
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
    }

    protected void animateContentPane(int position, FrameLayout parent){

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(vi == null){
            vi = inflater.inflate(R.layout.sliding_content,null);
        }

        TextView contentHeading = (TextView)vi.findViewById(R.id.content_heading);
        ListAdapter.setTextView(null,contentHeading,context,position);
        AnimatePane.parent = parent;
        vi.setOnClickListener(this);
        vi.setOnTouchListener(gestureListener);
        parent.addView(vi);
        once = true;

        revealSlidingContent();
    }

    protected static void revealSlidingContent() {
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

    protected static void hideSlidingContent() {

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
                parent.removeView(vi);
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
        if(vi.getVisibility() != View.GONE){
            hideSlidingContent();

        }
        else{
            revealSlidingContent();
        }
    }
}

