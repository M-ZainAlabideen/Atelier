package app.atelier.webservices.responses.cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.atelier.webservices.responses.attributes.OrderAttributeModel;

public class CartItem_ {
    @SerializedName("shopping_cart_type")
    @Expose
    public String shoppingCartType;
    @SerializedName("quantity")
    @Expose
    public Integer quantity;
    @SerializedName("product_id")
    @Expose
    public Integer productId;
    @SerializedName("customer_id")
    @Expose
    public Integer customerId;

    @SerializedName("product_attributes")
    @Expose
    public List<OrderAttributeModel> product_attributes;
}
