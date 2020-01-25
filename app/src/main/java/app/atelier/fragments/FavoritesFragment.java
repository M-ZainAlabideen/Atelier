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
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.cart.CartItem;
import app.atelier.webservices.responses.cart.CartItem_;
import app.atelier.webservices.responses.cart.CartProductModel;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.products.GetProducts;
import app.atelier.webservices.responses.products.ProductModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

public class FavoritesFragment extends Fragment {
    public static FragmentActivity activity;
    public static FavoritesFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.recyclerView_productsList)
    RecyclerView productsList;
    @BindView(R.id.loading)
    ProgressBar loading;
    List<CartProductModel> favoritesArrList = new ArrayList<>();
    ProductsAdapter productsAdapter;
    GridLayoutManager layoutManager;

    public static FavoritesFragment newInstance(FragmentActivity activity) {
        fragment = new FavoritesFragment();
        FavoritesFragment.activity = activity;
        sessionManager = new SessionManager(activity);
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
        MainActivity.title.setText(getString(R.string.my_favorite));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupAppbar("favorite", true, true);

        productsAdapter = new ProductsAdapter(activity,
                "favorite",
                null,
                favoritesArrList,
                new ProductsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Navigator.loadFragment(activity, ProductDetailsFragment.newInstance(activity, favoritesArrList.get(position).productId),
                                R.id.main_frameLayout_Container, true);
                    }

                    @Override
                    public void onAddCartClick(int position) {
                        if (favoritesArrList.get(position).product.attributes != null && favoritesArrList.get(position).product.attributes.size() > 0) {
                            Navigator.loadFragment(activity, ProductDetailsFragment.newInstance(activity, favoritesArrList.get(position).productId), R.id.main_frameLayout_Container, true);
                        } else {
                            CartProductsApi(favoritesArrList.get(position).product.vendorId, position);
                        }
                    }

                    @Override
                    public void onAddFavoriteClick(int position, ImageView addFavorite) {
                        if (addFavorite.getDrawable().getConstantState() ==
                                getResources().getDrawable(R.mipmap.icon_add_fav_sel).getConstantState()) {
                            deleteFavoriteApi(position, addFavorite);
                        } else {

                            CartItem_ favoriteItem_ = new CartItem_();
                            favoriteItem_.productId = favoritesArrList.get(position).productId;
                            favoriteItem_.customerId = Integer.valueOf(sessionManager.getUserId());
                            favoriteItem_.quantity = 1;
                            favoriteItem_.shoppingCartType = "2";
                            CartItem favoriteItem = new CartItem();
                            favoriteItem.shoppingCartItem = favoriteItem_;
                            addCartOrFavoriteApi("addToFavorite", favoriteItem);

                        }
                    }
                });
        layoutManager = new GridLayoutManager(activity, 2);
        productsList.setLayoutManager(layoutManager);
        productsList.setAdapter(productsAdapter);

        favoritesApi();
    }


    public void favoritesApi() {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().shoppingCartItems(
                Constants.AUTHORIZATION_VALUE, sessionManager.getUserLanguage()
                , sessionManager.getUserId(),
                "2",
                new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts getFavorites, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getFavorites != null) {
                            if (getFavorites.CartProducts.size() > 0) {
                                favoritesArrList.clear();
                                favoritesArrList.addAll(getFavorites.CartProducts);
                                productsAdapter.notifyDataSetChanged();
                            } else {
                                Snackbar.make(loading, getString(R.string.empty_favorites), Snackbar.LENGTH_SHORT).show();
                            }
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

    public void addCartOrFavoriteApi(final String type, CartItem cartItem) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().createShoppingCart(
                Constants.AUTHORIZATION_VALUE, sessionManager.getUserLanguage(),
                Constants.CONTENT_TYPE_VALUE, cartItem,
                new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts getCartProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCartProducts != null) {
                            if (getCartProducts.CartProducts.size() > 0) {
                                if (type.equals("addToFavorite")) {
                                    Snackbar.make(loading, getString(R.string.product_favorites_added), Snackbar.LENGTH_SHORT).show();
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
                        GlobalFunctions.showErrorMessage(error, loading);
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
                String.valueOf(favoritesArrList.get(position).productId),
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

                                favoritesArrList.remove(position);
                                productsList.getAdapter().notifyItemRemoved(position);
                                productsList.getAdapter().notifyItemRangeChanged(0, favoritesArrList.size(), favoritesArrList);
                                if (favoritesArrList.size() == 0) {
                                    Snackbar.make(loading, activity.getResources().getString(R.string.empty_favorites), Snackbar.LENGTH_LONG).show();
                                    addFavorite.setImageResource(R.mipmap.icon_add_fav_unsel);
                                }

                            }
                        }

                        loading.setVisibility(View.GONE);
                    }


                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error, loading);
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
                            cartItem_.productId = favoritesArrList.get(position).productId;
                            cartItem_.customerId = Integer.valueOf(sessionManager.getUserId());
                            cartItem_.quantity = 1;
                            cartItem_.shoppingCartType = "1";
                            CartItem cartItem = new CartItem();
                            cartItem.shoppingCartItem = cartItem_;
                            addCartOrFavoriteApi("addToCart", cartItem);
                        } else {
                            Snackbar.make(loading, getString(R.string.selectTheSameVendor), Snackbar.LENGTH_SHORT).show();
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
