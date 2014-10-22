package ppc.signalize.mira.nervous.concurrent;

import ppc.signalize.mira.Voice;
import ppc.signalize.mira.face.MiraAbstractActivity;
import ppc.signalize.mira.brain.Consideration;

/**
 * Created by Aron on 3/17/14.
 */
public class AsyncMiraResponse extends AsyncMouth {

    String spoken = "";

    public AsyncMiraResponse(Voice mv) {
        super(mv, true);
    }

    @Override
    protected Long doInBackground(String... params) {
        for (String s : params) {
            if (s.length() > 0) {
                mWorld.appendText(s, MiraAbstractActivity.ALIGN_VOICE);
                Consideration considered = mWorld.getMira().consider(s);
                spoken = speechCycle(considered.mResponse);
            }
        }
        return 0L;
    }

}
