package app.atelier.webservices.responses.attributes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderAttributeModel {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("code")
    @Expose
    public Integer code;
    @SerializedName("value")
    @Expose
    public String value;
    @SerializedName("type")
    @Expose
    public String type;
}
