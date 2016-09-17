package io.memorylane.videomaker;

import java.util.List;

import io.memorylane.model.Asset;

/**
 * Created by annam on 17.09.2016.
 */
public class SxmlFactory {

    public final static String DURATION = "2.5";

    public static String produce(List<String> urls) {
        StringBuilder sb = new StringBuilder();
        sb.append("<movie service=\"craftsman-1.0\">\n");
        sb.append("  <body>\n");
        sb.append("    <stack>\n");

        for (String url : urls) {
            sb.append("      <effect type=\"none\" duration=\"");
            sb.append(DURATION);
            sb.append("\">\n");
            sb.append("        <image filename = \"");
            sb.append(url);
            sb.append("\"/>\n");
            sb.append("      </effect>\n");
        }

        sb.append("    </stack>\n");
        sb.append("  </body>\n");
        sb.append("</movie>");


        return sb.toString();
        //return "<movie service=\"craftsman-1.0\">\n  <body>\n    <effect type=\"none\">\n      <video filename=\"http://s3.amazonaws.com/stupeflix-assets/apiusecase/footage.mov\" skip=\"3.0\" duration=\"5.0\"/>\n    </effect>\n  </body>\n</movie>";
    }
}
