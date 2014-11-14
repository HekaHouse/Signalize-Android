package ppc.signalize.mira;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.aliasi.classify.JointClassification;

import ppc.signalize.mira.conversation.IConversation;

import ppc.signalize.mira.brain.Consideration;
import ppc.signalize.mira.brain.Intuition;
import ppc.signalize.mira.nervous.concurrent.AsyncMouth;


/**
 * Created by Aron on 3/7/14.
 * Changed by Mukundan on 7/9/14. Connection to Service.
 */
public class Mira implements Runnable,ServiceConnection {

    private final String ConversationServiceTAG= "ppc.signalize.mira.conversation.ConversationService";
    private final String ConversationSericePackage = "ppc.signalize.mira.conversation";
    private final String StartServiceBroadcast = "ppc.signalize.mira.conversation.startService";
    private IConversation service;
    Context context;
    private static final String TAG = "ppc/signalize/mira";
    public Voice _world;
    public boolean _is_awake;
    private boolean withPrompt;

    public void sendBroadcasttoService(){
        Intent intent = new Intent();
        intent.setAction(StartServiceBroadcast);
        context.sendBroadcast(intent);
        connectToService();
    }

    private void connectToService() {
        Intent intent;
        intent = new Intent(ConversationServiceTAG);
        intent.setClassName(ConversationSericePackage,ConversationServiceTAG);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "The Service will be connected soon!");
    }

    public Mira(Voice c) {
        _world = c;
        this.context = getApplicationContext();
    }


    public void writeAIMLOut(){
        try {
            service.writeAIMLOut();
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG,"Remote Exception " + e.getCause());
        }
    }
    @Override
    public void run() {

        Intuition intuit = new Intuition(false, _world);
        while (intuit.thready.activeCount() > 1) {
            pause(1000);
        }
        Log.d(TAG, "run ppc.signalize.mira");
        _is_awake = true;
        Log.d(TAG, "ppc.signalize.mira loaded");
        allLoaded();
    }

    private void pause(int milli) {

        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    public void allLoaded() {

        new AsyncMouth(_world, withPrompt).execute(_world.getString(R.string.all_loaded));
    }


    public void init(boolean prompt_when_done) {
        sendBroadcasttoService();
        withPrompt = prompt_when_done;

        new Thread(this).start();
    }

    public Context getApplicationContext() {
        return _world.getApplicationContext();
    }


    public Consideration consider(String s) {
        String raw = null;
        try {
            raw = service.process(s);
            JointClassification sent = null, sever = null;
            //JointClassification sent = Intuition.classify(s, Intuition.SENTIMENT);
            //JointClassification sever = Intuition.classify(s, Intuition.SEVERITY);
            return new Consideration(sent, sever, _world, raw, s);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG,"Remote Exception " + e.getCause());
        }

        return null;
    }

    @Deprecated
    public void listen() {
        listen("Hello");
    }
    public void listen(String greeting) {
        new AsyncMouth(_world, true).execute(greeting);
    }

    @Deprecated
    public void stop_listen() {
        stop_listen("Goodbye");
    }

    public void stop_listen(String closing) {
        new AsyncMouth(_world, false).execute(closing);
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder sBind) {

        service = IConversation.Stub.asInterface(sBind);
        Log.d(TAG,"The service is now connected");
        Toast.makeText(context, "The service is now connected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG,"Disconnected from the service");

    }


    public static String buildTopicTag(String s) {
        return s;
    }

}
