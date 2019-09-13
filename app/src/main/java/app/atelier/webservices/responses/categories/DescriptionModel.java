package app.atelier.webservices.responses.categories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DescriptionModel {
    @SerializedName("language_id")
    @Expose
    public Integer languageId;
    @SerializedName("localized_description")
    @Expose
    public Object localizedDescription;
}
