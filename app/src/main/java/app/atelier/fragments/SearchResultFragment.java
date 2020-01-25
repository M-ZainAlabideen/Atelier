package app.atelier.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.ProductsAdapter;
import app.atelier.adapters.SearchResultsAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.cart.CartItem;
import app.atelier.webservices.responses.cart.CartItem_;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.products.GetProducts;
import app.atelier.webservices.responses.products.ProductModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

public class SearchResultFragment extends Fragment {
    public static FragmentActivity activity;
    public static SearchResultFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.search_recyclerView_searchList)
    RecyclerView searchList;
    @BindView(R.id.loading)
    ProgressBar loading;

    List<ProductModel> searchArrList = new ArrayList<>();
    ProductsAdapter searchAdapter;
    GridLayoutManager layoutManager;

    public int pageIndex = 1;
    private boolean isLoading = false;
    public boolean isLastPage = false;

    public static SearchResultFragment newInstance(FragmentActivity activity,String query) {
        fragment = new SearchResultFragment();
        SearchResultFragment.activity = activity;
        sessionManager = new SessionManager(activity);
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
        MainActivity.setupAppbar("",true,true);

        layoutManager = new GridLayoutManager(activity,2);
        searchAdapter = new ProductsAdapter(activity, "search", searchArrList, null, new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Navigator.loadFragment(activity, ProductDetailsFragment.newInstance(activity, searchArrList.get(position).id),
                        R.id.main_frameLayout_Container, true);
            }

            @Override
            public void onAddCartClick(int position) {
                CartProductsApi(searchArrList.get(position).vendorId,position);
            }

            @Override
            public void onAddFavoriteClick(int position, ImageView addFavorite) {
                if (addFavorite.getDrawable().getConstantState() ==
                        getResources().getDrawable(R.mipmap.icon_add_fav_sel).getConstantState()) {
                    deleteFavoriteApi(position, addFavorite);
                } else {
                    CartItem_ favoriteItem_ = new CartItem_();
                    favoriteItem_.productId = searchArrList.get(position).id;
                    favoriteItem_.customerId = Integer.valueOf(sessionManager.getUserId());
                    favoriteItem_.quantity = 1;
                    favoriteItem_.shoppingCartType = "2";
                    CartItem favoriteItem = new CartItem();
                    favoriteItem.shoppingCartItem = favoriteItem_;
                    addCartOrFavoriteApi("addToFavorite", favoriteItem, addFavorite);

                }
            }
        });
        searchList.setLayoutManager(layoutManager);
        searchList.setAdapter(searchAdapter);

        if(searchArrList.size() > 0){
            loading.setVisibility(View.GONE);
        }
        else {
            searchProductsApi(getArguments().getString("query"));
        }
    }

    public void searchProductsApi(final String query){
        AtelierApiConfig.getCallingAPIInterface().searchProducts(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                null,
                null,
                Constants.LIMIT,
                String.valueOf(pageIndex),
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
                        if(getProducts != null) {
                            if(getProducts.products != null){
                                searchArrList.clear();
                                searchArrList.addAll(getProducts.products);
                                //after response coming , must notify the Adapter to make the data visible on screen
                                searchAdapter.notifyDataSetChanged();
                                if (searchArrList.size() == 0) {
                                    //if the size of newsList equal 0 , it's mean no data and make lastPage true
                                    isLastPage = true;
                                    searchList.setVisibility(View.GONE);
                                    Snackbar.make(loading,getString(R.string.empty_search),Snackbar.LENGTH_SHORT).show();

                                } else {

                                    //add Scroll listener to the recycler , for pagination
                                    searchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                        }

                                        @Override
                                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                            super.onScrolled(recyclerView, dx, dy);
                                            if (!isLastPage) {
                                                int visibleItemCount = layoutManager.getChildCount();

                                                int totalItemCount = layoutManager.getItemCount();

                                                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                                /*isLoading variable used for check if the user send many requests
                                for pagination(make many scrolls in the same time)
                                1- if isLoading true >> there is request already sent so,
                                no more requests till the response of last request coming
                                2- else >> send new request for load more data (News)*/
                                                if (!isLoading) {

                                                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                                        isLoading = true;

                                                        pageIndex = pageIndex + 1;

                                                        getMoreProducts(query);

                                                    }
                                                }
                                            }
                                        }
                                    });
                                }


                            }}
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error,loading);
                    }
                }

        );
    }

    public void getMoreProducts(String query){
        AtelierApiConfig.getCallingAPIInterface().searchProducts(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                null,
                null,
                Constants.LIMIT,
                String.valueOf(pageIndex),
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
                        if (getProducts != null) {

                            if (getProducts.products.isEmpty()) {

                                isLastPage = true;
                                pageIndex = pageIndex - 1;
                            }
                            else{
                                searchArrList.addAll(getProducts.products);
                                searchAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;


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

    public void addCartOrFavoriteApi(final String type, CartItem cartItem, final ImageView addFavorite) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().createShoppingCart(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                Constants.CONTENT_TYPE_VALUE, cartItem,
                new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts getCartProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCartProducts != null) {
                            if (getCartProducts.CartProducts.size() > 0) {
                                if (type.equals("addToFavorite")) {
                                    Snackbar.make(loading, getString(R.string.product_favorites_added), Snackbar.LENGTH_SHORT).show();
                                    addFavorite.setImageResource(R.mipmap.icon_add_fav_sel);
                                } else {
                                    new AlertDialog.Builder(activity)
                                            .setTitle(activity.getString(R.string.app_name))
                                            .setMessage(activity.getString(R.string.product_added))
                                            .setPositiveButton(R.string.continue_shopping, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton(activity.getString(R.string.checkout), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Navigator.loadFragment(activity, CartFragment.newInstance(activity), R.id.main_frameLayout_Container, true);
                                                }
                                            })
                                            .setIcon(R.mipmap.logo)
                                            .show();
                                    MainActivity.emptyCart = false;
                                    MainActivity.notification.setVisibility(View.VISIBLE);
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

    private void deleteFavoriteApi(final int position, final ImageView addFavorite) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().deleteFavoriteItem(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                "2",
                String.valueOf(searchArrList.get(position).id),
                sessionManager.getUserId(),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        TypedInput body = response.getBody();
                        String outResponse = "";

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                            StringBuilder out = new StringBuilder();
                            String newLine = System.getProperty("line.separator");
                            String line;
                            while ((line = reader.readLine()) != null) {
                                out.append(line);
                                out.append(newLine);
                            }

                            outResponse = out.toString();

                        } catch (Exception ex) {

                            ex.printStackTrace();
                        }

                        if (outResponse != null) {
                            outResponse = outResponse.replace("\"", "");
                            outResponse = outResponse.replace("\n", "");
                            if (outResponse.equalsIgnoreCase("{}")) {
                                Snackbar.make(loading, getString(R.string.unfavorite), Snackbar.LENGTH_SHORT).show();
                                addFavorite.setImageResource(R.mipmap.icon_add_fav_unsel);
                            }
                        }

                        loading.setVisibility(View.GONE);
                    }


                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error,loading);
                    }
                }
        );

    }



    public void CartProductsApi(final int selectedVendorId, final int position) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().shoppingCartItems(
                Constants.AUTHORIZATION_VALUE, sessionManager.getUserLanguage()
                , sessionManager.getUserId(), "1", new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts getCartProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        if ((getCartProducts.CartProducts.size() > 0 && selectedVendorId == getCartProducts.CartProducts.get(0).product.vendorId)
                                || getCartProducts.CartProducts.size() == 0) {
                            CartItem_ cartItem_ = new CartItem_();
                            cartItem_.productId = searchArrList.get(position).id;
                            cartItem_.customerId = Integer.valueOf(sessionManager.getUserId());
                            cartItem_.quantity = 1;
                            cartItem_.shoppingCartType = "1";
                            CartItem cartItem = new CartItem();
                            cartItem.shoppingCartItem = cartItem_;
                            addCartOrFavoriteApi("addToCart", cartItem, null);
                        } else {
                            Snackbar.make(loading,getString(R.string.selectTheSameVendor),Snackbar.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error, loading);
                    }
                }
        );
    }
}
