package ppc.signalize.perspectives.content;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import ppc.signalize.api.types.Authorization;
import ppc.signalize.api.types.Feedback;
import ppc.signalize.perspectives.PerspectiveDetailFragment;
import ppc.signalize.perspectives.PerspectiveListActivity;
import ppc.signalize.perspectives.R;
import ppc.signalize.perspectives.content.data.FeedbackDataSource;
import ppc.signalize.perspectives.content.data.types.FeedbackData;
import ppc.signalize.perspectives.https.SignalCollector;


public class Signalize implements Runnable, View.OnTouchListener {

    private static final String TAG = "Signalize";
    public static Object MODE_PRIVATE;
    /**
     * An array of sample (content) items.
     */
    public static List<DetailItem> ITEMS = new ArrayList<DetailItem>();

    /**
     * A map of sample (content) items, by ID.
     */
    public static Map<String, DetailItem> ITEM_MAP = new HashMap<String, DetailItem>();
    public static Map<String, String> DASHBOARD_MAP = new HashMap<String, String>();
    public static Map<String, String> SIGNALS_MAP = new HashMap<String, String>();
    public static Map<String, String> VOICES_MAP = new HashMap<String, String>();
    public static Map<String, String> ADMIN_MAP = new HashMap<String, String>();
    private final PerspectiveListActivity active;
    Runnable mStatusChecker;
    private int mInterval = 30000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    //private ArrayList<Feedback> myFeedback = new ArrayList<Feedback>();
    private int runningCount = 0;
    private HashMap<String, String> js_store = new HashMap<String, String>();
    private FeedbackDataSource datasource;

    public Signalize(PerspectiveListActivity c) {
        active = c;
        MODE_PRIVATE = active.MODE_PRIVATE;
        addItem(new DetailItem("1", getDashboard(c, this)));
        addItem(new DetailItem("2", getSignals(c, this)));
        addItem(new DetailItem("3", getVoices(c, this)));
        addItem(new DetailItem("4", getAdmin(c, this)));
        mHandler = new Handler();
        final Signalize me = this;

        datasource = new FeedbackDataSource(active);
        datasource.open();

        runningCount = datasource.getCommentCount();

        mStatusChecker = new Runnable() {
            @Override
            public void run() {

                new Thread(me).start();
            }
        };

        //startRepeatingTask();
    }

    private static void watchRepository() {

    }

    private static ArrayList<Card> getDashboard(PerspectiveListActivity c, Signalize s) {
        DASHBOARD_MAP.put("Sentiment Gauge", "");
        DASHBOARD_MAP.put("Tier Gauge", "");
        DASHBOARD_MAP.put("Sentiment Track", "");

        return makeCards(DASHBOARD_MAP, c, s);
    }

    private static ArrayList<Card> makeCards(Map<String, String> menu, PerspectiveListActivity c, Signalize s) {
        ArrayList<Card> cards = new ArrayList<Card>();


        for (String key : menu.keySet()) {

            //Create a Card

            Card card;
            if (key.equals("Sentiment Gauge")) {
                card = new WebCard(c, s, WebCard.SENTIMENT);
                card.setClickable(true);
                card.setOnClickListener(c);
            } else if (key.equals("Tier Gauge")) {
                card = new WebCard(c, s, WebCard.TIER);
            } else if (key.equals("Sentiment Track")) {
                card = new WebCard(c, s, WebCard.TRACK_SENTIMENT);
            } else {
                card = new Card(c);
                CardHeader header = new CardHeader(c);
                header.setTitle(key);
                //Add Header to card
                card.addCardHeader(header);
                card.setTitle(menu.get(key));
            }
            //Create a CardHeader


            cards.add(card);
        }
        return cards;
    }

    private static ArrayList<Card> getSignals(PerspectiveListActivity c, Signalize s) {
        SIGNALS_MAP.put("Monitor", "real time feed of patient messages");
        SIGNALS_MAP.put("Tier One", "Tier one messages organized by sentiment and severity");
        SIGNALS_MAP.put("Tier Two", "Tier two messages organized by sentiment and severity");
        return makeCards(SIGNALS_MAP, c, s);
    }

