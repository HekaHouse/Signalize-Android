package ppc.signalize.perspectives;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;
import ppc.signalize.perspectives.content.Signalize;

/**
 * A fragment representing a single Perspective detail screen.
 * This fragment is either contained in a {@link ppc.signalize.perspectives.PerspectiveListActivity}
 * in two-pane mode (on tablets) or a {@link ppc.signalize.perspectives.PerspectiveDetailActivity}
 * on handsets.
 */
public class SentimentDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "sentiment_track";

    /**
     * The content content this fragment is presenting.
     */
    private Signalize.DetailItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SentimentDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the content content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = Signalize.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web, container, false);

        // Show the content content as text in a TextView.
        //if (mItem != null) {
        WebView content = (WebView) rootView.findViewById(R.id.webView);
        content.getSettings().setJavaScriptEnabled(true);

        content.loadUrl("file:///android_asset/" + ARG_ITEM_ID + "_full.html");
        //init_cards((CardGridView) rootView.findViewById(R.id.card_grid));
        //}

        return rootView;
    }

    public void init_cards(CardGridView v) {

        ArrayList<Card> cards = mItem.content;

        CardGridArrayAdapter mCardArrayAdapter = new CardGridArrayAdapter(getActivity(), cards);


        v.setAdapter(mCardArrayAdapter);
    }
}
