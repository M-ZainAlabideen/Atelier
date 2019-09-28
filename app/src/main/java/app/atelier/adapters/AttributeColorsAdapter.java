package app.atelier.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.util.ArrayList;

import app.atelier.R;
import app.atelier.fragments.ProductDetailsFragment;
import app.atelier.webservices.responses.attributes.AttributeValueModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AttributeColorsAdapter extends RecyclerView.Adapter<AttributeColorsAdapter.viewHolder> {

    Context context;
    ArrayList<AttributeValueModel> attributeValue;
    int index;
    ColorStateList csl;
    String color = "";
    int selectedPosition = -1;

    public AttributeColorsAdapter(Context context, ArrayList<AttributeValueModel> attributeValue, int index) {
        this.context = context;
        this.attributeValue = attributeValue;
        this.index = index;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.attributeValue_checkType)
        CheckBox checkBox;
        @BindView(R.id.attributeValue_radioType)
        RadioButton radioButton;
        @BindView(R.id.attributeValue_img_colorType)
        ImageView colorType;
        
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public AttributeColorsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_attribute_values, viewGroup, false);
        return new AttributeColorsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, final int position) {
        int imgH = ((BitmapDrawable) context.getResources().getDrawable(
                R.mipmap.box_sel)).getBitmap().getHeight();

        int imgW = ((BitmapDrawable) context.getResources().getDrawable(
                R.mipmap.box_sel)).getBitmap().getHeight();

        viewHolder.checkBox.getLayoutParams().height = imgH;
        viewHolder.checkBox.getLayoutParams().width = imgW;

        if (attributeValue.get(position) != null) {

            for (int i = 0; i < attributeValue.size(); i++) {
                color = attributeValue.get(position).color_squares_rgb;
                csl = new ColorStateList(new int[][]{new int[position]}, new int[]{Color.parseColor(color)});
                viewHolder.checkBox.setButtonTintList(csl);
                viewHolder.checkBox.setBackgroundColor(Color.parseColor(color));

                if (color.equals("#ffffff")) {
                    viewHolder.checkBox.setBackground(context.getDrawable(R.drawable.rounded_black));
                    viewHolder.colorType.setImageDrawable(context.getDrawable(R.mipmap.icon_check_mark_pink));
                } else {


                }
            }
            viewHolder.checkBox.setTag(position);
            if (position == selectedPosition) {
                viewHolder.checkBox.setChecked(true);
                viewHolder.colorType.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkBox.setChecked(false);
                viewHolder.colorType.setVisibility(View.GONE);

            }
            viewHolder.checkBox.setOnClickListener(onStateChangedListener(viewHolder.checkBox, position));

            viewHolder.radioButton.setVisibility(View.GONE);
            if (attributeValue.get(position).is_pre_selected) {
                viewHolder.checkBox.setChecked(true);
                viewHolder.colorType.setVisibility(View.VISIBLE);
            }

        }

            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < attributeValue.size(); i++) {
                            if (i == position) {
                                ProductDetailsFragment.attributesList.get(index).attribute_values
                                        .get(i).is_pre_selected = true;
                            } else {
                                ProductDetailsFragment.attributesList.get(index).
                                        attribute_values.get(i).is_pre_selected = false;
                            }
                        }
                    }
                    else {
                        ProductDetailsFragment.attributesList.get(index).attribute_values
                                .get(position).is_pre_selected = false;
                    }
                }
            });


        }

    private View.OnClickListener onStateChangedListener(final CheckBox checkBox, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    selectedPosition = position;
                }
                else {
                    selectedPosition = -1;
                }


                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return attributeValue.size();
    }

}
