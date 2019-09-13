package app.atelier.webservices.responses.states;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StateModel {
    @SerializedName("country_id")
    @Expose
    public Integer countryId;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("abbreviation")
    @Expose
    public String abbreviation;
    @SerializedName("published")
    @Expose
    public Boolean published;
    @SerializedName("display_order")
    @Expose
    public Integer displayOrder;
    @SerializedName("id")
    @Expose
    public Integer id;
    
}
