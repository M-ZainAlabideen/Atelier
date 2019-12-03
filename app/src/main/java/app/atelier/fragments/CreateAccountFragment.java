package app.atelier.fragments;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SNIHostName;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.AppController;
import app.atelier.classes.Constants;
import app.atelier.classes.FixControl;
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
import retrofit.mime.TypedInput;

public class CreateAccountFragment extends Fragment {
    public static FragmentActivity activity;
    public static CreateAccountFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.createAccount_cl_container)
    ConstraintLayout container;
    @BindView(R.id.createAccount_editText_userName)
    EditText userName;
    @BindView(R.id.createAccount_editText_phone)
    EditText phone;
    @BindView(R.id.createAccount_editText_mail)
    EditText mail;
    @BindView(R.id.createAccount_editText_password)
    EditText password;
    @BindView(R.id.createAccount_checkBox_agree)
    CheckBox agree;
    @BindView(R.id.loading)
    ProgressBar loading;

    String regid = "";

    public static CreateAccountFragment newInstance(FragmentActivity activity, String comingFrom) {
        fragment = new CreateAccountFragment();
        CreateAccountFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        Bundle b = new Bundle();
        b.putString("comingFrom", comingFrom);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_create_account, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.GONE);
        MainActivity.bottomAppbar.setVisibility(View.GONE);
        FixControl.setupUI(container,activity);
        agree.setOnCheckedChangeListener(checkedListener);

    }


    CompoundButton.OnCheckedChangeListener checkedListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //change the image and checkedState
            if (!isChecked) {
                buttonView.setButtonDrawable(R.mipmap.check_unsel);
                buttonView.setChecked(false);
            } else {
                buttonView.setButtonDrawable(R.mipmap.check_sel);
                buttonView.setChecked(true);
            }
        }
    };


    @OnClick(R.id.createAccount_txtView_terms)
    public void termsClick() {
        Navigator.loadFragment(activity, TopicsPageFragment.newInstance(activity, "13"), R.id.main_frameLayout_Container, true);
    }

    @OnClick(R.id.createAccount_btn_done)
    public void doneClick() {
        String userNameStr = userName.getText().toString();
        String phoneStr = phone.getText().toString();
        String mailStr = mail.getText().toString();
        String passwordStr = password.getText().toString();
        if (userNameStr.isEmpty() || userNameStr == null || userNameStr.matches("")
                || phoneStr.isEmpty() || phoneStr == null || phoneStr.matches("")
                || mailStr.isEmpty() || mailStr == null || mailStr.matches("")
                || passwordStr.isEmpty() || passwordStr == null || passwordStr.matches("")) {
            Snackbar.make(loading, activity.getResources().getString(R.string.required_fields), Snackbar.LENGTH_LONG).show();
        } else if (!agree.isChecked()) {
            Snackbar.make(loading, activity.getResources().getString(R.string.agree_required), Snackbar.LENGTH_LONG).show();
        } else if(!FixControl.isValidEmail(mailStr)){
            Snackbar.make(loading,getString(R.string.enter_email), Snackbar.LENGTH_SHORT).show();
        }
        else {
            CustomerModel customer = new CustomerModel();
            customer.userName = userNameStr;
            customer.phone = phoneStr;
            customer.email = mailStr;
            customer.password = passwordStr;
            List<Integer> objectList = new ArrayList<>();
            objectList.add(Constants.NormalUserRoleId);
            customer.roleIds = objectList;
            updateCustomerApi(customer);
        }
    }

    public void updateCustomerApi(CustomerModel customer) {
        loading.setVisibility(View.VISIBLE);

        GetCustomers getCustomers = new GetCustomers();
        getCustomers.customer = customer;
        AtelierApiConfig.getCallingAPIInterface().updateCustomer(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                Constants.CONTENT_TYPE_VALUE,
                getCustomers,
                new Callback<GetCustomers>() {
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

                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                            if (!task.isSuccessful()) {
                                                Log.w("login", "getInstanceId failed", task.getException());
                                                return;
                                            }

                                            // Get new Instance ID token
                                            regid = task.getResult().getToken();

                                            Log.e("registerationid Splash ", "regid -> "+regid);

                                            registerInBackground();


                                        }
                                    });

                            MainActivity.accountOrLogin.setText(activity.getResources().getString(R.string.account));
                            if (getArguments().getString("comingFrom").equals("cart")) {
                                backToCart();
                            } else {
                                Navigator.loadFragment(activity, MainCategoriesFragment.newInstance(activity), R.id.main_frameLayout_Container, false);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error, loading);

                    }
                });
    }

    private void backToCart() {
        FragmentManager fm = activity.getSupportFragmentManager();
        for (int i = 0; i < 2; ++i) {
            fm.popBackStack();
        }
    }

    private void registerInBackground() {

        AtelierApiConfig.getCallingAPIInterface().insertToken(Constants.AUTHORIZATION, regid, "2", AppController.getInstance().getIMEI(), sessionManager.getUserId().length() > 0 ? sessionManager.getUserId() : null, new Callback<Response>() {
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

                    sessionManager.setRegId(regid);


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
