package ppc.signalize.mira.body.parts.nervous.concurrent;

import android.os.AsyncTask;

import ppc.signalize.mira.MyVoice;

/**
 * Created by Aron on 3/17/14.
 */
public class AsyncEarOpener extends AsyncTask<String,Integer,Long> {
    private final MyVoice mWorld;

    public AsyncEarOpener(MyVoice mv) {

        mWorld = mv;


    }
    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Long doInBackground(String... params) {
        return null;
    }
    @Override
    protected void onPostExecute(Long result) {

        mWorld.startSpeechRecognitionService();
    }
}
