package io.memorylane.dto;

import io.memorylane.Utils;
import io.memorylane.model.Asset;

/**
 * Created by annam on 17.09.2016.
 */
public class PhotoAssetDto extends AssetDto {

    public PhotoAssetDto(String url) {
        super(url);
    }

    public String getSxml() {
        return "<overlay duration=\"3.0\">\n" +
                "   <image filename=\"" + Utils.escapeForXml( url) + "\">\n" +
                "       <filter type=\"fitAdapter\"></filter>\n" +
                "   </image>\n" +
                "   <animator type=\"grow\" growStart=\"-0.3\" growEnd=\"0.0\"/>" +
                "</overlay>\n";
    }
}
