package org.alicebot.ab;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class representing the AIML bot
 */

/**
 * Created by Aron on 3/9/14.
 * Changed by Mukundan on 6/28/14.
 */
public class Ghost extends Bot {

    private static Context context;
    public static ArrayList<String> listOfPatterns = new ArrayList<String>();
    private ArrayList<String> executing;
    public static String TAG = "Ghost";
    private HashMap<String, ArrayList<Category>> categoryQueue = new HashMap<String, ArrayList<Category>>();

    public Ghost(String _name, String _path) {


        super(_name, _path, "auto");
        // Super constructor calls setAllPaths(_path,_name)

    }

    public static boolean isInternalStorage() {
        if(FileUtils.getStorageType() == FileUtils.STORAGE_TYPE.INTERNAL_STORAGE)
            return true;
        return false;
    }

    public static void setInternalStorage(boolean internalStorage) {
        FileUtils.setStorageType(FileUtils.STORAGE_TYPE.INTERNAL_STORAGE);
    }

    public static void setExternalStorage(){
        FileUtils.setStorageType(FileUtils.STORAGE_TYPE.EXTERNAL_STORAGE);
    }

    public AssetManager getAssets(){
        Log.d(TAG,"Assets DIR");
        return Ghost.context.getAssets();
    }
    public static File getFilesDir(){
        Log.d(TAG,"Files DIR");
        return context.getFilesDir();
    }

