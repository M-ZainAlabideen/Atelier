package app.atelier.webservices.responses.addresses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressModel implements Serializable {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("company")
    @Expose
    public String company;
    @SerializedName("country_id")
    @Expose
    public Integer countryId;
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("state_province_id")
    @Expose
    public Integer stateProvinceId;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("address1")
    @Expose
    public String address1;
    @SerializedName("address2")
    @Expose
    public String address2;
    @SerializedName("zip_postal_code")
    @Expose
    public String zipPostalCode;
    @SerializedName("phone_number")
    @Expose
    public String phoneNumber;
    @SerializedName("fax_number")
    @Expose
    public String faxNumber;
    @SerializedName("created_on_utc")
    @Expose
    public String createdOnUtc;
    @SerializedName("province")
    @Expose
    public String province;

    public boolean isSelected;
}
