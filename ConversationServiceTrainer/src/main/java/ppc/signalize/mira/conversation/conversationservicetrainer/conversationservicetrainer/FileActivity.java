package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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


public class FileActivity extends Activity implements View.OnClickListener{
    private final String ConversationServicePackage = "ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer";
    private TextView fileName,pattern,currentResponse;
    private EditText newResponse;
    private Button setResponse;
    private String strFileName,strPattern,strTemplate;
    private final String fileIntent = "fileName";
    private final String patternIntent = "pattern";
    private final String templateIntent = "template";

    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document dom = null;
    private Element docRoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        strFileName = extras.getString(fileIntent);
        strPattern = extras.getString(patternIntent);
        strTemplate = extras.getString(templateIntent);
        setContentView(R.layout.activity_file);
        fileName = (TextView)findViewById(R.id.fileName);
        pattern = (TextView)findViewById(R.id.pattern);
        currentResponse = (TextView)findViewById(R.id.currentResponse);
        newResponse = (EditText)findViewById(R.id.newResponse);
        setResponse = (Button)findViewById(R.id.setResponse);
        setResponse.setOnClickListener(this);
        fileName.setText(strFileName);
        pattern.setText(strPattern);

        openFile();
        Node responseElement = getReqResponse();
        currentResponse.setText(xmltoString(responseElement));

        Button viewFile = (Button)findViewById(R.id.viewFile);
        viewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),ViewFileActivity.class);
                intent.putExtra("fileName",strFileName);
                startActivity(intent);
            }
        });

        /*if(responseElement!=null){
            String response = "";
            response = buildResponse(responseElement.getFirstChild().getNextSibling(),"");
            currentResponse.setText(response);
        }
        else{
            Toast.makeText(this,"ResponseElement Null",Toast.LENGTH_LONG).show();
        }*/
    }
    private String xmltoString(Node element){
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
    private void setResponseElement(Node responseElement){
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
        xmltoString(responseElement);
        String strnewResponse = newResponse.getText().toString();
        Element element = dom.createElement("template");
        element.setTextContent(strnewResponse);
        responseElement.appendChild(element);
        TransformerFactory transformerFactory;
        Transformer transformer;
        transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(dom);
            StreamResult result = new StreamResult(new File(getFilesDir(),"MIRA/aiml/" + strFileName));
            Log.d("Stream","Got Stream");
            transformer.transform(source, result);
            Toast.makeText(this,"Wrote to file",Toast.LENGTH_LONG).show();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
            Log.e("CAUSE", e.getCause().toString());
            Log.getStackTraceString(e);
            Log.e("TransformerException","Cannot transform AIML");
        }

        
        currentResponse.setText(xmltoString(responseElement));
    }
    private Node getReqResponse(){
        Node responseElement = null;
        responseElement = getRequiredResponse(docRoot, strPattern.split("<")[0]);
        return responseElement;
    }

    private void openFile(){
        try {
            InputStream file = new FileInputStream(new File(getFilesDir(),"MIRA/aiml/" + strFileName));
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            dom = builder.parse(file);
            docRoot = dom.getDocumentElement();
            file.close();
            Toast.makeText(this,strPattern.split("<")[0],Toast.LENGTH_SHORT).show();

            Toast.makeText(this,"Able to access the assets folder",Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.e("ParseException","Cannot open File");
        } catch (ParserConfigurationException e) {
            Log.e("ParseException","Parser Config Exception");
        } catch (SAXException e) {
            Log.e("ParseException","SAX Exception");;
        }
    }

    private Node getRequiredResponse(Element root, String pattern){
        Node ele = null;
        NodeList patterns = root.getElementsByTagName("pattern");
        for(int i=0;i<patterns.getLength();++i){
            Node item = patterns.item(i);
            String pat = item.getTextContent();
            if(pat.equals(pattern.trim())){
                ele =  item.getParentNode();
                Log.e("PARENT NODE","PARENT");
                xmltoString(ele);
                Toast.makeText(this,ele.getNodeName(),Toast.LENGTH_LONG).show();
                return ele;
            }
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(dom == null){
            openFile();
        }
        Node responseElement = getReqResponse();
        setResponseElement(responseElement);

    }
}
