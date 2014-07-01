package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mukundan on 6/26/14.
 */
public class FileUtils {
    private static String TAG = "File Utils";
    private static Context context = null;
    static String AIMLdir = "MIRA/aiml";
    static String MIRAdir = "MIRA";
    public static void copyAssetsToStorage(){
        copyNonEmptyDirectories();
    }
    private static void copyNonEmptyDirectories() {
        AssetManager assetManager = context.getAssets();
        String []files;
        try {
            files = assetManager.list(MIRAdir);
            for(String file:files){
                String []contents = assetManager.list(MIRAdir + "/" + file);
                if(contents == null || contents.length == 0){
                    Toast.makeText(context,file + " is an empty directory or does not exists",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, file + " is an empty directory or does not exists");
                }
                else{
                    File dir = new File(context.getFilesDir(),MIRAdir + "/" + file);

                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    for (String filename : contents) {
                        InputStream inputStream = assetManager.open(MIRAdir + "/" + file + "/" + filename);
                        //Toast.makeText(this, filename, Toast.LENGTH_SHORT).show();
                        OutputStream outputStream = new FileOutputStream(new File(dir, filename));
                        CopyStream(inputStream, outputStream);
                        outputStream.flush();
                        inputStream.close();
                        outputStream.close();
                    }
                    Toast.makeText(context, "Copied directory " + file + " to "+ dir.getCanonicalPath(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Copied directory " + file + " to "+ dir.getCanonicalPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void CopyStream(InputStream Input, OutputStream Output) throws IOException {
        byte[] buffer = new byte[5120];
        int length = Input.read(buffer);
        while (length > 0) {
            Output.write(buffer, 0, length);
            length = Input.read(buffer);
        }
    }
    public static boolean MIRAdirExists(){
        File f = new File(context.getFilesDir(),MIRAdir + "/" + AIMLdir);
        return f.exists();
    }
    public static void setContext(Context context){
        FileUtils.context = context;
    }
}
