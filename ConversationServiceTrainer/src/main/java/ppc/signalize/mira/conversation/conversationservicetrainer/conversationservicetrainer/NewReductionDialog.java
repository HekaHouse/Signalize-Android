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

    public NewReductionDialog(Context context) {
        super(context);

    }

    public NewReductionDialog(Activity activity, int position) {
        super(activity);
        this.activity = (activity instanceof TrainerActivity)?(TrainerActivity)activity:null;
        this.position = position;
        this.setContentView(R.layout.reduction_dialog);
        this.setTitle("Add New Reduction?");
        ((TextView)this.findViewById(R.id.reduction_dialog_text)).setText(activity.getString(R.string.new_reduction_dialog_prompt));

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
                intent.putExtra(UtilityStrings.fileIntent, TrainerActivity.listNames.get(position));
                intent.putExtra(UtilityStrings.inputPatternIntent, TrainerActivity.inputPattern);
                activity.startActivity(intent);
            }
            this.dismiss();
        }
        else if(v.getId() == R.id.reduction_dialog_cancel_button){
            this.dismiss();
        }

    }
}
