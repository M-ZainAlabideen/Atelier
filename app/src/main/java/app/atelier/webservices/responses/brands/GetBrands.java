package app.atelier.webservices.responses.brands;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBrands {
    @SerializedName("manufacturers")
    @Expose
    public List<BrandModel> brands;
}
