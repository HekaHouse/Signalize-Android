package org.alicebot.ab;

import java.util.ArrayList;

/**
 * Created by Aron on 4/1/2014.
 * A part of Signalize for Project Patient Care
 */
public class AsyncAIML implements Runnable {
    private final Ghost myGhost;
    private final String myFile;
    private ArrayList<Category> moreCategories = new ArrayList<Category>();
    private String file = null;

    public AsyncAIML(Ghost g, String filed) {
        myFile = filed;
        myGhost = g;
    }


    @Override
    public void run() {
        try {
            ArrayList<Category> categories = AndroidAIMLProcessor.AIMLToCategories(MagicStrings.aiml_path, myFile);
            myGhost.addMoreCategories(myFile, categories);
        } catch (Exception iex) {
            System.out.println("Problem loading " + myFile);
            iex.printStackTrace();
        }
    }


}
