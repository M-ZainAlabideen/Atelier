package app.atelier.webservices.responses.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import app.atelier.webservices.responses.products.ProductModel;

public class OrderItems implements Serializable {
    @SerializedName("cards")
    @Expose
    public List<Cards> cardsList = null;
    @SerializedName("product_attributes")
    @Expose
    public List<OrderAttributeModel> productAttributes = null;
    @SerializedName("quantity")
    @Expose
    public Integer quantity;
    @SerializedName("unit_price_incl_tax")
    @Expose
    public Double unitPriceInclTax;
    @SerializedName("unit_price_excl_tax")
    @Expose
    public Double unitPriceExclTax;
    @SerializedName("price_incl_tax")
    @Expose
    public Double priceInclTax;
    @SerializedName("price_excl_tax")
    @Expose
    public Double priceExclTax;
    @SerializedName("discount_amount_incl_tax")
    @Expose
    public Double discountAmountInclTax;
    @SerializedName("discount_amount_excl_tax")
    @Expose
    public Double discountAmountExclTax;
    @SerializedName("original_product_cost")
    @Expose
    public Double originalProductCost;
    @SerializedName("attribute_description")
    @Expose
    public String attributeDescription;
    @SerializedName("download_count")
    @Expose
    public Integer downloadCount;
    @SerializedName("isDownload_activated")
    @Expose
    public Boolean isDownloadActivated;
    @SerializedName("license_download_id")
    @Expose
    public Integer licenseDownloadId;
    @SerializedName("item_weight")
    @Expose
    public Integer itemWeight;
    @SerializedName("rental_start_date_utc")
    @Expose
    public Object rentalStartDateUtc;
    @SerializedName("rental_end_date_utc")
    @Expose
    public Object rentalEndDateUtc;
    @SerializedName("product")
    @Expose
    public ProductModel product;
    @SerializedName("product_id")
    @Expose
    public Integer productId;
    @SerializedName("id")
    @Expose
    public Integer id;
}
