package ppc.signalize.mira.nervous;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.List;

import ppc.signalize.mira.Voice;
import ppc.signalize.mira.nervous.concurrent.AsyncMiraResponse;
import ppc.signalize.mira.nervous.concurrent.AsyncRecordNote;
import ppc.signalize.mira.util.text.MiraWordList;
import root.gast.speech.SpeechRecognitionUtil;
import root.gast.speech.activation.SpeechActivationListener;
import root.gast.speech.activation.SpeechActivator;
import root.gast.speech.text.match.SoundsLikeWordMatcher;

/**
 * Created by Aron on 3/15/14.
 */
public class MiraActivator implements SpeechActivator, RecognitionListener {
    private static final String TAG = "WordActivator";
    public SpeechRecognizer recognizer;
    private Voice context;
    private SoundsLikeWordMatcher matcher;
    private Class mService;
    private SpeechActivationListener resultListener;
    private boolean activation = false;
    private boolean noting;

    public MiraActivator(Voice context, SpeechActivationListener resultListener, String... targetWords) {
        this.context = context;
        this.matcher = new SoundsLikeWordMatcher(targetWords);
        this.resultListener = resultListener;
    }

    @Override
    public void detectActivation() {
        recognizeSpeechDirectly();
    }

    private void recognizeSpeechDirectly() {
        Intent recognizerIntent =
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // accept partial results if they come
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        SpeechRecognitionUtil.recognizeSpeechDirectly(context.getApplicationContext(),
                recognizerIntent, this, getSpeechRecognizer());
    }

    public void stop() {
        if (getSpeechRecognizer() != null) {
            getSpeechRecognizer().stopListening();
            getSpeechRecognizer().cancel();
            getSpeechRecognizer().destroy();
            recognizer = null;
        }
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "full results");
        receiveResults(results);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "partial results");
        //receivePartialResults(partialResults);
    }

    /**
     * common method to process any results bundle from {@link android.speech.SpeechRecognizer}
     */
    private void receiveResults(Bundle results) {
        if ((results != null)
                && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            float[] scores =
                    results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
            receiveWhatWasHeard(heard, scores);
        } else {
            Log.d(TAG, "no results");
        }
    }

    private void receiveWhatWasHeard(List<String> heard, float[] scores) {

        boolean heardTargetWord = false;
        // find the target word
        String recognized = heard.get(0);
        Log.d(TAG, "processing " + recognized);


        if (!context.isNoting()) {
            Log.d(TAG, "making considerate response");
            new AsyncMiraResponse(context,activation).execute(recognized);
        } else if (context.isNoting()) {
            Log.d(TAG, "recording a note");
            new AsyncRecordNote(context).execute(recognized);
        }
    }

    @Override
    public void onError(int errorCode) {
        if ((errorCode == SpeechRecognizer.ERROR_NO_MATCH)
                || (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {
            Log.d(TAG, "didn't recognize anything");
            // keep going
            recognizeSpeechDirectly();
        } else {
            Log.d(TAG,
                    "FAILED "
                            + SpeechRecognitionUtil
                            .diagnoseErrorCode(errorCode)
            );
        }
    }

    /**
     * lazy initialize the speech recognizer
     */
    private SpeechRecognizer getSpeechRecognizer() {
        if (recognizer == null) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context.getApplicationContext());
        }
        return recognizer;
    }

    // other unused methods from RecognitionListener...

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "ready for speech " + params);
    }

    @Override
    public void onEndOfSpeech() {
    }

    /**
     * @see android.speech.RecognitionListener#onBeginningOfSpeech()
     */
    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }
}
