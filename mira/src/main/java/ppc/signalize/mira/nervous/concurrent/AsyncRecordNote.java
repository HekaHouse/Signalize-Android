package ppc.signalize.mira.nervous.concurrent;

import ppc.signalize.mira.R;
import ppc.signalize.mira.Voice;
import ppc.signalize.mira.face.MiraAbstractActivity;
import ppc.signalize.mira.brain.Consideration;

/**
 * Created by Aron on 11/5/14.
 */
public class AsyncRecordNote extends AsyncMouth {

    String spoken = "";

    public AsyncRecordNote(Voice mv) {
        super(mv, true);
    }

    @Override
    protected Long doInBackground(String... params) {
        for (String s : params) {
            if (s.length() > 0) {
                mWorld.appendText(s, MiraAbstractActivity.ALIGN_VOICE);
            }
        }

        return 0L;
    }

    @Override
    protected void onPostExecute(Long result) {
        new AsyncMouth(mWorld,true).execute(mWorld.getString(R.string.anything_else_to_say));
    }
}
