package app.atelier.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.OrdersAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.customers.CustomerModel;
import app.atelier.webservices.responses.customers.GetCustomers;
import app.atelier.webservices.responses.orders.GetOrders;
import app.atelier.webservices.responses.orders.OrderModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrdersFragment extends Fragment {
    public static FragmentActivity activity;
    public static OrdersFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.orders_recyclerView_ordersList)
    RecyclerView ordersList;
    @BindView(R.id.loading)
    ProgressBar loading;

    public int pageIndex = 1;
    private boolean isLoading = false;
    public boolean isLastPage = false;

    List<OrderModel> ordersArrList = new ArrayList<>();
    OrdersAdapter ordersAdapter;
    LinearLayoutManager layoutManager;

    public static OrdersFragment newInstance(FragmentActivity activity) {
        fragment = new OrdersFragment();
        OrdersFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(activity.getResources().getString(R.string.account));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupAppbar("",true,true);

        ordersAdapter = new OrdersAdapter(activity, ordersArrList);
        layoutManager = new LinearLayoutManager(activity);
        ordersList.setLayoutManager(layoutManager);
        ordersList.setAdapter(ordersAdapter);

        if (ordersArrList.size() > 0) {
            loading.setVisibility(View.GONE);
        } else {
            ordersApi();
        }

    }


    public void ordersApi() {
        AtelierApiConfig.getCallingAPIInterface().orders(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                String.valueOf(pageIndex),
                Constants.LIMIT,
                new Callback<GetOrders>() {
                    @Override
                    public void success(GetOrders getOrders, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getOrders.orders.size() > 0) {
                            ordersArrList.addAll(getOrders.orders);
                            ordersAdapter.notifyDataSetChanged();

                            //add Scroll listener to the recycler , for pagination
                            ordersList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                                                moreOrders();

                                            }
                                        }
                                    }
                                }
                            });

                        }
                        else {
                            isLastPage = true;
                            Snackbar.make(loading,getString(R.string.no_data),Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error,loading);
                    }
                });
    }

    public void moreOrders(){
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().orders(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                String.valueOf(pageIndex), Constants.LIMIT,
                new Callback<GetOrders>() {
                    @Override
                    public void success(GetOrders getOrders, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getOrders != null) {

                            if (getOrders.orders.isEmpty()) {

                                isLastPage = true;
                                pageIndex = pageIndex - 1;
                            }
                            else{
                                ordersArrList.addAll(getOrders.orders);
                                ordersAdapter.notifyDataSetChanged();
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

}
