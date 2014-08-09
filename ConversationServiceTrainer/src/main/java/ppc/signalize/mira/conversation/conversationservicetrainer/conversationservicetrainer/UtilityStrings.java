package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import java.util.HashMap;

/**
 * Created by mukundan on 7/22/14.
 */
public class UtilityStrings {
    protected static final String cannot_modify_text = "Internal/Assets Storage selected!! Cannot create/modify files!! READ ONLY";
    protected static final String not_safe_text = "Not safe to delete reduction.Other patterns may depend upon this reduction.";
    protected static final String deleteReducitonTitle = "Delete Reduction?";
    protected static final String XML_PROCESSING_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    protected static final int DELETE_REDUCTION_RESULT_CODE = 1;
    protected static final String positionIntent = "positioninList";
    protected static final String newFileIntent = "newFile";
    protected static final String fileIntent = "fileName";
    protected static final String patternIntent = "pattern";
    protected static final String inputPatternIntent = "inputPattern";
    protected static final String currentResponseIntent = "currentResponse";
    protected static final String ConversationServiceTAG= "ppc.signalize.mira.conversation.ConversationService";
    protected static final String StartServiceBroadcast = "ppc.signalize.mira.conversation.startService";
    protected static final String ConversationServicePackage = "ppc.signalize.mira.conversation";
    protected static final HashMap<String,String> buttonIdMap = new HashMap<String,String>();
    protected static final HashMap<String,String> buttonToolTipMap = new HashMap<String,String>();
    protected static enum TAGTOADD{AIML,THAT_PREVIOUS,
        INPUT,THATSTAR,TOPICSTAR,BOT,
        UPPERCASE,LOWERCASE,FORMAL,SENTENCE,
        LI,GOSSIP,OOB,
        TEMPLATE,RANDOM,SRAI,CATEGORY,PATTERN,
        TOPIC,STAR,GET,SET,CONDITION,GENDER,
    DATE,ID,SIZE,VERSION,SR,THAT,PERSON,PERSON2,
        THINK,LEARN};
    static {

        /**
         * Loaded the tooltip and tag from the web page
         * http://www.alicebot.org/documentation/aiml101.html
         *
         * Reference manual used
         * http://www.alicebot.org/documentation/aiml-reference.html
         *
         * */
        buttonIdMap.put("aiml","file");
        buttonIdMap.put("topic","category root");
        buttonIdMap.put("category","category");
        buttonIdMap.put("pattern","category child");
        buttonIdMap.put("that","category child");
        buttonIdMap.put("template","category child");
        buttonIdMap.put("star","template possible");
        buttonIdMap.put("that_previous","template possible");
        buttonIdMap.put("input","unknown");
        buttonIdMap.put("thatstar","template possible");
        buttonIdMap.put("topicstar","template possible");
        buttonIdMap.put("get","template child pattern possible");
        buttonIdMap.put("bot","template or pattern possible");
        buttonIdMap.put("sr","template possible");
        buttonIdMap.put("person2","template possible");
        buttonIdMap.put("person","template possible");
        buttonIdMap.put("gender","template child");
        buttonIdMap.put("date","builtin");
        buttonIdMap.put("id","builtin");
        buttonIdMap.put("size","builtin");
        buttonIdMap.put("version","builtin");
        buttonIdMap.put("uppercase","template child");
        buttonIdMap.put("lowercase","template child");
        buttonIdMap.put("formal","template child");
        buttonIdMap.put("sentence","template child");
        buttonIdMap.put("condition","template possible");
        buttonIdMap.put("random","add_random_tag");
        buttonIdMap.put("li","add_li_tag");
        buttonIdMap.put("set","add_set_tag");
        buttonIdMap.put("srai","add_srai_tag"); // This alone is a Spinner as well as button
        buttonIdMap.put("think","add_think_tag");
        buttonIdMap.put("learn","add_learn_tag");
        buttonIdMap.put("oob","add_oob_tag");

        /////////////////////////////////////////////////////////

        buttonToolTipMap.put("aiml","AIML block delimeter");
        buttonToolTipMap.put("topic","name = 'X' Is AIML pattern");
        buttonToolTipMap.put("category","AIML knowledge unit");
        buttonToolTipMap.put("pattern","AIML input pattern");
        buttonToolTipMap.put("that","contains AIML pattern");
        buttonToolTipMap.put("template","AIML response template");
        buttonToolTipMap.put("star","binding of *");
        buttonToolTipMap.put("that_previous","previous bot utterance");
        buttonToolTipMap.put("input","input sentence");
        buttonToolTipMap.put("thatstar","binding of * in that");
        buttonToolTipMap.put("topicstar","binding of * in topic");
        buttonToolTipMap.put("get","<get name = 'yyy'/>Botmaster defined yyy, default");
        buttonToolTipMap.put("bot","Custom bot parameter");
        buttonToolTipMap.put("sr","Short-cut element <srai><star/></srai>");
        buttonToolTipMap.put("person2","<person2><star /></person2>");
        buttonToolTipMap.put("person","<person><star /></person>");
        buttonToolTipMap.put("gender","Shortcut element <gender><star/></gender>");
        buttonToolTipMap.put("date","date and time");
        buttonToolTipMap.put("id","client identifer");
        buttonToolTipMap.put("size","# of categories loaded");
        buttonToolTipMap.put("version","AIML program version");
        buttonToolTipMap.put("uppercase","convert all text to Uppercase");
        buttonToolTipMap.put("lowercase","convert all text to Lowercase");
        buttonToolTipMap.put("formal","capitalize every word");
        buttonToolTipMap.put("sentence","capitalize the first word");
        buttonToolTipMap.put("condition","Predicate Branch");
        buttonToolTipMap.put("random","Random uniform selection");
        buttonToolTipMap.put("li","used by <random>,<condition>");
        buttonToolTipMap.put("set","<set name = 'yyy'> May return yyy or value");
        buttonToolTipMap.put("gossip","Append to file");
        buttonToolTipMap.put("srai","Recursion"); // This alone is a Spinner
        buttonToolTipMap.put("think","Hides side-effects");
        buttonToolTipMap.put("learn","AIML loading");
        buttonToolTipMap.put("oob","Out of Band Services");
    }
}
