package app.atelier.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.Constants;
import app.atelier.classes.FixControl;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.LocaleHelper;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.categories.CategoryModel;
import app.atelier.webservices.responses.categories.GetCategories;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainCategoriesFragment extends Fragment {
    public static FragmentActivity activity;
    public static MainCategoriesFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.mainCategories_imgView_first)
    ImageView first;
    @BindView(R.id.mainCategories_imgView_second)
    ImageView second;
    @BindView(R.id.mainCategories_tv_firstTitle)
    TextView firstTitle;
    @BindView(R.id.mainCategories_tv_secondTitle)
    TextView secondTitle;
    @BindView(R.id.loading)
    ProgressBar loading;

    ArrayList<CategoryModel> mainCategoriesArrList = new ArrayList<>();

    public static MainCategoriesFragment newInstance(FragmentActivity activity) {
        fragment = new MainCategoriesFragment();
        MainCategoriesFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_main_categories, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.GONE);
        MainActivity.bottomAppbar.setVisibility(View.GONE);
        first.setVisibility(View.GONE);
        second.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        if (mainCategoriesArrList.size() == 0) {
            categoriesApi();
        } else {
            setData(mainCategoriesArrList);
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.mainCategories_imgView_first)
    public void suitsClick() {
        MainActivity.mainCategoryId = mainCategoriesArrList.get(0).id;
        Navigator.loadFragment(activity, HomeFragment.newInstance(activity, MainActivity.mainCategoryId), R.id.main_frameLayout_Container, true);
    }

    @OnClick(R.id.mainCategories_imgView_second)
    public void dressesClick() {
        MainActivity.mainCategoryId = mainCategoriesArrList.get(1).id;
        Navigator.loadFragment(activity, HomeFragment.newInstance(activity, MainActivity.mainCategoryId), R.id.main_frameLayout_Container, true);
    }

    @OnClick(R.id.mainCategories_tv_changeLang)
    public void changeLangClick(){
           /*for changing the language of App
        1- check the value of language in sharedPreference and Reflects the language
         2- set the new value of language in local and change the value of sharedPreference to new value
         3- restart the mainActivity with noAnimation
        * */

        if (sessionManager.getUserLanguage().equals("en")) {
            sessionManager.setUserLanguage("ar");
            MainActivity.isEnglish = false;
        }
        else{
            sessionManager.setUserLanguage("en");
            MainActivity.isEnglish = true;
        }
        LocaleHelper.setLocale(activity,sessionManager.getUserLanguage());

        activity.finish();
        activity.overridePendingTransition(0, 0);
        startActivity(new Intent(activity, MainActivity.class));

    }
    public void categoriesApi() {
        AtelierApiConfig.getCallingAPIInterface().categories(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                new Callback<GetCategories>() {
                    @Override
                    public void success(GetCategories getCategories, Response response) {
                        if (getCategories.categories != null) {
                            if (getCategories.categories.size() > 0) {
                                mainCategoriesArrList.addAll(getCategories.categories);
                                setData(mainCategoriesArrList);
                                first.setVisibility(View.VISIBLE);
                                second.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.GONE);

                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error,loading);
                    }
                });
    }

    public void setData(List<CategoryModel> mainCategoriesArrList) {
        int Width = FixControl.getImageWidth(activity, R.mipmap.dresses_icon);
        int Height = FixControl.getImageHeight(activity, R.mipmap.dresses_icon);
        first.getLayoutParams().height = Height;
       first.getLayoutParams().width = Width;
        if (mainCategoriesArrList.get(0).getPhoto() != null
                && !mainCategoriesArrList.get(0).getPhoto().matches("")
                && !mainCategoriesArrList.get(0).getPhoto().isEmpty()) {

            Glide.with(activity).load(mainCategoriesArrList.get(0).getPhoto())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.dresses_icon)
                            .error(R.mipmap.dresses_icon))
                    .into(first);
        }



    int Width2 = FixControl.getImageWidth(activity, R.mipmap.abaya_icon);
    int Height2 = FixControl.getImageHeight(activity, R.mipmap.abaya_icon);
        second.getLayoutParams().height = Height2;
       second.getLayoutParams().width = Width2;
        if (mainCategoriesArrList.get(1).getPhoto() != null
            && !mainCategoriesArrList.get(1).getPhoto().matches("")
                && !mainCategoriesArrList.get(1).getPhoto().isEmpty()) {

        Glide.with(activity).load(mainCategoriesArrList.get(1).getPhoto())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.abaya_icon)
                        .error(R.mipmap.abaya_icon))
                .into(second);
    }

        firstTitle.setText(mainCategoriesArrList.get(0).getLocalizedName());
        secondTitle.setText(mainCategoriesArrList.get(1).getLocalizedName());

    }
}