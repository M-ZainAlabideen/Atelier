package app.atelier.webservices.responses.customers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.atelier.webservices.responses.errors.Errors;

public class GetCustomers {
    @SerializedName("customers")
    @Expose
    public List<CustomerModel> customers;


    @SerializedName("customer")
    @Expose
    public CustomerModel customer;


    @SerializedName("errors")
    @Expose
    public Errors errors;

}

