package app.atelier.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.AddressesAdapter;
import app.atelier.adapters.PaymentMethodsAdapter;
import app.atelier.adapters.ShippingMethodsAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.addresses.AddressModel;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.customers.GetCustomers;
import app.atelier.webservices.responses.orders.GetOrders;
import app.atelier.webservices.responses.orders.OrderModel;
import app.atelier.webservices.responses.products.Delivery;
import app.atelier.webservices.responses.shipping.GetShippingMethods;
import app.atelier.webservices.responses.shipping.ShippingModel;
import app.atelier.webservices.responses.stores.GetStores;
import app.atelier.webservices.responses.stores.PaymentMethodModel;
import app.atelier.webservices.responses.stores.PaymentModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

public class AddressesFragment extends Fragment {
    public static FragmentActivity activity;
    public static AddressesFragment fragment;
    public static SessionManager sessionManager;
    String selectedAddressId = "";
    int selectedBillingAddressPosition = 0;
    int selectedShippingAddressPosition = 0;


    @BindView(R.id.addresses_cb_theSameAddress)
    CheckBox theSameAddress;
    @BindView(R.id.addresses_recyclerView_addressesList)
    RecyclerView addressesList;
    @BindView(R.id.addresses_btn_next)
    Button next;
    @BindView(R.id.loading)
    ProgressBar loading;

    List<AddressModel> addressesArrList = new ArrayList<>();
    AddressesAdapter addressesAdapter;
    LinearLayoutManager layoutManager;
    GetOrders orders = new GetOrders();

    List<PaymentModel> paymentSystems = new ArrayList<>();
    List<PaymentMethodModel> paymentMethods = new ArrayList<>();
    List<ShippingModel> shippingMethods = new ArrayList<>();
    String paymentSystemName;
    String paymentMethodCode;
    String shippingMethodName;
    ShippingModel shipping;

