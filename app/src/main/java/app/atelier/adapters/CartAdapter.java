package app.atelier.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.Constants;
import app.atelier.classes.FixControl;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.cart.CartItem;
import app.atelier.webservices.responses.cart.CartItem_;
import app.atelier.webservices.responses.cart.CartProductModel;
import app.atelier.webservices.responses.cart.GetCartProducts;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.viewHolder> {

    Context context;
    List<CartProductModel> cartArrayList;
    OnItemClickListener listener;


    public CartAdapter(Context context, List<CartProductModel> cartArrayList,CartAdapter.OnItemClickListener listener) {
        this.context = context;
        this.cartArrayList = cartArrayList;
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_linearLayout_background)
        public LinearLayout background;
        @BindView(R.id.cart_constLayout_foreground)
        public ConstraintLayout foreground;

        @BindView(R.id.cart_imgView_productImg)
        public ImageView productImg;
        @BindView(R.id.cart_txtView_title)
        public TextView title;
        @BindView(R.id.cart_txtView_description)
        public TextView description;
        @BindView(R.id.cart_txtView_price)
        public TextView price;
        @BindView(R.id.cart_txtView_quantity)
        public TextView quantity;
        @BindView(R.id.cart_imgView_plus)
        public ImageView plus;
        @BindView(R.id.cart_imgView_minus)
        public ImageView minus;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public CartAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_cart, viewGroup, false);
        return new CartAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartAdapter.viewHolder viewHolder, final int position) {


        int Width = FixControl.getImageWidth(context, R.mipmap.placeholder_cart_product);
        int Height = FixControl.getImageHeight(context, R.mipmap.placeholder_cart_product);
        viewHolder.productImg.getLayoutParams().height = Height;
        viewHolder.productImg.getLayoutParams().width = Width;
        if (cartArrayList.get(position).product.getPhoto() != null
                && !cartArrayList.get(position).product.getPhoto().matches("")
                && !cartArrayList.get(position).product.getPhoto().isEmpty()) {
            Glide.with(context.getApplicationContext()).load(cartArrayList.get(position).product.getPhoto())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.placeholder_cart_product))
                    .into(viewHolder.productImg);
        }
        String localizedNameStr = cartArrayList.get(position).product.getLocalizedName();
        String descriptionStr = cartArrayList.get(position).product.getFullDescription();
        if(localizedNameStr.length() > 15)
        viewHolder.title.setText(localizedNameStr.substring(0,15)+"...");
        else
            viewHolder.title.setText(localizedNameStr);

        if(descriptionStr.length() > 35)
            viewHolder.description.setText(descriptionStr.substring(0,24)+"...");
        else
            viewHolder.description.setText(descriptionStr);

        viewHolder.price.setText(cartArrayList.get(position).formattedSubTotal);
        viewHolder.quantity.setText(String.valueOf(cartArrayList.get(position).quantity));

        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onPlusClick(position);
            }
        });

        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMinusClick(position);

            }
        });
    }

    public void removeItem(int position) {
        listener.onDeleteItemClick(position);
//        cartArrayList.remove(position);
//        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onPlusClick(int position);

        void onMinusClick(int position);

        void onDeleteItemClick(int position);

    }



    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

}
