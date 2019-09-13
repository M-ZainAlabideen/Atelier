package app.atelier.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.atelier.R;
import app.atelier.webservices.responses.countries.CountryModel;
import app.atelier.webservices.responses.states.StateModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PopUpAdapter extends RecyclerView.Adapter<PopUpAdapter.viewHolder> {
    Context context;
    List<CountryModel> countriesArrList;
    List<StateModel> statesArrList;
    String type;

    public PopUpAdapter(Context context, String type, List<CountryModel> countriesArrList, List<StateModel> statesArrList) {
        this.context = context;
        this.type = type;
        this.countriesArrList = countriesArrList;
        this.statesArrList = statesArrList;
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.popUp_Text)
        TextView Text;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public PopUpAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_pop_up, viewGroup, false);
        return new PopUpAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull PopUpAdapter.viewHolder viewHolder, final int position) {
        if (type.equals("country")) {
                viewHolder.Text.setText(countriesArrList.get(position).name);
        } else
            viewHolder.Text.setText(statesArrList.get(position).name);
    }

    @Override
    public int getItemCount() {
        if (type.equals("country"))
            return countriesArrList.size();
        else
            return statesArrList.size();
    }

}
