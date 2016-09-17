package io.memorylane.dto;

/**
 * Created by annam on 17.09.2016.
 */
public abstract class AssetDto {
    public String url;

    public AssetDto(String url) {
        this.url = url;
    }

    public abstract String getSxml();
}
