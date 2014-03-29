package ppc.signalize.perspectives.content;

import android.os.AsyncTask;
import android.text.Spannable;

import ppc.signalize.perspectives.PerspectiveListActivity;

/**
 * Created by Aron on 3/26/14.
 */
public class AsyncAppendTask extends AsyncTask<String, Integer, Long> {
    private PerspectiveListActivity active;
    private Spannable append;

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     */
    public AsyncAppendTask(PerspectiveListActivity a, Spannable ap) {
        active = a;
        append = ap;

    }

    @Override
    protected Long doInBackground(String... params) {
        return 0L;
    }

    @Override
    protected void onPostExecute(Long result) {
        active.getActiveFragment().setText(append);
    }
}
