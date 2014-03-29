package ppc.signalize.perspectives.content;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import it.gmariotti.cardslib.library.internal.Card;
import ppc.signalize.perspectives.R;

/**
 * Created by Aron on 3/22/14.
 */
public class WebCard extends Card implements View.OnTouchListener {

    public static final String TIER = "tier_gauge";
    public static final String SENTIMENT = "sentiment_gauge";
    public static final String TRACK_SENTIMENT = "sentiment_track";
    protected WebView content;
    private Signalize mySig;
    /**
     * Constructor with a custom inner layout
     *
     * @param context
     */
    private String type = "";


    /**
     * @param context
     * @param innerLayout
     */
    public WebCard(Context context, String s, int innerLayout) {
        super(context, innerLayout);
        type = s;
        init();
    }

    public WebCard(Context c, Signalize sig, String s) {
        super(c, R.layout.card_list_inner_web);
        type = s;
        mySig = sig;
        init();
    }

    /**
     * Init
     */
    private void init() {

    }


    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {


        content = (WebView) parent.findViewById(R.id.canvas_js);
        content.getSettings().setJavaScriptEnabled(true);
        content.addJavascriptInterface(mySig.getJSObject(), "Signalize");
        content.setFocusable(false);
        content.setOnTouchListener(this);
        content.loadUrl("file:///android_asset/" + type + ".html");
        if (mySig.hasStore(type)) {
            restoreUntilAvailable();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        getCardView().callOnClick();
        return false;
    }

    public void pause() {
        pauseUntilAvailable();

        setStoreUntilAvailable();

    }

    private void setStoreUntilAvailable() {
        content.evaluateJavascript("(function() { return typeof setStore == 'function'; })();", new ValueCallback<String>() {

            @Override
            public void onReceiveValue(String s) {
                if (s.equals("true")) {
                    Log.d(TAG, "setstore:" + type);
                    Log.d(TAG, "return:" + s);
                    boolean pause = true;
                    content.evaluateJavascript("javascript:setStore()", null);
                } else {
                    Log.d(TAG, "setstore-fail:" + type);
                    setStoreUntilAvailable();
                }
            }
        });
    }

    private void pauseUntilAvailable() {

        content.evaluateJavascript("(function() { return typeof pause == 'function'; })();", new ValueCallback<String>() {

            @Override
            public void onReceiveValue(String s) {
                if (s.equals("true")) {
                    Log.d(TAG, "pause:" + type);
                    Log.d(TAG, "return:" + s);
                    boolean pause = true;
                    content.evaluateJavascript("pause()", null);
                } else {
                    Log.d(TAG, "pause-fail:" + type);
                    pauseUntilAvailable();
                }
            }
        });

    }

    private void restoreUntilAvailable() {

        content.evaluateJavascript("(function() { return typeof restore == 'function'; })();", new ValueCallback<String>() {

            @Override
            public void onReceiveValue(String s) {
                if (s.equals("true")) {
                    Log.d(TAG, "restore:" + type);
                    Log.d(TAG, "return:" + s);
                    boolean pause = true;
                    String js = "restore(\"" + mySig.getStore(type) + "\")";
                    Log.d(TAG, js);
                    content.evaluateJavascript(js, new ValueCallback<String>() {

                        @Override
                        public void onReceiveValue(String s) {
                            Log.d(TAG, "restore-complete:" + type);
                            Log.d(TAG, "return:" + s);
                            boolean pause = true;
                        }
                    });
                } else {
                    Log.d(TAG, "restore-fail:" + type);
                    restoreUntilAvailable();
                }

            }
        });
    }

}