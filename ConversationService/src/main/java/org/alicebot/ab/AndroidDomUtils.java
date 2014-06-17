package org.alicebot.ab;

import android.content.res.AssetManager;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Aron on 4/2/2014.
 * A part of Signalize for Project Patient Care
 */
public class AndroidDomUtils {

    public static AssetManager mgr;

    public static XmlPullParser parseFile(String fileName) throws Exception {
        XmlPullParserFactory dbFactory = XmlPullParserFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        XmlPullParser dBuilder = dbFactory.newPullParser();

        dBuilder.setInput(mgr.open(fileName), null);

        return dBuilder;
    }


    public static XmlPullParser parseString(String string) throws Exception {
        InputStream is = new ByteArrayInputStream(string.getBytes("UTF-16"));

        XmlPullParserFactory dbFactory = XmlPullParserFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        XmlPullParser dBuilder = dbFactory.newPullParser();

        dBuilder.setInput(is, null);

        return dBuilder;
    }


    /**
     * convert an XML node to an XML statement
     *
     * @param node current XML node
     * @return XML string
     */
    public static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "no");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }
}
