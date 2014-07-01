package org.alicebot.ab;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Aron on 4/4/2014.
 * A part of Signalize for Project Patient Care
 */
public class AndroidAIMLSet extends AIMLSet {


    /**
     * constructor
     *
     * @param name name of set
     */
    public AndroidAIMLSet(String name) {
        super(name);
    }


    @Override
    public void readAIMLSet(Bot bot) {
        System.out.println("Reading AIML Set " + MagicStrings.sets_path + "/" + setName + ".txt");
        Ghost ghost = (Ghost) bot;
        try {
            if(!Ghost.isInternalStorage()) {

                readAIMLSetFromInputStream(ghost.getAssets().open(MagicStrings.sets_path + "/" + setName + ".txt"), bot);
            }
            else{
                File file = new File(ghost.getFilesDir(),MagicStrings.sets_path + "/" + setName + ".txt");
                readAIMLSetFromInputStream(new FileInputStream(file),bot);
            }

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }
}
