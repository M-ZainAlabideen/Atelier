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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.FixControl;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.fragments.OrderDetailsFragment;
import app.atelier.webservices.responses.orders.OrderModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.viewHolder> {

    Context context;
    List<OrderModel> ordersArrayList;
SessionManager sessionManager;

    public OrdersAdapter(Context context, List<OrderModel> ordersArrayList) {
        this.context = context;
        this.ordersArrayList = ordersArrayList;
        sessionManager = new SessionManager(context);
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


        String details = context.getString(R.string.order_status) + ": " + ordersArrayList.get(position).orderStatus;

        if (ordersArrayList.get(position).createdOnUtc != null) {
            details = details + "\n" + context.getString(R.string.order_date) + ": " + convertDateToString(ordersArrayList.get(position).createdOnUtc);
        }

        details = details + "\n" + context.getString(R.string.order_total) + ": " + ordersArrayList.get(position).orderTotal
                + " " + sessionManager.getCurrencyCode();

        viewHolder.orderDescription.setText(details);
        viewHolder.orderNo.setText(context.getString(R.string.order_number) + ": " + ordersArrayList.get(position).id);
        if (MainActivity.isEnglish) {
            viewHolder.arrow.setRotation(180);
        }
        viewHolder.orderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.loadFragment((FragmentActivity) context,
                        OrderDetailsFragment.newInstance((FragmentActivity) context, ordersArrayList.get(position)),
                        R.id.main_frameLayout_Container, true);
            }
        });

    }


    @Override
    public int getItemCount() {
        return ordersArrayList.size();
    }


    public static String convertDateToString(String date) {

        String dateResult = "";
        Locale locale = null;
        if (MainActivity.isEnglish)
            locale = new Locale("en");
        else
            locale = new Locale("ar");

        SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",locale);

        dateFormatter1.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat dateFormatter2 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa",locale);

        int index = date.lastIndexOf('/');

        try {

            dateResult = dateFormatter2.format(dateFormatter1.parse(date.substring(index + 1)));

        } catch (ParseException e) {

            e.printStackTrace();

        }

        return dateResult;

    }


}
