package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.RemoteException;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import ppc.signalize.mira.conversation.IConversation;

/**
 * Created by mukundan on 6/26/14.
 */
public class FileUtils {
    private static String TAG = "File Utils";
    private static Context context = null;
    static String AIMLdir = "MIRA/aiml";
    static String changedString = "";
    static String MIRAdir = "MIRA";
    protected static XMLState xmlstate = new XMLState();
    protected static void setContext(Context context){
        FileUtils.context = context;
    }
    protected static void copyAssetsToStorage(){
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
    protected static String getChangedNodeString(Node responseText){
        String response = xmltoString(responseText);
        Log.d(TAG,""+response.indexOf("?>"));
        int index = response.indexOf("?>");
        index += 2;
        response = response.substring(index);

        return response;
    }
    protected static void setColor(TextView textView, String text, String subString, int color) {
        textView.setText(text, TextView.BufferType.SPANNABLE);
        Spannable span = (Spannable) textView.getText();
        int i = text.indexOf(subString);
        span.setSpan(new ForegroundColorSpan(color), i, i+subString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    protected static Node getReqResponse(String strPattern){
        Node responseElement = null;
        Toast.makeText(context, strPattern.split("<")[0], Toast.LENGTH_SHORT).show();
        responseElement = getRequiredResponse(FileUtils.xmlstate.docRoot, strPattern.split("<")[0]);
        return responseElement;
    }

    protected static String xmltoString(Node element){
        StringWriter str = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = null;
        DOMSource src = new DOMSource(element);
        StreamResult res = new StreamResult(str);
        try {
            t = tf.newTransformer();
            t.transform(src,res);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        System.out.println(str.toString());
        return str.toString();
    }
    private static Node getRequiredResponse(Element root, String pattern){
        Node ele = null;
        NodeList patterns = root.getElementsByTagName("pattern");
        for(int i=0;i<patterns.getLength();++i){
            Node item = patterns.item(i);
            String pat = item.getTextContent();
            if(pat.equals(pattern.trim())){
                ele =  item.getParentNode();
                Log.e("PARENT NODE","PARENT");
                FileUtils.xmltoString(ele);
                Toast.makeText(context,ele.getNodeName(),Toast.LENGTH_LONG).show();
                return ele;
            }
        }
        return null;
    }
    protected static void openFile(String filename){
        if(xmlstate.dom == null) {
            try {
                Log.d(TAG, "Opening file " + AIMLdir + "/" + filename);
                InputStream file = new FileInputStream(new File(context.getFilesDir(), AIMLdir + "/" + filename));
                xmlstate.init();
                xmlstate.setDom(file);
                file.close();
                if (context != null) {
                    Toast.makeText(context, "Able to access the assets folder", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Log.e("ParseException", "Cannot open File");
                Log.e("ParserException", e.getCause().toString());
            } catch (ParserConfigurationException e) {
                Log.e("ParseException", "Parser Config Exception");
            } catch (SAXException e) {
                Log.e("ParseException", "SAX Exception");
                ;
            }
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
    protected static String setResponseElement(Node responseElement, String newResponse, String strFileName){
        NodeList childNodes = responseElement.getChildNodes();
        Node child = null;
        String response = "";
        for(int i=0;i<childNodes.getLength();++i){

            child = childNodes.item(i);
            if(child.getNodeName().contains("template")){
                break;
            }
        }
        responseElement.removeChild(child);
        FileUtils.xmltoString(responseElement);
        String strnewResponse = newResponse;
        Element element = xmlstate.dom.createElement("template");
        element.setTextContent(strnewResponse);
        responseElement.appendChild(element);
        TransformerFactory transformerFactory;
        Transformer transformer;
        transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(FileUtils.xmlstate.dom);
            StreamResult result = new StreamResult(new File(context.getFilesDir(),"MIRA/aiml/" + strFileName));
            Log.d("Stream","Got Stream");
            transformer.transform(source, result);
            Toast.makeText(context, "Wrote to file",Toast.LENGTH_LONG).show();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
            Log.e("CAUSE", e.getCause().toString());
            Log.getStackTraceString(e);
            Log.e("TransformerException","Cannot transform AIML");
        }
        return FileUtils.xmltoString(responseElement);
    }
    private static boolean containsSrai(Node element){
        if(element.hasChildNodes()) {
            NodeList childNodes = element.getChildNodes();
            Node child;
            for(int i =0; i < childNodes.getLength(); ++i){
                child = childNodes.item(i);
                if(child.getNodeName().equals("srai")){
                    return true;
                }
            }
        }
        return false;
    }
    protected static boolean isRecursiveResponse(Node responseElement){
        return containsSrai(responseElement);
    }
    private static boolean hasContent(Node element){
        if(element.getNodeName().equals("#text")){
            return true;
        }
        return false;
    }
    private static String getContent(Node element){
        if(hasContent(element)){
            return element.getNodeValue();
        }
        return null;
    }
    protected static String getSraiContent(Node responseElement){
        if(responseElement.hasChildNodes()) {
            NodeList childNodes = responseElement.getChildNodes();
            Node child;
            for(int i =0; i < childNodes.getLength(); ++i){
                child = childNodes.item(i);
                if(child.getNodeName().equals("srai")){
                    return getContent(child);
                }
            }
        }
        return null;
    }
    protected static String getRecursiveFileName(IConversation service, String pattern) throws RemoteException {
        service.process(pattern);
        return service.getFilename();
    }
}
class XMLState{
    protected static DocumentBuilderFactory factory;
    protected static DocumentBuilder builder;
    protected static Document dom = null;
    protected static Element docRoot;

    protected static void setDom(){
        dom = null;
    }

    protected void setDom(InputStream file) throws IOException, SAXException {
        dom = builder.parse(file);
        docRoot = dom.getDocumentElement();
    }

    protected void init() throws ParserConfigurationException {
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

}