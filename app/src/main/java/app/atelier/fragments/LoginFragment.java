package app.atelier.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import app.atelier.MainActivity;
import app.atelier.R;

import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.LocaleHelper;
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

public class LoginFragment extends Fragment {
    public static FragmentActivity activity;
    public static LoginFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.login_editText_userName)
    EditText userName;
    @BindView(R.id.login_editText_password)
    EditText password;
    @BindView(R.id.login_imgView_arrow)
    ImageView arrow;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static LoginFragment newInstance(FragmentActivity activity, String comingFrom) {
        fragment = new LoginFragment();
        LoginFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        Bundle b = new Bundle();
        b.putString("comingFrom", comingFrom);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.GONE);
        MainActivity.bottomAppbar.setVisibility(View.GONE);

        if (MainActivity.isEnglish) {
            arrow.setRotation(180);
        }
    }


    @OnClick(R.id.login_txtView_forgetPass)
    public void forgetPassClick() {
        Navigator.loadFragment(activity, ForgetPasswordFragment.newInstance(activity), R.id.main_frameLayout_Container, true);
    }

    @OnClick(R.id.login_btn_login)
    public void loginClick() {
        String userNameStr = userName.getText().toString();
        String passwordStr = password.getText().toString();
        if (userNameStr.isEmpty() || userNameStr == null || userNameStr.matches("")
                || passwordStr.isEmpty() || passwordStr == null || passwordStr.matches("")) {
            Snackbar.make(loading, activity.getResources().getString(R.string.required_fields), Snackbar.LENGTH_LONG).show();
        } else {
            loginApi(userNameStr, passwordStr);
        }
    }

    @OnClick(R.id.login_btn_createAccount)
    public void createAccountClick() {
        Navigator.loadFragment(activity, CreateAccountFragment.newInstance(activity, getArguments().getString("comingFrom")), R.id.main_frameLayout_Container, true);
    }

    @OnClick(R.id.login_view_skip)
    public void skipClick() {
        sessionManager.ContinueAsGuest();
        if (getArguments().getString("comingFrom").equals("cart")) {
            Navigator.loadFragment(activity, AddressesFragment.newInstance(activity,"cart"), R.id.main_frameLayout_Container, false);
        }

        else {
            Navigator.loadFragment(activity, MainCategoriesFragment.newInstance(activity), R.id.main_frameLayout_Container, false);
        }
    }

    @OnClick(R.id.login_tv_changeLang)
    public void changeLangClick(){
           /*for changing the language of App
        1- check the value of language in sharedPreference and Reflects the language
         2- set the new value of language in local and change the value of sharedPreference to new value
         3- restart the mainActivity with noAnimation
        * */

         if (sessionManager.getUserLanguage().equals("en")) {
            sessionManager.setUserLanguage("ar");
            MainActivity.isEnglish = false;
        }
        else{
            sessionManager.setUserLanguage("en");
            MainActivity.isEnglish = true;
        }
        LocaleHelper.setLocale(activity,sessionManager.getUserLanguage());

        activity.finish();
        activity.overridePendingTransition(0, 0);
        startActivity(new Intent(activity, MainActivity.class));

    }

    public void loginApi(String userName, String password) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().login(sessionManager.getUserLanguage(),
                Constants.AUTHORIZATION_VALUE
                , userName, password, sessionManager.getUserId(), new Callback<GetCustomers>() {
                    @Override
                    public void success(GetCustomers getCustomers, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCustomers.customers.size() > 0) {
                            CustomerModel customer = getCustomers.customers.get(0);
                            sessionManager.setUser(
                                    customer.id,
                                    customer.userName,
                                    customer.firstName,
                                    customer.lastName,
                                    customer.phone,
                                    customer.email,
                                    customer.password);
                            MainActivity.accountOrLogin.setText(activity.getResources().getString(R.string.account));
                            if (getArguments().getString("comingFrom").equals("cart")) {
                                getFragmentManager().popBackStack();
                            } else {
                                Navigator.loadFragment(activity, MainCategoriesFragment.newInstance(activity), R.id.main_frameLayout_Container, false);
                            }
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
