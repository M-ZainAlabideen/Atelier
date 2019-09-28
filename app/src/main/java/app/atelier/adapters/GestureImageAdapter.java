package app.atelier.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cuneytayyildiz.gestureimageview.GestureImageView;

import java.util.ArrayList;

import app.atelier.R;

public class GestureImageAdapter extends PagerAdapter {
    public final ArrayList<String> imagesArrayList;
    public Context context;

    public GestureImageAdapter(Context context, ArrayList<String> imagesArrayList) {

        this.context=context;
        this.imagesArrayList = imagesArrayList;
    }


    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = LayoutInflater.from(context).inflate(R.layout.item_gesture_image, view, false);
        assert imageLayout != null;
        final GestureImageView gestureImageItem = (GestureImageView) imageLayout.findViewById(R.id.gesture_image);
        if (imagesArrayList.get(position) != null
                && !imagesArrayList.get(position).matches("")
                && !imagesArrayList.get(position).isEmpty()) {

            Glide.with(context).load(imagesArrayList.get(position)).apply(new RequestOptions().placeholder(R.mipmap.placeholder_slider)).into(gestureImageItem);
        }
        view.addView(imageLayout, 0);
        return imageLayout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imagesArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}


