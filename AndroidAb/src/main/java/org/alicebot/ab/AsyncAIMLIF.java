package org.alicebot.ab;

import java.util.ArrayList;

/**
 * Created by Aron on 4/5/2014.
 * A part of Signalize for Project Patient Care
 */
public class AsyncAIMLIF implements Runnable {
    private final AIMLBrainCompiler myGhost;
    private final String myFile;
    private ArrayList<Category> moreCategories = new ArrayList<Category>();
    private String file = null;

    public AsyncAIMLIF(AIMLBrainCompiler g, String filed) {
        myFile = filed;
        myGhost = g;
    }


    @Override
    public void run() {
        try {
            moreCategories = myGhost.readIFCategories(MagicStrings.aimlif_path + "/" + myFile);
            myGhost.addMoreCategories(myFile, moreCategories);
        } catch (Exception iex) {
            System.out.println("Problem loading " + myFile);
            iex.printStackTrace();
        }
    }
}
