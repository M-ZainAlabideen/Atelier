package app.atelier.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.OrderDetailsAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.FileDownloader;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.orders.GetOrders;
import app.atelier.webservices.responses.orders.OrderItems;
import app.atelier.webservices.responses.orders.OrderModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrderDetailsFragment extends Fragment {
    public static FragmentActivity activity;
    public static OrderDetailsFragment fragment;

    @BindView(R.id.orderDetails_recyclerView_detailsList)
    RecyclerView detailsList;
    @BindView(R.id.orderDetails_txtView_details)
    TextView details;
    @BindView(R.id.orderDetails_txtView_total)
    TextView total;
    @BindView(R.id.orderDetails_linearLayout_bottomContainer)
    LinearLayout bottomContainer;
    @BindView(R.id.orderDetails_btn_reorder)
    Button reorder;
    @BindView(R.id.orderDetails_btn_pdfInvoice)
    Button pdfInvoice;
    @BindView(R.id.loading)
    ProgressBar loading;

    OrderModel order;
    List<OrderItems> orderDetailsArrList = new ArrayList<>();
    OrderDetailsAdapter orderDetailsAdapter;
    RecyclerView.LayoutManager layoutManager;

    public static OrderDetailsFragment newInstance(FragmentActivity activity, OrderModel order) {
        fragment = new OrderDetailsFragment();
        OrderDetailsFragment.activity = activity;
        Bundle b = new Bundle();
        b.putSerializable("order", order);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_order_details, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(activity.getResources().getString(R.string.order_details));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupBottomAppbar("");

        order = (OrderModel) getArguments().getSerializable("order");
        Toast.makeText(activity, order.id+"", Toast.LENGTH_SHORT).show();
        orderDetailsAdapter = new OrderDetailsAdapter(activity, orderDetailsArrList,
                order);
        layoutManager = new LinearLayoutManager(activity);
        detailsList.setLayoutManager(layoutManager);
        detailsList.setAdapter(orderDetailsAdapter);

//        if (order != null) {
//            if (order.orderItems != null) {
//                if (order.orderItems.size() > 0) {
//                    loading.setVisibility(View.GONE);
//                    setData();
//                }
//                else{
                    orderByIdApi();
               // }
           // }
      //  }

    }

    @OnClick(R.id.orderDetails_btn_reorder)
    public void reorderClick() {
        reorder();
    }

    @OnClick(R.id.orderDetails_btn_pdfInvoice)
    public void pdfInvoiceClick() {
        if (order.orderStatus.equalsIgnoreCase(Constants.ORDER_COMPLETE_STATUS) &&
                order.paymentStatus.equalsIgnoreCase(Constants.PAYMENT_STATUS_PAID)) {

            new OrderDetailsFragment.DownloadFile().execute(
                    Constants.DOWNLOAD_INVOICE.replace("aaa", String.valueOf(order.id)),
                    Calendar.getInstance().getTimeInMillis() + ".pdf");
        }

    }

    public void orderByIdApi() {
        Toast.makeText(activity, order.id+" test", Toast.LENGTH_SHORT).show();

        AtelierApiConfig.getCallingAPIInterface().orderById(
                Constants.AUTHORIZATION_VALUE,
                MainActivity.language,
                String.valueOf(order.id),
                new Callback<GetOrders>() {
                    @Override
                    public void success(GetOrders getOrders, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getOrders != null) {
                            if (getOrders.orders.size() > 0) {
                                order = getOrders.orders.get(0);
                                orderDetailsArrList.addAll(order.orderItems);
                                orderDetailsAdapter.notifyDataSetChanged();
                                setData();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void reorder() {

        loading.setVisibility(View.VISIBLE);


        AtelierApiConfig.getCallingAPIInterface().reorder(
                Constants.AUTHORIZATION_VALUE,
                MainActivity.language,
                String.valueOf(order.id),
                new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts outResponse, retrofit.client.Response response) {

                        if (outResponse != null) {

                            Navigator.loadFragment(activity, CartFragment.newInstance(activity), R.id.main_frameLayout_Container, true);
                        }
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(loading, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                    }

                });

    }

    private void setData() {

        if (order != null) {

            if (getArguments().getBoolean(Constants.CODE)) {
                if (order.orderStatus.equalsIgnoreCase(Constants.ORDER_COMPLETE_STATUS) &&
                        order.paymentStatus.equalsIgnoreCase(Constants.PAYMENT_STATUS_PAID)) {
                    if (isAdded()) {
                        new AlertDialog.Builder(activity)
                                .setTitle(getString(R.string.confirm))
                                .setMessage(getString(R.string.order_done))
                                .setPositiveButton(getString(android.R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();
                                            }
                                        })
                                .setCancelable(false)
                                .setIcon(R.mipmap.logo)
                                .show();
                    }
                }
            }

            String htmlContent = "";

            htmlContent = "<h3><b>" + activity.getString(R.string.order) + "&nbsp;#" + order.id + "</b></h3>";

            if (order.createdOnUtc != null) {

                if (order.createdOnUtc.length() > 0) {

                    htmlContent = htmlContent + "" + activity.getString(R.string.order_date) + ":&nbsp;" + order.createdOnUtc;

                }

            }

            if (order.orderStatus != null) {

                if (order.orderStatus.length() > 0) {

                    htmlContent = htmlContent + "<br />" + activity.getString(R.string.order_status) + ":&nbsp;" + order.orderStatus;

                }

            }

            if (order.orderTotal != null) {

                if (order.orderStatus.length() > 0) {

                    htmlContent = htmlContent + "<br />" + activity.getString(R.string.order_total)
                            + ":&nbsp;<b>" + order.customerCurrencyCode + order.orderTotal + "" + "</b>";

                }

            }

            if (order.billingAddress != null) {

                htmlContent = htmlContent + "<br /><br /><b>" +
                        activity.getString(R.string.billing_address)
                        + "</b><br />" +
                        activity.getString(R.string.name)
                        + ":&nbsp;" +
                        order.billingAddress.firstName +
                        " " + order.billingAddress.lastName;

                htmlContent = htmlContent + "<br />" + activity.getString(R.string.mail) + ":&nbsp;" + order.billingAddress.email;

                htmlContent = htmlContent + "<br />" + activity.getString(R.string.phone) + ":&nbsp;" + order.billingAddress.phoneNumber;

                htmlContent = htmlContent + "<br />" + activity.getString(R.string.state) + ":&nbsp;" + order.billingAddress.province;

                htmlContent = htmlContent + "<br />" + activity.getString(R.string.country) + ":&nbsp;" + order.billingAddress.country;

            }

            htmlContent = htmlContent + "<br /><br /><b>" + activity.getString(R.string.payment) + "</b><br />"
                    + activity.getString(R.string.payment_method) + "&nbsp;"
                    + order.paymentMethodSystemName.split("\\.")[order.paymentMethodSystemName.split("\\.").length - 1];

            htmlContent = htmlContent + "<br />" + activity.getString(R.string.payment_status) + "&nbsp;" + order.paymentStatus;

            htmlContent = htmlContent + "<br /><br /><b>" + activity.getString(R.string.products) + "</b>";

            Log.d("htmlContent", "" + htmlContent);

            if (Build.VERSION.SDK_INT >= 24) {

                details.setText(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY));
            } else {

                details.setText(Html.fromHtml(htmlContent));

            }

        }

        total.setText(activity.getString(R.string.total_price) + ": " +
                order.customerCurrencyCode + order.orderTotal);


        pdfInvoice.setVisibility(View.GONE);

        bottomContainer.setVisibility(View.VISIBLE);

        if (order.orderStatus.equalsIgnoreCase(Constants.ORDER_COMPLETE_STATUS) &&
                order.paymentStatus.equalsIgnoreCase(Constants.PAYMENT_STATUS_PAID)) {

            pdfInvoice.setVisibility(View.VISIBLE);

            reorder.setVisibility(View.VISIBLE);
        }

    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {
        String fileUrl;
        String fileName;
        File file;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {

            fileUrl = strings[0];
            fileName = strings[1];
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "Atelier");
            folder.mkdir();
            file = new File(folder, fileName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, file);
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loading.setVisibility(View.GONE);
            Snackbar.make(loading, activity.getResources().getString(R.string.file_manager), Snackbar.LENGTH_LONG).show();
            Intent target = new Intent(Intent.ACTION_VIEW);

            target.setDataAndType(Uri.fromFile(file), "application/pdf");

            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "Open File");

            try {
                activity.startActivity(intent);

            } catch (ActivityNotFoundException e) {

            }
        }
    }
}