    public static AddressesFragment newInstance(FragmentActivity activity, String flag, Integer selectedBillingAddressPosition) {
        fragment = new AddressesFragment();
        AddressesFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        Bundle b = new Bundle();
        b.putString("flag", flag);
        if (flag.equals("shipping")) {
            b.putInt("position", selectedBillingAddressPosition);
        }
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_addresses, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(activity.getResources().getString(R.string.my_addresses));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupAppbar("", true, true);

        if (getArguments().getString("flag").equals("myAccount")) {
            next.setVisibility(View.GONE);
        } else if (getArguments().getString("flag").equals("shipping")) {
            theSameAddress.setVisibility(View.GONE);
        }
        layoutManager = new LinearLayoutManager(activity);
        addressesAdapter = new AddressesAdapter(activity, addressesArrList, getArguments().getString("flag"),
                new AddressesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (getArguments().getString("flag").equalsIgnoreCase("cart") ||
                                getArguments().getString("flag").equals("shipping")) {
                            selectAddress(position);
                        }
                    }

                    @Override
                    public void onItemDeleteClick(final int position) {
                        new AlertDialog.Builder(activity)
                                .setTitle(getString(R.string.app_name))
                                .setMessage(getString(R.string.delete_address))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (addressesArrList.size() > position)
                                            deleteAddress(position);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(R.mipmap.logo)
                                .show();
                    }

                    @Override
                    public void onItemEditClick(final int position) {
                        Navigator.loadFragment(activity, AddNewAddressFragment.newInstance(
                                activity,
                                "",
                                "edit", addressesArrList.get(position)), R.id.main_frameLayout_Container, true);

                    }
                });


        addressesList.setLayoutManager(layoutManager);
        addressesList.setAdapter(addressesAdapter);

        AddressApi();
        theSameAddress.setOnCheckedChangeListener(checkedListener);
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

    @OnClick(R.id.addresses_txtView_add)
    public void addClick() {
        Navigator.loadFragment(activity, AddNewAddressFragment.newInstance(
                activity,
                getArguments().getString("flag"),
                "add", null), R.id.main_frameLayout_Container, true);
    }

    @OnClick(R.id.addresses_btn_next)
    public void nextClick() {
        if (selectedAddressId.length() > 0) {
            if (getArguments().getString("flag").equals("shipping")) {
                getShippingMethods();
            } else {
                if (theSameAddress.isChecked()) {
                    selectedShippingAddressPosition = selectedBillingAddressPosition;
                    getShippingMethods();
                } else {
                    Navigator.loadFragment(activity, AddressesFragment.newInstance(activity, "shipping", selectedBillingAddressPosition), R.id.main_frameLayout_Container, true);
                }

            }
        } else {
            Snackbar.make(loading, getString(R.string.select_address), Snackbar.LENGTH_LONG).show();
        }
    }

    private void AddressApi() {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().customerAddresses(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                new Callback<GetCustomers>() {
                    @Override
                    public void success(GetCustomers outResponse, retrofit.client.Response response) {
                        loading.setVisibility(View.GONE);
                        addressesArrList.clear();
                        addressesArrList.addAll(outResponse.customers.get(0).addresses);
                        addressesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error, loading);
                    }
                });
    }

    private void deleteAddress(final int position) {

        AtelierApiConfig.getCallingAPIInterface().deleteAddress(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                addressesArrList.get(position).id,
                new Callback<Response>() {
                    @Override
                    public void success(Response s, Response response) {

                        TypedInput body = response.getBody();
                        String outResponse = "";

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));

                            StringBuilder out = new StringBuilder();

                            String newLine = System.getProperty("line.separator");

                            String line;

                            while ((line = reader.readLine()) != null) {
                                out.append(line);
                                out.append(newLine);
                            }

                            outResponse = out.toString();

                        } catch (Exception ex) {

                            ex.printStackTrace();

                        }

                        if (outResponse != null) {
                            outResponse = outResponse.replace("\"", "");
                            outResponse = outResponse.replace("\n", "");
                            if (outResponse.equalsIgnoreCase("{}")) {

                                addressesArrList.remove(position);
                                addressesList.getAdapter().notifyItemRemoved(position);

                            }

                        }

                        loading.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error, loading);
                    }

                });

    }

    private void selectAddress(int position) {

        for (int i = 0; i < addressesArrList.size(); i++) {
            AddressModel addresses = addressesArrList.get(i);
            addresses.isSelected = false;
            addressesArrList.set(i, addresses);
        }

        AddressModel addresses = addressesArrList.get(position);
        if (getArguments().getString("flag").equals("shipping")) {
            selectedShippingAddressPosition = position;
            selectedBillingAddressPosition = getArguments().getInt("position");
        } else {
            selectedBillingAddressPosition = position;
        }
        selectedAddressId = addresses.id;
        addresses.isSelected = true;
        addressesArrList.set(position, addresses);
        addressesAdapter.notifyDataSetChanged();
    }

    public void getPaymentMethods() {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().currentStore(
                Constants.AUTHORIZATION_VALUE,
                new Callback<GetStores>() {
                    @Override
                    public void success(GetStores getStores, Response response) {
                        if (getStores != null) {
                            if (getStores.stores.get(0).storePaymentMethods != null) {
                                if (getStores.stores.get(0).storePaymentMethods.size() > 0) {
                                    paymentSystems.clear();
                                    paymentMethods.clear();
                                    paymentSystems.addAll(getStores.stores.get(0).storePaymentMethods);
                                    for (PaymentModel value : paymentSystems) {
                                        paymentMethods.addAll(value.paymentMethodsList);
                                    }
                                    if (paymentMethods.size() == 1) {
                                        paymentSystemName = paymentSystems.get(0).systemName;
                                        paymentMethodCode = paymentMethods.get(0).PaymentMethodCode;
                                        shoppingCartItemsForOrderApi();
                                    } else if (paymentMethods.size() > 1) {
                                        CustomPaymentMethodsDialog dialog = new CustomPaymentMethodsDialog(activity, paymentMethods);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.show();

                                        DisplayMetrics displayMetrics = new DisplayMetrics();
                                        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                        int displayWidth = displayMetrics.widthPixels;
                                        int displayHeight = displayMetrics.heightPixels;
                                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                                        layoutParams.copyFrom(dialog.getWindow().getAttributes());
                                        int dialogWindowWidth = (int) (displayWidth * 0.8f);
                                        int dialogWindowHeight = (int) (displayHeight * 0.3f);
                                        layoutParams.width = dialogWindowWidth;
                                        layoutParams.height = dialogWindowHeight;
                                        dialog.getWindow().setAttributes(layoutParams);
                                        dialog.getWindow().setGravity(Gravity.CENTER);

                                    }
                                    loading.setVisibility(View.GONE);
                                }
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

    public void getShippingMethods() {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().shipping(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                addressesArrList.get(selectedShippingAddressPosition).countryId,
                addressesArrList.get(selectedShippingAddressPosition).city,
                new Callback<GetShippingMethods>() {
                    @Override
                    public void success(GetShippingMethods getShippingMethods, Response response) {
                        if (getShippingMethods != null) {
                            if (getShippingMethods.shipping != null) {
                                if (getShippingMethods.shipping.size() > 0) {
                                    shippingMethods.clear();
                                    shippingMethods.addAll(getShippingMethods.shipping);


                                    shipping = shippingMethods.get(0);
                                    getPaymentMethods();

//
//                                    CustomShippingMethodsDialog dialog = new CustomShippingMethodsDialog(activity, shippingMethods);
//                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                    dialog.show();
//
//                                    DisplayMetrics displayMetrics = new DisplayMetrics();
//                                    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                                    int displayWidth = displayMetrics.widthPixels;
//                                    int displayHeight = displayMetrics.heightPixels;
//                                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//                                    layoutParams.copyFrom(dialog.getWindow().getAttributes());
//                                    int dialogWindowWidth = (int) (displayWidth * 0.8f);
//                                    int dialogWindowHeight = (int) (displayHeight * 0.3f);
//                                    layoutParams.width = dialogWindowWidth;
//                                    layoutParams.height = dialogWindowHeight;
//                                    dialog.getWindow().setAttributes(layoutParams);
//                                    dialog.getWindow().setGravity(Gravity.CENTER);

                                }
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

    public void shoppingCartItemsForOrderApi() {

        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().shoppingCartItemsForOrder(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts outResponse, retrofit.client.Response response) {

                        if (outResponse != null) {
                            if (outResponse.CartProducts.size() > 0) {
                                createOrders();
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

    private void createOrders() {
        prepareOrdersData();
        AtelierApiConfig.getCallingAPIInterface().createOrders(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                Constants.CONTENT_TYPE_VALUE,
                orders,
                new Callback<Response>() {
                    @Override
                    public void success(Response s, Response response) {
                        TypedInput body = response.getBody();
                        String outResponse = "";

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                            StringBuilder out = new StringBuilder();
                            String newLine = System.getProperty("line.separator");
                            String line;
                            while ((line = reader.readLine()) != null) {
                                out.append(line);
                                out.append(newLine);
                            }
                            loading.setVisibility(View.GONE);
                            outResponse = out.toString();
                            outResponse = outResponse.replaceAll("\"", "");
                            String[] mArrayStringValues = outResponse.split(",");
                            String payURL = mArrayStringValues[0];
                            if (payURL.contains("http")) {
                                Bundle b = new Bundle();
                                b.putString("id", payURL);
                                b.putString("order_id", mArrayStringValues[1]);
                                Fragment fragment = PayPaymentFragment.newInstance(activity);
                                fragment.setArguments(b);
                                Navigator.loadFragment(activity, fragment, R.id.main_frameLayout_Container, true);
                            } else {
                                OrderModel order = new OrderModel();
                                order.id = Integer.parseInt(mArrayStringValues[1].replaceAll("\n", ""));
                                clearStack();
                                MainActivity.shoppingCartItemsCount();
                                Fragment fragment = OrderDetailsFragment.newInstance(activity, order);
                                Navigator.loadFragment(activity, fragment, R.id.main_frameLayout_Container, true);

                            }


                        } catch (Exception ex) {

                            ex.printStackTrace();

                        }

                        if (outResponse != null) {

                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error, loading);
                    }

                });

    }

    private void prepareOrdersData() {

        OrderModel order = new OrderModel();
        order.customerId = Integer.parseInt(sessionManager.getUserId());
        order.paymentMethodSystemName = paymentSystemName;
        order.paymentMethodCode = paymentMethodCode;
        order.payment_by = 2;
        order.use_reward_points = "false";
        order.orderShippingExclTax = shipping.Price;
        order.orderShippingInclTax = shipping.Price;
        order.shippingMethod = shipping.name;
        order.shippingRateComputationMethodSystemName = shipping.SystemName;

        order.billingAddressId = (Integer.parseInt(addressesArrList.get(selectedBillingAddressPosition).id));
        order.shippingAddressId = (Integer.parseInt(addressesArrList.get(selectedShippingAddressPosition).id));

        AddressModel billingAddress = new AddressModel();

        billingAddress.firstName = (addressesArrList.get(selectedBillingAddressPosition).firstName);

        billingAddress.lastName = (addressesArrList.get(selectedBillingAddressPosition).lastName);

        billingAddress.email = (addressesArrList.get(selectedBillingAddressPosition).email);

        billingAddress.countryId = (addressesArrList.get(selectedBillingAddressPosition).countryId);

        billingAddress.stateProvinceId = (addressesArrList.get(selectedBillingAddressPosition).stateProvinceId);

        billingAddress.phoneNumber = (addressesArrList.get(selectedBillingAddressPosition).phoneNumber);

        billingAddress.city = (addressesArrList.get(selectedBillingAddressPosition).city);

        billingAddress.id = (addressesArrList.get(selectedBillingAddressPosition).id);

        order.billingAddress = billingAddress;

        AddressModel shippingAddress = new AddressModel();

        shippingAddress.firstName = (addressesArrList.get(selectedShippingAddressPosition).firstName);

        shippingAddress.lastName = (addressesArrList.get(selectedShippingAddressPosition).lastName);

        shippingAddress.email = (addressesArrList.get(selectedShippingAddressPosition).email);

        shippingAddress.countryId = (addressesArrList.get(selectedShippingAddressPosition).countryId);

        shippingAddress.stateProvinceId = (addressesArrList.get(selectedShippingAddressPosition).stateProvinceId);

        shippingAddress.phoneNumber = (addressesArrList.get(selectedShippingAddressPosition).phoneNumber);

        shippingAddress.city = (addressesArrList.get(selectedShippingAddressPosition).city);

        shippingAddress.id = (addressesArrList.get(selectedShippingAddressPosition).id);

        order.shippingAddress = shippingAddress;

        orders.order = order;

    }

    private void clearStack() {
        FragmentManager fm = activity.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount() - 2; ++i) {
            fm.popBackStack();
        }
    }

    class CustomPaymentMethodsDialog extends Dialog {

        public FragmentActivity activity;
        RecyclerView methods;
        public Button confirm, cancel;
        List<PaymentMethodModel> paymentMethodsList;
        PaymentMethodsAdapter paymentMethodsAdapter;
        LinearLayoutManager layoutManager;

        public CustomPaymentMethodsDialog(FragmentActivity activity, List<PaymentMethodModel> paymentMethodsList) {
            super(activity);
            this.activity = activity;
            this.paymentMethodsList = paymentMethodsList;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_methods);
            methods = (RecyclerView) findViewById(R.id.dialog_methods_rv_methods);
            confirm = (Button) findViewById(R.id.dialog_methods_btn_confirm);
            cancel = (Button) findViewById(R.id.dialog_methods_btn_cancel);

            layoutManager = new LinearLayoutManager(activity);
            paymentMethodsAdapter = new PaymentMethodsAdapter(activity, paymentMethodsList);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paymentMethodCode = paymentMethods.get(PaymentMethodsAdapter.mSelectedItem).PaymentMethodCode;
                    for (PaymentModel value : paymentSystems) {
                        for (PaymentMethodModel innerValue : value.paymentMethodsList) {
                            if (paymentMethodCode.equals(innerValue.PaymentMethodCode)) {
                                paymentSystemName = value.systemName;
                            }
                        }
                    }
                    dismiss();
                    PaymentMethodsAdapter.mSelectedItem = 0;
                    shoppingCartItemsForOrderApi();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    PaymentMethodsAdapter.mSelectedItem = 0;
                }
            });
            methods.setLayoutManager(layoutManager);
            methods.setAdapter(paymentMethodsAdapter);
        }
    }

//    class CustomShippingMethodsDialog extends Dialog {
//
//        public FragmentActivity activity;
//        RecyclerView methods;
//        public Button confirm, cancel;
//        List<ShippingModel> shippingMethodsList;
//        ShippingMethodsAdapter shippingMethodsAdapter;
//        LinearLayoutManager layoutManager;
//
//        public CustomShippingMethodsDialog(FragmentActivity activity, List<ShippingModel> shippingMethodsList) {
//            super(activity);
//            this.activity = activity;
//            this.shippingMethodsList = shippingMethodsList;
//        }
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            setContentView(R.layout.custom_dialog_methods);
//            methods = (RecyclerView) findViewById(R.id.dialog_methods_rv_methods);
//            confirm = (Button) findViewById(R.id.dialog_methods_btn_confirm);
//            cancel = (Button) findViewById(R.id.dialog_methods_btn_cancel);
//
//            layoutManager = new LinearLayoutManager(activity);
//            shippingMethodsAdapter = new ShippingMethodsAdapter(activity, shippingMethodsList);
//
//            confirm.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    shipping = shippingMethods.get(ShippingMethodsAdapter.mSelectedItem);
//                    dismiss();
//                    PaymentMethodsAdapter.mSelectedItem = 0;
//                    getPaymentMethods();
//                }
//            });
//            cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dismiss();
//                    ShippingMethodsAdapter.mSelectedItem = 0;
//                }
//            });
//            methods.setLayoutManager(layoutManager);
//            methods.setAdapter(shippingMethodsAdapter);
//        }
//    }


}

