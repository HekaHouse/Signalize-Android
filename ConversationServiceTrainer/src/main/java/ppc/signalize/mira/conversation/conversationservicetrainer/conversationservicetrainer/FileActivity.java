package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.content.Context;
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
    private Document dom;
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


        currentResponse.setText(strTemplate);



        /*if(responseElement!=null){
            String response = "";
            response = buildResponse(responseElement.getFirstChild().getNextSibling(),"");
            currentResponse.setText(response);
        }
        else{
            Toast.makeText(this,"ResponseElement Null",Toast.LENGTH_LONG).show();
        }*/
    }
    private void setResponseElement(Node responseElement){
        NodeList childNodes = responseElement.getChildNodes();
        Node child = null;
        String response = "";
        for(int i=0;i<childNodes.getLength();++i){

            child = childNodes.item(i);
            response += "<" + child.getNodeName() + ">\n";
            if(child.getNodeName().contains("template")){
                break;
            }
        }
        responseElement.removeChild(child);
        String strnewResponse = newResponse.getText().toString();
        Element element = dom.createElement("template");
        element.setTextContent(strnewResponse);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(dom);
            StreamResult result = new StreamResult(new File("MIRA/aiml/" + strFileName));
            transformer.transform(source, result);
            Toast.makeText(this,"Wrote to file",Toast.LENGTH_LONG).show();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
            Log.getStackTraceString(e);
            Log.e("TransformerException","Cannot transform AIML");
        }

        
        currentResponse.setText(response);
    }

    private Node openFile(){
        Node responseElement = null;
        try {
            InputStream file = new FileInputStream(new File(getFilesDir(),"MIRA/aiml/" + strFileName));
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            dom = builder.parse(file);
            docRoot = dom.getDocumentElement();
            file.close();
            Toast.makeText(this,strPattern.split("<")[0],Toast.LENGTH_SHORT).show();
            responseElement = getRequiredResponse(docRoot, strPattern.split("<")[0]);
            Toast.makeText(this,"Able to access the assets folder",Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.e("ParseException","Cannot open File");
        } catch (ParserConfigurationException e) {
            Log.e("ParseException","Parser Config Exception");
        } catch (SAXException e) {
            Log.e("ParseException","SAX Exception");;
        }
        return responseElement;
    }

    private Node getRequiredResponse(Element root, String pattern){
        Node ele = null;
        NodeList patterns = root.getElementsByTagName("pattern");
        for(int i=0;i<patterns.getLength();++i){
            Node item = patterns.item(i);
            String pat = item.getTextContent();
            if(pat.contains(pattern.trim())){
                ele =  item.getParentNode();
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
        Node responseElement = openFile();
        setResponseElement(responseElement);

    }
}