    private static ArrayList<Card> getVoices(PerspectiveListActivity c, Signalize s) {
        VOICES_MAP.put("Station Comm", "real time messaging, with voice and video options");
        VOICES_MAP.put("Caregiver Comm", "real time messaging, with voice and video options");
        VOICES_MAP.put("PatientData Comm", "real time messaging, with voice and video options");
        return makeCards(VOICES_MAP, c, s);
    }

    private static ArrayList<Card> getAdmin(PerspectiveListActivity c, Signalize s) {
        ADMIN_MAP.put("MIRA", "adjust MIRA's responses to voice prompts");
        ADMIN_MAP.put("Intuition", "rebuild models for analyzing messages (sentiment, severity)");
        ADMIN_MAP.put("Tiers", "adjust tier rules and create new tiers");
        return makeCards(ADMIN_MAP, c, s);
    }

    private static void addItem(DetailItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);

    }

    public void authorize(Authorization au) {
        SignalCollector.authorize(au.access_token);
        startRepeatingTask();
    }

    public SharedPreferences getPreferences(Object mode_private) {
        return active.getPreferences(active.MODE_PRIVATE);
    }

    public Activity getContext() {
        return active;
    }

    public JsObject getJSObject() {
        return new JsObject(this);
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof ImageView) {
            ImageView iv = (ImageView) v;
            String tagged = (String) iv.getTag();
            if (tagged.equals("dead")) {
                iv.setTag("live");
                iv.setColorFilter(Color.TRANSPARENT);
                iv.setImageDrawable(active.getResources().getDrawable(R.drawable.mic_live));
                active.mira.listen();
                //new AsyncEarOpener(active.myVoice).execute("");
            } else if (tagged.equals("live")) {
                iv.setTag("dead");
                iv.setColorFilter(Color.DKGRAY);
                iv.setImageDrawable(active.getResources().getDrawable(R.drawable.mic_dead));
                active.mira.stop_listen();
                //new AsyncEarCloser(active.myVoice).execute("");
            }
        }
        return false;
    }

    public void pauseWebCards(PerspectiveDetailFragment pdf) {

        for (Card c : pdf.getCards()) {
            if (c instanceof WebCard) {
                WebCard wc = (WebCard) c;
                wc.pause();
                //outState.putString(wc.getStoreLabel(),wc.getStore());
            }
        }

    }

    public boolean hasStore(String type) {
        return js_store.containsKey(type) && js_store.get(type) != null;
    }

    public String getStore(String type) {
        boolean paused = false;
        if (js_store.get(type) == null)
            paused = true;
        return js_store.get(type);
    }

    @Override
    public void run() {
        List<Feedback> l = SignalCollector.collectSignals(this, runningCount);
        runningCount = runningCount + 10;
        for (Feedback f : l) {
            datasource.createFeedback(f);
        }
        mHandler.postDelayed(mStatusChecker, mInterval);
    }

    public void startRepeatingTask() {
        new Thread(mStatusChecker).start();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    /**
     * A content item representing a piece of content.
     */
    public static class DetailItem {
        public String id;
        public ArrayList<Card> content;

        public DetailItem(String id, ArrayList<Card> content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    public class JsObject {
        public JsObject(Signalize sig) {

        }

        @JavascriptInterface
        public double incomingSentiment(int index) {
            //Log.d(TAG, "js sentiment track looking for "+index);
            FeedbackData f = datasource.getComment(index);
            if (f != null) {
                Spannable wordtoSpan = new SpannableString("\r\n" + f.comment + "\r\n");
                int end = wordtoSpan.length();
                if (f.sentiment <= .45)
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                else if (f.sentiment >= .55)
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                else
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //new AsyncAppendTask(active, wordtoSpan).execute("");
                return f.sentiment;
            } else
                return -1;
        }

        @JavascriptInterface
        public double tallySentiment(int index) {
            //Log.d(TAG, "js sentiment tally looking for "+index);
            FeedbackData f = datasource.getComment(index);
            if (f != null)
                return f.sentiment;
            else
                return -1;
        }

        @JavascriptInterface
        public double incomingTier(int index) {
            //Log.d(TAG, "js tier tally looking for "+index);
            FeedbackData f = datasource.getComment(index);
            if (f != null)
                return f.tier;
            else
                return -1;
        }

        @JavascriptInterface
        public void setStore(String type, String store) {
            js_store.put(type, store);
        }
    }
}
