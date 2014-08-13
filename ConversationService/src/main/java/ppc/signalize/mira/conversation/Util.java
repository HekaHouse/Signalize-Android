package ppc.signalize.mira.conversation;

import android.content.Context;
import android.content.SharedPreferences;

import org.alicebot.ab.FileUtils;
import org.alicebot.ab.Ghost;

/**
 * Created by mukundan on 5/28/14.
 * A utility class providing static methods.
 */
public class Util {

    protected static String _name = "MIRA";
    protected static String _AIML_path = "bots/";
    private static FileUtils.STORAGE_TYPE storageType = FileUtils.STORAGE_TYPE.EXTERNAL_STORAGE;
    protected static void setStorageType(Context context){
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.share_preference_file),Context.MODE_PRIVATE);
        String name = prefs.getString(context.getString(R.string.storage_type_pref),"EXTERNAL_STORAGE");
        Util.setStorageType(FileUtils.STORAGE_TYPE.valueOf(name));

    }
    protected static void setStorageType(FileUtils.STORAGE_TYPE storageType){
        Util.storageType = storageType;
    }
    protected  static FileUtils.STORAGE_TYPE getStorageType(){
        return Util.storageType;
    }
}
