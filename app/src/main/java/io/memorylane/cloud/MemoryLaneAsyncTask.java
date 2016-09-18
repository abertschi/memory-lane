package io.memorylane.cloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.memorylane.R;
import io.memorylane.dto.AssetDto;
import io.memorylane.dto.PhotoAssetDto;
import io.memorylane.dto.VideoAssetDto;
import io.memorylane.model.Asset;
import io.memorylane.videomaker.SxmlFactory;
import io.memorylane.videomaker.VideomakerApi;
import io.memorylane.videomaker.VideomakerRequest;
import io.memorylane.videomaker.VideomakerRequestFactory;
import io.memorylane.videomaker.VideomakerResponse;
import io.memorylane.videomaker.VideomakerResponseResult;
import io.memorylane.videomaker.VideomakerTask;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MemoryLaneAsyncTask extends AsyncTask<Asset, Void, Boolean> {

    CloudController cloud;

    public Activity activity;
    public ProgressDialog dialog;

    public MemoryLaneAsyncTask(Activity activity) {
        super();
        this.activity = activity;
        this.dialog = new ProgressDialog(activity);
        this.dialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        cloud = new CloudController();
        this.dialog.setMessage("I'll be hack \uD83D\uDD25");
        this.dialog.show();
    }

    @Override
    protected Boolean doInBackground(Asset... assets) {
        ConcurrentHashMap<Asset, String> uploadedAssets = new ConcurrentHashMap<>();

        List<UploadTask> uploadTasks = new ArrayList<>();

        for (Asset asset : assets) {

            if(asset.getUploadedFile() == null) {
                if (asset.isPicutre()) {
                    uploadTasks.add(cloud.putImage(asset));
                } else {
                    uploadTasks.add(cloud.putMovie(asset));
                }
            } else {
                uploadedAssets.put(asset, asset.getUploadedFile());
            }
        }

        Task<Void> tasks = Tasks.whenAll(uploadTasks);

        // wait for competition
        try {
            Tasks.await(tasks);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("ERROR", e.getMessage(), e);
        }

        uploadedAssets.putAll(cloud.getUploadedAssets());

        updatePaths(uploadedAssets);

        VideomakerResponseResult result = createVideo(uploadedAssets);


        Log.i("TAG", Objects.toString(uploadedAssets));
        Log.i("TAG", Objects.toString(result));
        return true;
    }

    private void updatePaths(ConcurrentHashMap<Asset, String> uploadedAssets) {
        Realm realm = Realm.getDefaultInstance();
        for (Map.Entry<Asset, String> entry : uploadedAssets.entrySet()) {
            realm.beginTransaction();
            Asset asset = realm.where(Asset.class).equalTo("path", entry.getKey().getPath()).findFirst();
            if(asset != null){
                asset.setUploadedFile(entry.getValue());
            }
            realm.commitTransaction();
        }
    }

    protected VideomakerResponseResult createVideo(ConcurrentHashMap<Asset, String> uploadedAssets) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dragon.stupeflix.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        // prepare call in Retrofit 2.0
        VideomakerApi videomakerApi = retrofit.create(VideomakerApi.class);

        VideomakerTask tasks = createTasks(uploadedAssets);

        VideomakerRequest request = VideomakerRequestFactory.produce(tasks);
        Call<List<VideomakerResponse>> call = videomakerApi.post(request);

        try {
            Response<List<VideomakerResponse>> response = call.execute();

            List<VideomakerResponse> body = response.body();
            VideomakerResponseResult result = body.get(0).result;

            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result.export)));
            Log.i("Video", "Video Playing....");

            Log.i("video url", result.export);

            return result;

        } catch (IOException e) {
            Log.e("AsyncTask", e.getMessage(), e);
            return null;
        }
    }

    protected VideomakerTask createTasks(ConcurrentHashMap<Asset, String> uploadedAssets) {

        List<AssetDto> urls = new ArrayList<>();

        for (Map.Entry<Asset, String> entry :uploadedAssets.entrySet()) {
            if(entry.getKey().isPicutre()) {
                urls.add(new PhotoAssetDto(entry.getValue()));
            } else {
                urls.add(new VideoAssetDto(entry.getValue()));
            }
        }

        VideomakerTask task = new VideomakerTask();
        task.task_name = "video.create";
        task.definition = SxmlFactory.produce("HackZurich 2016", urls);
        return task;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
