package ppc.signalize.mira.conversation;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

/**
 * Created by mukundan on 5/28/14.
 */
public class ConversationService extends Service{
    Conversation conversation;
    Context context;
    NotificationCompat.Builder mBuilder;
    protected static boolean started = false;

    private final String TAG = "ConversationService";
    private NotificationManager notificationManager;
    @Override
    public void onCreate() {
        super.onCreate();
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.customer_service_)
                .setContentTitle(getString(R.string.mira_service))
                .setContentText(getString(R.string.mira_service_running));
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        started = true;
        context = this;
        conversation = Conversation.initialize(context);
        Log.d(TAG,"The Service was created");
        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),StartServiceReciever.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Log.d(TAG, "Disabled Braodcast Receiver");
        Toast.makeText(this,"Service was created",Toast.LENGTH_LONG).show();
        notificationManager.notify(mBuilder.hashCode(), mBuilder.build());
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
        Log.d(TAG,"Enabled Broadcast Receiver");
        notificationManager.cancel(mBuilder.hashCode());
        started = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"The service is now unbound from the application");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(intent.getAction().equals("ppc.signalize.mira.conversation.ConversationService")){
            if(!started){
                context = this;
                conversation = Conversation.initialize(context);
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
            Log.d(TAG,"The service is now bound to the application");
            return new Proxy();
        }
        return null;
    }
    class Proxy extends IConversation.Stub{


        @Override
        public String process(String input) throws RemoteException {
            String response = conversation.process(input);
            Log.d(TAG,"Going to send "+ response);
            return response;
        }

        @Override
        public String inputThatTopic() throws RemoteException {
            return conversation.inputThatTopic();
        }

        @Override
        public String getPatterns() throws RemoteException {
            return conversation.getPatterns();
        }

        @Override
        public String getFilename() throws RemoteException {
            return conversation.getFilename();
        }

        @Override
        public String getFilenames() throws RemoteException {
            return conversation.getFilenames();
        }

        @Override
        public String getTemplate() throws RemoteException {
            return conversation.getTemplate();
        }

        @Override
        public void writeAIMLOut() throws RemoteException {
            conversation.writeAIMLOut();
        }

        @Override
        public void reSync() throws RemoteException {
            conversation = Conversation.sync(context);
        }

        @Override
        public List<String> listOfPatterns() throws RemoteException {
            return conversation.getListofPatterns();
        }

        @Override
        public String getStorageType() throws RemoteException {
            return conversation.getStorageType();
        }
    }

}
