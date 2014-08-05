package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Map;


/**
 * Created by mukundan on 7/24/14.
 */
public class AddButtons {

    private LinearLayout root ;
    private Context context;
    private EditText editText;
    private int childcount;
    public AddButtons(Context context,LinearLayout root, EditText editText){
        this.root = root;
        this.context = context;
        this.editText = editText;
    }

    public void setEditText(EditText editText){
        this.editText = editText;
    }

    public boolean addButtons(){
        if(root.getChildCount() == 1){
            childcount = 0;
            LinearLayout linearLayout;
            linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            Iterator idIterator = UtilityStrings.buttonIdMap.entrySet().iterator();
            while(idIterator.hasNext()){
                Map.Entry pairs = (Map.Entry) idIterator.next();
                final String key = pairs.getKey().toString();
                Button button = new Button(context);

                button.setText(pairs.getKey().toString());
                button.setOnClickListener(new Listeners.AddTagListener(context, UtilityStrings.TAGTOADD.valueOf(key.toUpperCase()), editText));
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(context, UtilityStrings.buttonToolTipMap.get(key), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                childcount ++;
                linearLayout.addView(button);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)button.getLayoutParams();
                params.weight = 1f;
                button.setLayoutParams(params);
                if(childcount == 2){
                    root.addView(linearLayout);
                    linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    childcount = 0;
                }

                Log.d("AddButtons", "Added button " + key);

            }
            if(childcount!=0){
                root.addView(linearLayout);
            }
            return true;
        }
        return false;
    }

}
