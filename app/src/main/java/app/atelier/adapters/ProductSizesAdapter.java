package app.atelier.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.atelier.R;
import app.atelier.models.ProductSizesModel;
import butterknife.ButterKnife;

public class ProductSizesAdapter extends RecyclerView.Adapter<ProductSizesAdapter.viewHolder> {

    Context context;
    ArrayList<ProductSizesModel> productSizesArrayList;

    public ProductSizesAdapter(Context context, ArrayList<ProductSizesModel> productSizesArrayList) {
        this.context = context;
        this.productSizesArrayList = productSizesArrayList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ProductSizesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_attribute_values_size, viewGroup, false);
        return new ProductSizesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSizesAdapter.viewHolder viewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return productSizesArrayList.size();
    }

}

