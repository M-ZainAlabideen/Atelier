package app.atelier.webservices.responses.countries;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryModel {
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("allows_billing")
    @Expose
    public Boolean allowsBilling;
    @SerializedName("allows_shipping")
    @Expose
    public Boolean allowsShipping;
    @SerializedName("two_letter_iso_code")
    @Expose
    public String twoLetterIsoCode;
    @SerializedName("three_letter_iso_code")
    @Expose
    public String threeLetterIsoCode;
    @SerializedName("numeric_iso_code")
    @Expose
    public Integer numericIsoCode;
    @SerializedName("subject_to_vat")
    @Expose
    public Boolean subjectToVat;
    @SerializedName("published")
    @Expose
    public Boolean published;
    @SerializedName("display_order")
    @Expose
    public Integer displayOrder;
    @SerializedName("limited_to_stores")
    @Expose
    public Boolean limitedToStores;
    @SerializedName("id")
    @Expose
    public Integer id;
}
