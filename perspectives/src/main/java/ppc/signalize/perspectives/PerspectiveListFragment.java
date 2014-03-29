package ppc.signalize.perspectives;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import ppc.signalize.perspectives.content.Signalize;

/**
 * A list fragment representing a list of Perspectives. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link PerspectiveDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PerspectiveListFragment extends ListFragment implements Card.OnCardClickListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * A content implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };
    ;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    private HashMap<String, String> menu = new HashMap<String, String>();
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PerspectiveListFragment() {
        menu.put("Admin", "AI Enhancement");
        menu.put("Voices", "Connect with patients, caregivers and stations");
        menu.put("Signals", "Messages flagged for review");
        menu.put("Dashboard", "Gauges and Charts");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

    }

    private void initCards() {
        ArrayList<Card> cards = new ArrayList<Card>();


        for (String key : menu.keySet()) {
            //Create a Card
            Card card = new Card(getActivity().getApplicationContext());

            //Create a CardHeader
            CardHeader header = new CardHeader(getActivity().getApplicationContext());

            header.setTitle(key);

            //Add Header to card
            card.addCardHeader(header);

            card.setOnClickListener(this);

            card.setTitle(menu.get(key));

            cards.add(card);
        }


        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);


        setListAdapter(mCardArrayAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onActivityCreated(Bundle s) {
        super.onActivityCreated(s);
        initCards();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the content implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onClick(Card card, View view) {
        ListView cards = (ListView) view.getParent();

        onListItemClick(cards, view, cards.getPositionForView(view), view.getId());
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        if (Signalize.DASHBOARD_MAP.isEmpty()) {
            new Signalize((PerspectiveListActivity) getActivity());
        }
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(Signalize.ITEMS.get(position).id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }
}
