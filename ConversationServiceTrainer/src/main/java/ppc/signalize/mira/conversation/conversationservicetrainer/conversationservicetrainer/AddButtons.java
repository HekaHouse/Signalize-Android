package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Map;

import ppc.signalize.mira.conversation.Util;

/**
 * Created by mukundan on 7/24/14.
 */
public class AddButtons {

    private LinearLayout root ;
    private Context context;
    private EditText editText;
    public AddButtons(Context context,LinearLayout root, EditText editText){
        this.root = root;
        this.context = context;
        this.editText = editText;
    }

    public boolean addButtons(){
        if(root.getChildCount() == 1){
            Iterator idIterator = UtilityStrings.buttonIdMap.entrySet().iterator();
            while(idIterator.hasNext()){
                Map.Entry pairs = (Map.Entry) idIterator.next();
                final String key = pairs.getKey().toString();
                if(!key.equals("srai")) {
                    Button button = new Button(context);

                    button.setText(pairs.getKey().toString());
                    button.setOnClickListener(new Listeners.AddTagListener(context, UtilityStrings.TAGTOADD.valueOf(key.toUpperCase()),editText));
                    button.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Toast.makeText(context, UtilityStrings.buttonToolTipMap.get(key),Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });
                    root.addView(button);
                    Log.d("AddButtons", "Added button " + key);
                }
            }
            return true;
        }
        return false;
    }

}
