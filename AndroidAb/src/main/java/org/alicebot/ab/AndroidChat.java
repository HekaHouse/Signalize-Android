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

    @Override
    public String multisentenceRespond(String request) {
        //MagicBooleans.trace("chat.multisentenceRespond(request: " + request + ")");
        String response="";
        matchTrace="";
        try {
            String normalized = bot.preProcessor.normalize(request);
            //normalized = JapaneseUtils.tokenizeSentence(normalized);
            //MagicBooleans.trace("in chat.multisentenceRespond(), normalized: " + normalized);
            String sentences[] = bot.preProcessor.sentenceSplit(normalized);
            History<String> contextThatHistory = new History<String>("contextThat");
            for (int i = 0; i < sentences.length; i++) {
                //System.out.println("Human: "+sentences[i]);
                AIMLProcessor.trace_count = 0;
                String reply = respond(sentences[i], contextThatHistory);
                response += "  "+reply;
                //System.out.println("Robot: "+reply);
            }
            requestHistory.add(request);
            responseHistory.add(response);
            thatHistory.add(contextThatHistory);
            response = response.replaceAll("[\n]+", "\n");
            response = response.trim();
        } catch (Exception ex) {
            ex.printStackTrace();
            return MagicStrings.error_bot_response;
        }

        //if (doWrites) {
            //bot.writeLearnfIFCategories();
        //}
        //MagicBooleans.trace("in chat.multisentenceRespond(), returning: " + response);
        return response;
    }
    @Override
    String respond(String input, String that, String topic, History contextThatHistory) {
        //MagicBooleans.trace("chat.respond(input: " + input + ", that: " + that + ", topic: " + topic + ", contextThatHistory: " + contextThatHistory + ")");
        String response;
        inputHistory.add(input);
        response = AIMLProcessor.respond(input, that, topic, this);

        if (response.startsWith(topic))
            response = response.replaceFirst(topic,"");
        //MagicBooleans.trace("in chat.respond(), response: " + response);
        String normResponse = bot.preProcessor.normalize(response);
        //MagicBooleans.trace("in chat.respond(), normResponse: " + normResponse);
        //if (MagicBooleans.jp_tokenize) normResponse = JapaneseUtils.tokenizeSentence(normResponse);
        String sentences[] = bot.preProcessor.sentenceSplit(normResponse);
        for (int i = 0; i < sentences.length; i++) {
            that = sentences[i];
            //System.out.println("That "+i+" '"+that+"'");
            if (that.trim().equals("")) that = MagicStrings.default_that;
            contextThatHistory.add(that);
        }
        String result = response.trim()+"  ";
        //MagicBooleans.trace("in chat.respond(), returning: " + result);
        return result;
    }
}
