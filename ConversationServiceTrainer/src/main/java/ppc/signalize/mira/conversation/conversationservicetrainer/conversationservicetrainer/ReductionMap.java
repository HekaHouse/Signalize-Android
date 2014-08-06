package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by mukundan on 8/6/14.
 */
public class ReductionMap {

    private final String TAG = "Reduction Map";

    private ArrayList<String> inputs;

    public ReductionMap(ArrayList<String> inputs){
        this.inputs = inputs;
    }

    public boolean containsPattern(String pattern){
        return inputs.contains(pattern);
    }

    public boolean isTopPattern(String pattern){
        if(containsPattern(pattern)){
            Log.d(TAG, "Index of pattern " + inputs.indexOf(pattern));
            if(inputs.indexOf(pattern) != 0 && inputs.size() != 1){
                return false;
            }
        }
        return true;
    }

}
