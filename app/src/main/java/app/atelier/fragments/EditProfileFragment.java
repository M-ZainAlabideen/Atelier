package app.atelier.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import app.atelier.MainActivity;
import app.atelier.R;
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

public class EditProfileFragment extends Fragment {
    public static FragmentActivity activity;
    public static EditProfileFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.editProfile_cl_container)
    ConstraintLayout container;
    @BindView(R.id.editProfile_editText_userName)
    EditText userName;
    @BindView(R.id.editProfile_editText_firstName)
    EditText firstName;
    @BindView(R.id.editProfile_editText_lastName)
    EditText lastName;
    @BindView(R.id.editProfile_editText_phone)
    EditText phone;
    @BindView(R.id.editProfile_editText_mail)
    EditText mail;
    @BindView(R.id.loading)
    ProgressBar loading;


    public static EditProfileFragment newInstance(FragmentActivity activity) {
        fragment = new EditProfileFragment();
        EditProfileFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(activity.getResources().getString(R.string.edit_profile));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupAppbar("", true, true);
        FixControl.setupUI(container,activity);
        GlobalFunctions.DisableLayout(container);
        getCustomerById();
    }

    @OnClick(R.id.editProfile_btn_save)
    public void saveClick() {
        String userNameStr = userName.getText().toString();
        String firstNameStr = firstName.getText().toString();
        String lastNameStr = lastName.getText().toString();
        String phoneStr = phone.getText().toString();
        String mailStr = mail.getText().toString();

        if (userNameStr == null || userNameStr.matches("")) {
            Snackbar.make(loading, activity.getResources().getString(R.string.user_name_required), Snackbar.LENGTH_SHORT).show();
        } else if (phoneStr == null || phoneStr.matches("")) {
            Snackbar.make(loading, activity.getResources().getString(R.string.phone_required), Snackbar.LENGTH_SHORT).show();
        } else if (mailStr == null || mailStr.matches("")) {
            Snackbar.make(loading, activity.getResources().getString(R.string.mail_required), Snackbar.LENGTH_SHORT).show();
        } else if(!FixControl.isValidEmail(mailStr)){
            Snackbar.make(loading,getString(R.string.enter_email), Snackbar.LENGTH_SHORT).show();
        }
        else {
            CustomerModel customer = new CustomerModel();
            customer.userName = userNameStr;
            customer.firstName = firstNameStr;
            customer.lastName = lastNameStr;
            customer.phone = phoneStr;
            customer.email = mailStr;
            updateCustomerApi(customer);
        }
    }

    public void updateCustomerApi(CustomerModel customer) {
        loading.setVisibility(View.VISIBLE);

        GetCustomers getCustomers = new GetCustomers();
        getCustomers.customer = customer;
        AtelierApiConfig.getCallingAPIInterface().updateCustomer(Constants.AUTHORIZATION_VALUE, 
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
                            Snackbar.make(loading, activity.getResources().getString(R.string.successfully_updated), Snackbar.LENGTH_SHORT).show();
                            Navigator.loadFragment(activity, MyAccountFragment.newInstance(activity), R.id.main_frameLayout_Container, false);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error, loading);

                    }
                });
    }

    public void getCustomerById() {
        AtelierApiConfig.getCallingAPIInterface().customerById(
                sessionManager.getUserLanguage(), Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserId(), new Callback<GetCustomers>() {
                    @Override
                    public void success(GetCustomers getCustomers, Response response) {
                        if (getCustomers != null) {
                            if (getCustomers.customers != null) {
                                if (getCustomers.customers.size() > 0) {
                                    userName.setText(getCustomers.customers.get(0).userName);
                                    firstName.setText(getCustomers.customers.get(0).firstName);
                                    lastName.setText(getCustomers.customers.get(0).lastName);
                                    phone.setText(getCustomers.customers.get(0).phone);
                                    mail.setText(getCustomers.customers.get(0).email);
                                    loading.setVisibility(View.GONE);
                                    GlobalFunctions.EnableLayout(container);
                                }
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }
        );
    }

}
