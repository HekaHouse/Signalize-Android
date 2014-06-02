package ppc.signalize.mira.conversation;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by mukundan on 5/28/14.
 */
public class ConversationService extends Service{
    Conversation conversation;
    private final String TAG = "ConversationService";
    @Override
    public void onCreate() {
        super.onCreate();
        conversation = Conversation.initialize(this.getApplicationContext());
        Log.d(TAG,"The Service was created");
        PackageManager pm = getPackageManager();
        ComponentName compName =
        new ComponentName(getApplicationContext(),StartServiceReciever.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Log.d(TAG,"Disabled Braodcast Receiver");
        Toast.makeText(this,"Service was created",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"The Service was destroyed");
        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),StartServiceReciever.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Log.d(TAG,"Enabled Braodcast Receiver");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"The service is now unbound from the application");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(intent.getAction().equals("ppc.signalize.mira.conversation.ConversationService")){
            Log.d(TAG,"The service is now bound to the application");
            return new Proxy();
        }
        return null;
    }
    class Proxy extends IConversation.Stub{

        @Override
        public String process(String input) throws RemoteException {
            return conversation.process(input);
        }
    }
}
