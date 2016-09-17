package io.memorylane.dto;

/**
 * Created by annam on 17.09.2016.
 */
public class VideoAssetDto extends AssetDto {

    public VideoAssetDto(String url) {
        super(url);
    }

    public String getSxml() {
        return "<overlay>\n" +
                "   <video filename=\"" + url + "\">\n" +
                "       <filter type=\"fitAdapter\"></filter>\n" +
                "   </video>\n" +
                "</overlay>\n";
    }
}
