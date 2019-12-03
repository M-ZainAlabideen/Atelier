package app.atelier.webservices.responses.customers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.atelier.webservices.responses.addresses.AddressModel;

public class CustomerModel {
    @SerializedName("billing_address")
    @Expose
    public AddressModel billingAddress;

    @SerializedName("addresses")
    @Expose
    public List<AddressModel> addresses;

    @SerializedName("username")
    @Expose
    public String userName;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("first_name")
    @Expose
    public String firstName;

    @SerializedName("last_name")
    @Expose
    public String lastName;

    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("password")
    @Expose
    public String password;


    @SerializedName("role_ids")
    @Expose
    public List<Integer> roleIds;

    @SerializedName("id")
    @Expose
    public int id;

}
