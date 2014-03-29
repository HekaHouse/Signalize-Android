package ppc.signalize.perspectives.https;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import ppc.signalize.api.SignalizeService;
import ppc.signalize.api.types.Feedback;
import ppc.signalize.mira.body.parts.skeleton.crypt.CryptHelper;
import ppc.signalize.perspectives.content.Signalize;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.URLClient;

/**
 * Created by Aron on 3/8/14.
 */
public class SignalCollector {

    public final static int MYVOICE = 0;
    public final static int MIRA = 1;
    private final static String client_id = "d8d8c94cc4efb5e16b81";
    private final static String client_secret = "a3d5b5b7b68e0a640902a11cb71822e3d8cf8c98";
    private final static String user = "MIRA";
    private final static String pass = "M1RAp@ss";
    public static SharedPreferences oauth_access;
    public static URLClient myClient;
    public static String access_key;
    public static RestAdapter restAdapter;
    private static SignalizeService service;
    private static CryptHelper cryptor = new CryptHelper();

    /* Not safe to run on main thread
    * */
    public static List<Feedback> collectSignals(Signalize s, int index) {
        try {
            return service.collectFeedback(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<Feedback>();
    }


    public static void establishNetworking(Signalize s) {
        oauth_access = s.getPreferences(s.MODE_PRIVATE);

        myClient = new URLClient(s);


        access_key = oauth_access.getString("access", "");
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://repository.signalize.ws")
                .setClient(myClient)
                .build();

        service = restAdapter.create(SignalizeService.class);

        if (access_key.length() > 0) {
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://repository.signalize.ws")
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade requestFacade) {
                            requestFacade.addHeader("Accept", "application/json");
                            requestFacade.addHeader("Authorization", access_key);
                        }
                    })
                    .setClient(myClient)
                    .build();
            service = restAdapter.create(SignalizeService.class);
        } else {
//            service.gain_access(client_id, client_secret, "password", user, pass, new AuthorizationCallback(s));
        }
    }


    public static void authorize(String access_token) {
        SharedPreferences.Editor oauth_edit = SignalCollector.oauth_access.edit();
        oauth_edit.putString("access", access_token);
        oauth_edit.commit();
        access_key = access_token;
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://repository.signalize.ws")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addHeader("Accept", "application/json");
                        requestFacade.addHeader("Authorization", access_key);
                    }
                })
                .setClient(myClient)
                .build();
        service = restAdapter.create(SignalizeService.class);
    }
}
