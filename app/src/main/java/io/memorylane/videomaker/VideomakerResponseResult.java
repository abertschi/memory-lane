package io.memorylane.videomaker;

/**
 * Created by annam on 17.09.2016.
 */
public class VideomakerResponseResult {
    public String export;
    public String preview;
    public String thumbnail;
    public Double duration;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VideomakerResponseResult{");
        sb.append("export='").append(export).append('\'');
        sb.append(", preview='").append(preview).append('\'');
        sb.append(", thumbnail='").append(thumbnail).append('\'');
        sb.append(", duration=").append(duration);
        sb.append('}');
        return sb.toString();
    }
}
