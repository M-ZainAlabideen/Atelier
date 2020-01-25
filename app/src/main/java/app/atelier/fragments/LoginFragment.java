package app.atelier.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import app.atelier.MainActivity;
import app.atelier.R;

import app.atelier.classes.AppController;
import app.atelier.classes.Constants;
import app.atelier.classes.FixControl;
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
import retrofit.mime.TypedInput;

public class LoginFragment extends Fragment {
    public static FragmentActivity activity;
    public static LoginFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.login_cl_container)
    ConstraintLayout container;
    @BindView(R.id.login_editText_userName)
    EditText userName;
    @BindView(R.id.login_editText_password)
    EditText password;
    @BindView(R.id.login_imgView_arrow)
    ImageView arrow;
    @BindView(R.id.loading)
    ProgressBar loading;

    String regId = "";

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
        FixControl.setupUI(container,activity);
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
            guestCheckout();
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
        GlobalFunctions.setUpFont(activity);
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
                            sessionManager.setUserId(String.valueOf(customer.id));
                            sessionManager.setUserName(customer.userName);
                            sessionManager.setFirstName(customer.firstName);
                            sessionManager.setLastName(customer.lastName);
                            sessionManager.setPhone(customer.phone);
                            sessionManager.setEmail(customer.email);
                            sessionManager.LoginSession();
                            initializeFirebaseToken();
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

    private void guestCheckout() {

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

                                        if (sessionManager.getUserName() != null
                                                && !sessionManager.getUserName().matches("")) {
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
                                            Fragment fragment = AddressesFragment.newInstance(activity, "cart",null);
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

    public void initializeFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("splash", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        regId = task.getResult().getToken();

                        Log.e("registrationId Main ", "regId -> "+regId +"------------"+ sessionManager.getUserId());

                        registerInBackground();


                    }
                });
    }

    private void registerInBackground() {

        AtelierApiConfig.getCallingAPIInterface().insertToken(Constants.AUTHORIZATION_VALUE, regId, "2", AppController.getInstance().getIMEI(), sessionManager.getUserId().length() > 0 ? sessionManager.getUserId() : null, new Callback<Response>() {
            @Override
            public void success(retrofit.client.Response s, retrofit.client.Response response) {

                TypedInput body = response.getBody();

                try {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));

                    StringBuilder out = new StringBuilder();

                    String newLine = System.getProperty("line.separator");

                    String line;

                    while ((line = reader.readLine()) != null) {
                        out.append(line);
                        out.append(newLine);
                    }

                    String outResponse = out.toString();
                    Log.d("outResponse", "" + outResponse);

                    sessionManager.setRegId(regId);


                } catch (Exception ex) {

                    ex.printStackTrace();


                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }
}
