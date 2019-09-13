package app.atelier.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

public class CreateAccountFragment extends Fragment {
    public static FragmentActivity activity;
    public static CreateAccountFragment fragment;

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


    public static CreateAccountFragment newInstance(FragmentActivity activity) {
        fragment = new CreateAccountFragment();
        CreateAccountFragment.activity = activity;
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
        MainActivity.setupBottomAppbar("");

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
        } else if (!agree.isChecked()){
            Snackbar.make(loading, activity.getResources().getString(R.string.agree_required), Snackbar.LENGTH_LONG).show();
        }
        else {
            CustomerModel customer = new CustomerModel();
            customer.userName = userNameStr;
            customer.phone = phoneStr;
            customer.email = mailStr;
            customer.password = passwordStr;
            updateCustomerApi(customer);
        }
    }

    public void updateCustomerApi(CustomerModel customer) {
        loading.setVisibility(View.VISIBLE);

        GetCustomers getCustomers = new GetCustomers();
        getCustomers.customer = customer;
        AtelierApiConfig.getCallingAPIInterface().updateCustomer(Constants.AUTHORIZATION_VALUE,MainActivity.language,
                SessionManager.getUserId(activity), Constants.CONTENT_TYPE_VALUE, getCustomers, new Callback<GetCustomers>() {
                    @Override
                    public void success(GetCustomers getCustomers, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCustomers.customers.size() > 0) {
                            CustomerModel customer = getCustomers.customers.get(0);
                            SessionManager.setUser(activity,customer.id,
                                    customer.userName,
                                   customer.firstName,
                                    customer.lastName,
                                    customer.phone,
                                    customer.email,
                                    customer.password);
                            MainActivity.accountOrLogin.setText(activity.getResources().getString(R.string.account));
                            Navigator.loadFragment(activity,MainCategoriesFragment.newInstance(activity),R.id.main_frameLayout_Container,false);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(loading,activity.getResources().getString(R.string.error),Snackbar.LENGTH_LONG).show();

                    }
                });
    }

    public void setData() {
    }


}
