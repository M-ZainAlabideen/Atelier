package app.atelier.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import app.atelier.R;
import app.atelier.classes.Navigator;
import app.atelier.fragments.ProductDetailsFragment;
import app.atelier.webservices.responses.orders.OrderItems;
import app.atelier.webservices.responses.orders.OrderModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.viewHolder> {

    Context context;
    List<OrderItems> orderDetailsArrList;
    OrderModel order;


    public OrderDetailsAdapter(Context context, List<OrderItems> orderDetailsArrList,OrderModel order) {
        this.context = context;
        this.orderDetailsArrList = orderDetailsArrList;
        this.order = order;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.orderDetails_txtView_title)
        TextView title;
        @BindView(R.id.orderDetails_txtView_price)
        TextView price;
        @BindView(R.id.orderDetails_txtView_quantity)
        TextView quantity;
        @BindView(R.id.orderDetails_txtView_total)
        TextView total;
        @BindView(R.id.orderDetails_txtView_vendorName)
        TextView vendorName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public OrderDetailsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_order_details, viewGroup, false);
        return new OrderDetailsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderDetailsAdapter.viewHolder viewHolder, final int position) {

        viewHolder.title.setText(orderDetailsArrList.get(position).product.getLocalizedName() + "\n" +
                orderDetailsArrList.get(position).attributeDescription);

        if (orderDetailsArrList.get(position).product.vendor != null) {
            viewHolder.vendorName.setText(orderDetailsArrList.get(position).product.vendor.name);
        } else {
            viewHolder.vendorName.setVisibility(View.GONE);
        }

        viewHolder.quantity.setText(context.getString(R.string.quantity) + ": " +   orderDetailsArrList.get(position).quantity);
        viewHolder.price.setText(context.getString(R.string.price) + ": " + orderDetailsArrList.get(position).unitPriceInclTax
        + context.getString(R.string.currency));
        viewHolder.total.setText(context.getString(R.string.total_price) + ": " + orderDetailsArrList.get(position).priceExclTax
                + context.getString(R.string.currency));
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.loadFragment((FragmentActivity)context, ProductDetailsFragment.newInstance((FragmentActivity)context), R.id.main_frameLayout_Container, true);

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderDetailsArrList.size();
    }

}
