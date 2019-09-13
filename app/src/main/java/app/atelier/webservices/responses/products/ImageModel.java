package app.atelier.webservices.responses.products;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageModel {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("picture_id")
    @Expose
    public Integer pictureId;
    @SerializedName("position")
    @Expose
    public Integer position;
    @SerializedName("src")
    @Expose
    public String src;
}
