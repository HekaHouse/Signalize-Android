package ppc.signalize.mira.conversation;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by mukundan on 6/9/14.
 */
class XMLParse {
    public static ArrayList<ListRow> XMLParser(String xmlDoc) throws XmlPullParserException,IOException
    {
        ArrayList<ListRow> tags = new ArrayList<ListRow>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(xmlDoc));
        int eventType = xpp.getEventType();
        int topicLine = -999, patternLine = -999;
        boolean patternFlag = false;
        String localTopic = null, localPattern = null;
        while(eventType != XmlPullParser.END_DOCUMENT){
            if(eventType == XmlPullParser.START_TAG){
                if(xpp.getName().equals("topic")){
                    topicLine = xpp.getLineNumber();
                    localTopic = xpp.getAttributeValue(0);

                }
                if(xpp.getName().equals("category")){
                    patternLine = xpp.getLineNumber();
                }
                if(xpp.getName().equals("pattern")){
                    patternFlag = true;
                }
            }
            if(eventType == XmlPullParser.TEXT){
                if(patternFlag) {
                    localPattern = xpp.getText();

                    if (localPattern!=null&&topicLine == -999) {
                        ListRow listRow = new ListRow();
                        listRow.setTopic(null);
                        listRow.setPattern(localPattern);
                        listRow.setFullXml(patternLine);
                        tags.add(listRow);
                        patternLine = -999;
                    }
                    if (localPattern!=null&&topicLine != -999) {
                        ListRow listRow = new ListRow();
                        listRow.setTopic(localTopic);
                        listRow.setPattern(localPattern);
                        listRow.setFullXml(topicLine);
                        tags.add(listRow);
                        patternLine = -999;
                    }
                }
            }
            if(eventType == XmlPullParser.END_TAG){
                if(xpp.getName().equals("pattern")){
                    patternFlag = false;
                    localPattern = null;
                }
                if(xpp.getName().equals("topic")){
                    topicLine = -999;
                }
            }
            eventType = xpp.next();
        }
        return tags;
    }

}