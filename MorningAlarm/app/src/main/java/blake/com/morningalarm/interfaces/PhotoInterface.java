package blake.com.morningalarm.interfaces;

import blake.com.morningalarm.models.pictures.PhotoRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Raiders on 6/10/16.
 */
public interface PhotoInterface {

    @GET("api/")
    Call<PhotoRoot> getPicture(@Query("key") String key, @Query("category") String category, @Query("per_page") int picAmount);
}
