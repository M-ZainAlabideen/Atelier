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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import app.atelier.R;
import app.atelier.classes.FixControl;
import app.atelier.classes.Navigator;
import app.atelier.fragments.CategoriesFragment;
import app.atelier.fragments.ProductsFragment;
import app.atelier.webservices.responses.categories.CategoryModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.viewHolder> {

    Context context;
    ArrayList<CategoryModel> categoriesArrayList;

    public CategoriesAdapter(Context context, ArrayList<CategoryModel> categoriesArrayList) {
        this.context = context;
        this.categoriesArrayList = categoriesArrayList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_imgView_categoryImg)
        ImageView categoryImg;
        @BindView(R.id.category_txtView_title)
        TextView categoryTitle;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public CategoriesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_category, viewGroup, false);
        return new CategoriesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.viewHolder viewHolder, final int position) {
        int Width = FixControl.getImageWidth(context, R.mipmap.placeholder_category);
        int Height = FixControl.getImageHeight(context, R.mipmap.placeholder_category);
        viewHolder.categoryImg.getLayoutParams().height = Height;
        viewHolder.categoryImg.getLayoutParams().width = Width;
        if (categoriesArrayList.get(position).getPhoto() != null
                && !categoriesArrayList.get(position).getPhoto().matches("")) {
            Glide.with(context).load(categoriesArrayList.get(position).getPhoto())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.placeholder_category)
                            .error(R.mipmap.placeholder_category))
                    .into(viewHolder.categoryImg);
        }
        if (categoriesArrayList.get(position).getLocalizedName() != null &&
                !categoriesArrayList.get(position).getLocalizedName().matches("")) {
            if (categoriesArrayList.get(position).getLocalizedName().length() >= 10) {
                viewHolder.categoryTitle.setText(categoriesArrayList.get(position).getLocalizedName().substring(0, 9));
            } else {
                viewHolder.categoryTitle.setText(categoriesArrayList.get(position).getLocalizedName());
            }
        }

        viewHolder.categoryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoriesArrayList.get(position).hasSubCategories) {
                    Navigator.loadFragment((FragmentActivity) context,
                            CategoriesFragment.newInstance((FragmentActivity) context,
                                    categoriesArrayList.get(position).getLocalizedName(),
                                    categoriesArrayList.get(position).id),
                            R.id.main_frameLayout_Container,
                            true);

                } else {
                    Navigator.loadFragment((FragmentActivity) context,
                            ProductsFragment.newInstance((FragmentActivity) context,
                                    categoriesArrayList.get(position).getLocalizedName(),
                                    categoriesArrayList.get(position).id,
                                    "0"),
                            R.id.main_frameLayout_Container,
                            true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesArrayList.size();
    }

}
