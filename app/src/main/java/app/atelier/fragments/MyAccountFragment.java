package app.atelier.fragments;

import android.graphics.Typeface;
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
import android.widget.TextView;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
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
    public static SessionManager sessionManager;
    
    @BindView(R.id.myAccount_imgView_editProfileArrow)
    ImageView editProfileArrow;
    @BindView(R.id.myAccount_imgView_changePassArrow)
    ImageView changePassArrow;
    @BindView(R.id.myAccount_imgView_myOrdersArrow)
    ImageView myOrdersArrow;
    @BindView(R.id.myAccount_imgView_logoutArrow)
    ImageView logoutArrow;
    @BindView(R.id.myAccount_imgView_addressesArrow)
    ImageView addressesArrow;
    @BindView(R.id.myAccount_tv_editProfile)
    TextView editProfile;
    @BindView(R.id.myAccount_tv_changePass)
    TextView changePass;
    @BindView(R.id.myAccount_tv_myOrders)
    TextView myOrders;
    @BindView(R.id.myAccount_tv_addresses)
    TextView addresses;
    @BindView(R.id.myAccount_tv_logout)
    TextView logout;


    @BindView(R.id.myAccount_imgView_logoutImg)
    ImageView logoutImg;

    @BindView(R.id.loading)
    ProgressBar loading;
    
    public static MyAccountFragment newInstance(FragmentActivity activity) {
        fragment = new MyAccountFragment();
        MyAccountFragment.activity = activity;
        sessionManager = new SessionManager(activity);
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
        MainActivity.setupAppbar("",true,true);

        if (sessionManager.getUserLanguage().equals("en")) {
            Typeface enBold = Typeface.createFromAsset(activity.getAssets(), "montserrat_medium.ttf");
            editProfile.setTypeface(enBold);
            myOrders.setTypeface(enBold);
            addresses.setTypeface(enBold);
            changePass.setTypeface(enBold);
            logout.setTypeface(enBold);

            editProfileArrow.setRotation(180);
            changePassArrow.setRotation(180);
            myOrdersArrow.setRotation(180);
            logoutArrow.setRotation(180);
            addressesArrow.setRotation(180);
            logoutImg.setRotation(180);
        } else {
            Typeface arBold = Typeface.createFromAsset(activity.getAssets(), "droid_arabic_kufi_bold.ttf");
            editProfile.setTypeface(arBold);
            myOrders.setTypeface(arBold);
            addresses.setTypeface(arBold);
            changePass.setTypeface(arBold);
            logout.setTypeface(arBold);
        }
    }

    @OnClick(R.id.myAccount_View_editProfile)
    public void editProfileClick(){
        Navigator.loadFragment(activity, EditProfileFragment.newInstance(activity), R.id.main_frameLayout_Container, true);

    }

    @OnClick(R.id.myAccount_View_changePass)
    public void changePasswordClick(){
        Navigator.loadFragment(activity, ChangePasswordFragment.newInstance(activity), R.id.main_frameLayout_Container, true);
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
                Constants.AUTHORIZATION_VALUE, 
                sessionManager.getUserLanguage(), 
                Constants.CONTENT_TYPE_VALUE,
                new Callback<GetCustomers>() {
                    @Override
                    public void success(GetCustomers getCustomers, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCustomers != null) {
                            CustomerModel customer = getCustomers.customers.get(0);
                            sessionManager.setUserId(String.valueOf(customer.id));
                            sessionManager.setUserName(customer.userName);
                            sessionManager.setFirstName(customer.firstName);
                            sessionManager.setLastName(customer.lastName);
                            sessionManager.setPhone(customer.phone);
                            sessionManager.setEmail(customer.email);
                            sessionManager.guestLogout();
                            MainActivity.accountOrLogin.setText(activity.getResources().getString(R.string.login));
                            MainActivity.shoppingCartItemsCount();
                            Navigator.loadFragment(activity,LoginFragment.newInstance(activity,"myAccount"),R.id.main_frameLayout_Container,false);
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
