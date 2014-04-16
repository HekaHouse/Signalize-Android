package ppc.signalize.perspectives.content;

import android.os.AsyncTask;
import android.text.SpannableString;
import android.widget.TextView;

import ppc.signalize.perspectives.PerspectiveListActivity;
import ppc.signalize.perspectives.R;
import ppc.signalize.perspectives.content.data.types.FeedbackData;

/**
 * Created by Aron on 3/26/14.
 */
public class AsyncAppendTask extends AsyncTask<String, Integer, Long> {
    private final FeedbackData feedback;
    private PerspectiveListActivity active;
    private SpannableString append;

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     */
    public AsyncAppendTask(PerspectiveListActivity a, SpannableString ap, FeedbackData f) {
        active = a;
        append = ap;
        feedback = f;
    }

    @Override
    protected Long doInBackground(String... params) {
        append = new ClickableImageSpan(feedback, append, (TextView) active.findViewById(R.id.commentary), active.getResources(), ClickableImageSpan.CALL_BUTTON).getSpan();
        append = new ClickableImageSpan(feedback, append, (TextView) active.findViewById(R.id.commentary), active.getResources(), ClickableImageSpan.WRITE_BUTTON).getSpan();
        append = new ClickableImageSpan(feedback, append, (TextView) active.findViewById(R.id.commentary), active.getResources(), ClickableImageSpan.CALENDAR_BUTTON).getSpan();
        return 0L;
    }

    @Override
    protected void onPostExecute(Long result) {
        active.getActiveFragment().setText(append);
    }
}
