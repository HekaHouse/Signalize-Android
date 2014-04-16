package ppc.signalize.mira.body.parts.nervous.wifi;


import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;
import ppc.signalize.mira.R;


/**
 * Created by Aron on 3/22/14.
 */
public class VoiceCard extends Card implements View.OnTouchListener {

    protected TextView content;
    private WifiP2pDevice mDevice = null;
    /**
     * Constructor with a custom inner layout
     *
     * @param context
     */
    private String type = "";


    public VoiceCard(Context c, WifiP2pDevice device) {
        super(c, R.layout.card_list_inner_voice);
        mDevice = device;
        init();
    }

    /**
     * Init
     */
    private void init() {

    }


    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {


        content = (TextView) parent.findViewById(R.id.card_main_inner_simple_title);
        content.setFocusable(false);
        content.setOnTouchListener(this);


    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        getCardView().callOnClick();
        return false;
    }


    public WifiP2pDevice getDevice() {
        return mDevice;
    }
}