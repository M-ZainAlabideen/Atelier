package app.atelier.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.FixControl;
import app.atelier.classes.Navigator;
import app.atelier.fragments.CategoriesFragment;
import app.atelier.fragments.HomeFragment;
import app.atelier.fragments.ProductsFragment;
import app.atelier.webservices.responses.brands.BrandModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.viewHolder> {

    Context context;
    ArrayList<BrandModel> brandsArrayList;

    public BrandsAdapter(Context context, ArrayList<BrandModel> brandsArrayList) {
        this.context = context;
        this.brandsArrayList = brandsArrayList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.shop_circleImgView_shopImg)
        ImageView shopImg;
        @BindView(R.id.shop_txtView_shopName)
        TextView shopName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public BrandsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_brand, viewGroup, false);
        return new BrandsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandsAdapter.viewHolder viewHolder, final int position) {
        int Width = FixControl.getImageWidth(context, R.mipmap.placeholder_brands);
        int Height = FixControl.getImageHeight(context, R.mipmap.placeholder_brands);
        viewHolder.shopImg.getLayoutParams().height = Height;
        viewHolder.shopImg.getLayoutParams().width = Width;
        if (brandsArrayList.get(position).getPhoto() != null
                && !brandsArrayList.get(position).getPhoto().matches("")
                && !brandsArrayList.get(position).getPhoto().isEmpty()) {

            Glide.with(context).load(brandsArrayList.get(position).getPhoto())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.placeholder_brands)
                            .error(R.mipmap.placeholder_brands))
                    .into(viewHolder.shopImg);
        }
        if (brandsArrayList.get(position).getLocalizedName() != null && !brandsArrayList.get(position).getLocalizedName().matches("")) {
            if (brandsArrayList.get(position).getLocalizedName().length() >= 10) {
                viewHolder.shopName.setText(brandsArrayList.get(position).getLocalizedName().substring(0, 9));
            } else {
                viewHolder.shopName.setText(brandsArrayList.get(position).getLocalizedName());
            }
        }

        viewHolder.shopImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.brandId = brandsArrayList.get(position).id;
                Navigator.loadFragment((FragmentActivity) context,
                        ProductsFragment.newInstance((FragmentActivity) context,
                                brandsArrayList.get(position).getLocalizedName(),
                                MainActivity.mainCategoryId,
                                MainActivity.brandId),
                        R.id.main_frameLayout_Container,
                        true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brandsArrayList.size();
    }

}
