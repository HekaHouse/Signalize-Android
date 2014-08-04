package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;

/**
 * Created by mukundan on 8/1/14.
 */
public class DeleteReduction extends Activity{

    String strFilename;
    String strPattern;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        Intent intent = new Intent();
        intent.putExtra(UtilityStrings.positionIntent,extras.getInt(UtilityStrings.positionIntent));
        strFilename = extras.getString(UtilityStrings.fileIntent);
        strPattern = extras.getString(UtilityStrings.patternIntent);
        Log.d("Delete Reduction","STR PAttern " + strPattern);
        Log.d("Delete Reduction","STR FILENAME " + strFilename);
        try {
            FileUtility.openFile(strFilename);
            Node responseElement = FileUtility.getReqResponse(strPattern);
            FileUtility.xmltoString(responseElement);
            FileUtility.removeNode(responseElement);
            FileUtility.saveFile(strFilename);
        } catch (TransformerException e) {
            e.printStackTrace();
            setResult(RESULT_CANCELED,intent);
        }
        setResult(RESULT_OK,intent);
        finish();
    }
}
