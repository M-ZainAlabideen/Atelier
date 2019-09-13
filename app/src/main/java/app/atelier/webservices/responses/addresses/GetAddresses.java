package app.atelier.webservices.responses.addresses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetAddresses {
    @SerializedName("addresses")
    @Expose
    public ArrayList<AddressModel> addresses = null;

    @SerializedName("address")
    @Expose
    public AddressModel address = null;

}
