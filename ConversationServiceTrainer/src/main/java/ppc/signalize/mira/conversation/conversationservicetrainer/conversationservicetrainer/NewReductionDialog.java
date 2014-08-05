package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by mukundan on 8/4/14.
 */
public class NewReductionDialog extends Dialog implements View.OnClickListener {
    TrainerActivity activity;
    int position;
    Button ok_button,cancel_button;
    String fileName;
    String inputPattern;

    public NewReductionDialog(Context context) {
        super(context);

    }

    public NewReductionDialog(Activity activity, int position) {
        super(activity);
        this.activity = (activity instanceof TrainerActivity)?(TrainerActivity)activity:null;
        this.position = position;
        this.setContentView(R.layout.reduction_dialog);
        this.setTitle(getContext().getString(R.string.add_new_reduction_question));
        ((TextView)this.findViewById(R.id.reduction_dialog_text)).setText(activity.getString(R.string.new_reduction_dialog_prompt));
        fileName = null;
        inputPattern = null;
        ok_button = (Button) this.findViewById(R.id.reduction_dialog_ok_button);
        cancel_button = (Button) this.findViewById(R.id.reduction_dialog_cancel_button);
        ok_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

    }

    public NewReductionDialog(Activity activity1, String fileName, String inputPattern){
        super(activity1);
        this.activity = (activity1 instanceof TrainerActivity)?(TrainerActivity)activity1:null;
        this.position = -1;
        this.setContentView(R.layout.reduction_dialog);
        this.setTitle(getContext().getString(R.string.add_new_reduction_question));
        ((TextView)this.findViewById(R.id.reduction_dialog_text)).setText(activity.getString(R.string.new_reduction_dialog_prompt));
        this.fileName = fileName;
        this.inputPattern = inputPattern;
        ok_button = (Button) this.findViewById(R.id.reduction_dialog_ok_button);
        cancel_button = (Button) this.findViewById(R.id.reduction_dialog_cancel_button);
        ok_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.reduction_dialog_ok_button){
            if(activity!=null) {
                Intent intent = new Intent(getContext(), NewReduction.class);
                if(fileName == null) {
                    intent.putExtra(UtilityStrings.fileIntent, TrainerActivity.listNames.get(position));
                }
                else{
                    intent.putExtra(UtilityStrings.fileIntent,fileName);
                }
                if(inputPattern == null) {
                    intent.putExtra(UtilityStrings.inputPatternIntent, TrainerActivity.inputPattern);
                }
                else{
                    intent.putExtra(UtilityStrings.inputPatternIntent, inputPattern);
                }
                activity.startActivity(intent);
            }
            this.dismiss();
        }
        else if(v.getId() == R.id.reduction_dialog_cancel_button){
            this.dismiss();
        }

    }
}
