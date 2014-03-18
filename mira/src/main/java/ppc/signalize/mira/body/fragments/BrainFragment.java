package ppc.signalize.mira.body.fragments;

import android.app.Fragment;
import android.os.Bundle;

import ppc.signalize.mira.body.parts.brain.Brain;

/**
 * Created by Aron on 3/8/14.
 */
public class BrainFragment extends Fragment {

    // data object we want to retain
    private Brain _brain;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(Brain b) {
        _brain=b;
    }

    public Brain getData() {
        return _brain;
    }
}
