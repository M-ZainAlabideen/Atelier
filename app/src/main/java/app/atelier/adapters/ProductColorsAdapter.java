package app.atelier.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.atelier.R;
import app.atelier.models.ProductColorsModel;
import butterknife.ButterKnife;

public class ProductColorsAdapter extends RecyclerView.Adapter<ProductColorsAdapter.viewHolder> {

    Context context;
    ArrayList<ProductColorsModel> productColorsArrayList;

    public ProductColorsAdapter(Context context, ArrayList<ProductColorsModel> productColorsArrayList) {
        this.context = context;
        this.productColorsArrayList = productColorsArrayList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ProductColorsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_product_color, viewGroup, false);
        return new ProductColorsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductColorsAdapter.viewHolder viewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return productColorsArrayList.size();
    }

}
