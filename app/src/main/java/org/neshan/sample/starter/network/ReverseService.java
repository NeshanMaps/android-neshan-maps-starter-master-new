package org.neshan.sample.starter.network;

import org.neshan.sample.starter.model.address.NeshanAddress;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * @author Ali (alirezaiyann@gmail.com)
 * @since 4/18/2020 5:48 PM.
 */
public interface ReverseService {
    // TODO: replace "YOUR_API_KEY" with your api key
    @Headers("Api-Key: YOUR-API-KEY")
    @GET("/v2/reverse")
    Call<NeshanAddress> getReverse(@Query("lat") Double lat, @Query("lng") Double lng);
}
