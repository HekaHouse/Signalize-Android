package ppc.signalize.mira.nervous.concurrent;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;

import ppc.signalize.mira.Voice;
import ppc.signalize.mira.R;
import ppc.signalize.mira.face.MiraAbstractActivity;

/**
 * Created by Aron on 3/17/14.
 */
public class AsyncMouth extends AsyncTask<String, Integer, Long> {
    private static final String TAG = "AsyncMouth";
    private static boolean speech_cycle_active = false;
    public final boolean withPrompt;
    protected final Voice mWorld;

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    public AsyncMouth(Voice mv, boolean p) {
        mWorld = mv;
        withPrompt = p;
    }

    @Override
    protected void onPreExecute() {
        if (mWorld.isSpeechRecognitionServiceActive)
            new AsyncEarCloser(mWorld).execute(mWorld.getString(R.string.all_loaded));
    }

    @Override
    protected Long doInBackground(String... params) {
        for (String s : params) {
            speechCycle(s);
        }
        return 0L;
    }

    public String speechCycle(String considered) {

        while (!mWorld.hasTTS() || mWorld.getTTS().isSpeaking() || speech_cycle_active) {
            mWorld.pause(10);
        }
        speech_cycle_active = true;
        Log.d(TAG, "begin speech cycle");
        HashMap params = new HashMap();
        //AudioManager.STREAM_MUSIC
        params.put("streamType", "3");
        params.put("utteranceId", "MIR_Response");
        params.put("embeddedTts", "true");


        mWorld.appendText(considered, MiraAbstractActivity.ALIGN_MIRA);

        mWorld.getTTS().speak(considered, TextToSpeech.QUEUE_FLUSH, params);

        while (mWorld.getTTS().isSpeaking()) {
            mWorld.pause(10);
        }

        Log.d(TAG, "end speech cycle");
        mWorld.pause(500);
        speech_cycle_active = false;
        return considered;
    }

    public String speechCycle(String considered,String oob) {


        if (oob != null) {
            mWorld.delegateOob(oob);
        }
        while (!mWorld.hasTTS() || mWorld.getTTS().isSpeaking() || speech_cycle_active) {
            mWorld.pause(10);
        }
        speech_cycle_active = true;
        Log.d(TAG, "begin speech cycle");
        HashMap params = new HashMap();
        //AudioManager.STREAM_MUSIC
        params.put("streamType", "3");
        params.put("utteranceId", "MIR_Response");
        params.put("embeddedTts", "true");


        mWorld.appendText(considered, MiraAbstractActivity.ALIGN_MIRA);

        mWorld.getTTS().speak(considered, TextToSpeech.QUEUE_FLUSH, params);

        while (mWorld.getTTS().isSpeaking()) {
            mWorld.pause(10);
        }

        Log.d(TAG, "end speech cycle");
        mWorld.pause(500);
        speech_cycle_active = false;
        return considered;
    }

    @Override
    protected void onPostExecute(Long result) {
        if (withPrompt)
            new AsyncEarOpener(mWorld).execute(mWorld.getString(R.string.all_loaded));
    }
}
