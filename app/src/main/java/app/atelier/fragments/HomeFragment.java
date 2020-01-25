package app.atelier.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.duolingo.open.rtlviewpager.RtlViewPager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.BrandsAdapter;
import app.atelier.adapters.SliderAdapter;
import app.atelier.classes.AppController;
import app.atelier.classes.Constants;
import app.atelier.classes.FixControl;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.SessionManager;
import app.atelier.classes.WrapContentRtlViewPager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.brands.BrandModel;
import app.atelier.webservices.responses.brands.GetBrands;
import app.atelier.webservices.responses.categories.CategoryModel;
import app.atelier.webservices.responses.sliders.GetSlider;
import app.atelier.webservices.responses.sliders.SliderModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeFragment extends Fragment {
    public static FragmentActivity activity;
    public static HomeFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.home_RtlViewPager_slider)
    RtlViewPager slider;
    @BindView(R.id.imageView5)
    ImageView imageView5;
    @BindView(R.id.home_circleIndicator_sliderCircle)
    CircleIndicator sliderCircle;
    @BindView(R.id.home_recyclerView_brandsList)
    RecyclerView brandsList;
    @BindView(R.id.loading)
    ProgressBar loading;
    ArrayList<SliderModel> sliderArrayList = new ArrayList<>();
    SliderAdapter sliderAdapter;

    ArrayList<BrandModel> brandsArrayList = new ArrayList<>();
    GridLayoutManager layoutManager;
    BrandsAdapter brandsAdapter;

    private int currentPage = 0;
    private int NUM_PAGES = 0;

    public static HomeFragment newInstance(FragmentActivity activity, String mainCategoryId) {
        fragment = new HomeFragment();
        HomeFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        Bundle b = new Bundle();
        b.putString("mainCategoryId", mainCategoryId);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(activity.getResources().getString(R.string.atelier));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupAppbar("home",true,true);

        AppController.getInstance().trackEvent("Atelier", "Details", "Mobile");
        AppController.getInstance().trackEvent("Atelier", "Details", "Android");

        loading.setVisibility(View.VISIBLE);

        layoutManager = new GridLayoutManager(activity, 3);
        brandsAdapter = new BrandsAdapter(activity, brandsArrayList);
        brandsList.setLayoutManager(layoutManager);
        brandsList.setAdapter(brandsAdapter);

        sliderAdapter = new SliderAdapter(activity, sliderArrayList);
        slider.setAdapter(sliderAdapter);
        sliderCircle.setViewPager(slider);

        int Height = FixControl.getImageHeight(activity, R.mipmap.placeholder_slider);
        imageView5.getLayoutParams().height = Height;

        if (sliderArrayList.size() <= 0) {
            sliderApi();
        }

        else
            NUM_PAGES = sliderArrayList.size();

            slider.setCurrentItem(0,true);
            // Auto start of viewpager
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == NUM_PAGES) {
                        currentPage = 0;
                    }
                    slider.setCurrentItem(currentPage++, true);
                }
            };
            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 3000, 3000);

            // Pager listener over indicator
            sliderCircle.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;

                }

                @Override
                public void onPageScrolled(int pos, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int pos) {

                }
            });



        if (brandsArrayList.size() == 0) {
            getBrandsApi();
        } else {
            loading.setVisibility(View.GONE);
        }
    }


    public void getBrandsApi() {
        AtelierApiConfig.getCallingAPIInterface().brands(Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),getArguments().getString("mainCategoryId"),
                new Callback<GetBrands>() {
                    @Override
                    public void success(GetBrands getBrands, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getBrands.brands.size() > 0) {
                            brandsArrayList.addAll(getBrands.brands);
                            brandsAdapter.notifyDataSetChanged();
                        }
                        else{
                            Snackbar.make(loading,getString(R.string.no_brands),Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error,loading);

                    }
                });
    }

    public void sliderApi(){
        AtelierApiConfig.getCallingAPIInterface().sliders(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
             getArguments().getString("mainCategoryId"),
                new Callback<GetSlider>() {
                    @Override
                    public void success(GetSlider getSlider, Response response) {
                        if(getSlider != null){
                            if(getSlider.sliders != null){
                                if(getSlider.sliders.size() > 0){
                                    for (SliderModel value: getSlider.sliders) {
                                        if(value.image != null){
                                            if(value.image.src != null){
                                                sliderArrayList.add(value);

                                            }
                                        }
                                    }
                                    sliderAdapter = new SliderAdapter(activity, sliderArrayList);
                                    slider.setAdapter(sliderAdapter);
                                    sliderCircle.setViewPager(slider);
                                    NUM_PAGES = sliderArrayList.size();

                                }
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error,loading);
                    }
                }
        );
    }

}