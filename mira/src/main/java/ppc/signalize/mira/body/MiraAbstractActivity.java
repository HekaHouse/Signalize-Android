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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import ppc.signalize.mira.Mira;
import ppc.signalize.mira.MyVoice;
import ppc.signalize.mira.body.parts.nervous.activator.MiraActivator;
import root.gast.speech.SpeechRecognitionUtil;
import root.gast.speech.SpeechRecognizingActivity;
import root.gast.speech.activation.SpeechActivationListener;
import root.gast.speech.activation.SpeechActivator;
import root.gast.speech.tts.TextToSpeechInitializer;
import root.gast.speech.tts.TextToSpeechStartupListener;

/**
 * handy {@link android.app.Activity} that handles setting up both tts and speech for easy re-use
 * @author Greg Milette &#60;<a href="mailto:gregorym@gmail.com">gregorym@gmail.com</a>&#62;
 */
public abstract class MiraAbstractActivity extends
        SpeechRecognizingActivity implements TextToSpeechStartupListener, SpeechActivationListener {
    private static final String TAG = "MiraAbstractActivity";

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
    /**
     * @see root.gast.speech.SpeechRecognizingActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        myVoice = new MyVoice(this);
        isListeningForActivation = false;
        speechActivator = new MiraActivator(myVoice, this, new String[] {INITIATE,END_CONVERSATION});
        mira = new Mira(myVoice);
        ttsInit = new TextToSpeechInitializer(this, Locale.getDefault(), this);
    }


    @Override
    public void onSuccessfulInit(TextToSpeech tts)
    {
        Log.d(TAG, "successful init");
        this.tts = tts;
        activateUi();
        setTtsListener();
    }
    
    private void setTtsListener()
    {
        final MiraAbstractActivity callWithResult = this;
        if (Build.VERSION.SDK_INT >= 15)
        {
            int listenerResult = tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
            {
                @Override
                public void onDone(String utteranceId)
                {
                    callWithResult.onDone(utteranceId);
                }

                @Override
                public void onError(String utteranceId)
                {
                    callWithResult.onError(utteranceId);
                }

                @Override
                public void onStart(String utteranceId)
                {
                    callWithResult.onStart(utteranceId);
                }
            });
            if (listenerResult != TextToSpeech.SUCCESS)
            {
                Log.e(TAG, "failed to add utterance progress listener");
            }
        }
//        else
//        {
//            int listenerResult = tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener()
//            {
//                @Override
//                public void onUtteranceCompleted(String utteranceId)
//                {
//                    callWithResult.onDone(utteranceId);
//                }
//            });
//            if (listenerResult != TextToSpeech.SUCCESS)
//            {
//                Log.e(TAG, "failed to add utterance completed listener");
//            }
//        }
    }

    //sub class may override these, otherwise, one or the other 
    //will occur depending on the Android version
    public void onDone(String utteranceId)
    {
        Log.d(TAG,"done speaking");
    }

    public void onError(String utteranceId)
    {
    }

    public void onStart(String utteranceId)
    {

    }
    
    @Override
    public void onFailedToInit()
    {
        DialogInterface.OnClickListener onClickOk = makeOnFailedToInitHandler();
        AlertDialog a = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Unable to create text to speech")
                .setNeutralButton("Ok", onClickOk).create();
        a.show();
    }
    
    
    @Override
    public void onRequireLanguageData()
    {
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
    public void onWaitingForLanguageData()
    {
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
    
    private DialogInterface.OnClickListener makeOnClickInstallDialogListener()
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ttsInit.installLanguageData();
            }
        };
    }

    private DialogInterface.OnClickListener makeOnFailedToInitHandler()
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        };
    }
    
    //override in subclass
    
    protected void deactivateUi()
    {
        Log.d(TAG, "deactivate ui");
    }
    
    protected void activateUi()
    {
        Log.d(TAG, "activate ui");
    }

    @Override
    protected void speechNotAvailable()
    {
        DialogInterface.OnClickListener onClickOk = makeOnFailedToInitHandler();
        AlertDialog a =
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(
                                "This device does not support speech recognition. Click ok to quit.")
                        .setPositiveButton("Ok", onClickOk).create();
        a.show();
    }

    @Override
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
    @Override
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
    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "ON DESTROY stop");
        if (getTts() != null)
        {
            getTts().shutdown();
        }
        super.onDestroy();
    }
    @Override
    public void activated(boolean success)
    {
        Log.d(TAG, "activated...");

        //don't allow multiple activations
        if (!isListeningForActivation)
        {
            Toast.makeText(this, "Not activated because stopped",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (success)
        {
            Toast.makeText(this, "Activated, no longer listening",
                    Toast.LENGTH_SHORT).show();
            //start speech recognition here
        }
        else
        {
            Toast.makeText(this, "activation failed, no longer listening",
                    Toast.LENGTH_SHORT).show();
        }

        isListeningForActivation = false;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "ON PAUSE stop");
        // save before stopping
        wasListeningForActivation = isListeningForActivation;
        stopActivator();
    }



    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "ON RESUME was listening: " + wasListeningForActivation);
        if (wasListeningForActivation)
        {
            startActivator();
        }
    }

    // Note: onDestroy not needed since the activator was
    // stopped during onPause()

    // if the activity was destroyed these two methods are needed
    // to restore wasListening
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putBoolean(WAS_LISTENING_STATE, isListeningForActivation);
        Log.d(TAG, "saved state: " + isListeningForActivation);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        wasListeningForActivation =
                savedInstanceState.getBoolean(WAS_LISTENING_STATE);
        Log.d(TAG, "restored state: " + wasListeningForActivation);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
