package ppc.signalize.mira;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.io.File;

import ppc.signalize.mira.face.MiraAbstractActivity;
import ppc.signalize.mira.brain.Intuition;
import ppc.signalize.mira.nervous.concurrent.AsyncConsciousness;

/**
 * Created by Aron on 3/16/14.
 */
public class Voice extends UtteranceProgressListener {
    public boolean isSpeechRecognitionServiceActive = false;
    private MiraAbstractActivity maActive;
    private SharedPreferences access;
    //private boolean mInit = true;
    private boolean mIsStreamSolo;
    private AudioManager mAudioManager;
    private boolean doneSpeaking = true;


    public Voice(MiraAbstractActivity a) {
        maActive = a;
        access = a.getPreferences(a.MODE_PRIVATE);
        mAudioManager = (AudioManager) maActive.getSystemService(maActive.AUDIO_SERVICE);
    }



    public Context getApplicationContext() {
        return mActive().getApplicationContext();
    }

    private Mira mira() {

            return maActive.mira;
    }

    private Activity mActive() {

            return maActive;
    }

    private void startActivator() {

            maActive.startActivator();
    }

    private void stopActivator() {

            maActive.stopActivator();
    }

    private TextToSpeech tts() {

            return maActive.tts;
    }

    public String getString(int i) {
        return mActive().getString(i);
    }

    public void appendText(String words, int aligned) {
        if (maActive != null)
            new AsyncConsciousness(maActive, words, aligned).execute("");
    }

    /**
     * Changed by Mukundan on 7/9/14. Removed the brain functionality.
     * */

    /*public void sayAndPrompt(String string) {
    }

    public boolean hasBrainStore() {
        return false;
    }
*/


   /* public Brain get_brainStore() {
        return getMira()._brain;
    }

    public void set_brainStore(BrainFragment frag) {
        if (getMira() != null)
            getMira()._brain = frag.getData();
    }
*/
    public File getLinguisticRepo() {
        mActive().getDir("ling", mActive().MODE_PRIVATE).mkdirs();
        return mActive().getDir("ling", mActive().MODE_PRIVATE);
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
        return new File(getLinguisticRepo().getPath() + "/classify/polarity/" + Intuition.POLAR_CLASSIFICATION_FILE);
    }

    public File getSevereClassifierRepo() {
        new File(getLinguisticRepo().getPath() + "/classify/severity").mkdirs();
        return new File(getLinguisticRepo().getPath() + "/classify/severity/" + Intuition.SEVER_CLASSIFICATION_FILE);
    }
/*
    public AssetManager getBrainRepo() {
        return getApplicationContext().getAssets();
        //return mActive().getDir("bots", mActive().MODE_PRIVATE);
    }

    public File getBrainDepo() {
        return mActive().getDir("bots", mActive().MODE_PRIVATE);
    }

    public SharedPreferences getAccess() {
        return access;
    }

    public FragmentManager getFragmentManager() {
        return mActive().getFragmentManager();
    }
    */


    public AssetManager getAssets() {
        return mActive().getAssets();
    }

    public void startSpeechRecognitionService() {
        isSpeechRecognitionServiceActive = true;
        mutePrompt();

        startActivator();
    }


    public void stopSpeechRecognitionService() {
        isSpeechRecognitionServiceActive = false;
        stopActivator();
    }

    private void mutePrompt() {

        if (!mIsStreamSolo && doneSpeaking) {
            mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
            mIsStreamSolo = true;
        }
    }

    private void unmutePrompt() {
        if (mIsStreamSolo) {
            mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, false);
            mIsStreamSolo = false;
        }
    }
    /*
    public void speechCycle(String string) {

    }

    public String consider(String s) {
        return null;
    }


    public boolean initializing() {
        return mInit;
    }

    public boolean setInitFinished() {
        return mInit = false;
    }
    */

    public void pause(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public TextToSpeech getTTS() {
        return tts();
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
        return mira();
    }


    public boolean canListen() {
        if (maActive != null)
            return maActive.canListen();
        return true;
    }

    public boolean hasTTS() {
        if (maActive != null)
            return maActive.ttsLive;
        return true;
    }


    public void delegateOob(String oob) {

    }
}
