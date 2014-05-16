package ppc.signalize.mira.nervous.concurrent;

import android.util.Log;

import java.io.IOException;

import ppc.signalize.mira.Voice;
import ppc.signalize.mira.brain.Intuition;

/**
 * Created by Aron on 3/17/14.
 */
public class ModelingRunner implements Runnable {
    private final Voice mWorld;
    private final String type;
    private final boolean mEval;
    private String TAG = "IntuitionModeling";

    public ModelingRunner(String t, Voice mv, boolean eval) {
        type = t;
        mWorld = mv;
        mEval = eval;
    }
    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        Log.d(TAG, "loading "+type );
        try {
            if (type.equals(Intuition.SENTIMENT)) {
                Intuition.train(mWorld, Intuition.SENTIMENT);
                Intuition.setSentimentLoaded(true);
            } else {
                Intuition.train(mWorld, Intuition.SEVERITY);
                Intuition.setSeverityLoaded(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mEval) {
            Log.d(TAG, "run eval ");
            try {
                if (type.equals(Intuition.SENTIMENT)) {
                    Intuition.evaluate(mWorld, Intuition.SENTIMENT);
                } else {
                    Intuition.evaluate(mWorld, Intuition.SEVERITY);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, type+" loaded");
    }
}
