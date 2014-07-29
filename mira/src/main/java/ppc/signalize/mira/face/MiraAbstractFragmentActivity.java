package ppc.signalize.mira.face;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by mukundan on 7/11/14.
 */
public class MiraAbstractFragmentActivity extends FragmentActivity {
    private static MiraAbstractFragmentActivity ourInstance = new MiraAbstractFragmentActivity();

    public static MiraAbstractFragmentActivity getInstance() {
        return ourInstance;
    }

    public MiraAbstractFragmentActivity() {

    }

    public void setActiveFragment(Fragment fragment){

    }
}
