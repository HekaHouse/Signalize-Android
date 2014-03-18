package ppc.signalize.mira.body.parts.nervous.concurrent;

import ppc.signalize.mira.MyVoice;
import ppc.signalize.mira.body.parts.brain.Consideration;

/**
 * Created by Aron on 3/17/14.
 */
public class AsyncMiraResponse extends AsyncMouth {

    public AsyncMiraResponse(MyVoice mv) {
        super(mv);
    }

    @Override
    protected Long doInBackground(String... params) {
        for (String s: params) {
            if (s.length() > 0) {
                Consideration considered = mWorld.getMira().consider(s);
                speechCycle(considered.mResponse);
            }
        }
        return 0L;
    }
}
