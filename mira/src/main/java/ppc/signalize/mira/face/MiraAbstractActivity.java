/*
 * Copyright 2011 Greg Milette and Adam Stroud
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ppc.signalize.mira.face;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import ppc.signalize.mira.Mira;
import ppc.signalize.mira.Voice;
import ppc.signalize.mira.nervous.MiraActivator;
import root.gast.speech.LanguageDetailsChecker;
import root.gast.speech.OnLanguageDetailsListener;
import root.gast.speech.SpeechRecognitionUtil;
import root.gast.speech.SpeechRecognizingActivity;
import root.gast.speech.activation.SpeechActivationListener;
import root.gast.speech.activation.SpeechActivator;
import root.gast.speech.tts.TextToSpeechInitializer;
import root.gast.speech.tts.TextToSpeechStartupListener;

/**
* Changed by Mukundan on 7/9/14. Connection to service through Mira Facade.
* */
public abstract class MiraAbstractActivity extends
        SpeechRecognizingActivity implements  TextToSpeechStartupListener, SpeechActivationListener {
    public static final String TAG = "MiraAbstractActivity";

    private static final String WAS_LISTENING_STATE = "WAS_LISTENING";
    public static int ALIGN_MIRA = 0;
    public static int ALIGN_VOICE = 1;
    public SpeechActivator speechActivator;
    public Voice myVoice;
    public Mira mira;
    public TextToSpeechInitializer ttsInit;
    public TextToSpeech tts;
    public String INITIATE = "Hello Mira";
    public String END_CONVERSATION = "Goodbye Mira";
    public boolean ttsLive = false;

    private boolean isListeningForActivation;

    private boolean wasListeningForActivation;
    public static boolean isDialogDisplayed=false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean recognizerIntent =
                SpeechRecognitionUtil.isSpeechAvailable(this);
        if (!recognizerIntent) {
            speechNotAvailable();
        }
        boolean direct = SpeechRecognizer.isRecognitionAvailable(this);
        if (!direct) {
            directSpeechNotAvailable();
        }
        myVoice = new Voice(this);
        isListeningForActivation = false;
        speechActivator = new MiraActivator(myVoice, this, new String[]{INITIATE, END_CONVERSATION});
        mira = new Mira(myVoice);


    }

    @Override
    public void onResume() {
        super.onResume();

        boolean recognizerIntent = SpeechRecognitionUtil.isSpeechAvailable(this);
        if (!recognizerIntent) {
            speechNotAvailable();
        }
        boolean direct = SpeechRecognizer.isRecognitionAvailable(this);
        if (!direct) {
            directSpeechNotAvailable();
        }
        myVoice = new Voice(this);
        isListeningForActivation = false;
        speechActivator = new MiraActivator(myVoice, this, new String[]{INITIATE, END_CONVERSATION});
        mira = new Mira(myVoice);
    }

    public abstract Spannable createSpan(String words, int aligned);

    public abstract void prependMiraText(String toSpan, int aligned);

    public void init() {
        ttsInit = new TextToSpeechInitializer(this, Locale.getDefault(), this);

    }

    @Override
    public void onPause() {
        super.onPause();
        mira.writeAIMLOut();
        tts.stop();
        tts.shutdown();
    }

    @Override
    public void onSuccessfulInit(TextToSpeech tts) {
        Log.d(TAG, "successful init");
        ttsLive = true;
        this.tts = tts;
        setTtsListener();
        mira.init(true);
    }

    private void setTtsListener() {
        final MiraAbstractActivity callWithResult = this;
        if (Build.VERSION.SDK_INT >= 15) {
            int listenerResult = tts.setOnUtteranceProgressListener(myVoice);
            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance progress listener");
            }
        }

    }

    //sub class may override these, otherwise, one or the other
    //will occur depending on the Android version
    public void onDone(String utteranceId) {
        Log.d(TAG, "done speaking");
    }

    public void onError(String utteranceId) {
    }

    public void onStart(String utteranceId) {

    }

    @Override
    public void onFailedToInit() {
        DialogInterface.OnClickListener onClickOk = makeOnFailedToInitHandler();
        AlertDialog a = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Unable to create text to speech")
                .setNeutralButton("Ok", onClickOk).create();
        a.show();
    }


    @Override
    public void onRequireLanguageData() {
        DialogInterface.OnClickListener onClickOk = makeOnClickInstallDialogListener();
        DialogInterface.OnClickListener onClickCancel = makeOnFailedToInitHandler();
        AlertDialog a = new AlertDialog.Builder(this).setTitle(
                "Error")
                .setMessage("Requires Language data to proceed, would you like to install?")
                .setPositiveButton("Ok", onClickOk)
                .setNegativeButton("Cancel", onClickCancel).create();
        a.show();
    }

    @Override
    public void onWaitingForLanguageData() {
        //either wait for install
        DialogInterface.OnClickListener onClickWait = makeOnFailedToInitHandler();
        DialogInterface.OnClickListener onClickInstall = makeOnClickInstallDialogListener();

        AlertDialog a = new AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage(
                        "Please wait for the language data to finish installing and try again.")
                .setNegativeButton("Wait", onClickWait)
                .setPositiveButton("Retry", onClickInstall).create();
        a.show();
    }

    private DialogInterface.OnClickListener makeOnClickInstallDialogListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ttsInit.installLanguageData();
            }
        };
    }

    private DialogInterface.OnClickListener makeOnFailedToInitHandler() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
    }

    protected void checkForLanguage(final Locale language) {
        OnLanguageDetailsListener andThen = new OnLanguageDetailsListener() {
            @Override
            public void onLanguageDetailsReceived(LanguageDetailsChecker data) {
                // do a best match
                String languageToUse = data.matchLanguage(language);
                languageCheckResult(languageToUse);
            }
        };
        SpeechRecognitionUtil.getLanguageDetails(this, andThen);
    }

    /**
     * execute the RecognizerIntent, then call
     * might throw a {@link android.content.ActivityNotFoundException} if the
     * device cannot respond to the Intent
     */
    public void recognize(Intent recognizerIntent) {
        startActivityForResult(recognizerIntent,
                VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * Handle the results from the RecognizerIntent.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                List<String> heard =
                        data.
                                getStringArrayListExtra
                                        (RecognizerIntent.EXTRA_RESULTS);
                float[] scores =
                        data.
                                getFloatArrayExtra
                                        (RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                if (scores == null) {
                    for (int i = 0; i < heard.size(); i++) {
                        Log.d(TAG, i + ": " + heard.get(i));
                    }
                } else {
                    for (int i = 0; i < heard.size(); i++) {
                        Log.d(TAG, i + ": " + heard.get(i) + " score: "
                                + scores[i]);
                    }
                }

                receiveWhatWasHeard(heard, scores);
            } else {
                Log.d(TAG, "error code: " + resultCode);
                recognitionFailure(UNKNOWN_ERROR);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    protected void speechNotAvailable() {

    }


    protected void directSpeechNotAvailable() {
        //do nothing
    }

    protected void languageCheckResult(String languageToUse) {
        // not used
    }

    /**
     * result of speech recognition
     *
     * @param heard            possible speech to text conversions
     * @param confidenceScores
     */

    protected void receiveWhatWasHeard(List<String> heard, float[] confidenceScores) {

    }

    protected void recognitionFailure(int errorCode) {
        String message = SpeechRecognitionUtil.diagnoseErrorCode(errorCode);
        Log.d(TAG, "speech error: " + message);
    }


    public void startActivator() {
        if (isListeningForActivation) {

            Log.d(TAG, "activator not started, already started");
            // only activate once
            return;
        }

        if (speechActivator != null) {


            isListeningForActivation = true;


            Log.d(TAG, "activator started");
            speechActivator.detectActivation();
        }
    }

    protected abstract void startListening();

    public void stopActivator() {
        if (speechActivator != null) {
            Log.d(TAG, "activator stopped");
            speechActivator.stop();
        }
        isListeningForActivation = false;
    }

    protected TextToSpeech getTts() {
        return tts;
    }


    //direct speech recognition methods follow

    /**
     * Uses {@link android.speech.SpeechRecognizer} to perform recognition and then calls
     * {@link #receiveWhatWasHeard(java.util.List, float[])} with the results <br>
     * calling this method otherwise if it isn't available the code will report
     * an error
     */
    public void recognizeDirectly(Intent recognizerIntent) {
        // SpeechRecognizer requires EXTRA_CALLING_PACKAGE, so add if it's not
        // here
        if (!recognizerIntent.hasExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE)) {
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    "com.content");
        }
        //SpeechRecognizer recognizer = getSpeechRecognizer();
        //recognizer.startListening(recognizerIntent);
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "full results");
        receiveResults(results);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "partial results");
        receiveResults(partialResults);
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
        }
    }

    @Override
    public void onError(int errorCode) {
        recognitionFailure(errorCode);
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

    public void onPartialResultsUnsupported(Bundle partialResults) {
        Log.d(TAG, "partial results");
        if (partialResults
                .containsKey(SpeechRecognitionUtil.UNSUPPORTED_GOOGLE_RESULTS)) {
            String[] heard =
                    partialResults
                            .getStringArray(SpeechRecognitionUtil.UNSUPPORTED_GOOGLE_RESULTS);
            float[] scores =
                    partialResults
                            .getFloatArray(SpeechRecognitionUtil.UNSUPPORTED_GOOGLE_RESULTS_CONFIDENCE);
            receiveWhatWasHeard(Arrays.asList(heard), scores);
        } else {
            receiveResults(partialResults);
        }
    }

    @Override
    public void activated(boolean success) {
        Log.d(TAG, "activated...");

        //don't allow multiple activations
        if (!isListeningForActivation) {
            Toast.makeText(this, "Not activated because stopped",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (success) {
            Toast.makeText(this, "Activated, no longer listening",
                    Toast.LENGTH_SHORT).show();
            //start speech recognition here
        } else {
            Toast.makeText(this, "activation failed, no longer listening",
                    Toast.LENGTH_SHORT).show();
        }

        isListeningForActivation = false;
    }

    public abstract boolean canListen();

    public abstract void displayDialog();
}
