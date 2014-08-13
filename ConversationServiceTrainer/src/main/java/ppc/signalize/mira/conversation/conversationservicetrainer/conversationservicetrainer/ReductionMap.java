package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by mukundan on 8/6/14.
 * A simple reduction map class that checks if the required reduction is in the top of the list or not
 * Needs more work on perfect safe deletion
 * Need to get the mappings of the patterns based on srai from the service and based on that map check if
 * the reduction is save to delete
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