    public static void setContext(Context context){
        Ghost.context = context;
    }
    public static void setContext(Context context, FileUtils.STORAGE_TYPE storageType){
        Ghost.context = context;
        if(storageType == FileUtils.STORAGE_TYPE.EXTERNAL_STORAGE){
            Ghost.setExternalStorage();
        }
        else if(storageType == FileUtils.STORAGE_TYPE.INTERNAL_STORAGE){
            Ghost.setInternalStorage(true);
        }
    }
    /**
     * Load all AIML Sets
     */
    @Override
    void addAIMLSets() {
        Timer timer = new Timer();
        timer.start();
        try {
            // Directory path here
            String[] listOfFiles;

            listOfFiles = FileUtils.listFiles(context,MagicStrings.sets_path);
            if (listOfFiles.length > 0) {

                for (String listOfFile : listOfFiles) {

                    if (listOfFile.endsWith(".txt") || listOfFile.endsWith(".TXT")) {
                        System.out.println(listOfFile);
                        String setName = listOfFile.substring(0, listOfFile.length() - ".txt".length());
                        System.out.println("Read AIML Set " + setName);
                        AndroidAIMLSet aimlSet = new AndroidAIMLSet(setName);
                        aimlSet.readAIMLSet(this);
                        setMap.put(setName, aimlSet);
                    }

                }
            } else
                System.out.println("addAIMLSets: " + MagicStrings.sets_path + " does not exist.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Load all AIML Maps
     */
    @Override
    void addAIMLMaps() {
        Timer timer = new Timer();
        timer.start();
        try {
            String[] listOfFiles;

            listOfFiles = FileUtils.listFiles(context,MagicStrings.maps_path);
            if (listOfFiles.length > 0) {

                System.out.println("Loading AIML Map files from " + MagicStrings.maps_path);
                for (String listOfFile : listOfFiles) {

                    if (listOfFile.endsWith(".txt") || listOfFile.endsWith(".TXT")) {
                        System.out.println(listOfFile);
                        String mapName = listOfFile.substring(0, listOfFile.length() - ".txt".length());
                        System.out.println("Read AIML Map " + mapName);
                        AndroidAIMLMap aimlMap = new AndroidAIMLMap(mapName);
                        aimlMap.readAIMLMap(this);
                        mapMap.put(mapName, aimlMap);
                    }

                }
            } else
                System.out.println("addMaps: " + MagicStrings.aiml_path + " does not exist.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * load bot properties
     */
    @Override
    void addProperties() {
        try {
            properties.getPropertiesFromInputStream(context.getAssets().open(MagicStrings.config_path + "/properties.txt"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setAllPaths(String root, String name) {
        MagicStrings.bot_path = "";
        MagicStrings.bot_name_path = name;
        System.out.println("Name = " + name + " Path = " + MagicStrings.bot_name_path);
        MagicStrings.aiml_path = MagicStrings.bot_name_path + "/aiml";

        if(FileUtils.getStorageType() == FileUtils.STORAGE_TYPE.INTERNAL_STORAGE){
            MagicStrings.aimlif_path = context.getFilesDir() + "/aimlif";
            MagicStrings.log_path = context.getFilesDir() + "/logs";
        }
        else{
            MagicStrings.aimlif_path = Environment.getExternalStorageDirectory() + "/aimlif";
            MagicStrings.log_path = Environment.getExternalStorageDirectory() + "/logs";
        }
        System.out.println("AIMLIF PATH "+MagicStrings.aimlif_path);
        boolean f = new File(MagicStrings.aimlif_path).mkdirs();
        Log.w("CREATED DIRECTORY AIMLIF",""+f);
        MagicStrings.config_path = MagicStrings.bot_name_path + "/config";


        f = new File(MagicStrings.log_path).mkdirs();
        Log.w("CREATED DIRECTORY AIMLIF",""+f);
        MagicStrings.sets_path = MagicStrings.bot_name_path + "/sets";
        MagicStrings.maps_path = MagicStrings.bot_name_path + "/maps";
        System.out.println(MagicStrings.root_path);
        System.out.println(MagicStrings.bot_path);
        System.out.println(MagicStrings.bot_name_path);
        System.out.println(MagicStrings.aiml_path);
        System.out.println(MagicStrings.aimlif_path);
        System.out.println(MagicStrings.config_path);
        System.out.println(MagicStrings.log_path);
        System.out.println(MagicStrings.sets_path);
        System.out.println(MagicStrings.maps_path);
    }

    @Override
    void addMoreCategories(String file, ArrayList<Category> moreCategories) {
        if (categoryQueue == null)
            categoryQueue = new HashMap<String, ArrayList<Category>>();
        categoryQueue.put(file, moreCategories);
        executing.remove(file);
        Log.d(TAG, "completed: " + file);
    }

    /**
     * Load all brain categories from AIML directory
     */
    @Override
    void addCategoriesFromAIML() {
        if (executing == null)
            executing = new ArrayList<String>();
        Timer timer = new Timer();
        timer.start();
        try {
            // Directory path here
            String[] listOfFiles;
            AssetManager ass = context.getAssets();
            AndroidDomUtils.mgr = ass;
            listOfFiles = FileUtils.listFiles(context,MagicStrings.aiml_path);
            if (listOfFiles.length > 0) {
                System.out.println("Loading AIML files from " + MagicStrings.aiml_path);
                for (String file : listOfFiles) {
                    if (file.endsWith(".aiml") || file.endsWith(".AIML")) {
                        Log.d(TAG, "executing: " + file);
                        executing.add(file);
                        Thread t = new Thread(new AsyncAIML(this, file), file);
                        t.start();
                        pause(100, 4);
                    }
                }

            } else
                System.out.println("addCategories: " + MagicStrings.aiml_path + " does not exist.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        pause(1000, 0);
        Log.d(TAG, "building brain");
        processCategoryQueue();
        System.out.println("Loaded " + brain.getCategories().size() + " categories in " + timer.elapsedTimeSecs() + " sec");
    }

    private void processCategoryQueue() {
        for (String file : categoryQueue.keySet()) {
            if (file.contains(MagicStrings.deleted_aiml_file)) {
                for (Category c : categoryQueue.get(file)) {
                    //System.out.println("Delete "+c.getPattern());
                    deletedGraph.addCategory(c);
                    listOfPatterns.remove(c.getPattern());
                    Log.e(TAG,"Removed Pattern" + c.getPattern());
                }
            } else if (file.contains(MagicStrings.unfinished_aiml_file)) {
                for (Category c : categoryQueue.get(file)) {
                    //System.out.println("Delete "+c.getPattern());
                    if (brain.findNode(c) == null){
                        unfinishedGraph.addCategory(c);
                        listOfPatterns.remove(c.getPattern());
                        Log.e(TAG,"Removed Pattern" + c.getPattern());
                    }

                    else System.out.println("unfinished " + c.inputThatTopic() + " found in brain");
                }
            } else if (file.contains(MagicStrings.learnf_aiml_file)) {
                System.out.println("Reading Learnf file");
                for (Category c : categoryQueue.get(file)) {
                    brain.addCategory(c);
                    if(!listOfPatterns.contains(c.getPattern())){
                        listOfPatterns.add(c.getPattern());
                        Log.e(TAG,"Added Pattern" + c.getPattern());
                    }
                    learnfGraph.addCategory(c);
                    patternGraph.addCategory(c);
                }
                //this.categories.addAll(moreCategories);
            } else {
                for (Category c : categoryQueue.get(file)) {
                    //brain.printgraph();
                    try {
                        brain.addCategory(c);
                        if(!listOfPatterns.contains(c.getPattern())){
                            listOfPatterns.add(c.getPattern());
                            Log.e(TAG,"Added Pattern" + c.getPattern());
                        }
                        patternGraph.addCategory(c);
                    } catch (Exception e) {
                        Log.d(TAG, "Failed to load category: " + e.getCause());
                    }
                    //brain.printgraph();
                }
                //this.categories.addAll(moreCategories);
            }
        }
    }


    /**
     * load all brain categories from AIMLIF directory
     */
    @Override
    void addCategoriesFromAIMLIF() {
        if (executing == null)
            executing = new ArrayList<String>();
        Timer timer = new Timer();
        timer.start();
        try {
            // Directory path here
            File looky = new File(MagicStrings.aimlif_path);
            if (looky.listFiles().length > 0) {
                String[] listOfFiles = looky.list();
                System.out.println("Loading AIML files from " + MagicStrings.aimlif_path);
                for (String file : listOfFiles) {

                    if (file.endsWith(MagicStrings.aimlif_file_suffix) || file.endsWith(MagicStrings.aimlif_file_suffix.toUpperCase())) {
                        //System.out.println(file);
                        try {
                            Log.d(TAG, "executing: " + file);
                            executing.add(file);

                            new Thread(new AsyncAIMLIF(this, file), file).start();
                            pause(100, 4);


                            //   MemStats.memStats();
                        } catch (Exception iex) {
                            System.out.println("Problem loading " + file);
                            iex.printStackTrace();
                        }
                    }

                }
            } else
                System.out.println("addCategories: " + MagicStrings.aimlif_path + " does not exist.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        pause(1000, 0);
        if (categoryQueue != null && categoryQueue.size() > 0) {
            Log.d(TAG, "building memory");
            processCategoryQueue();
        }
        System.out.println("Loaded " + brain.getCategories().size() + " categories in " + timer.elapsedTimeSecs() + " sec");
    }

    private void pause(int milli, int until_executing_less_than) {
        while (executing.size() > until_executing_less_than) {
            try {
                Thread.sleep(milli);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}

