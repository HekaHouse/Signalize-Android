package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.content.Context;
import android.os.RemoteException;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
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
public class FileUtility extends org.alicebot.ab.FileUtils{
    private static String TAG = "File Utils";

    static String changedString = "";
    protected static XMLState xmlstate = new XMLState();
    protected static void setContext(Context context){
        FileUtility.context = context;
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

    protected static Node removeNode(Node element){
        return element.getParentNode().removeChild(element);
    }

    protected static Node getReqResponse(String strPattern){
        Node responseElement = null;
        Toast.makeText(context, strPattern.split("<")[0], Toast.LENGTH_SHORT).show();
        if(strPattern.contains("<")) {
            Log.e(TAG,"Testing XML Pattern " + strPattern);
            responseElement = getRequiredResponse(XMLState.docRoot, strPattern.split("<")[0]);
        }
        else{
            Log.e(TAG,"Testing XML Pattern " + strPattern);
            responseElement = getRequiredResponse(XMLState.docRoot, strPattern);
        }
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
    protected static Node getRequiredResponse(Element root, String pattern){
        Node ele = null;
        NodeList patterns = root.getElementsByTagName("pattern");
        for(int i=0;i<patterns.getLength();++i){
            Node item = patterns.item(i);
            String pat = item.getTextContent();
            if(pat.equals(pattern.trim())){
                ele =  item.getParentNode();
                Log.e("PARENT NODE","PARENT");
                FileUtility.xmltoString(ele);
                Toast.makeText(context,ele.getNodeName(),Toast.LENGTH_LONG).show();
                return ele;
            }
        }
        return null;
    }
    protected static boolean createFile(String filename, String content){
        try {
            PrintWriter file = new PrintWriter(new File(org.alicebot.ab.FileUtils.getStorageDirectory(), AIMLdir + "/" + filename));
            file.println(content);
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG,"File not Found Exception");
            return false;
        }
        return true;
    }
    protected static void openFile(String filename){
        if(xmlstate.dom == null) {
            try {
                Log.d(TAG, "Opening file " + AIMLdir + "/" + filename);
                if(org.alicebot.ab.FileUtils.getStorageDirectory() == null){
                    AdvancedSettings.showErrorToast(context,"Only Assets Storage Defined!!!");
                    throw new IOException("Only Assets Storage Defined");
                }else {
                    InputStream file = new FileInputStream(new File(org.alicebot.ab.FileUtils.getStorageDirectory(), AIMLdir + "/" + filename));
                    xmlstate.init();
                    xmlstate.setDom(file);
                    file.close();
                    if (context != null) {
                        Toast.makeText(context, "Able to access the assets folder", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (IOException e) {
                Log.e("ParseException", "Cannot open File");
                Log.e("ParserException", e.getCause().toString());
            } catch (ParserConfigurationException e) {
                Log.e("ParseException", "Parser Config Exception");
            } catch (SAXException e) {
                Log.e("ParseException", "SAX Exception");
                Log.e("SAX Exception ",e.getCause() + "" + e.getMessage());
            }
        }
    }

    protected static Node appendXmlFragment(Node parent,String fragment) throws IOException, SAXException {
        Document doc = parent.getOwnerDocument();
        Node fragmentNode = XMLState.builder.parse(
                new InputSource(new StringReader(fragment)))
                .getDocumentElement();
        fragmentNode = doc.importNode(fragmentNode, true);
        parent.appendChild(fragmentNode);
        return fragmentNode;
    }

    protected static String setAdvancedResponseElement(Node responseElement, String newResponse, String strFileName) throws IOException, SAXException {
        Node parent = responseElement.getParentNode();
        parent.removeChild(responseElement);
        Node response = appendXmlFragment(parent,newResponse);
        TransformerFactory transformerFactory;
        Transformer transformer;
        transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(FileUtility.xmlstate.dom);
            StreamResult result = new StreamResult(new File(org.alicebot.ab.FileUtils.getStorageDirectory(),"MIRA/aiml/" + strFileName));
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
        return FileUtility.xmltoString(response);
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
        FileUtility.xmltoString(responseElement);
        String strnewResponse = newResponse;
        Element element = xmlstate.dom.createElement("template");
        element.setTextContent(strnewResponse);
        responseElement.appendChild(element);
        TransformerFactory transformerFactory;
        Transformer transformer;
        transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(FileUtility.xmlstate.dom);
            StreamResult result = new StreamResult(new File(org.alicebot.ab.FileUtils.getStorageDirectory(),AIMLdir +"/" + strFileName));
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
        return FileUtility.xmltoString(responseElement);
    }

    protected static void saveFile(String strFileName) throws TransformerException {
        TransformerFactory transformerFactory;
        Transformer transformer;
        transformerFactory = TransformerFactory.newInstance();

        transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(FileUtility.xmlstate.dom);
        StreamResult result = new StreamResult(new File(org.alicebot.ab.FileUtils.getStorageDirectory(),AIMLdir + "/" + strFileName));
        Log.d("Stream","Got Stream");
        transformer.transform(source, result);
        Toast.makeText(context, "Wrote to file",Toast.LENGTH_LONG).show();

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
        FileUtility.xmltoString(docRoot);
    }

    protected void init() throws ParserConfigurationException {
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

}
class GenericAIMLValidator {
    private final String TAG="Generic AIML Validator";
    protected DocumentBuilderFactory factory= null;
    protected DocumentBuilder builder = null;
    protected Document dom = null;
    protected Element docRoot;
    protected void setDom(String xml) throws IOException, SAXException {
        InputSource inputSource = new InputSource(new StringReader(xml));
        dom = builder.parse(inputSource);
        docRoot = dom.getDocumentElement();
    }
    protected void init() throws ParserConfigurationException {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
    }
    public GenericAIMLValidator() throws ParserConfigurationException {
        init();
    }
    public GenericAIMLValidator(String xml) throws IOException, SAXException, ParserConfigurationException {
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        setDom(xml);
    }
    public boolean isValidChild(String root, String []childrenInOrder){

        if(docRoot.getNodeName().equalsIgnoreCase(root)){
            Node node = getNextElementNode(docRoot.getFirstChild());
            for(int i=0;i<childrenInOrder.length;++i,node = getNextElementNode(node.getNextSibling())){
                String child = childrenInOrder[i];
                Log.d(TAG,node.getNodeName());
                if(node.getNodeName().equalsIgnoreCase(child)){
                    Log.d(TAG,"EQUAL");
                    if(i == childrenInOrder.length-1){
                        return true;
                    }
                }
                else{
                    return false;
                }
            }
        }
        return false;
    }
    private Node getNextElementNode(Node node){
        while(node != docRoot.getLastChild() && node.getNodeType() == Node.TEXT_NODE ){
            Log.d(TAG,"TEXT CONTENT" + node.getTextContent());
            node = node.getNextSibling();
        }
        Log.d(TAG,"Next Element node " + node.getNodeName());
        return node;
    }
}