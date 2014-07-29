package org.alicebot.ab;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Aron on 4/2/2014.
 * A part of Signalize for Project Patient Care
 */
public class AndroidAIMLProcessor {
    /**
     * when parsing an AIML file, process a category element.
     *
     * @param n        current XML parse XmlPullParser.
     * @param categories                      list of categories found so far.
     * @param topic                           value of topic in case this category is wrapped in a <topic> tag
     * @param aimlFile                        name of AIML file being parsed.
     */
    public static AIMLProcessorExtension extension;
    private static String TAG = "AIMLProcessing";


    /**
     * convert an AIML file to a list of categories.
     *
     * @param directory directory containing the AIML file.
     * @param aimlFile  AIML file name.
     * @return list of categories.
     */
    public static ArrayList<Category> AIMLToCategories(String directory, String aimlFile) {
        ArrayList categories = new ArrayList<Category>();
        try {

            XmlPullParser xml = AndroidDomUtils.parseFile(directory + "/" + aimlFile);      // <aiml> tag
            AimlPullProcessor app = new AimlPullProcessor(xml, aimlFile);

            String language = MagicStrings.default_language;
            String topic = "";
            int eventType = 0;
            eventType = app.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d(TAG, "Start document " + aimlFile);
                } else if (eventType == XmlPullParser.END_DOCUMENT) {
                    Log.d(TAG, "End document " + aimlFile);
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (app.isCategory())
                        categories.add(app.getCategory());
                    else if (app.isTopic())
                        categories.addAll(app.getTopicCategories());
                }
                eventType = app.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }


}



