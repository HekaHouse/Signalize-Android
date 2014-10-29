package ppc.signalize.myvoice.util.fling;

import android.view.GestureDetector;
import android.view.MotionEvent;

import ppc.signalize.myvoice.util.animate.AnimatePane;
import ppc.signalize.myvoice.util.animate.VerticalAnimatePane;

/**
 * Created by Aron on 7/27/2014.
 */
public class FlingDownDetector extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    public FlingDownDetector() {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // nothing
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                VerticalAnimatePane.hideSlidingContent();
                return true;
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}

