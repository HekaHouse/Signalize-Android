package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import java.util.HashMap;

/**
 * Created by mukundan on 7/22/14.
 */
public class UtilityStrings {
    protected static final String fileIntent = "fileName";
    protected static final String patternIntent = "pattern";
    protected static final String currentResponseIntent = "currentResponse";
    protected static final String ConversationServiceTAG= "ppc.signalize.mira.conversation.ConversationService";
    protected static final String StartServiceBroadcast = "ppc.signalize.mira.conversation.startService";
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
         * */
        buttonIdMap.put("aiml","add_aiml_tag");
        buttonIdMap.put("topic","add_topic_tag");
        buttonIdMap.put("category","add_category_tag");
        buttonIdMap.put("pattern","add_pattern_tag");
        buttonIdMap.put("that","add_that_tag");
        buttonIdMap.put("template","add_template_tag");
        buttonIdMap.put("star","add_star_tag");
        buttonIdMap.put("that_previous","add_that_previous_tag");
        buttonIdMap.put("input","add_input_tag");
        buttonIdMap.put("thatstar","add_thatstar_tag");
        buttonIdMap.put("topicstar","add_topicstar_tag");
        buttonIdMap.put("get","add_get_tag");
        buttonIdMap.put("bot","add_bot_tag");
        buttonIdMap.put("sr","add_sr_tag");
        buttonIdMap.put("person2","add_person2_tag");
        buttonIdMap.put("person","add_person_tag");
        buttonIdMap.put("gender","add_gender_tag");
        buttonIdMap.put("date","add_date_tag");
        buttonIdMap.put("id","add_id_tag");
        buttonIdMap.put("size","add_size_tag");
        buttonIdMap.put("version","add_version_tag");
        buttonIdMap.put("uppercase","add_uppercase_tag");
        buttonIdMap.put("lowercase","add_lowercase_tag");
        buttonIdMap.put("formal","add_formal_tag");
        buttonIdMap.put("sentence","add_sentence_tag");
        buttonIdMap.put("condition","add_condition_tag");
        buttonIdMap.put("random","add_random_tag");
        buttonIdMap.put("li","add_li_tag");
        buttonIdMap.put("set","add_set_tag");
        buttonIdMap.put("gossip","add_gossip_tag");
        buttonIdMap.put("srai","add_srai_tag"); // This alone is a Spinner
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
