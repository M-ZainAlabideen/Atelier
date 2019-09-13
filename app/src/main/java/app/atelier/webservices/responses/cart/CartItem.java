package app.atelier.webservices.responses.cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CartItem {
    @SerializedName("shopping_cart_item")
    @Expose
    public CartItem_ shoppingCartItem;
}
