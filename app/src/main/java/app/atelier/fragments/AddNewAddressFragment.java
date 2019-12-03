package app.atelier.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    @BindView(R.id.addAddress_cl_container)
    ConstraintLayout container;
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

    public static FragmentActivity activity;
    public static AddNewAddressFragment fragment;
    private SessionManager sessionManager;
    List<CountryModel> countriesArrList = new ArrayList<>();
    List<StateModel> statesArrList = new ArrayList<>();
    private AlertDialog dialog;
    AddressModel myAddress;

    public static AddNewAddressFragment newInstance(FragmentActivity activity,
                                                    String comingFrom,
                                                    String flag,
                                                    AddressModel address) {
        fragment = new AddNewAddressFragment();
        AddNewAddressFragment.activity = activity;
        Bundle b = new Bundle();
        b.putString("comingFrom", comingFrom);
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

        //set The title of fragment
        MainActivity.title.setText(activity.getResources().getString(R.string.add_address));

        //make the appbar and bottomAppbar visible
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);

        /*setup the appbar and bottom appBar and
         * make bottomAppbar without selection and topAppbar has SearchView and has sideMenu
         **/
        MainActivity.setupAppbar("", true, true);

        // this function make click anywhere in the screen close any opened keyboard
        FixControl.setupUI(container, activity);

        /*
         *initialize the Session Manger which used for create the session of user ,setting and getting userData
         * such as id,email,userName ..etc ,making login and logout for user and so on.
         * */
        sessionManager = new SessionManager(activity);


        /*in case of english >> change the background Image of country and state
         * to change arrow location from right to left (for working perfectly as spinner)
         **/

        if (MainActivity.isEnglish) {
            countryBg.setImageResource(R.mipmap.spinner_bg);
            stateBg.setImageResource(R.mipmap.spinner_bg);
        }

        /*when back from screen to this screen the countryList still has data so, its not useful to call api again
         *this condition mean just call api when countriesList has no data
         * */
        if (countriesArrList.size() <= 0) {
            countriesApi();
        }
        /*
         * this fragment(screen) used for 2 purpose add New Address and edit Address
         * so check for flag to know if coming from addNewAddress or editAddress
         * 1- in case of editAddress , set the data of this address in boxes(editTexts)
         * to make editing on it and save
         * 2- in case of addNewAddress , set the userData from his sessionManager as default Data
         * and user can edit it and click save to add new address
         * */
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
            firstName.setText(sessionManager.getFirstName());
            lastName.setText(sessionManager.getLastName());
            phone.setText(sessionManager.getPhone());
            mail.setText(sessionManager.getEmail());
        }
    }

    //click on Country >> open the popUp of countries for selecting one
    @OnClick(R.id.addAddress_view_selectCountry)
    public void countryClick() {
        createPopUp(activity, "country", countriesArrList, null);
    }

    //click on State >> open the popUp of states for selecting one
    @OnClick(R.id.addAddress_view_selectCity)
    public void stateClick() {
        createPopUp(activity, "state", null, statesArrList);
    }

    //click done for saving of addressData
    @OnClick(R.id.addAddress_btn_done)
    public void doneClick() {
        //make check that all data entered before saving (firstName,lastName,phone,email,details,country and state)
        String firstNameStr = firstName.getText().toString();
        String lastNameStr = lastName.getText().toString();
        String phoneStr = phone.getText().toString();
        String mailStr = mail.getText().toString();
        String detailsStr = details.getText().toString();
        String countryStr = country.getText().toString();
        String stateStr = state.getText().toString();

        if (firstNameStr == null || firstNameStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.first_name_required), Snackbar.LENGTH_SHORT).show();
        } else if (lastNameStr == null || lastNameStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.last_name_required), Snackbar.LENGTH_SHORT).show();
        } else if (phoneStr == null || phoneStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.phone_required), Snackbar.LENGTH_SHORT).show();
        } else if (mailStr == null || mailStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.mail_required), Snackbar.LENGTH_SHORT).show();
        } else if (countryStr.equals(getString(R.string.select_country))) {
            Snackbar.make(loading, getString(R.string.country_required), Snackbar.LENGTH_SHORT).show();
        } else if (stateStr.equals(getString(R.string.select_area))) {
            Snackbar.make(loading, getString(R.string.state_required), Snackbar.LENGTH_SHORT).show();
        } else {
            /*
             * in case of all data entered correctly
             * set the data in address object to pass this object in the api Calling
             * */
            myAddress.firstName = firstNameStr;
            myAddress.lastName = lastNameStr;
            myAddress.phoneNumber = phoneStr;
            myAddress.email = mailStr;
            myAddress.country = countryStr;
            myAddress.province = stateStr;
            myAddress.address1 = detailsStr;
            GetAddresses getAddresses = new GetAddresses();
            getAddresses.address = myAddress;

            //in case of comingFrom edit address >> call editAddressApi
            // else, call addAddressApi
            if (getArguments().getString("flag").equals("edit")) {
                editAddressApi(getAddresses);
            } else {
                addAddressApi(getAddresses);
            }

        }
    }

    //custom popUp(AlertDialog) of selecting country or selecting state
    public void createPopUp(final Context context, final String type, final List<CountryModel> countriesArrList, final List<StateModel> statesArrList) {
        //Declaration and initialization the DialogBuilder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //inflate the custom design of alertDialog
        View popUpView = ((Activity) context).getLayoutInflater().inflate(R.layout.custom_pop_up, null);

        //Declaration and initialization the recyclerView from the inflating customView
        RecyclerView popUpRecycler = (RecyclerView) popUpView.findViewById(R.id.popUp_Recycler);

        //set the layoutManager of recyclerView
        popUpRecycler.setLayoutManager(new LinearLayoutManager(context));

        //set the adapter of recyclerView
        popUpRecycler.setAdapter(new PopUpAdapter(context, type, countriesArrList, statesArrList));

        //make the dialogBuilder cancelable
        builder.setCancelable(true);

        //connect the customView with dialogBuilder
        builder.setView(popUpView);

        //create the alertDialog from the builder
        dialog = builder.create();

        //show the alertDialog
        dialog.show();

        //handling the items click of recyclerView (using Custom OnItemTouchListener)
        popUpRecycler.addOnItemTouchListener(new RecyclerItemClickListener(context, popUpRecycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*
                *check the type
                * in case of countrySelection click >> the type = country
                * else >> the type = state
                */
                if (type.equalsIgnoreCase("country")) {
                    /*
                    *check if the new selected country equal the current country >> do nothing
                    * else >>
                    * 1- set the new selected country at countryTextView
                    * 2- initialize the the value in the stateTextView (for selection a new state related to the new countrySelection)
                    * 3- save the new selectedCountryId
                    * 4- call satesApi with the new CountryId
                    */
                    if (!country.getText().toString().equals(countriesArrList.get(position).name)) {
                        country.setText(countriesArrList.get(position).name);
                        state.setText(getString(R.string.select_area));
                        myAddress.countryId = countriesArrList.get(position).id;
                        stateProvincesApi(myAddress.countryId + "");
                    }
                } else {
                    /*
                    * in case of selectNewState
                     * 1- set the new selected state at stateTextView
                     * 2- save the new selectedStateId
                     * */
                    myAddress.stateProvinceId = statesArrList.get(position).id;
                    state.setText(statesArrList.get(position).name);
                }

                //finally call the function which close the popUp
                closePopUp();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    public void closePopUp() {
        //close the alertDialog(popUp)
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
                        GlobalFunctions.showErrorMessage(error, loading);
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
                        GlobalFunctions.showErrorMessage(error, loading);
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
                                Navigator.loadFragment(activity, AddressesFragment.newInstance(activity, getArguments().getString("comingFrom")), R.id.main_frameLayout_Container, false);

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
                                getFragmentManager().popBackStack();
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

}

