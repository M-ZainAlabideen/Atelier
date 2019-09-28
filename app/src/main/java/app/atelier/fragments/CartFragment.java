package app.atelier.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.CartAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.Navigator;
import app.atelier.classes.RecyclerItemTouchHelper;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.cart.CartItem;
import app.atelier.webservices.responses.cart.CartItem_;
import app.atelier.webservices.responses.cart.CartProductModel;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.coupon.GetCouponOrderTotal;
import app.atelier.webservices.responses.customers.GetCustomers;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

public class CartFragment extends Fragment {
    public static FragmentActivity activity;
    public static CartFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.cart_recyclerView_cartList)
    RecyclerView cartList;

    @BindView(R.id.Cart_tv_discount)
    TextView discount;
    @BindView(R.id.Cart_tv_total)
    TextView total;
    @BindView(R.id.Cart_tv_subtotalValue)
    TextView subtotalValue;
    @BindView(R.id.Cart_tv_discountValue)
    TextView discountValue;
    @BindView(R.id.Cart_tv_totalValue)
    TextView totalValue;
    @BindView(R.id.Cart_et_coupon)
    TextView coupon;

    @BindView(R.id.Cart_btn_checkout)
    Button checkout;
    @BindView(R.id.cart_constraintLayout_container)
    ConstraintLayout container;
    @BindView(R.id.loading)
    ProgressBar loading;

    RecyclerView.LayoutManager layoutManager;
    CartAdapter cartAdapter;
    List<CartProductModel> cartArrList = new ArrayList<>();
    CartItem cartItem = new CartItem();

    double OrderTotalDiscount = -1;

    public static CartFragment newInstance(FragmentActivity activity) {
        fragment = new CartFragment();
        CartFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(activity.getResources().getString(R.string.cart));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupAppbar("cart", true, true);
        container.setVisibility(View.GONE);
        if (sessionManager.getUserLanguage().equals("en")) {
            Typeface enBold = Typeface.createFromAsset(activity.getAssets(), "montserrat_medium.ttf");
            totalValue.setTypeface(enBold);
            subtotalValue.setTypeface(enBold);
            discountValue.setTypeface(enBold);
            checkout.setTypeface(enBold);
        } else {
            Typeface arBold = Typeface.createFromAsset(activity.getAssets(), "droid_arabic_kufi_bold.ttf");
            totalValue.setTypeface(arBold);
            subtotalValue.setTypeface(arBold);
            discountValue.setTypeface(arBold);
            checkout.setTypeface(arBold);
        }

        layoutManager = new LinearLayoutManager(activity);
        cartAdapter = new CartAdapter(activity, cartArrList, new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Navigator.loadFragment(activity, ProductDetailsFragment.newInstance(activity,cartArrList.get(position)), R.id.main_frameLayout_Container, true);
            }

            @Override
            public void onPlusClick(int position) {

                boolean isError = false;

                if (cartArrList.get(position).product.orderMaximumQuantity != null) {

                    if (cartArrList.get(position).product.orderMaximumQuantity < (cartArrList.get(position).quantity + 1)) {

                        isError = true;

                        Snackbar.make(loading, activity.getResources().getString(R.string.maximum)
                                + " " + cartArrList.get(position).product.orderMaximumQuantity, Snackbar.LENGTH_SHORT).show();
                    }

                }
                if (!isError) {
                    updateCartProductAPi(position, true);
                }
            }

            @Override
            public void onMinusClick(int position) {

                if (cartArrList.get(position).quantity > 1) {

                    updateCartProductAPi(position, false);

                } else {

                    deleteCartProductApi(position);

                }
            }

            @Override
            public void onDeleteItemClick(final int position) {
                new AlertDialog.Builder(activity)
                        .setTitle(activity.getString(R.string.message))
                        .setMessage(activity.getString(R.string.sure_for_delete))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteCartProductApi(position);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.mipmap.logo)
                        .show();
            }
        });

        cartList.setLayoutManager(layoutManager);
        cartList.setAdapter(cartAdapter);

        if (cartArrList.size() > 0) {
            loading.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
            setTotalPrice();
        } else {
            CartProductsApi(false);
        }


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,
                new RecyclerItemTouchHelper.RecyclerItemTouchHelperListener() {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
                        cartAdapter.removeItem(viewHolder.getAdapterPosition());

                    }
                });
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(cartList);

    }

    @OnClick(R.id.Cart_btn_applyCoupon)
    public void applyCouponClick() {
        String couponStr = coupon.getText().toString();
        if (couponStr == null || couponStr.matches("")) {
            Snackbar.make(loading, getString(R.string.enter_coupon), Snackbar.LENGTH_SHORT).show();
        } else {
            applyCouponApi(couponStr);
        }
    }

    @OnClick(R.id.Cart_btn_checkout)
    public void checkoutClick() {
        {
            if (sessionManager.getUser().get("userName") != null
                    && !sessionManager.getUser().get("userName").matches("")) {
                checkout();
            } else {
                Fragment fragment = LoginFragment.newInstance(activity, "cart");
                Navigator.loadFragment(activity, fragment, R.id.main_frameLayout_Container, true);
            }
        }
    }


    private void checkout() {

        loading.setVisibility(View.VISIBLE);

        AtelierApiConfig.getCallingAPIInterface().
                customerById(
                        sessionManager.getUserLanguage(),
                        Constants.AUTHORIZATION_VALUE,
                        sessionManager.getUserId(),
                        new Callback<GetCustomers>() {
                            @Override
                            public void success(GetCustomers outResponse, retrofit.client.Response response) {

                                if (outResponse.customers != null) {

                                    if (outResponse.customers.size() > 0) {

                                        boolean isAddressEmpty = true;

                                        if (sessionManager.getUser().get("userName") != null
                                                && !sessionManager.getUser().get("userName").matches("")) {
                                            if (outResponse.customers.get(0).addresses.size() > 0) {
                                                isAddressEmpty = false;
                                            }
                                        } else {

                                            if (outResponse.customers.get(0).billingAddress != null) {
                                                isAddressEmpty = false;
                                            }
                                        }

                                        if (isAddressEmpty) {
                                            Fragment fragment = AddNewAddressFragment.newInstance(activity,
                                                    "cart",
                                                    "add", null);
                                            Navigator.loadFragment(activity, fragment, R.id.main_frameLayout_Container, true);
                                        } else {
                                            Fragment fragment = AddressesFragment.newInstance(activity, "cart");
                                            Navigator.loadFragment(activity, fragment, R.id.main_frameLayout_Container, true);
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

                        });

    }

    private void applyCouponApi(String coupon) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().applyCoupon(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                coupon,
                new Callback<GetCouponOrderTotal>() {
                    @Override
                    public void success(GetCouponOrderTotal getCouponOrderTotal, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCouponOrderTotal != null) {
                            if (getCouponOrderTotal.couponOrderTotal != null) {
                                if (getCouponOrderTotal.couponOrderTotal.size() > 0) {
                                    CartProductsApi(true);
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

    private void prepareCartProductData(int position, boolean isAdd) {

        CartItem_ cartItem_ = new CartItem_();
        cartItem_.customerId = cartArrList.get(position).customerId;
        cartItem_.productId = cartArrList.get(position).productId;
        cartItem_.quantity = isAdd ? (cartArrList.get(position).quantity + 1) : (cartArrList.get(position).quantity - 1);

        cartItem.shoppingCartItem = cartItem_;

    }

    public void updateCartProductAPi(final int position, boolean isAdd) {
        loading.setVisibility(View.VISIBLE);
        prepareCartProductData(position, isAdd);
        AtelierApiConfig.getCallingAPIInterface().updateShoppingCart(Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(), Constants.CONTENT_TYPE_VALUE,
                String.valueOf(cartArrList.get(position).id), cartItem, new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts getCartProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCartProducts != null) {
                            if (getCartProducts.CartProducts != null) {
                                if (getCartProducts.CartProducts.size() > 0) {
                                    cartArrList.set(position, getCartProducts.CartProducts.get(0));
                                    cartList.getAdapter().notifyItemChanged(position, cartArrList.get(position));
                                    setTotalPrice();
                                } else
                                    MainActivity.notification.setVisibility(View.INVISIBLE);

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

    private void deleteCartProductApi(final int position) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().deleteShoppingCartItem(
                Constants.AUTHORIZATION_VALUE, sessionManager.getUserLanguage(),
                String.valueOf(cartArrList.get(position).id), new Callback<Response>() {
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

                                cartArrList.remove(position);
                                cartList.getAdapter().notifyItemRemoved(position);
                                cartList.getAdapter().notifyItemRangeChanged(0, cartArrList.size(), cartArrList);
                                if (cartArrList.size() == 0) {
                                    Snackbar.make(loading, activity.getResources().getString(R.string.empty_cart), Snackbar.LENGTH_LONG).show();
                                    container.setVisibility(View.GONE);
                                    MainActivity.shoppingCartItemsCount(activity);
                                    MainActivity.notification.setVisibility(View.INVISIBLE);
                                }

                                setTotalPrice();

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

    private void setTotalPrice() {
        double total = 0;
        for (CartProductModel item : cartArrList) {
            total = total + (item.quantity * item.product.price);
        }
        if (OrderTotalDiscount > -1) {
            totalValue.setText(total + " " + sessionManager.getCurrencyCode());
            discountValue.setText("-"+OrderTotalDiscount + " " + sessionManager.getCurrencyCode());
            subtotalValue.setText(total - OrderTotalDiscount + " " + sessionManager.getCurrencyCode());
        }
        else
            subtotalValue.setText(total + " " + sessionManager.getCurrencyCode());
    }

    public void CartProductsApi(final boolean isCoupon) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().shoppingCartItems(
                Constants.AUTHORIZATION_VALUE, sessionManager.getUserLanguage()
                , sessionManager.getUserId(), "1", new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts getCartProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        if (!isCoupon) {
                            if (getCartProducts != null) {
                                if (getCartProducts.CartProducts.size() > 0) {
                                    cartArrList.addAll(getCartProducts.CartProducts);
                                    cartAdapter.notifyDataSetChanged();
                                    container.setVisibility(View.VISIBLE);
                                    setTotalPrice();
                                }
                            }}
                            if (getCartProducts.couponOrderTotal != null) {
                                if (getCartProducts.couponOrderTotal.OrderTotalDiscount != null) {
                                    OrderTotalDiscount = Double.parseDouble(getCartProducts.couponOrderTotal.OrderTotalDiscount);
                                    total.setVisibility(View.VISIBLE);
                                    totalValue.setVisibility(View.VISIBLE);
                                    discount.setVisibility(View.VISIBLE);
                                    discountValue.setVisibility(View.VISIBLE);
                                    setTotalPrice();

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

}
