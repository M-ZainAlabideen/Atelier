package app.atelier.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.SearchResultsAdapter;
import app.atelier.classes.Constants;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.products.GetProducts;
import app.atelier.webservices.responses.products.ProductModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchResultFragment extends Fragment {
    public static FragmentActivity activity;
    public static SearchResultFragment fragment;

    @BindView(R.id.search_recyclerView_searchList)
    RecyclerView searchList;
    @BindView(R.id.loading)
    ProgressBar loading;

    List<ProductModel> searchArrList = new ArrayList<>();
    SearchResultsAdapter searchAdapter;
    LinearLayoutManager layoutManager;

    public static SearchResultFragment newInstance(FragmentActivity activity,String query) {
        fragment = new SearchResultFragment();
        SearchResultFragment.activity = activity;
        Bundle b= new Bundle();
        b.putString("query",query);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_search_result, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupBottomAppbar("");

        layoutManager = new LinearLayoutManager(activity);
        searchAdapter = new SearchResultsAdapter(activity,searchArrList);
        searchList.setLayoutManager(layoutManager);
        searchList.setAdapter(searchAdapter);

        if(searchArrList.size() > 0){
            loading.setVisibility(View.GONE);
        }
        else {
            searchProductsApi(getArguments().getString("query"));
        }
    }

    public void searchProductsApi(String query){
        AtelierApiConfig.getCallingAPIInterface().searchProducts(
                Constants.AUTHORIZATION_VALUE,
                MainActivity.language,
                null,
                null,
                Constants.LIMIT,
                "1",
                query,
                null,
                null,
                null,
                null,
                null,
                new Callback<GetProducts>() {
                    @Override
                    public void success(GetProducts getProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        if(getProducts != null){
                            if(getProducts.products.size() > 0){
                                searchArrList.addAll(getProducts.products);
                                searchAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }

        );
    }

}
