package ppc.signalize.perspectives;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;
import ppc.signalize.perspectives.content.Signalize;

/**
 * A fragment representing a single Perspective detail screen.
 * This fragment is either contained in a {@link PerspectiveListActivity}
 * in two-pane mode (on tablets) or a {@link PerspectiveDetailActivity}
 * on handsets.
 */
public class PerspectiveDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private final Signalize mySig;

    /**
     * The content content this fragment is presenting.
     */
    private Signalize.DetailItem mItem;
    private View rootView;
    private ArrayList<Card> cards = new ArrayList<Card>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     *
     * @param mySig
     */
    public PerspectiveDetailFragment(Signalize mySig) {
        this.mySig = mySig;
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
        rootView = inflater.inflate(R.layout.fragment_perspective_detail, container, false);
        ImageView mic = (ImageView) rootView.findViewById(R.id.mic);
        mic.setColorFilter(Color.DKGRAY);
        mic.setOnTouchListener(mySig);
        ((TextView) rootView.findViewById(R.id.commentary)).setText("", TextView.BufferType.SPANNABLE);
        // Show the content content as text in a TextView.

        if (mItem != null) {
            init_cards((CardGridView) rootView.findViewById(R.id.card_grid));
        }

        return rootView;
    }

    public void init_cards(CardGridView v) {

        cards = mItem.content;

        CardGridArrayAdapter mCardArrayAdapter = new CardGridArrayAdapter(getActivity(), cards);


        v.setAdapter(mCardArrayAdapter);
    }

    public CardGridView getCardGrid() {
        return ((CardGridView) rootView.findViewById(R.id.card_grid));
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    @Override
    public void onPause() {
        super.onPause();
        mySig.pauseWebCards(this);
    }


    public void setText(Spannable wordtoSpan) {
        Spannable currentText = (Spannable) ((TextView) rootView.findViewById(R.id.commentary)).getText();
        CharSequence indexedText = TextUtils.concat(wordtoSpan, currentText);
        ((TextView) rootView.findViewById(R.id.commentary)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) rootView.findViewById(R.id.commentary)).setText(indexedText);

    }
}
