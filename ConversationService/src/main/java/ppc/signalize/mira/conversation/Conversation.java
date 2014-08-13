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
 * @author mukundan
 * Created by mukundan on 5/28/14.
 *
 * A class which implements all the methods specified in the IConversation interface.
 * All the function calls are synchronous.
 * Function calls from the same process blocks the calling thread.
 * Function calss from different process blocks the thread from the tread pool.
 */
public final class Conversation {
    private String TAG = "ConversationClass";
    public static Ghost ghost=null;
    private AndroidChat session=null;
    private Nodemapper nodemapper;
    private static boolean resync = false;
    AndroidAIML androidAIML;
    private String inputs, filenames;


    /**
     * Private constructor to initialize the object.
     * Initializes te Ghost object with context and also the AndroidChat object
     * @param context : The calling context
     */
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

    /**
     * A function which returns the chatbot's response to a given input
     * @param input : Input string pattern
     * @return Response string from the chatbot
     */

    public String process(String input) {
        String response = session.multisentenceRespond(input);
        Log.d(TAG,"GOT RESPONSE "+ response);
        androidAIML.initialize();
        getNodemapper(input);
        return response;
    }

    /**
     * A function that returns the inputThatTopic values
     * @return String representation of the the input, that and topic values
     */
    public String inputThatTopic(){
        if(nodemapper != null){
            return nodemapper.category.inputThatTopic();
        }
        return null;
    }

    /**
     * A method to write AIMLIF files
     */

    public void writeAIMLOut(){
        Log.d(TAG,"Writing AIMLIF Files");
        ghost.writeAIMLIFFiles();

    }

    /**
     * A function that returns the current input patterns
     * @return A delimited string representation of the recursive input patterns
     */
    public String getPatterns(){
        if(nodemapper!=null) {
            return inputs;
        }
        return null;
    }

    /**
     * A functions which returns the list of all patterns in the chatbot.
     * @return List of all pattern strings
     */

    public List<String> getListofPatterns(){
        ArrayList<String> patterns = new ArrayList<String>();
        ArrayList<Category> categories = ghost.brain.getCategories();
        for(Category c: categories){
            if(!patterns.contains(c.getPattern()))
                patterns.add(c.getPattern());
        }
        return patterns;
    }

    /**
     * A function which returns the current filename
     * @return A delimited string representation of the current filename
     */

    public String getFilename(){
        if(nodemapper != null){
            return nodemapper.category.getFilename();
        }
        return null;

    }
    /**
     * A function which returns the filenames
     * @return A delimited string representation of the filenames recursively
     */
    public String getFilenames(){
        return filenames;
    }


    /**
     * A function that returns the current template
     * @return Return the current template value of the node
     */
    public String getTemplate(){
        if(nodemapper != null){
            return "<template>" + nodemapper.category.getTemplate() + "</template>";
        }
        return null;
    }

    /**
     * A private function to get the nodemapper object of the matching node
     * A function that also sets the values of the filenames and inputs field of this class
     * @param input Input pattern
     */


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

    /**
     * An initialize method to initialize the class object
     * @param context The calling context
     * @return Object of the class
     */
    public static Conversation initialize(Context context){
        return new Conversation(context);
    }


    /**
     * A function that recreates this class's object
     * @param context The calling context
     * @return Object of the class
     */
    public static Conversation sync(Context context){
        resync = true;
        Conversation synced = new Conversation(context);
        resync = false;
        Log.d("ConversationClass","Resynced");
        return synced;
    }

    /**
     * A function which returns the storage type
     * @return Returns the enum value of the Selected Storage Type
     */

    public String getStorageType(){
        return FileUtils.getStorageType().name();
    }


}
