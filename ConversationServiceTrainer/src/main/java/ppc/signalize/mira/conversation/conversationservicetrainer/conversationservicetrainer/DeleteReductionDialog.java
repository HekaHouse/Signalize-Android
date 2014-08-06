package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by mukundan on 8/1/14.
 */
public class DeleteReductionDialog extends Dialog implements View.OnClickListener {
    TrainerActivity activity;
    int position;
    private Button ok_button,cancel_button;
    private String pattern;

    public DeleteReductionDialog(Context context) {
        super(context);

    }

    public DeleteReductionDialog(Activity activity, int position) {
        super(activity);
        this.activity = (activity instanceof TrainerActivity)?(TrainerActivity)activity:null;
        this.position = position;
        pattern = TrainerActivity.listInpNames.get(position);
        this.setContentView(R.layout.reduction_dialog);
        this.setTitle(UtilityStrings.deleteReducitonTitle);
        if(TrainerActivity.map.isTopPattern(pattern)) {
            ((TextView)this.findViewById(R.id.reduction_dialog_text)).setText(activity.getString(R.string.delete_reduction_dialog_prompt) +  " " + pattern);
        }
        else{
            ((TextView)this.findViewById(R.id.reduction_dialog_text)).setText(
                    UtilityStrings.not_safe_text + "\n" +
                    activity.getString(R.string.delete_reduction_dialog_prompt) +  " " + pattern);
        }
        ok_button = (Button) this.findViewById(R.id.reduction_dialog_ok_button);
        cancel_button = (Button) this.findViewById(R.id.reduction_dialog_cancel_button);
        ok_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.reduction_dialog_ok_button){
            if(activity!=null) {
                Intent intent = new Intent((activity != null) ? activity : getContext(), DeleteReduction.class);
                intent.putExtra(UtilityStrings.fileIntent, TrainerActivity.listNames.get(position));
                intent.putExtra(UtilityStrings.patternIntent, pattern);
                activity.startActivityForResult(intent, UtilityStrings.DELETE_REDUCTION_RESULT_CODE);
            }
            this.dismiss();
        }
        else if(v.getId() == R.id.reduction_dialog_cancel_button){
            this.dismiss();
        }

    }
}
