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
    public Nodemapper getNodemapper(String request){
        /*String normalized = bot.preProcessor.normalize(request);
        String sentences[] = bot.preProcessor.sentenceSplit(normalized);
        Log.w(TAG, "Normalized " + normalized);
        History<String> contextThatHistory = new History<String>("contextThat");
        for (int i = 0; i < sentences.length; i++) {
            //System.out.println("Human: "+sentences[i]);
            AIMLProcessor.trace_count = 0;
            String reply = respond(sentences[i], contextThatHistory);

            //System.out.println("Robot: "+reply);
        }
        for(String sentence:sentences){
            Log.w(TAG,"Sentences "+ sentence);
        }*/
        inputHistory.printHistory();
        String input = inputHistory.get(0);
        History hist = thatHistory.get(0);
        String that;
        if (hist == null) that = MagicStrings.default_that;
        else that = hist.getString(0);
        thatHistory.printHistory();
        String topic = predicates.get("topic");
        Log.w("ConversationClass","Input = "+input + " That = "+that+ " Topic = "+topic );
        return this.bot.brain.match(input,that,topic);
    }
}
