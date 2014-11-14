package ppc.signalize.mira.brain;

import android.util.Log;

import com.aliasi.classify.JointClassification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ppc.signalize.mira.Voice;

/**
 * Created by Aron on 3/17/14.
 */
public class Consideration {
    private static final String TAG = "Consideration";
    public final Voice mVoice;
    public JointClassification mSentiment;
    public JointClassification mSeverity;
    public String mResponse;
    public String mOob;
    public String mExtra;
    public String mOrig;
    public String mRaw;
    public String mTopic;
    public Pattern oobTag = Pattern.compile(".*?(<oob>.*</oob>).*", Pattern.DOTALL);
    public Pattern topicTag = Pattern.compile(".*?(\\^.*\\^).*", Pattern.DOTALL);
    public Pattern extraTag = Pattern.compile(".*?(<.*>).*", Pattern.DOTALL);

    public Consideration(JointClassification sent, JointClassification sever, Voice mv, String raw_resp, String orig) {
        mSentiment = sent;
        mSeverity = sever;
        mRaw = raw_resp;
        mOrig = orig;
        mVoice = mv;
        Matcher match_oob = oobTag.matcher(mRaw);
        Matcher match_topic = topicTag.matcher(mRaw);
        if (match_oob.matches()) {
            mOob = match_oob.group(1);
            mResponse = mRaw.replace(mOob, "");
        } else if (match_topic.matches()) {
            mTopic = match_topic.group(1);
            mResponse = mRaw.replace(mTopic, "");
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
