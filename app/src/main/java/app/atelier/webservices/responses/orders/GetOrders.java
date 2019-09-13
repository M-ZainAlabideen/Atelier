package app.atelier.webservices.responses.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetOrders {
    @SerializedName("orders")
    @Expose
    public List<OrderModel> orders;

    @SerializedName("order")
    @Expose
    public OrderModel order;

}
