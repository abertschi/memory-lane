package io.memorylane.videomaker;

import java.util.ArrayList;
import java.util.List;

import io.memorylane.dto.AssetDto;
import io.memorylane.dto.DoubleVideoAssetDto;
import io.memorylane.dto.PhotoAssetDto;
import io.memorylane.dto.VideoAssetDto;

/**
 * Created by annam on 17.09.2016.
 */
public class VideomakerRequestFactory {

    private final static String SECRET = "7JW4XXBGVZBNXLMLRPCN44PTBE";
    private final static String TASK_NAME = "video.create";
    private final static boolean BLOCK = true;

    public static VideomakerRequest produce() {
        VideomakerRequest request = new VideomakerRequest();
        request.secret = SECRET;
        request.block = BLOCK;
        request.tasks = getTasks();
        return request;
    }

    private static List<VideomakerTask> getTasks() {
        List<VideomakerTask> tasks = new ArrayList<>();
        tasks.add(getTask());
        return tasks;
    }

    private static VideomakerTask getTask() {
        VideomakerTask task = new VideomakerTask();
        task.definition = SxmlFactory.produce("HackZurich 2016", getUrlList());
        task.task_name = TASK_NAME;
        return task;
    }

    private static List<AssetDto> getUrlList() {
        List<AssetDto> urls = new ArrayList<>();
        urls.add(new PhotoAssetDto("http://www.aspca.org/sites/default/files/cat-care_cat-nutrition-tips_overweight_body4_left.jpg"));
        urls.add(new PhotoAssetDto("http://f.tqn.com/y/cats/1/S/6/V/4/cat-deadmouse2081x1446.jpg"));
        urls.add(new VideoAssetDto("http://s3.amazonaws.com/stupeflix-assets/apiusecase/dive.mov"));
        urls.add(new PhotoAssetDto("https://www.petfinder.com/wp-content/uploads/2012/11/140272627-grooming-needs-senior-cat-632x475.jpg"));
        urls.add(new DoubleVideoAssetDto("http://s3.amazonaws.com/stupeflix-assets/apiusecase/dive.mov", "http://s3.amazonaws.com/stupeflix-assets/apiusecase/dive.mov"));
        urls.add(new PhotoAssetDto("http://static.boredpanda.com/blog/wp-content/uploads/2016/04/beautiful-fluffy-cat-british-longhair-13.jpg"));
        return urls;
    }
}
