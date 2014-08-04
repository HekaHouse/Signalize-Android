package aiml.aimlValidate;

import android.content.Context;
import android.util.Log;

import org.alicebot.ab.R;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.StringReader;

import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.stream.StreamSource;
import mf.javax.xml.validation.Schema;
import mf.javax.xml.validation.Validator;
import mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory;
public class AIMLValidate {
    protected static final String TAG = "AIML VALIDATION";
    Validator validator;
    XMLSchemaFactory factory;
    Source schemaFile,xmlSource;
    Schema schema;
    Context context;

    public AIMLValidate(Context context){
        this.context = context;
    }

    public boolean aimlValidate(String xmlDoc) throws Exception {
        try {
            factory = new XMLSchemaFactory();
            schemaFile = new StreamSource(context.getResources().openRawResource(R.raw.aiml_schema));
            xmlSource = new StreamSource(new StringReader(xmlDoc));
            schema = factory.newSchema(schemaFile);
            validator = schema.newValidator();
            validator.setErrorHandler(new SimpleErrorHandler());
            validator.validate(xmlSource);

        } catch (SAXException e) {

            Log.e(TAG,"SAX EXCEPTION "+e.getMessage());
            throw e;
        } catch (IOException e) {
            Log.e(TAG,"IO EXCEPTION");
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"EXCEPTION");
            throw e;
        } catch (Error e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }
}


class SimpleErrorHandler implements ErrorHandler{



    @Override
    public void error(SAXParseException arg0) throws SAXException {
        // TODO Auto-generated method stub
        Log.e(AIMLValidate.TAG,"Error " + arg0.getLineNumber() + " Message " + arg0.getMessage());

    }

    @Override
    public void fatalError(SAXParseException arg0) throws SAXException {
        // TODO Auto-generated method stub
        Log.e(AIMLValidate.TAG, "Fatal Error " + arg0.getLineNumber() + " Message " + arg0.getMessage());

    }

    @Override
    public void warning(SAXParseException arg0) throws SAXException {
        // TODO Auto-generated method stub
        Log.w(AIMLValidate.TAG,"Warning " + arg0.getLineNumber() + " Message " + arg0.getMessage());
    }

}
