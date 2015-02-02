package org.alicebot.ab;

/**
 * Created by Aron on 4/4/2014.
 * A part of Signalize for Project Patient Care
 */
public class AndroidAIMLMap extends AIMLMap {


    /**
     * constructor to create a new AIML Map
     *
     * @param name the name of the map
     */
    public AndroidAIMLMap(String name) {
        super(name);
    }


    /**
     * read an AIML map for a bot
     *
     * @param bot the bot associated with this map.
     */
    @Override
    public void readAIMLMap(Bot bot) {
        System.out.println("Reading AIML Map " + MagicStrings.maps_path + "/" + mapName + ".txt");
        try {
            AIMLBrainCompiler ghost = (AIMLBrainCompiler) bot;
            readAIMLMapFromInputStream(ghost.getAssets().open(MagicStrings.maps_path + "/" + mapName + ".txt"), ghost);
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }


}