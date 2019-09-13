package app.atelier.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import app.atelier.R;
import app.atelier.classes.FixControl;
import app.atelier.classes.Navigator;
import app.atelier.fragments.GestureImageFragment;

public class SliderAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> sliderArrayList;

    public SliderAdapter(Context context, ArrayList<String> sliderArrayList) {
        this.context = context;
        this.sliderArrayList = sliderArrayList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_slider, container, false);
        assert childView != null;
        ImageView sliderImage = (ImageView) childView.findViewById(R.id.slider_imgView_sliderImg);

        int Width = FixControl.getImageWidth(context, R.mipmap.placeholder_slider);
        int Height = FixControl.getImageHeight(context, R.mipmap.placeholder_slider);
        sliderImage.getLayoutParams().height = Height;
        sliderImage.getLayoutParams().width = Width;
        if (sliderArrayList.get(position) != null
                && !sliderArrayList.get(position).matches("")
                && !sliderArrayList.get(position).isEmpty()) {
            Glide.with(context.getApplicationContext()).load(sliderArrayList.get(position)).apply(new RequestOptions()
                    .placeholder(R.mipmap.placeholder_slider)).into(sliderImage);
        }
        sliderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.loadFragment((FragmentActivity) context, GestureImageFragment.newInstance((FragmentActivity) context, sliderArrayList, position), R.id.main_frameLayout_Container, true);
            }
        });

        container.addView(childView, 0);
        return childView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return sliderArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }

}