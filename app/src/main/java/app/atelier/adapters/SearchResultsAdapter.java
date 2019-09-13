package app.atelier.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import app.atelier.R;
import app.atelier.classes.FixControl;
import app.atelier.webservices.responses.products.ProductModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.viewHolder> {

    Context context;
    List<ProductModel> searchArrList;

    public SearchResultsAdapter(Context context, List<ProductModel> searchArrList) {
        this.context = context;
        this.searchArrList = searchArrList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.product_circleImgView_searchImg)
        CircleImageView searchImg;
        @BindView(R.id.product_txtView_title)
        TextView title;
        @BindView(R.id.product_txtView_description)
        TextView description;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public SearchResultsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_search_result, viewGroup, false);
        return new SearchResultsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsAdapter.viewHolder viewHolder, int position) {

        int Width = FixControl.getImageWidth(context, R.mipmap.placeholder_search_product);
        int Height = FixControl.getImageHeight(context, R.mipmap.placeholder_search_product);
        viewHolder.searchImg.getLayoutParams().height = Height;
        viewHolder.searchImg.getLayoutParams().width = Width;
        if (searchArrList.get(position).getPhoto() != null
                && !searchArrList.get(position).getPhoto().matches("")
                && !searchArrList.get(position).getPhoto().isEmpty()) {
            Glide.with(context.getApplicationContext()).load(searchArrList.get(position).getPhoto())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.placeholder_search_product))
                    .into(viewHolder.searchImg);
        }
        String localizedNameStr = searchArrList.get(position).getLocalizedName();
        String descriptionStr = searchArrList.get(position).getFullDescription();
        if(localizedNameStr.length() > 15)
            viewHolder.title.setText(localizedNameStr.substring(0,15)+"...");
        else
            viewHolder.title.setText(localizedNameStr);

        if(descriptionStr.length() > 35)
            viewHolder.description.setText(descriptionStr.substring(0,24)+"...");
        else
            viewHolder.description.setText(descriptionStr);
    }

    @Override
    public int getItemCount() {
        return searchArrList.size();
    }

}

