package org.alicebot.ab;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mukundan on 7/20/14.
 */
public class FileUtils {

    public static String delimiter = "~";
    private static String TAG = "FILE UTILS";
    public static enum STORAGE_TYPE{INTERNAL_STORAGE,EXTERNAL_STORAGE,ASSETS_STORAGE};
    private static STORAGE_TYPE storageType = STORAGE_TYPE.ASSETS_STORAGE;
    public static Context context = null;
    public static String AIMLdir = "MIRA/aiml";
    public static String MIRAdir = "MIRA";
    public static void setContext(Context context){
        FileUtils.context = context;
    }

    /**
     * A method to copy the files in Assets folder to the corresponding storage
     */
    public static void copyAssetsToStorage(){
        if(storageType == STORAGE_TYPE.INTERNAL_STORAGE) {
            Log.d(TAG,"Going to copy to internal storage");
            Toast.makeText(context,"Going to copy to internal storage",Toast.LENGTH_LONG).show();
            copyNonEmptyDirectoriesToInternalStorage();
        }
        else if(storageType == STORAGE_TYPE.EXTERNAL_STORAGE){
            Log.d(TAG,"Going to copy to external storage");
            Toast.makeText(context,"Going to copy to external storage",Toast.LENGTH_LONG).show();
            copyNonEmptyDirectoriesToExternalStorage();
        }
    }

    /**
     *
     * A method to copy the directories and files to the Internal Storage
     */
    private static void copyNonEmptyDirectoriesToInternalStorage() {
        AssetManager assetManager = context.getAssets();
        String []files;
        try {
            files = assetManager.list(MIRAdir);
            for(String file:files){
                String []contents = assetManager.list(MIRAdir + "/" + file);
                if(contents == null || contents.length == 0){
                    Toast.makeText(context, file + " is an empty directory or does not exists", Toast.LENGTH_SHORT).show();
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

    /**
     * A function that checks whether the current brain store exists. Can be used to check if files need to
     * be copied into the directory or not.
     * @return true if directories and files exists false otherwise
     */

    public static boolean storeExists(){
        if(FileUtils.getStorageType() == STORAGE_TYPE.INTERNAL_STORAGE){
            File dir = new File(context.getFilesDir(),MIRAdir);
            return dir.exists();
        }
        if(FileUtils.getStorageType() == STORAGE_TYPE.EXTERNAL_STORAGE){
            File dir = new File(Environment.getExternalStorageDirectory(),MIRAdir);
            return dir.exists();
        }
        return false;
    }

    /**
     * A method to copy all non empty directories to External Storage.
     */

    private static void copyNonEmptyDirectoriesToExternalStorage() {
        AssetManager assetManager = context.getAssets();
        String []files;
        try {
            files = assetManager.list(MIRAdir);
            for(String file:files){
                String []contents = assetManager.list(MIRAdir + "/" + file);
                if(contents == null || contents.length == 0){
                    Toast.makeText(context, file + " is an empty directory or does not exists", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, file + " is an empty directory or does not exists");
                }
                else{
                    File dir = new File(Environment.getExternalStorageDirectory(),MIRAdir + "/" + file);

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

    /**
     * http://stackoverflow.com/a/21270373 modified a bit to suit this code
     * @param Input Input File Stream that needs to be copied
     * @param Output Output File Stream that needs to be copied into
     * @throws IOException
     */
    private static void CopyStream(InputStream Input, OutputStream Output) throws IOException {
        byte[] buffer = new byte[5120];
        int length = Input.read(buffer);
        while (length > 0) {
            Output.write(buffer, 0, length);
            length = Input.read(buffer);
        }
    }

    /**
     * A function which returns the current writable storage directory
     * @return Returns the storage directory either in the internal or the external storage else null.
     */

    public static File getStorageDirectory(){
        if(storageType == STORAGE_TYPE.INTERNAL_STORAGE){
            return context.getFilesDir();
        }
        if(storageType == STORAGE_TYPE.EXTERNAL_STORAGE){
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    /**
     * Set the storage type field
     * @param s : ENUM value to be either INTERNAL_STORAGE or EXTERNAL_STORAGE or ASSETS_STORAGE
     */
    public static void setStorageType(STORAGE_TYPE s){
        storageType = s;
    }

    /**
     * Getter method for storageType field
     * @return RETURNS THE ENUM VALUE
     */

    public static STORAGE_TYPE getStorageType(){
        return storageType;
    }

    /**
     * A function which lists the files in the current path specified by path
     * @param context : The context associated with the function call
     * @param path : The path that needs to be listed
     * @return Returns a string array of all the files in the specified path
     * @throws IOException
     */

    public static String[] listFiles(Context context, String path) throws IOException {
        String[] listOfFiles;
        if (FileUtils.getStorageType() == STORAGE_TYPE.ASSETS_STORAGE) {
            if (context.getAssets().list(path).length > 0) {
                listOfFiles = context.getAssets().list(path);
                return listOfFiles;
            }
        }
        if(FileUtils.getStorageType() == STORAGE_TYPE.INTERNAL_STORAGE){
            File file = new File(context.getFilesDir(),path);
            if(file.list().length > 0) {
                listOfFiles = file.list();
                return listOfFiles;
            }
        }
        if(FileUtils.getStorageType() == STORAGE_TYPE.EXTERNAL_STORAGE){
            File file= new File(Environment.getExternalStorageDirectory(),path);
            if(file.list().length > 0) {
                listOfFiles = file.list();
                return listOfFiles;
            }
        }
        return null;
    }
}
