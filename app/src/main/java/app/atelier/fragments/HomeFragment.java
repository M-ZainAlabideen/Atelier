package app.atelier.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.BrandsAdapter;
import app.atelier.adapters.SliderAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.WrapContentRtlViewPager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.brands.BrandModel;
import app.atelier.webservices.responses.brands.GetBrands;
import app.atelier.webservices.responses.categories.CategoryModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeFragment extends Fragment {
    public static FragmentActivity activity;
    public static HomeFragment fragment;

    @BindView(R.id.home_RtlViewPager_slider)
    WrapContentRtlViewPager slider;
    @BindView(R.id.home_circleIndicator_sliderCircle)
    CircleIndicator sliderCircle;
    @BindView(R.id.home_recyclerView_brandsList)
    RecyclerView brandsList;
    @BindView(R.id.loading)
    ProgressBar loading;
    ArrayList<String> sliderArrayList = new ArrayList<>();
    SliderAdapter sliderAdapter;

    ArrayList<BrandModel> brandsArrayList = new ArrayList<>();
    GridLayoutManager layoutManager;
    BrandsAdapter brandsAdapter;


    public static HomeFragment newInstance(FragmentActivity activity, String mainCategoryId) {
        fragment = new HomeFragment();
        HomeFragment.activity = activity;
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
        MainActivity.setupBottomAppbar("home");

        loading.setVisibility(View.VISIBLE);


        sliderArrayList.clear();
        sliderArrayList.add("http://www.gaanaa.net/up/uploads/1381628529055.jpg");
        sliderArrayList.add("https://i.ytimg.com/vi/0S40aHerXPE/hqdefault.jpg");
        sliderAdapter = new SliderAdapter(activity, sliderArrayList);
        slider.setAdapter(sliderAdapter);
        sliderCircle.setViewPager(slider);

        layoutManager = new GridLayoutManager(activity, 3);
        brandsAdapter = new BrandsAdapter(activity, brandsArrayList);
        brandsList.setLayoutManager(layoutManager);
        brandsList.setAdapter(brandsAdapter);

        if (sliderArrayList.size() <= 0) {
            //  ApiCall();
        } else {
            setData();
        }


        if (brandsArrayList.size() == 0) {
            getBrandsApi();
        } else {
            loading.setVisibility(View.GONE);
        }
    }


    public void getBrandsApi() {
        AtelierApiConfig.getCallingAPIInterface().brands(Constants.AUTHORIZATION_VALUE,
                MainActivity.language,getArguments().getString("mainCategoryId"),
                new Callback<GetBrands>() {
                    @Override
                    public void success(GetBrands getBrands, Response response) {
                        if (getBrands.brands.size() > 0) {
                            brandsArrayList.addAll(getBrands.brands);
                            brandsAdapter.notifyDataSetChanged();
                            loading.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(loading,activity.getResources().getString(R.string.error),Snackbar.LENGTH_LONG).show();

                    }
                });
    }

    public void sliderApi(){}

    public void setData() {
    }

    public void ApiCall2() {
        setData2();
    }

    public void setData2() {
    }
}