package ppc.signalize.myvoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import ppc.signalize.mira.face.MiraAbstractActivity;
import ppc.signalize.mira.nervous.concurrent.AsyncMiraTextResponse;
import ppc.signalize.myvoice.util.SystemUiHider;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MyVoiceActivity extends MiraAbstractActivity {

    public static final String TAG = "MyVoiceActivity";
    Handler updateConversationHandler;
    private TextView mira_text;
    private ToggleButton mira_ear;
    private boolean bypassCheckChange = false;
    private LayoutInflater vi;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_voice);

        vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        updateConversationHandler = new Handler();

        prepareHeading(vi);


        prepareInput(vi);

        prepareContent(vi);

    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View contentView = findViewById(R.id.fullscreen_content);
        if (hasFocus) {
            contentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    @Override
    public Spannable createSpan(String words, int aligned) {
        Spannable wordtoSpan = new SpannableString("\r\n" + words + "\r\n");
        int end = wordtoSpan.length();
        if (aligned == ALIGN_MIRA)
            wordtoSpan.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), 0, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        else
            wordtoSpan.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        return colorSpan(wordtoSpan, aligned);
    }

    public Spannable colorSpan(Spannable span, int color) {
        int end = span.length();
        if (color == ALIGN_MIRA)
            span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mira_text)), 0, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        else
            span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.voice_text)), 0, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return span;
    }

    private void prepareContent(LayoutInflater vi) {
        View v = vi.inflate(R.layout.text_content,null);

        mira_text = (TextView) v.findViewById(R.id._text);
        mira_ear = (ToggleButton) findViewById(R.id.mira_ear);

        mira_ear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (bypassCheckChange)
                    return;
                if (isChecked & !myVoice.isSpeechRecognitionServiceActive) {
                    mira.listen();
                } else if (!isChecked && myVoice.isSpeechRecognitionServiceActive) {
                    mira.stop_listen();
                }
            }
        });
        View insertPoint = findViewById(R.id.content_frame);
        ((LinearLayout)insertPoint).addView(v);

    }

    @Override
    public void prependMiraText(String toSpan, int aligned) {
        Spannable span = createSpan(toSpan, aligned);
        Spannable currentText = (Spannable) mira_text.getText();
        CharSequence newText = TextUtils.concat(span, currentText);
        mira_text.setText(newText);
    }

    @Override
    protected void startListening() {
        bypassCheckChange = true;
        mira_ear.setChecked(true);
        bypassCheckChange = false;
    }

    @Override
    public boolean canListen() {

        return mira_ear.isChecked();
    }

    @JavascriptInterface
    public void postOffer(String offer) {

        Toast.makeText(MyVoiceActivity.this, offer,
                Toast.LENGTH_SHORT).show();
    }


    private void prepareHeading(LayoutInflater vi) {
        View display = vi.inflate(R.layout.content_head, null);
        TextView text = (TextView) display.findViewById(R.id.header_text);
        text.setText(getString(R.string.mira_header));

        View insertPoint = findViewById(R.id.heading_frame);
        ((LinearLayout) insertPoint).addView(display);

    }

    private void prepareInput(LayoutInflater vi) {
        View display = vi.inflate(R.layout.text_entry, null);

        final EditText eddie = (EditText) display.findViewById(R.id.entry_text);
        eddie.setInputType(InputType.TYPE_NULL);

        final ImageButton keys = (ImageButton) display.findViewById(R.id.keys);

        keys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchEddie(eddie);
            }
        });

        eddie.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchEddie(eddie);
                return false;
            }
        });
        View insertPoint = findViewById(R.id.input_frame);
        ((LinearLayout) insertPoint).addView(display);

    }

    private void touchEddie(final EditText eddie) {
        eddie.setInputType(InputType.TYPE_CLASS_TEXT);
        eddie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null) {
                    // if shift key is down, then we want to insert the '\n' char in the TextView;
                    // otherwise, the default action is to send the message.
                    if (!event.isShiftPressed()) {
                        new AsyncMiraTextResponse(myVoice).execute(String.valueOf(v.getText()));
                        v.setText("");
                        return true;
                    }
                    return false;
                }


                new AsyncMiraTextResponse(myVoice).execute(String.valueOf(v.getText()));
                v.setText("");
                eddie.setInputType(InputType.TYPE_NULL);
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.toggleSoftInput(0, 0);

                return true;

            }
        });
        eddie.setTextColor(Color.DKGRAY);
        eddie.requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(eddie, InputMethodManager.SHOW_FORCED);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }


}
