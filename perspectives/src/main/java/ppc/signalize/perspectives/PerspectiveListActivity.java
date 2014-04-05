package ppc.signalize.perspectives;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import it.gmariotti.cardslib.library.internal.Card;
import ppc.signalize.mira.MyVoice;
import ppc.signalize.mira.body.MiraAbstractFragmentActivity;
import ppc.signalize.perspectives.content.Signalize;
import ppc.signalize.perspectives.content.WebCard;
import ppc.signalize.perspectives.https.SignalCollector;


/**
 * An activity representing a list of Perspectives. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PerspectiveDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PerspectiveListFragment} and the item details
 * (if present) is a {@link PerspectiveDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link PerspectiveListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class PerspectiveListActivity extends MiraAbstractFragmentActivity
        implements PerspectiveListFragment.Callbacks, Card.OnCardClickListener {

    private static final boolean TOGGLE_ON_CLICK = true;
    private static final String TAG = "MyVoice";
    private static final String LOG = "ppc";
    public Signalize mySig;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private boolean mIsBound = false;
    private boolean isSpeechRecognitionServiceActive;
    private Intent listeningIntent;
    private boolean speech_cycle_active;
    private MyVoice myvoice;
    private PerspectiveDetailFragment activeFragment = null;
    private PerspectiveListFragment activeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perspective_list);

        mySig = new Signalize(this);
        SignalCollector.establishNetworking(mySig);
        PerspectiveDetailActivity.mySig = mySig;
        mySig.startRepeatingTask();
        activeList = (PerspectiveListFragment) getSupportFragmentManager().findFragmentById(R.id.perspective_list);
        if (findViewById(R.id.perspective_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            activeList.setActivateOnItemClick(true);
        }
        //initialize mira
        //init();
    }

    /**
     * Callback method from {@link PerspectiveListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(PerspectiveDetailFragment.ARG_ITEM_ID, id);
            PerspectiveDetailFragment fragment = new PerspectiveDetailFragment(mySig);
            activeFragment = fragment;
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.perspective_detail_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commit();

        } else {
            PerspectiveDetailActivity.myList = this;
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, PerspectiveDetailActivity.class);
            detailIntent.putExtra(PerspectiveDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onClick(Card c, View v) {
        if (c instanceof WebCard) {
            WebCard w = (WebCard) c;
            Bundle arguments = new Bundle();
            arguments.putString(SentimentDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(SentimentDetailFragment.ARG_ITEM_ID));
            SentimentDetailFragment fragment = new SentimentDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.perspective_detail_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                    .commit();
        }
    }

    public PerspectiveDetailFragment getActiveFragment() {


        return activeFragment;
    }

    @Override
    public void setActiveFragment(Fragment fragment) {
        activeFragment = (PerspectiveDetailFragment) fragment;
    }
}
