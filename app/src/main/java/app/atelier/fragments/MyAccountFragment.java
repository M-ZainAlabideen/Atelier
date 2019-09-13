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
import android.widget.ProgressBar;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.Constants;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.customers.CustomerModel;
import app.atelier.webservices.responses.customers.GetCustomers;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyAccountFragment extends Fragment {
    public static FragmentActivity activity;
    public static MyAccountFragment fragment;
    
    @BindView(R.id.myAccount_imgView_editProfileArrow)
    ImageView editProfileArrow;
    @BindView(R.id.myAccount_imgView_myOrdersArrow)
    ImageView myOrdersArrow;
    @BindView(R.id.myAccount_imgView_logoutArrow)
    ImageView logoutArrow;
    @BindView(R.id.myAccount_imgView_addressesArrow)
    ImageView addressesArrow;

    @BindView(R.id.myAccount_imgView_logoutImg)
    ImageView logoutImg;

    @BindView(R.id.loading)
    ProgressBar loading;
    
    public static MyAccountFragment newInstance(FragmentActivity activity) {
        fragment = new MyAccountFragment();
        MyAccountFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_my_account, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(activity.getResources().getString(R.string.account));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupBottomAppbar("");

        if(MainActivity.isEnglish){
            editProfileArrow.setRotation(180);
            myOrdersArrow.setRotation(180);
            logoutArrow.setRotation(180);
            addressesArrow.setRotation(180);
            logoutImg.setRotation(180);
        }
    }

    @OnClick(R.id.myAccount_View_editProfile)
    public void editProfileClick(){
        Navigator.loadFragment(activity, EditProfileFragment.newInstance(activity), R.id.main_frameLayout_Container, true);

    }

    @OnClick(R.id.myAccount_View_myOrders)
    public void myOrdersClick(){
        Navigator.loadFragment(activity, OrdersFragment.newInstance(activity), R.id.main_frameLayout_Container, true);

    }
    @OnClick(R.id.myAccount_View_addresses)
    public void addressesClick(){
        Navigator.loadFragment(activity, AddressesFragment.newInstance(activity,"myAccount"), R.id.main_frameLayout_Container, true);
    }
    @OnClick(R.id.myAccount_View_logout)
    public void logoutClick(){
        //fix it
        createGuestCustomerApi();
    }

    public void createGuestCustomerApi() {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().createGuestCustomer(
                Constants.AUTHORIZATION_VALUE, MainActivity.language, Constants.CONTENT_TYPE_VALUE,
                new Callback<GetCustomers>() {
                    @Override
                    public void success(GetCustomers getCustomers, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCustomers != null) {
                            CustomerModel customer = getCustomers.customers.get(0);
                            SessionManager.setUser(activity,
                                    customer.id,
                                    customer.userName,
                                    customer.firstName,
                                    customer.lastName,
                                    customer.phone,
                                    customer.email,
                                    customer.password);
                            MainActivity.accountOrLogin.setText(activity.getResources().getString(R.string.login));
                            Navigator.loadFragment(activity,LoginFragment.newInstance(activity),R.id.main_frameLayout_Container,false);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(loading, getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                    }
                }
        );
    }
}
