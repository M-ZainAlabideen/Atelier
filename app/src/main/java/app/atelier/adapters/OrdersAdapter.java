package app.atelier.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.FixControl;
import app.atelier.classes.Navigator;
import app.atelier.fragments.OrderDetailsFragment;
import app.atelier.webservices.responses.orders.OrderModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.viewHolder> {

    Context context;
    List<OrderModel> ordersArrayList;


    public OrdersAdapter(Context context, List<OrderModel> ordersArrayList) {
        this.context = context;
        this.ordersArrayList = ordersArrayList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.order_txtView_orderNo)
        public TextView orderNo;
        @BindView(R.id.order_txtView_orderDescription)
        public TextView orderDescription;
        @BindView(R.id.order_view_orderDetails)
        public View orderDetails;
        @BindView(R.id.order_imgView_arrow)
        public ImageView arrow;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public OrdersAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_order, viewGroup, false);
        return new OrdersAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrdersAdapter.viewHolder viewHolder, final int position) {


        String details = context.getString(R.string.order_status) + ": " +ordersArrayList.get(position).orderStatus;

        if(ordersArrayList.get(position).createdOnUtc != null){

            details = details + "\n" + context.getString(R.string.order_date) + ": " + ordersArrayList.get(position).createdOnUtc;

        }

        details = details + "\n" + context.getString(R.string.order_total) + ": " +ordersArrayList.get(position).orderTotal
       + " "+context.getString(R.string.currency);

        viewHolder.orderDescription.setText(details);
        viewHolder.orderNo.setText(context.getString(R.string.order_number) + ": " +ordersArrayList.get(position).id);
        if(MainActivity.isEnglish){
            viewHolder.arrow.setRotation(180);
        }
        viewHolder.orderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.loadFragment((FragmentActivity)context,
                        OrderDetailsFragment.newInstance((FragmentActivity)context,ordersArrayList.get(position)),
                        R.id.main_frameLayout_Container,true);
            }
        });

    }




    @Override
    public int getItemCount() {
        return ordersArrayList.size();
    }

}
