package ppc.signalize.mira.body.parts.brain;

import android.util.Log;

import org.alicebot.ab.Chat;


/**
 * Created by Aron on 3/8/14.
 */
public class Brain  {
    private static final long serialVersionUID = 0L;
    private static final String TAG = "brain";
    private String _path;
    private Chat _session;
    private boolean isLoaded=false;
    private static String _name = "";
    private Ghost _ghost;
    private int count_down=0;
    private String considered="";

    public boolean is_labelling() {
        if (count_down > 0 && _is_labelling) {
            count_down--;
            if (count_down == 0)
                _is_labelling = false;
        }

        return _is_labelling;
    }

    public void set_labelling(boolean b) {
        if (b)
            count_down = 10;
        this._is_labelling = b;
    }

    private boolean _is_labelling;

    public Brain(String name, String path) {
        _name = name;
        _path = path;
    }



    public void init() {
        Log.d(TAG, "init brain");
        _ghost = new Ghost(_name,_path);
        _session = new Chat(_ghost);
        isLoaded = true;
    }

    public boolean isLoaded() {
            return isLoaded;
    }


    
    public String process(String input) {
        if (considered.length() > 0)
            input = considered +" "+ input;
        return _session.multisentenceRespond(input);
    }
}
