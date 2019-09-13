package app.atelier.webservices.responses.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cards {

    @SerializedName("purchased_with_order_item_id")
    @Expose
    private Integer purchasedWithOrderItemId;
    @SerializedName("card_type_id")
    @Expose
    private Integer cardTypeId;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("card_coupon_code")
    @Expose
    private String cardCouponCode;
    @SerializedName("is_card_activated")
    @Expose
    private Boolean isCardActivated;
    @SerializedName("is_redeemed")
    @Expose
    private Boolean isRedeemed;
    @SerializedName("created_on_utc")
    @Expose
    private String createdOnUtc;
    @SerializedName("redeemed_on_utc")
    @Expose
    private String redeemedOnUtc;
    @SerializedName("id")
    @Expose
    private Integer id;
}
