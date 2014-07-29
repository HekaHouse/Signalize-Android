package ppc.signalize.mira.conversation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mukundan on 6/2/14.
 */
public class StartServiceReciever extends BroadcastReceiver{
    private boolean startService = false;
    private final String StartServiceBroadcast = "ppc.signalize.mira.conversation.startService";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ppc.signalize.mira.conversation.Receiver","Going to start Service");
        if(intent.getAction().equals(StartServiceBroadcast)&&!startService){
            Intent serviceIntent = new Intent(context,ConversationService.class);
            context.startService(serviceIntent);
            Log.d("ppc.signalize.mira.conversation.Receiver","Started Service");
            startService = true;
        }
    }
}
