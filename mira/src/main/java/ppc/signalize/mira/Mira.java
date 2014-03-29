package ppc.signalize.mira;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.aliasi.classify.JointClassification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ppc.signalize.mira.body.fragments.BrainFragment;
import ppc.signalize.mira.body.parts.brain.Brain;
import ppc.signalize.mira.body.parts.brain.Consideration;
import ppc.signalize.mira.body.parts.brain.Intuition;
import ppc.signalize.mira.body.parts.nervous.concurrent.AsyncMouth;


/**
 * Created by Aron on 3/7/14.
 */
public class Mira implements Runnable {

    private static final String TAG = "ppc/signalize/mira";
    public MyVoice _world;
    public boolean _is_awake;
    public int _result;
    public Brain _brain;
    protected String _name = "MIRA";
    protected String _AIML_path;
    private int _sleeping_check = 0;
    private boolean withPrompt;

    public Mira(MyVoice c) {
        _world = c;


        File bots = _world.getBrainRepo();
        if (bots.listFiles().length < 1) {
            loadAIML();
        }
        _AIML_path = bots.getAbsolutePath();


    }

    private Brain createBrain() {
        // if we have not stored our brain make a new one
        if (!_world.hasBrainStore()) {
            FragmentManager fm = _world.getFragmentManager();
            BrainFragment frag = new BrainFragment();
            fm.beginTransaction().add(frag, "data").commit();
            _brain = new Brain(_name, _AIML_path);
            frag.setData(_brain);
            _world.set_brainStore(frag);
        }
        // restore previous brain
        else {
            _brain = _world.get_brainStore();
        }
        return _brain;
    }

    private void loadAIML() {
        AssetManager am = _world.getAssets();
        try {
            ZipInputStream is = new ZipInputStream(am.open("aiml.zip"));
            String out = _world.getBrainRepo().getPath();

            new File(out + "/bots/MIRA/aimlif/").mkdirs();

            byte[] buffer = new byte[2048];
            try {

                // now iterate through each item in the stream. The get next
                // entry call will return a ZipEntry for each file in the
                // stream
                ZipEntry entry;
                while ((entry = is.getNextEntry()) != null) {
                    String s = String.format("Entry: %s len %d added %TD",
                            entry.getName(), entry.getSize(),
                            new Date(entry.getTime()));

                    String fallout = out + "/bots/MIRA/" + entry.getName();
                    if (entry.isDirectory()) {
                        new File(fallout + "/").mkdir();
                        continue;
                    }

                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(fallout);
                        int len = 0;
                        while ((len = is.read(buffer)) > 0) {
                            output.write(buffer, 0, len);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // we must always close the output file
                        if (output != null) output.close();
                    }
                }
            } finally {
                // we must always close the zip file.
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        new Intuition(false, _world);
        Log.d(TAG, "run ppc.signalize.mira");
        _brain.init();
        _is_awake = true;
        Log.d(TAG, "ppc.signalize.mira loaded");
        allLoaded();
    }

    public void stillLoading() {
//        _sleeping_check = _sleeping_check + 1;
//        if (_sleeping_check < 2)
//            _mouth.speak("still having my morning coffee, give me a minute to get my brain back");
//        else if (_sleeping_check < 5)
//            _mouth.speak("sorry just a bit longer");
//        else if (_sleeping_check == 5)
//            _mouth.speak("Hey! back off! I will be ready shortly");
//        else
//            _mouth.speak("still loading my brain");
    }

    public void missedThat() {

        //_mouth.speak("sorry I missed that, feel free to try again");
    }

    public void allLoaded() {

        new AsyncMouth(_world, withPrompt).execute(_world.getString(R.string.all_loaded));
    }


    public void init(boolean prompt_when_done) {
        withPrompt = prompt_when_done;
        _brain = createBrain();
        new Thread(this).start();
    }

    public Context getApplicationContext() {
        return _world.getApplicationContext();
    }


    public Consideration consider(String s) {
        String raw = _brain.process(s);
        JointClassification sent = Intuition.classify(s, Intuition.SENTIMENT);
        JointClassification sever = Intuition.classify(s, Intuition.SEVERITY);
        return new Consideration(sent, sever, _world, raw, s);
    }

    public void listen() {
        new AsyncMouth(_world, true).execute("Hello");
    }

    public void stop_listen() {
        new AsyncMouth(_world, false).execute("Goodbye");
    }
}
