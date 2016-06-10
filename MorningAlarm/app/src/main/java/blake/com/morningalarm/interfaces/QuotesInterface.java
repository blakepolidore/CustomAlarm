package blake.com.morningalarm.interfaces;

import blake.com.morningalarm.models.Root;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Raiders on 6/9/16.
 */
public interface QuotesInterface {
    @GET("qod.json")
    Call<Root> getQuotes();
}
