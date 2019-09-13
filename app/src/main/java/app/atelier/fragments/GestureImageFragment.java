package app.atelier.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duolingo.open.rtlviewpager.RtlViewPager;

import java.util.ArrayList;

import app.atelier.R;
import app.atelier.adapters.GestureImageAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GestureImageFragment extends Fragment {
    static FragmentActivity activity;
    static GestureImageFragment fragment;
    private ArrayList<String> imagesArrayList=new ArrayList<>();
    int position;

    @BindView(R.id.gestureImages_pager)
    RtlViewPager pager;
    public static GestureImageFragment newInstance(FragmentActivity activity, ArrayList<String> imagesArrayList, int position) {
        fragment = new GestureImageFragment();
        GestureImageFragment.activity = activity;
        fragment.imagesArrayList = imagesArrayList;
        fragment.position = position;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_gesture_image, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //hidden MainAppBarLayout and Ads for making the image fullScreen
        pager.setAdapter(new GestureImageAdapter(activity, imagesArrayList));
        pager.setCurrentItem(position);

    }
}
