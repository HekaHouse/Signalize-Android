package ppc.signalize.api;

import java.util.List;

import ppc.signalize.api.types.Authorization;
import ppc.signalize.api.types.Feedback;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface SignalizeService {

    @GET("/signals/{index_after}/")
    public List<Feedback> collectFeedback(@Path("index_after") Integer index);

    @FormUrlEncoded
    @POST("/oauth2/access_token/")
    public void gain_access(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("username") String username,
            @Field("password") String password,
            Callback<Authorization> callback);
//
//    @FormUrlEncoded
//    @POST("/label_myvoice/")
//    public void label(@Field("comment") JSONObject comment, Callback<ScoreLabel> callback);
}
