package app.atelier.fragments;

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
import app.atelier.classes.Navigator;
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

    @BindView(R.id.mainCategories_linearLayout_container)
    LinearLayout container;
    @BindView(R.id.mainCategories_imgView_first)
    ImageView first;
    @BindView(R.id.mainCategories_imgView_second)
    ImageView second;
    @BindView(R.id.mainCategories_txtView_firstTitle)
    TextView firstTitle;
    @BindView(R.id.mainCategories_txtView_secondTitle)
    TextView secondTitle;

    @BindView(R.id.loading)
    ProgressBar loading;

    ArrayList<CategoryModel> mainCategoriesArrList = new ArrayList<>();

    public static MainCategoriesFragment newInstance(FragmentActivity activity) {
        fragment = new MainCategoriesFragment();
        MainCategoriesFragment.activity = activity;
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
        container.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        if (mainCategoriesArrList.size() == 0) {
            categoriesApi();
        } else {
            setData(mainCategoriesArrList);
            container.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.mainCategories_view_suits)
    public void suitsClick() {
        MainActivity.mainCategoryId = mainCategoriesArrList.get(0).id;
        Navigator.loadFragment(activity, HomeFragment.newInstance(activity, MainActivity.mainCategoryId), R.id.main_frameLayout_Container, true);
    }

    @OnClick(R.id.mainCategories_view_dresses)
    public void dressesClick() {
        MainActivity.mainCategoryId = mainCategoriesArrList.get(1).id;
        Navigator.loadFragment(activity, HomeFragment.newInstance(activity, MainActivity.mainCategoryId), R.id.main_frameLayout_Container, true);
    }

    public void categoriesApi() {
        AtelierApiConfig.getCallingAPIInterface().categories(Constants.AUTHORIZATION_VALUE,
                MainActivity.language, new Callback<GetCategories>() {
                    @Override
                    public void success(GetCategories getCategories, Response response) {
                        if (getCategories.categories != null) {
                            if (getCategories.categories.size() > 0) {
                                mainCategoriesArrList.addAll(getCategories.categories);
                                setData(mainCategoriesArrList);
                                container.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.GONE);

                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(loading,activity.getResources().getString(R.string.error),Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public void setData(List<CategoryModel> mainCategoriesArrList) {
        int Width = FixControl.getImageWidth(activity, R.mipmap.icon_dress1);
        int Height = FixControl.getImageHeight(activity, R.mipmap.icon_dress1);
        first.getLayoutParams().height = Height;
       first.getLayoutParams().width = Width;
        if (mainCategoriesArrList.get(0).getPhoto() != null
                && !mainCategoriesArrList.get(0).getPhoto().matches("")
                && !mainCategoriesArrList.get(0).getPhoto().isEmpty()) {

            Glide.with(activity).load(mainCategoriesArrList.get(0).getPhoto())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.icon_dress1)
                            .error(R.mipmap.icon_dress1))
                    .into(first);
        }



    int Width2 = FixControl.getImageWidth(activity, R.mipmap.icon_dress2);
    int Height2 = FixControl.getImageHeight(activity, R.mipmap.icon_dress2);
        second.getLayoutParams().height = Height2;
       second.getLayoutParams().width = Width2;
        if (mainCategoriesArrList.get(1).getPhoto() != null
            && !mainCategoriesArrList.get(1).getPhoto().matches("")
                && !mainCategoriesArrList.get(1).getPhoto().isEmpty()) {

        Glide.with(activity).load(mainCategoriesArrList.get(1).getPhoto())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.icon_dress2)
                        .error(R.mipmap.icon_dress2))
                .into(second);
    }

        firstTitle.setText(mainCategoriesArrList.get(0).getLocalizedName());
        secondTitle.setText(mainCategoriesArrList.get(1).getLocalizedName());
}
}