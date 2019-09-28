package app.atelier.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.PopUpAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.FixControl;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.Navigator;
import app.atelier.classes.RecyclerItemClickListener;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.addresses.AddressModel;
import app.atelier.webservices.responses.addresses.GetAddresses;
import app.atelier.webservices.responses.countries.CountryModel;
import app.atelier.webservices.responses.countries.GetCountries;
import app.atelier.webservices.responses.states.GetStates;
import app.atelier.webservices.responses.states.StateModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddNewAddressFragment extends Fragment {
    public static FragmentActivity activity;
    public static AddNewAddressFragment fragment;
    public static SessionManager sessionManager;
    @BindView(R.id.addAddress_editTxt_firstName)
    EditText firstName;
    @BindView(R.id.addAddress_editTxt_lastName)
    EditText lastName;
    @BindView(R.id.addAddress_editTxt_mail)
    EditText mail;
    @BindView(R.id.addAddress_editTxt_phone)
    EditText phone;
    @BindView(R.id.addAddress_editTxt_details)
    EditText details;
    @BindView(R.id.addAddress_txtView_country)
    TextView country;
    @BindView(R.id.addAddress_txtView_state)
    TextView state;
    @BindView(R.id.addAddress_imgView_countryBg)
    ImageView countryBg;
    @BindView(R.id.addAddress_imgView_stateBg)
    ImageView stateBg;
    @BindView(R.id.loading)
    ProgressBar loading;

    List<CountryModel> countriesArrList = new ArrayList<>();
    List<StateModel> statesArrList = new ArrayList<>();
    private AlertDialog dialog;
    AddressModel myAddress;
    public static Map<String, String> User;

    public static AddNewAddressFragment newInstance(FragmentActivity activity,
                                                    String comingFrom,
                                                    String flag,
                                                    AddressModel address) {
        fragment = new AddNewAddressFragment();
        AddNewAddressFragment.activity = activity;
        sessionManager = new SessionManager(activity);
         User = sessionManager.getUser();
        Bundle b = new Bundle();
        b.putString("comingFrom",comingFrom);
        b.putString("flag", flag);
        if (flag.equals("edit")) {
            b.putSerializable("address", address);
        }
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_add_new_address, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(activity.getResources().getString(R.string.add_address));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupAppbar("",true,true);

        if (MainActivity.isEnglish) {
            countryBg.setImageResource(R.mipmap.spinner_bg);
            stateBg.setImageResource(R.mipmap.spinner_bg);
        }
        if (countriesArrList.size() <= 0) {
            countriesApi();
        }
        if (getArguments().getString("flag").equals("edit")) {
            myAddress = (AddressModel) getArguments().getSerializable("address");
            firstName.setText(myAddress.firstName);
            lastName.setText(myAddress.lastName);
            phone.setText(myAddress.phoneNumber);
            mail.setText(myAddress.email);
            details.setText(myAddress.address1);
            country.setText(myAddress.country);
            state.setText(myAddress.province);
        } else {
            myAddress = new AddressModel();
            firstName.setText(User.get("firstName"));
            lastName.setText(User.get("lastName"));
            phone.setText(User.get("phone"));
            mail.setText(User.get("email"));
        }
    }

    @OnClick(R.id.addAddress_view_selectCountry)
    public void countryClick() {
        createPopUp(activity, "country", countriesArrList, null);
    }

    @OnClick(R.id.addAddress_view_selectCity)
    public void stateClick() {
        createPopUp(activity, "state", null, statesArrList);
    }

    @OnClick(R.id.addAddress_btn_done)
    public void doneClick() {
        String firstNameStr = firstName.getText().toString();
        String lastNameStr = lastName.getText().toString();
        String phoneStr = phone.getText().toString();
        String mailStr = mail.getText().toString();
        String detailsStr = details.getText().toString();
        String countryStr = country.getText().toString();
        String stateStr = state.getText().toString();
        if (firstNameStr == null || firstNameStr.matches("")) {
            Snackbar.make(loading, getString(R.string.first_name_required), Snackbar.LENGTH_SHORT).show();
        } else if (lastNameStr == null || lastNameStr.matches("")) {
            Snackbar.make(loading, getString(R.string.last_name_required), Snackbar.LENGTH_SHORT).show();
        } else if (phoneStr == null || phoneStr.matches("")) {
            Snackbar.make(loading, getString(R.string.phone_required), Snackbar.LENGTH_SHORT).show();
        } else if (mailStr == null || mailStr.matches("")) {
            Snackbar.make(loading, getString(R.string.mail_required), Snackbar.LENGTH_SHORT).show();
        } else if (countryStr.equals(getString(R.string.select_country))) {
            Snackbar.make(loading, getString(R.string.country_required), Snackbar.LENGTH_SHORT).show();
        } else if (stateStr.equals(getString(R.string.select_state))) {
            Snackbar.make(loading, getString(R.string.state_required), Snackbar.LENGTH_SHORT).show();
        } else {
            myAddress.firstName = firstNameStr;
            myAddress.lastName = lastNameStr;
            myAddress.phoneNumber = phoneStr;
            myAddress.email = mailStr;
            myAddress.country = countryStr;
            myAddress.province = stateStr;
            myAddress.address1 = detailsStr;
            GetAddresses getAddresses = new GetAddresses();
            getAddresses.address = myAddress;
            if (getArguments().getString("flag").equals("edit")) {
                editAddressApi(getAddresses);
            } else {
                addAddressApi(getAddresses);
            }

        }
    }


    public void createPopUp(final Context context,
                            final String type,
                            final List<CountryModel> countriesArrList,
                            final List<StateModel> statesArrList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View popUpView = ((Activity) context).getLayoutInflater().inflate(R.layout.custom_pop_up, null);
        RecyclerView popUpRecycler = (RecyclerView) popUpView.findViewById(R.id.popUp_Recycler);
        popUpRecycler.setLayoutManager(new LinearLayoutManager(context));
        popUpRecycler.setAdapter(new PopUpAdapter(context, type, countriesArrList, statesArrList));
        builder.setCancelable(true);
        builder.setView(popUpView);
        dialog = builder.create();
        dialog.show();
        popUpRecycler.addOnItemTouchListener(new RecyclerItemClickListener(context, popUpRecycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (type.equalsIgnoreCase("country")) {
                    if (!country.getText().toString().equals(countriesArrList.get(position).name)) {
                        country.setText(countriesArrList.get(position).name);
                        state.setText(getString(R.string.select_state));
                        myAddress.countryId = countriesArrList.get(position).id;
                        stateProvincesApi(myAddress.countryId + "");
                    }
                } else {
                    myAddress.stateProvinceId = statesArrList.get(position).id;
                    state.setText(statesArrList.get(position).name);
                }

                closePopUp();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    public void closePopUp() {
        dialog.cancel();

    }

    public void countriesApi() {
        AtelierApiConfig.getCallingAPIInterface().countries(Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(), new Callback<GetCountries>() {
                    @Override
                    public void success(GetCountries getCountries, Response response) {
                        if (getCountries != null) {
                            if (getCountries.countries.size() > 0) {
                                countriesArrList.addAll(getCountries.countries);
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

    public void stateProvincesApi(String countryId) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().stateProvinces(Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(), countryId, new Callback<GetStates>() {
                    @Override
                    public void success(GetStates getStates, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getStates != null) {
                            if (getStates.states.size() > 0) {
                                statesArrList.clear();
                                statesArrList.addAll(getStates.states);
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

    public void addAddressApi(GetAddresses getAddresses) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().addAddress(
                Constants.AUTHORIZATION_VALUE, sessionManager.getUserLanguage(),
                Constants.CONTENT_TYPE_VALUE, getAddresses,
                sessionManager.getUserId(),
                new Callback<GetAddresses>() {
                    @Override
                    public void success(GetAddresses getAddresses, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getAddresses != null) {
                            if (getAddresses.addresses.size() > 0) {
                                FixControl.hideKeyboard(firstName,activity);
                               Navigator.loadFragment(activity,AddressesFragment.newInstance(activity,getArguments().getString("comingFrom")),R.id.main_frameLayout_Container,false);

                            }
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

    public void editAddressApi(GetAddresses getAddresses) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().editAddress(Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(), Constants.CONTENT_TYPE_VALUE, getAddresses,
                sessionManager.getUserId(), myAddress.id,
                new Callback<GetAddresses>() {
                    @Override
                    public void success(GetAddresses getAddresses, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getAddresses != null) {
                            if (getAddresses.addresses.size() > 0) {
                                FixControl.hideKeyboard(firstName,activity);
                                getFragmentManager().popBackStack();
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

