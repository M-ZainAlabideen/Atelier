package app.atelier.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import app.atelier.R;
import app.atelier.classes.FixControl;
import app.atelier.webservices.responses.cart.CartProductModel;
import app.atelier.webservices.responses.orders.OrderModel;
import app.atelier.webservices.responses.products.ProductModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.viewHolder>  {

    Context context;
    String flag;
    List<ProductModel> productsArrList;
    List<CartProductModel> favoritesArrList;
    OnItemClickListener listener;

    public ProductsAdapter(Context context, String flag,
                           List<ProductModel> productsArrList,
                           List<CartProductModel> favoritesArrList,
                           OnItemClickListener listener) {
        this.context = context;
        this.productsArrList = productsArrList;
        this.favoritesArrList = favoritesArrList;
        this.flag = flag;
        this.listener = listener;
    }


    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.product_imgView_productImg)
        ImageView productImg;
        @BindView(R.id.product_imgView_addFavorite)
        ImageView addFavorite;
        @BindView(R.id.product_imgView_addCart)
        ImageView addCart;
        @BindView(R.id.product_txtView_title)
        TextView title;
        @BindView(R.id.product_txtView_price)
        TextView price;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ProductsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_product, viewGroup, false);
        return new ProductsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.viewHolder viewHolder, final int position) {
        if(flag.equals("favorite")){
            viewHolder.addFavorite.setImageResource(R.mipmap.icon_add_fav_sel);
            int Width = FixControl.getImageWidth(context, R.mipmap.placeholder_product);
            int Height = FixControl.getImageHeight(context, R.mipmap.placeholder_product);
            viewHolder.productImg.getLayoutParams().height = Height;
            viewHolder.productImg.getLayoutParams().width = Width;
            if (favoritesArrList.get(position).product.getPhoto() != null
                    && !favoritesArrList.get(position).product.getPhoto().matches("")
                    && !favoritesArrList.get(position).product.getPhoto().isEmpty()) {
                Glide.with(context.getApplicationContext()).load(favoritesArrList.get(position).product.getPhoto())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.placeholder_product))
                        .into(viewHolder.productImg);
            }

            viewHolder.title.setText(favoritesArrList.get(position).product.getLocalizedName());
            viewHolder.price.setText(String.valueOf(favoritesArrList.get(position).product.price)+
                    " "+context.getString(R.string.currency));
        }
        else{
            int Width = FixControl.getImageWidth(context, R.mipmap.placeholder_product);
            int Height = FixControl.getImageHeight(context, R.mipmap.placeholder_product);
            viewHolder.productImg.getLayoutParams().height = Height;
            viewHolder.productImg.getLayoutParams().width = Width;
            if (productsArrList.get(position).getPhoto() != null
                    && !productsArrList.get(position).getPhoto().matches("")
                    && !productsArrList.get(position).getPhoto().isEmpty()) {
                Glide.with(context.getApplicationContext()).load(productsArrList.get(position).getPhoto())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.placeholder_product))
                        .into(viewHolder.productImg);
            }

            viewHolder.title.setText(productsArrList.get(position).getLocalizedName());
            viewHolder.price.setText(String.valueOf(productsArrList.get(position).price)+
                    " "+context.getString(R.string.currency));

        }

        viewHolder.productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });

        viewHolder.addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddFavoriteClick(position);
            }
        });

        viewHolder.addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddCartClick(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        if(flag.equals("favorite")){
            return favoritesArrList.size();
        }
        else{
            return productsArrList.size();
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onAddCartClick(int position);

        void onAddFavoriteClick(int position);

    }

}

