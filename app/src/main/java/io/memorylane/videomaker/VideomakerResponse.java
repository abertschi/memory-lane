package io.memorylane.videomaker;

import java.util.List;

/**
 * Created by annam on 17.09.2016.
 */
public class VideomakerResponse {
    public String status;
    public String key;
    public VideomakerResponseResult result;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VideomakerResponse{");
        sb.append("status='").append(status).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", result=").append(result);
        sb.append('}');
        return sb.toString();
    }
}
