package ppc.signalize.mira.brain;

import android.text.SpannableString;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aron on 3/30/2014.
 * A part of Signalize for Project Patient Care
 * This class is the foundation for flagging content to be reviewed.
 * Salience is the essence which attracts attention
 */

public class Salience extends ArrayList<String> {

    public Pattern salient_pattern;


    public ArrayList<String> flag(SpannableString item) {
        ArrayList<String> flags = new ArrayList<String>();
        String is = String.valueOf(item.subSequence(0, item.length()));

        Matcher m = salient_pattern.matcher(is);
        while (m.find()) {
            flags.add(m.group());
        }
        return flags;
    }
}
