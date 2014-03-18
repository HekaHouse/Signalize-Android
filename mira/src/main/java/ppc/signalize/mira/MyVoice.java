package ppc.signalize.mira;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.io.File;

import ppc.signalize.mira.body.MiraAbstractActivity;
import ppc.signalize.mira.body.fragments.BrainFragment;
import ppc.signalize.mira.body.parts.brain.Brain;
import ppc.signalize.mira.body.parts.brain.Intuition;

/**
 * Created by Aron on 3/16/14.
 */
public class MyVoice extends UtteranceProgressListener  {
    private final MiraAbstractActivity mActive;

    private SharedPreferences access;
    private boolean mInit = true;
    public boolean isSpeechRecognitionServiceActive = false;
    private boolean mIsStreamSolo;
    private AudioManager mAudioManager;
    private boolean doneSpeaking = true;


    public MyVoice(MiraAbstractActivity a) {
        mActive = a;
        access = a.getPreferences(a.MODE_PRIVATE);
        mAudioManager = (AudioManager) mActive.getSystemService(mActive.AUDIO_SERVICE);
    }

    public Context getApplicationContext() {
        return mActive.getApplicationContext();
    }

    public String getString(int i) {
        return mActive.getString(i);
    }

    public void appendText(String recognized) {
    }

    public void sayAndPrompt(String string) {
    }

    public boolean hasBrainStore() {
        return false;
    }

    public void set_brainStore(BrainFragment frag) {

    }

    public Brain get_brainStore() {
        return null;
    }

    public File getLinguisticRepo() {
        mActive.getDir("ling", mActive.MODE_PRIVATE).mkdirs();
        return mActive.getDir("ling", mActive.MODE_PRIVATE);
    }

    public File getTrainingRepo() {
        new File(getLinguisticRepo().getPath() + "/train").mkdirs();
        return new File(getLinguisticRepo().getPath() + "/train");
    }

    public File getPolarTrainingRepo() {
        new File(getLinguisticRepo().getPath() + "/train/polarity").mkdirs();
        return new File(getLinguisticRepo().getPath() + "/train/polarity");
    }

    public File getSeverTrainingRepo() {
        new File(getLinguisticRepo().getPath() + "/train/severity").mkdirs();
        return new File(getLinguisticRepo().getPath() + "/train/severity");
    }

    public File getPolarClassifierRepo() {
        new File(getLinguisticRepo().getPath() + "/classify/polarity").mkdirs();
        return new File(getLinguisticRepo().getPath() + "/classify/polarity/"+ Intuition.POLAR_CLASSIFICATION_FILE);
    }
    public File getSevereClassifierRepo() {
        new File(getLinguisticRepo().getPath() + "/classify/severity").mkdirs();
        return new File(getLinguisticRepo().getPath() + "/classify/severity/"+ Intuition.SEVER_CLASSIFICATION_FILE);
    }

    public File getBrainRepo() {
        return mActive.getDir("bots", mActive.MODE_PRIVATE);
    }

    public SharedPreferences getAccess() {
        return access;
    }

    public FragmentManager getFragmentManager() {
        return mActive.getFragmentManager();
    }



    public AssetManager getAssets() {
        return mActive.getAssets();
    }

    public void startSpeechRecognitionService() {
        isSpeechRecognitionServiceActive=true;
        mutePrompt();
        mActive.startActivator();
    }


    public void stopSpeechRecognitionService() {
        isSpeechRecognitionServiceActive=false;
        mActive.stopActivator();
    }

    private void mutePrompt() {

        if (!mIsStreamSolo && doneSpeaking)
        {
            mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
            mIsStreamSolo = true;
        }
    }

    private void unmutePrompt() {
        if (mIsStreamSolo)
        {
            mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, false);
            mIsStreamSolo = false;
        }
    }

    public void speechCycle(String string) {

    }

    public String consider(String s) {
        return null;
    }


    public boolean initializing() {
        return mInit;
    }
    public boolean setInitFinished() {
        return mInit=false;
    }

    public void pause(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public TextToSpeech getTTS() {
        return mActive.tts;
    }



    /**
     * Called when an utterance "starts" as perceived by the caller. This will
     * be soon before audio is played back in the case of a {@link android.speech.tts.TextToSpeech#speak}
     * or before the first bytes of a file are written to storage in the case
     * of {@link android.speech.tts.TextToSpeech#synthesizeToFile}.
     *
     * @param utteranceId the utterance ID of the utterance.
     */
    @Override
    public void onStart(String utteranceId) {
        unmutePrompt();
        doneSpeaking = false;
    }



    /**
     * Called when an utterance has successfully completed processing.
     * All audio will have been played back by this point for audible output, and all
     * output will have been written to disk for file synthesis requests.
     * <p/>
     * This request is guaranteed to be called after {@link #onStart(String)}.
     *
     * @param utteranceId the utterance ID of the utterance.
     */
    @Override
    public void onDone(String utteranceId) {
        doneSpeaking = true;
    }

    /**
     * Called when an error has occurred during processing. This can be called
     * at any point in the synthesis process. Note that there might be calls
     * to {@link #onStart(String)} for specified utteranceId but there will never
     * be a call to both {@link #onDone(String)} and {@link #onError(String)} for
     * the same utterance.
     *
     * @param utteranceId the utterance ID of the utterance.
     */
    @Override
    public void onError(String utteranceId) {

    }

    public Mira getMira() {
        return mActive.mira;
    }
}
