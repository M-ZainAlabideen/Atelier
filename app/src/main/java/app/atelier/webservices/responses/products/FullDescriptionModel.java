package app.atelier.webservices.responses.products;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FullDescriptionModel {
    @SerializedName("language_id")
    @Expose
    public Integer languageId;
    @SerializedName("localized_full_description")
    @Expose
    public String fullDescription;

}
