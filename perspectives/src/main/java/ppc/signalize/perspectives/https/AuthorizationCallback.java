package ppc.signalize.perspectives.https;

import ppc.signalize.api.types.Authorization;
import ppc.signalize.perspectives.content.Signalize;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Aron on 3/24/14.
 */
public class AuthorizationCallback implements Callback<Authorization> {
    private final Signalize signals;

    public AuthorizationCallback(Signalize s) {
        signals = s;
    }

    @Override
    public void success(Authorization o, Response response) {
        Authorization au = (Authorization) o;
        signals.authorize(au);
    }

    @Override
    public void failure(RetrofitError error) {

        error.printStackTrace();
    }
}
