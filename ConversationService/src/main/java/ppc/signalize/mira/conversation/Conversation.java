package ppc.signalize.mira.conversation;

import android.content.Context;
import android.util.Log;

import net.reduls.sanmoku.dic.Char;

import org.alicebot.ab.AndroidAIML;
import org.alicebot.ab.AndroidChat;
import org.alicebot.ab.Category;
import org.alicebot.ab.FileUtils;
import org.alicebot.ab.Ghost;
import org.alicebot.ab.Nodemapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukundan on 5/28/14.
 */
public final class Conversation {
    private String TAG = "ConversationClass";
    public static Ghost ghost=null;
    private AndroidChat session=null;
    private Nodemapper nodemapper;
    private static boolean resync = false;
    AndroidAIML androidAIML;
    private String inputs, filenames;



    private Conversation(Context context) {
        Util.setStorageType(context);
        FileUtils.setContext(context);
        Ghost.setContext(context, Util.getStorageType());
        Log.d(TAG,"STORAGE TYPE IN FILEUTILS FILE " + FileUtils.getStorageType().name());
        Log.d(TAG,"STORAGE TYPE FROM UTIL FILE" + Util.getStorageType().name());
        //FileUtils.setContext(context);
        if(resync || ghost==null) ghost = new Ghost(Util._name, Util._AIML_path);
        session = new AndroidChat(ghost);
        androidAIML = new AndroidAIML();
    }

    public String process(String input) {
        String response = session.multisentenceRespond(input);
        Log.d(TAG,"GOT RESPONSE "+ response);
        androidAIML.initialize();
        getNodemapper(input);
        return response;
    }
    public String inputThatTopic(){
        if(nodemapper != null){
            return nodemapper.category.inputThatTopic();
        }
        return null;
    }

    public void writeAIMLOut(){
        Log.d(TAG,"Writing AIMLIF Files");
        ghost.writeAIMLIFFiles();

    }
    public String getPatterns(){
        if(nodemapper!=null) {
            return inputs;
        }
        return null;
    }

    public List<String> getListofPatterns(){
        ArrayList<String> patterns = new ArrayList<String>();
        ArrayList<Category> categories = ghost.brain.getCategories();
        for(Category c: categories){
            if(!patterns.contains(c.getPattern()))
                patterns.add(c.getPattern());
        }
        return patterns;
    }

    public String getFilename(){
        if(nodemapper != null){
            return nodemapper.category.getFilename();
        }
        return null;

    }

    public String getFilenames(){
        return filenames;
    }

    public String getTemplate(){
        if(nodemapper != null){
            return "<template>" + nodemapper.category.getTemplate() + "</template>";
        }
        return null;
    }


    private void getNodemapper(String input){
        nodemapper = session.getNodemapper();

        if(nodemapper != null) {
            Log.d(TAG,"GOT NODEMAPPER");
            androidAIML.respond(input, nodemapper.category.getThat(), nodemapper.category.getTopic(), session);
            Log.d(TAG,"GOT AIML RESPOND");
            filenames = androidAIML.strFileNames();
            inputs = androidAIML.strInputs();
            Log.w(TAG,filenames);
            Log.w(TAG,inputs);
            Log.w(TAG, "input that topic = " + nodemapper.category.inputThatTopic());
            Log.w(TAG, "Corresponding AIML File = " + nodemapper.category.getFilename());
            Log.d(TAG,"Filename " + nodemapper.category.getFilename());
            Log.d(TAG,"That " + nodemapper.category.getThat());
            Log.d(TAG,"Template " + nodemapper.category.getTemplate());
            Log.d(TAG,"Topic " + nodemapper.category.getTopic());
            Log.d(TAG,"Activation Count " + nodemapper.category.getActivationCnt());
            Log.d(TAG,"Pattern " + nodemapper.category.getPattern());
            Log.d(TAG,"Category Number " + nodemapper.category.getCategoryNumber());
        }
    }

    public static Conversation initialize(Context context){
        return new Conversation(context);
    }

    public static Conversation sync(Context context){
        resync = true;
        Conversation synced = new Conversation(context);
        resync = false;
        Log.d("ConversationClass","Resynced");
        return synced;
    }

    public String getStorageType(){
        return FileUtils.getStorageType().name();
    }


}
