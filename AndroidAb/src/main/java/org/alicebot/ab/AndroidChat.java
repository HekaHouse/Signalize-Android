package org.alicebot.ab;


import android.util.Log;

/**
 * Created by mukundan on 6/17/14.
 */
public class AndroidChat extends Chat {
    private String TAG = "AndroidChat";
    public AndroidChat(Bot bot) {
        super(bot);
    }

    /**
     * Returns the Nodemapper object of the last inputs node
     * @return Nodemapper object of the last input value's matching node
     */
    public Nodemapper getNodemapper(){
        inputHistory.printHistory();
        String input = inputHistory.get(0);
        History hist = thatHistory.get(0);
        String that;
        if (hist == null) that = MagicStrings.default_that;
        else that = hist.getString(0);
        thatHistory.printHistory();
        String topic = predicates.get("topic");
        Log.w("ConversationClass","Input = "+input + " That = "+that+ " Topic = "+topic );
        Nodemapper leaf = this.bot.brain.match(input,that,topic);
        return leaf;
    }
}
