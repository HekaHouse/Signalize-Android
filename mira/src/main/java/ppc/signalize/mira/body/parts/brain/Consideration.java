package ppc.signalize.mira.body.parts.brain;

import android.util.Log;

import com.aliasi.classify.JointClassification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aron on 3/17/14.
 */
public class Consideration {
    private static final String TAG = "Consideration";
    public JointClassification mSentiment;
    public JointClassification mSeverity;
    public String mResponse;
    public String mOob;
    public String mOrig;
    public String mRaw;
    public Pattern taggy = Pattern.compile("^[<>]*(<.*>)[<>]*$");
    public Consideration(JointClassification sent, JointClassification sever, String raw_resp, String orig) {
        mSentiment = sent;
        mSeverity = sever;
        mRaw = raw_resp;
        mOrig = orig;
        Matcher m = taggy.matcher(mRaw);
        if (m.matches()) {
            mOob = m.group(1);
            mResponse = mRaw.replace(mOob,"");
        } else
            mResponse = mRaw;

        Log.i(TAG,mSentiment.bestCategory()+":"+String.valueOf(mSentiment.conditionalProbability(mSentiment.bestCategory())));
        Log.i(TAG,mSeverity.bestCategory()+":"+String.valueOf(mSeverity.conditionalProbability(mSeverity.bestCategory())));
        Log.i(TAG,"to consider:"+mOrig);
        Log.i(TAG,"response:"+mResponse);

    }
}
