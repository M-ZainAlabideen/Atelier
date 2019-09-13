package app.atelier.webservices.responses.products;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShortDescriptionModel {
    @SerializedName("language_id")
    @Expose
    public Integer languageId;
    @SerializedName("localized_short_description")
    @Expose
    public String shortDescription;

}
