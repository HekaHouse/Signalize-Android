package ppc.signalize.perspectives.content;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import ppc.signalize.perspectives.R;
import ppc.signalize.perspectives.content.data.types.FeedbackData;

/**
 * Created by aron on 4/6/14.
 */
public class ClickableImageSpan extends ClickableSpan {
    public static final int CALL_BUTTON = 0;
    public static final int WRITE_BUTTON = 4;
    public static final int CALENDAR_BUTTON = 8;
    private final FeedbackData feedback;
    private final int _click;
    private SpannableString spannable;

    public ClickableImageSpan(FeedbackData f, SpannableString s, TextView t, Resources r, int click_type) {
        super();
        spannable = new SpannableString(s);
        feedback = f;

        _click = click_type;
        int id = getId(click_type);

        Drawable d = r.getDrawable(id);
        d.setBounds(0, 0, t.getLineHeight(), t.getLineHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(span, _click, _click + 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(this, _click, _click + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    private int getId(int click_type) {
        switch (click_type) {
            case CALL_BUTTON: {
                return R.drawable.telephone_;
            }
            case WRITE_BUTTON: {
                return R.drawable.pen_;
            }
            case CALENDAR_BUTTON: {
                return R.drawable.calendar_;
            }
        }
        return -1;
    }

    public SpannableString getSpan() {
        return spannable;
    }

    @Override
    public void onClick(View view) {
        if (_click == CALL_BUTTON) {
            //launch voices
        } else if (_click == WRITE_BUTTON) {
            //launch voices
        } else if (_click == CALENDAR_BUTTON) {
            //launch voices
        }
    }
}
