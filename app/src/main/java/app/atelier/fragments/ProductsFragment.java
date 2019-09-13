package app.atelier.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
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
import app.atelier.adapters.ProductsAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.cart.CartItem;
import app.atelier.webservices.responses.cart.CartItem_;
import app.atelier.webservices.responses.cart.CartProductModel;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.orders.GetOrders;
import app.atelier.webservices.responses.orders.OrderModel;
import app.atelier.webservices.responses.products.GetProducts;
import app.atelier.webservices.responses.products.ProductModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductsFragment extends Fragment {
    public static FragmentActivity activity;
    public static ProductsFragment fragment;

    @BindView(R.id.recyclerView_productsList)
    RecyclerView productsList;
    @BindView(R.id.loading)
    ProgressBar loading;
    List<ProductModel> productsArrList = new ArrayList<>();
    ProductsAdapter productsAdapter;
    GridLayoutManager layoutManager;

    public static ProductsFragment newInstance(FragmentActivity activity,
                                               String title,
                                               String categoryId,
                                               String brandId) {
        fragment = new ProductsFragment();
        ProductsFragment.activity = activity;
        Bundle b = new Bundle();
        b.putString("title",title);
        b.putString("categoryId", categoryId);
        b.putString("brandId", brandId);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_products, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(getArguments().getString("title"));
        MainActivity.setupBottomAppbar("");
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        productsAdapter = new ProductsAdapter(activity,
                "cart",
                productsArrList,
                null
                , new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onAddCartClick(int position) {
                CartItem_ cartItem_ = new CartItem_();


                    cartItem_.productId = productsArrList.get(position).id;
                cartItem_.customerId = Integer.valueOf(SessionManager.getUserId(activity));
                cartItem_.quantity = 1;
                cartItem_.shoppingCartType = "ShoppingCart";
                CartItem cartItem = new CartItem();
                cartItem.shoppingCartItem = cartItem_;
                addCartOrFavoriteApi("addToCart",cartItem);
            }

            @Override
            public void onAddFavoriteClick(int position) {
                CartItem_ favoriteItem_ = new CartItem_();


                    favoriteItem_.productId = productsArrList.get(position).id;

                favoriteItem_.customerId = Integer.valueOf(SessionManager.getUserId(activity));
                favoriteItem_.quantity = 1;
                favoriteItem_.shoppingCartType = "Wishlist";
                CartItem favoriteItem = new CartItem();
                favoriteItem.shoppingCartItem = favoriteItem_;
                addCartOrFavoriteApi("addToFavorite",favoriteItem);

            }
        });
        layoutManager = new GridLayoutManager(activity, 2);
        productsList.setLayoutManager(layoutManager);
        productsList.setAdapter(productsAdapter);

            if (productsArrList.size() > 0) {
                loading.setVisibility(View.GONE);
            } else {
                if (getArguments().getString("brandId").equals("0"))
                    productsApi(null);
        }
    }


    public void productsApi(String s) {
        AtelierApiConfig.getCallingAPIInterface().products(
                Constants.AUTHORIZATION_VALUE,
                MainActivity.language,
                getArguments().getString("categoryId"),
                s,
                Constants.LIMIT,
                "1", null, null, null,
                new Callback<GetProducts>() {
                    @Override
                    public void success(GetProducts getProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getProducts != null) {
                            if (getProducts.products.size() > 0) {
                                productsArrList.addAll(getProducts.products);
                                productsAdapter.notifyDataSetChanged();
                            } else {
                                Snackbar.make(loading, getString(R.string.no_data), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(loading, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                    }
                }

        );
    }

    public void addCartOrFavoriteApi(final String type, CartItem cartItem) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().createShoppingCart(
                Constants.AUTHORIZATION_VALUE, MainActivity.language,
                Constants.CONTENT_TYPE_VALUE, cartItem,
                new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts getCartProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCartProducts != null) {
                            if (getCartProducts.CartProducts.size() > 0) {
                                if (type.equals("addToFavorite")) {
                                    Snackbar.make(loading,getString(R.string.product_favorites_added),Snackbar.LENGTH_SHORT).show();
                                } else {
                                    new AlertDialog.Builder(activity)
                                            .setTitle(activity.getString(R.string.message))
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
                                    MainActivity.cart.setImageResource(R.mipmap.icon_cart_with_notifi);
                                }
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(loading, error.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
