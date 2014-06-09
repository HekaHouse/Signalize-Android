package ppc.signalize.mira.conversation;

import android.content.Context;

import org.alicebot.ab.Chat;
import org.alicebot.ab.Ghost;

/**
 * Created by mukundan on 5/28/14.
 */
public final class Conversation {
    public static Ghost ghost=null;
    private Chat session=null;


    private Conversation(Context context) {
        Ghost.setContext(context);
        if(ghost==null) ghost = new Ghost(Util._name, Util._AIML_path);
        session = new Chat(ghost);
    }

    public String process(String input) {
        return session.multisentenceRespond(input);
    }

    public static Conversation initialize(Context context){
        return new Conversation(context);
    }



}
