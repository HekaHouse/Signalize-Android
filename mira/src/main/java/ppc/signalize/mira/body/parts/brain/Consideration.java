package ppc.signalize.mira.body.parts.brain;

import android.util.Log;

import com.aliasi.classify.JointClassification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ppc.signalize.mira.MyVoice;

/**
 * Created by Aron on 3/17/14.
 */
public class Consideration {
    private static final String TAG = "Consideration";
    public final MyVoice mVoice;
    public JointClassification mSentiment;
    public JointClassification mSeverity;
    public String mResponse;
    public String mOob;
    public String mExtra;
    public String mOrig;
    public String mRaw;
    public Pattern oobTag = Pattern.compile(".*?(<oob>.*</oob>).*", Pattern.DOTALL);
    public Pattern extraTag = Pattern.compile(".*?(<.*>).*", Pattern.DOTALL);

    public Consideration(JointClassification sent, JointClassification sever, MyVoice mv, String raw_resp, String orig) {
        mSentiment = sent;
        mSeverity = sever;
        mRaw = raw_resp;
        mOrig = orig;
        mVoice = mv;
        Matcher match_oob = oobTag.matcher(mRaw);

        if (match_oob.matches()) {
            mOob = match_oob.group(1);
            mResponse = mRaw.replace(mOob, "");
        } else
            mResponse = mRaw;

        Matcher match_extra = extraTag.matcher(mResponse);
        if (match_extra.matches()) {
            mExtra = match_extra.group(1);
            mResponse = mResponse.replace(mExtra, "");
        }

        //Log.i(TAG, mSentiment.bestCategory() + ":" + String.valueOf(mSentiment.conditionalProbability(mSentiment.bestCategory())));
        //Log.i(TAG, mSeverity.bestCategory() + ":" + String.valueOf(mSeverity.conditionalProbability(mSeverity.bestCategory())));
        Log.i(TAG, "to consider:" + mOrig);
        Log.i(TAG, "response:" + mResponse);

    }
}
