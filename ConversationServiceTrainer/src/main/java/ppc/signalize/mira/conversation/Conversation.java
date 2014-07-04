package ppc.signalize.mira.conversation;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.alicebot.ab.AndroidAIML;
import org.alicebot.ab.AndroidChat;
import org.alicebot.ab.Ghost;
import org.alicebot.ab.Nodemapper;

/**
 * Created by mukundan on 5/28/14.
 */
public final class Conversation {
    private String TAG = "ConversationClass";
    public static Ghost ghost=null;
    private AndroidChat session=null;
    private Nodemapper nodemapper;
    private String input;
    AndroidAIML androidAIML;
    private String inputs, filenames;

    private Conversation(Context context) {
        Ghost.setContext(context);
        if(ghost==null) ghost = new Ghost(Util._name, Util._AIML_path);
        session = new AndroidChat(ghost);
        androidAIML = new AndroidAIML();
    }

    public String process(String input) {
        String response = session.multisentenceRespond(input);
        this.input = input;
        androidAIML.initialize();
        getNodemapper(input);
        return response;
    }
    public String getPatterns(){
        if(nodemapper!=null) {
            return inputs;
        }
        return null;
    }

    public String getFilename(){
        if(nodemapper!=null) {
            return filenames;
        }
        return null;

    }

    public String getTemplate(){
        if(nodemapper != null){
            return "<template>" + nodemapper.category.getTemplate() + "</template>";
        }
        return null;
    }


    private void getNodemapper(String input){
        nodemapper = session.getNodemapper(input);
        if(nodemapper != null) {
            androidAIML.respond(input, nodemapper.category.getThat(), nodemapper.category.getTopic(), session);
            filenames = androidAIML.strFileNames();
            inputs = androidAIML.strInputs();
            Log.w(TAG, "input that topic = " + nodemapper.category.inputThatTopic());
            Log.w(TAG, "Corresponding AIML File = " + nodemapper.category.getFilename());
        }
    }

    public static Conversation initialize(Context context){
        return new Conversation(context);
    }



}
