package io.memorylane.dto;

/**
 * Created by annam on 17.09.2016.
 */
public class DoubleVideoAssetDto extends AssetDto {

    public String smallVideoUrl;

    public DoubleVideoAssetDto(String url, String smallVideoUrl) {
        super(url);
        this.smallVideoUrl = smallVideoUrl;
    }

    public String getSxml() {
        return "<stack>\n" +
                "   <overlay>\n" +
                "       <video filename=\"" + url + "\">\n" +
                "           <filter type=\"fitAdapter\"></filter>\n" +
                "       </video>\n" +
                "   </overlay>  \n" +
                "   <overlay left=\"0\" top=\"0\" height=\"0.35\">\n" +
                "       <video filename=\"" + smallVideoUrl + "\">\n" +
                "           <filter type=\"fitAdapter\"></filter>\n" +
                "       </video>\n" +
                "   </overlay> \n" +
                "</stack>\n";
    }
}
