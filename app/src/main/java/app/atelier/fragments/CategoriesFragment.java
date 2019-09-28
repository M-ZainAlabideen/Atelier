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
import app.atelier.adapters.CategoriesAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.categories.CategoryModel;
import app.atelier.webservices.responses.categories.GetCategories;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CategoriesFragment extends Fragment {
    public static FragmentActivity activity;
    public static CategoriesFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.categories_recyclerView_List)
    RecyclerView categoriesList;
    @BindView(R.id.loading)
    ProgressBar loading;


    ArrayList<CategoryModel> categoriesArrayList = new ArrayList<>();
    GridLayoutManager layoutManager;
    CategoriesAdapter categoriesAdapter;

    public static CategoriesFragment newInstance(FragmentActivity activity,
                                                 String flag,
                                                 String CategoryId) {
        fragment = new CategoriesFragment();
        CategoriesFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        Bundle b = new Bundle();
        b.putString("flag",flag);
        b.putString("CategoryId", CategoryId);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(getArguments().getString("flag"));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupAppbar("categories",true,true);

        MainActivity.categories.setImageResource(R.mipmap.icon_cate_sel);
        layoutManager = new GridLayoutManager(activity, 3);
        categoriesAdapter = new CategoriesAdapter(activity, categoriesArrayList);
        categoriesList.setLayoutManager(layoutManager);
        categoriesList.setAdapter(categoriesAdapter);

        if (categoriesArrayList.size() <= 0) {
            subcategoriesApi();
        } else {
           loading.setVisibility(View.GONE);
        }

    }

    public void subcategoriesApi() {
        AtelierApiConfig.getCallingAPIInterface().subcategories(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                getArguments().getString("CategoryId")
                , new Callback<GetCategories>() {
                    @Override
                    public void success(GetCategories getCategories, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCategories.categories.size() > 0) {
                            categoriesArrayList.addAll(getCategories.categories);
                            categoriesAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error,loading);

                    }
                });
    }



}
