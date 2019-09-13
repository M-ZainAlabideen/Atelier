package app.atelier.webservices.responses.cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.atelier.webservices.responses.customers.CustomerModel;
import app.atelier.webservices.responses.orders.OrderAttributeModel;
import app.atelier.webservices.responses.products.ProductModel;

public class CartProductModel {
    @SerializedName("sub_total")
    @Expose
     public Double sub_total;
    @SerializedName("formatted_sub_total")
    @Expose
     public String formattedSubTotal;
    @SerializedName("formatted_item_sub_total")
    @Expose
     public String formatted_item_sub_total;

    @SerializedName("product_attributes")
    @Expose
     public List<OrderAttributeModel> productAttributes;


    @SerializedName("customer_entered_price")
    @Expose
     public Double customerEnteredPrice;
    @SerializedName("quantity")
    @Expose
     public Integer quantity;
    @SerializedName("rental_start_date_utc")
    @Expose
     public Object rentalStartDateUtc;
    @SerializedName("rental_end_date_utc")
    @Expose
     public Object rentalEndDateUtc;
    @SerializedName("created_on_utc")
    @Expose
     public String createdOnUtc;
    @SerializedName("updated_on_utc")
    @Expose
     public String updatedOnUtc;
    @SerializedName("shopping_cart_type")
    @Expose
     public String shoppingCartType;
    @SerializedName("product_id")
    @Expose
     public Integer productId;
    @SerializedName("product")
    @Expose
     public ProductModel product;
    @SerializedName("customer_id")
    @Expose
     public Integer customerId;
    @SerializedName("customer")
    @Expose
     public CustomerModel customer;
    @SerializedName("id")
    @Expose
     public Integer id;


}
