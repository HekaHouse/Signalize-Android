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
package ppc.signalize.mira.body;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ppc.signalize.mira.Mira;
import ppc.signalize.mira.MyVoice;
import root.gast.speech.LanguageDetailsChecker;
import root.gast.speech.OnLanguageDetailsListener;
import root.gast.speech.SpeechRecognitionUtil;
import root.gast.speech.activation.SpeechActivator;
import root.gast.speech.tts.TextToSpeechInitializer;

/**
 * copied from: SpeechRecognizerActivity
 * handy {@link android.app.Activity} that handles setting up both tts and speech for easy re-use
 * @author Greg Milette &#60;<a href="mailto:gregorym@gmail.com">gregorym@gmail.com</a>&#62;
 */
public abstract class MiraAbstractFragmentActivity extends FragmentActivity implements
        RecognitionListener
{
    private static final String TAG = "MiraAbstractFragmentActivity";

    /**
     * code to identify return recognition results
     */
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    public static final int UNKNOWN_ERROR = -1;

    private SpeechRecognizer recognizer;


    /**
     * store if currently listening
     */
    private boolean isListeningForActivation;

    /**
     * if paused, store what was happening so that onResume can restart it
     */
    private boolean wasListeningForActivation;


    /**
     * for saving {@link #wasListeningForActivation}
     * in the saved instance state
     */
    private static final String WAS_LISTENING_STATE = "WAS_LISTENING";


    public SpeechActivator speechActivator;
    public MyVoice myVoice;
    public Mira mira;
    public TextToSpeechInitializer ttsInit;
    public TextToSpeech tts;

    public String INITIATE = "Hello MiraActivity";
    public String END_CONVERSATION = "Goodbye MiraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        boolean recognizerIntent =
                SpeechRecognitionUtil.isSpeechAvailable(this);
        if (!recognizerIntent)
        {
            speechNotAvailable();
        }
        boolean direct = SpeechRecognizer.isRecognitionAvailable(this);
        if (!direct)
        {
            directSpeechNotAvailable();
        }
    }

    protected void checkForLanguage(final Locale language)
    {
        OnLanguageDetailsListener andThen = new OnLanguageDetailsListener()
        {
            @Override
            public void onLanguageDetailsReceived(LanguageDetailsChecker data)
            {
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
    public void recognize(Intent recognizerIntent)
    {
        startActivityForResult(recognizerIntent,
                VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * Handle the results from the RecognizerIntent.
     */
    @Override
    protected void
    onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                List<String> heard =
                        data.
                                getStringArrayListExtra
                                        (RecognizerIntent.EXTRA_RESULTS);
                float[] scores =
                        data.
                                getFloatArrayExtra
                                        (RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                if (scores == null)
                {
                    for (int i = 0; i < heard.size(); i++)
                    {
                        Log.d(TAG, i + ": " + heard.get(i));
                    }
                }
                else
                {
                    for (int i = 0; i < heard.size(); i++)
                    {
                        Log.d(TAG, i + ": " + heard.get(i) + " score: "
                                + scores[i]);
                    }
                }

                receiveWhatWasHeard(heard, scores);
            }
            else
            {
                Log.d(TAG, "error code: " + resultCode);
                recognitionFailure(UNKNOWN_ERROR);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    protected void speechNotAvailable()
    {

    }


    protected void directSpeechNotAvailable()
    {
        //do nothing
    }

    protected void languageCheckResult(String languageToUse)
    {
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

    protected void recognitionFailure(int errorCode)
    {
        String message = SpeechRecognitionUtil.diagnoseErrorCode(errorCode);
        Log.d(TAG, "speech error: " + message);
    }


    public void startActivator()
    {
        if (isListeningForActivation)
        {

            Log.d(TAG, "activator not started, already started");
            // only activate once
            return;
        }

        if (speechActivator != null)
        {
            isListeningForActivation = true;


            Log.d(TAG, "activator started");
            speechActivator.detectActivation();
        }
    }

    public void stopActivator()
    {
        if (speechActivator != null)
        {
            Log.d(TAG, "activator stopped");
            speechActivator.stop();
        }
        isListeningForActivation = false;
    }

    protected TextToSpeech getTts()
    {
        return tts;
    }



    //direct speech recognition methods follow

    /**
     * Uses {@link android.speech.SpeechRecognizer} to perform recognition and then calls
     * {@link #receiveWhatWasHeard(java.util.List, float[])} with the results <br>
     * calling this method otherwise if it isn't available the code will report
     * an error
     */
    public void recognizeDirectly(Intent recognizerIntent)
    {
        // SpeechRecognizer requires EXTRA_CALLING_PACKAGE, so add if it's not
        // here
        if (!recognizerIntent.hasExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE))
        {
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    "com.dummy");
        }
        SpeechRecognizer recognizer = getSpeechRecognizer();
        recognizer.startListening(recognizerIntent);
    }

    @Override
    public void onResults(Bundle results)
    {
        Log.d(TAG, "full results");
        receiveResults(results);
    }

    @Override
    public void onPartialResults(Bundle partialResults)
    {
        Log.d(TAG, "partial results");
        receiveResults(partialResults);
    }

    /**
     * common method to process any results bundle from {@link android.speech.SpeechRecognizer}
     */
    private void receiveResults(Bundle results)
    {
        if ((results != null)
                && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION))
        {
            List<String> heard =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            float[] scores =
                    results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
            receiveWhatWasHeard(heard, scores);
        }
    }

    @Override
    public void onError(int errorCode)
    {
        recognitionFailure(errorCode);
    }

    /**
     * stop the speech recognizer
     */
    @Override
    protected void onPause()
    {
        if (getSpeechRecognizer() != null)
        {
            getSpeechRecognizer().stopListening();
            getSpeechRecognizer().cancel();
            getSpeechRecognizer().destroy();
        }
        super.onPause();
    }

    /**
     * lazy initialize the speech recognizer
     */
    private SpeechRecognizer getSpeechRecognizer()
    {
        if (recognizer == null)
        {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(this);
        }
        return recognizer;
    }

    // other unused methods from RecognitionListener...

    @Override
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(TAG, "ready for speech " + params);
    }

    @Override
    public void onEndOfSpeech()
    {
    }

    /**
     * @see android.speech.RecognitionListener#onBeginningOfSpeech()
     */
    @Override
    public void onBeginningOfSpeech()
    {
    }

    @Override
    public void onBufferReceived(byte[] buffer)
    {
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {
    }

    @Override
    public void onEvent(int eventType, Bundle params)
    {
    }

    public void onPartialResultsUnsupported(Bundle partialResults)
    {
        Log.d(TAG, "partial results");
        if (partialResults
                .containsKey(SpeechRecognitionUtil.UNSUPPORTED_GOOGLE_RESULTS))
        {
            String[] heard =
                    partialResults
                            .getStringArray(SpeechRecognitionUtil.UNSUPPORTED_GOOGLE_RESULTS);
            float[] scores =
                    partialResults
                            .getFloatArray(SpeechRecognitionUtil.UNSUPPORTED_GOOGLE_RESULTS_CONFIDENCE);
            receiveWhatWasHeard(Arrays.asList(heard), scores);
        }
        else
        {
            receiveResults(partialResults);
        }
    }
}