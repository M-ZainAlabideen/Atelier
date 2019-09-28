package app.atelier.webservices.responses.cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import app.atelier.webservices.responses.coupon.CouponOrderTotalModel;

public class GetCartProducts {
    @SerializedName("shopping_carts")
    @Expose
    public List<CartProductModel> CartProducts;

    @SerializedName("order_totals")
    @Expose
    public CouponOrderTotalModel couponOrderTotal;

}
