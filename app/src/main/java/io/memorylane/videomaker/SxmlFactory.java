package io.memorylane.videomaker;

import android.util.Log;

import java.util.List;

import io.memorylane.dto.AssetDto;

/**
 * Created by annam on 17.09.2016.
 */
public class SxmlFactory {

    public static String produce(List<AssetDto> dtos) {
        StringBuilder sb = new StringBuilder();
        sb.append("<movie service=\"craftsman-1.0\">\n");
        sb.append("  <body>\n");
        sb.append("    <sequence>\n");

        for (AssetDto dto : dtos)
            sb.append(dto.getSxml());

        sb.append("    </sequence>\n");
        sb.append("  </body>\n");
        sb.append("</movie>");

        Log.i("SXML", sb.toString());
        return sb.toString();
    }
}
