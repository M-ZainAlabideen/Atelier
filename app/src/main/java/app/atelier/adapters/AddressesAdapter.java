package app.atelier.adapters;

import android.content.Context;
import android.graphics.Color;
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
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.FixControl;
import app.atelier.classes.Navigator;
import app.atelier.fragments.ProductsFragment;
import app.atelier.webservices.responses.addresses.AddressModel;
import app.atelier.webservices.responses.brands.BrandModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.viewHolder> {

    Context context;
    List<AddressModel> addressesArrList;
    OnItemClickListener listener;
    String flag;

    public AddressesAdapter(Context context,
                            List<AddressModel> addressesArrList,
                            String flag,
                            OnItemClickListener listener) {
        this.context = context;
        this.addressesArrList = addressesArrList;
        this.listener = listener;
        this.flag = flag;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.address_txtView_title)
        TextView title;
        @BindView(R.id.address_txtView_delete)
        TextView delete;
        @BindView(R.id.address_txtView_edit)
        TextView edit;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public AddressesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_address, viewGroup, false);
        return new AddressesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressesAdapter.viewHolder viewHolder, final int position) {

        if (flag.equalsIgnoreCase("cart")) {
            viewHolder.delete.setVisibility(View.GONE);
            viewHolder.edit.setVisibility(View.GONE);

            if (addressesArrList.get(position).isSelected) {
                viewHolder.title.setTextColor(Color.parseColor("#ff5b8a"));
            }
        }
        viewHolder.title.setText(getFullAddress(addressesArrList.get(position)));

        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemDeleteClick(position);
            }
        });

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemEditClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressesArrList.size();
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onItemDeleteClick(int position);

        void onItemEditClick(int position);

    }

    private String getFullAddress(AddressModel addresses) {

        String content = "";
        content = context.getString(R.string.name)+": "+ addresses.firstName + " " + addresses.lastName;
        if (addresses.email.length() > 0) {
            content = content + "\n" + context.getString(R.string.mail) + ": " + addresses.email;
        }
        if (addresses.phoneNumber.length() > 0) {
            content = content + "\n" + context.getString(R.string.phone2) + ": " + addresses.phoneNumber;
        }
        if (addresses.province != null)
            if (addresses.province.length() > 0) {
                content = content + "\n" +context.getString(R.string.state)+": " +addresses.province;
            }
        if (addresses.country != null)
            if (addresses.country.length() > 0) {
                content = content + "\n" + context.getString(R.string.country)+": " +addresses.country;
            }
        return content;

    }

}

