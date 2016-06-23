package blake.com.morningalarm.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Raiders on 6/23/16.
 */
public interface RonSwansonInterface {
    @GET("quotes")
    Call<String[]> getRonQuote();
}
