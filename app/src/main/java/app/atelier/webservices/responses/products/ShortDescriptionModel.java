package app.atelier.webservices.responses.products;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShortDescriptionModel implements Serializable {
    @SerializedName("language_id")
    @Expose
    public Integer languageId;
    @SerializedName("localized_short_description")
    @Expose
    public String shortDescription;

}
