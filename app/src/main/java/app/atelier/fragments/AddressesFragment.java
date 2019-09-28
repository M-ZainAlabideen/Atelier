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
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.AddressesAdapter;
import app.atelier.adapters.PaymentMethodsAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.addresses.AddressModel;
import app.atelier.webservices.responses.cart.CartProductModel;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.customers.GetCustomers;
import app.atelier.webservices.responses.orders.GetOrders;
import app.atelier.webservices.responses.orders.OrderModel;
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
    int selectedAddressPosition = 0;

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

    String paymentSystemName;
    String paymentMethodCode;

    public static AddressesFragment newInstance(FragmentActivity activity, String flag) {
        fragment = new AddressesFragment();
        AddressesFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        Bundle b = new Bundle();
        b.putString("flag", flag);
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
        }

        layoutManager = new LinearLayoutManager(activity);
        addressesAdapter = new AddressesAdapter(activity, addressesArrList, getArguments().getString("flag"),
                new AddressesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (getArguments().getString("flag").equalsIgnoreCase("cart")) {
                            selectAddress(position);
                        }
                    }

                    @Override
                    public void onItemDeleteClick(final int position) {
                        new AlertDialog.Builder(activity)
                                .setTitle(getString(R.string.confirm))
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
        if (addressesArrList.size() > 0) {
            loading.setVisibility(View.GONE);
        } else {
            AddressApi();
        }
    }

    @OnClick(R.id.addresses_txtView_add)
    public void addClick() {
        Navigator.loadFragment(activity, AddNewAddressFragment.newInstance(
                activity,
                getArguments().getString("flag"),
                "add", null), R.id.main_frameLayout_Container, true);
    }

    @OnClick(R.id.addresses_btn_next)
    public void nextClick() {

        if (addressesArrList.size() > selectedAddressPosition) {

            if (selectedAddressId.length() > 0) {
                getPaymentMethods();
                //test();
                // Navigator.loadFragment(activity,PaymentMethodsFragment.newInstance(activity),R.id.main_frameLayout_Container,true);
            } else {
                Snackbar.make(loading, getString(R.string.select_address), Snackbar.LENGTH_LONG).show();
            }

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
        selectedAddressPosition = position;
        selectedAddressId = addresses.id;
        addresses.isSelected = true;
        addressesArrList.set(position, addresses);
        addressesAdapter.notifyDataSetChanged();
        //addressesList.getAdapter().notifyItemRangeChanged(0, addressesArrList.size(), addressesArrList);

    }


    ///////////////////////////////////////////////////////

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
                                        //createDialogPayment();
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

    public void shoppingCartItemsForOrderApi() {
        {
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
                                    createOrders(outResponse.CartProducts);
                                    loading.setVisibility(View.GONE);
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

    private void createOrders(List<CartProductModel> cartArrayList) {
        loading.setVisibility(View.VISIBLE);
        prepareOrdersData(cartArrayList);
        AtelierApiConfig.getCallingAPIInterface().createOrders(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                Constants.CONTENT_TYPE_VALUE,
                orders,
                new Callback<Response>() {
                    @Override
                    public void success(Response s, Response response) {
                        loading.setVisibility(View.GONE);
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
                                MainActivity.shoppingCartItemsCount(activity);
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

    private void prepareOrdersData(List<CartProductModel> cartArrayList) {

        OrderModel order = new OrderModel();
        order.paymentMethodSystemName = paymentSystemName;
        order.paymentMethodCode = paymentMethodCode;
        order.payment_by = 2;
        order.use_reward_points = "false";

        order.billingAddressId = (Integer.parseInt(addressesArrList.get(selectedAddressPosition).id));

        order.customerId = Integer.parseInt(sessionManager.getUserId());

        AddressModel billingAddress = new AddressModel();

        billingAddress.firstName = (addressesArrList.get(selectedAddressPosition).firstName);

        billingAddress.lastName = (addressesArrList.get(selectedAddressPosition).lastName);

        billingAddress.email = (addressesArrList.get(selectedAddressPosition).email);

        billingAddress.countryId = (addressesArrList.get(selectedAddressPosition).countryId);

        billingAddress.stateProvinceId = (addressesArrList.get(selectedAddressPosition).stateProvinceId);

        billingAddress.phoneNumber = (addressesArrList.get(selectedAddressPosition).phoneNumber);

        billingAddress.id = (addressesArrList.get(selectedAddressPosition).id);

        order.billingAddress = (billingAddress);

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
            setContentView(R.layout.custom_dialog_payment_methods);
            methods = (RecyclerView) findViewById(R.id.dialog_payment_methods_rv_methods);
            confirm = (Button) findViewById(R.id.dialog_payment_methods_btn_confirm);
            cancel = (Button) findViewById(R.id.dialog_payment_methods_btn_cancel);

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

}

