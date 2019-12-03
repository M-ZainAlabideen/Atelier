package app.atelier.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
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
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.attributes.AttributeModel;
import app.atelier.webservices.responses.attributes.AttributeValueModel;
import app.atelier.webservices.responses.attributes.OrderAttributeModel;
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
    SessionManager sessionManager;


    public CartAdapter(Context context, List<CartProductModel> cartArrayList, CartAdapter.OnItemClickListener listener) {
        this.context = context;
        this.cartArrayList = cartArrayList;
        this.listener = listener;
        sessionManager = new SessionManager(context);
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_linearLayout_background)
        public LinearLayout background;
        @BindView(R.id.cart_constLayout_foreground)
        public ConstraintLayout foreground;

        @BindView(R.id.cart_imgView_productImg)
        public ImageView productImg;
        @BindView(R.id.cart_imgView_delete2)
        public ImageView delete2;
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

        if (sessionManager.getUserLanguage().equals("en")) {
            Typeface enBold = Typeface.createFromAsset(context.getAssets(), "montserrat_medium.ttf");
            viewHolder.title.setTypeface(enBold);
        } else {
            Typeface arBold = Typeface.createFromAsset(context.getAssets(), "droid_arabic_kufi_bold.ttf");
            viewHolder.title.setTypeface(arBold);
        }
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

        if (localizedNameStr.length() > 15)
            viewHolder.title.setText(localizedNameStr.substring(0, 15) + "...");
        else
            viewHolder.title.setText(localizedNameStr);

        String s = "";
        for (OrderAttributeModel p : cartArrayList.get(position).productAttributes) {
            for (AttributeModel a : cartArrayList.get(position).product.attributes) {
                if (p.id == a.id) {
                    if (a.localized_names != null) {
                        s += a.localized_names.get(0).localizedName + ": ";
                    } else {
                        s += a.product_attribute_name + ": ";
                    }
//                    if (p.id == 17 || p.id == 18 || p.id == 19 || p.id == 20) {
//                        s += p.value + " " + context.getString(R.string.cm) + "\n";
//                    }
                     if (p.type.equals("MultilineTextbox") || p.type.equals("TextBox")) {
                        s += p.value + "\n";
                    } else {
                        for (AttributeValueModel av : a.attribute_values) {
                            if (p.value.equals(av.id)) {
                                s += av.localized_names.get(0).localizedName + " [" + av.price_after_adjustment + "]\n";
                            }
                        }
                    }
                }
            }
        }

        viewHolder.description.setText(s);

        viewHolder.price.setText(cartArrayList.get(position).formatted_item_sub_total);
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

        viewHolder.delete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteItemClick(position);
            }
        });
    }

    public void removeItem(int position) {
        listener.onDeleteItemClick(position);
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
