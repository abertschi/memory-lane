package io.memorylane.videomaker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by annam on 17.09.2016.
 */
public interface VideomakerApi {
    @POST("/v2/create")
    Call<List<VideomakerResponse>> post(@Body VideomakerRequest request);
}
