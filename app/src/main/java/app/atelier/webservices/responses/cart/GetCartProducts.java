package app.atelier.webservices.responses.cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetCartProducts {
    @SerializedName("shopping_carts")
    @Expose
    public List<CartProductModel> CartProducts;

}
