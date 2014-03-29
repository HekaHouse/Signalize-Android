package ppc.signalize.mira.body.parts.nervous.concurrent;

import ppc.signalize.mira.MyVoice;
import ppc.signalize.mira.body.MiraAbstractActivity;
import ppc.signalize.mira.body.parts.brain.Consideration;

/**
 * Created by Aron on 3/17/14.
 */
public class AsyncMiraTextResponse extends AsyncMouth {


    public AsyncMiraTextResponse(MyVoice mv) {
        super(mv, true);
    }

    @Override
    protected Long doInBackground(String... params) {
        for (String s : params) {
            if (s.length() > 0) {
                mWorld.appendText(s, MiraAbstractActivity.ALIGN_VOICE);
                Consideration considered = mWorld.getMira().consider(s);
                mWorld.appendText(considered.mResponse, MiraAbstractActivity.ALIGN_MIRA);
            }
        }
        return 0L;
    }
}
