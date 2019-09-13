package app.atelier.webservices.responses.vendors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetVendors {
    @SerializedName("vendors")
    @Expose
    public List<VendorModel> vendors;
}
