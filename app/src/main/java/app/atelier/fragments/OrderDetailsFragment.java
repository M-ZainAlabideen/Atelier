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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.OrderDetailsAdapter;
import app.atelier.classes.Constants;
import app.atelier.classes.FileDownloader;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.DownloadAPIInterface;
import app.atelier.webservices.DownloadApiConfig;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.orders.GetOrders;
import app.atelier.webservices.responses.orders.OrderItems;
import app.atelier.webservices.responses.orders.OrderModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class OrderDetailsFragment extends Fragment {
    public static FragmentActivity activity;
    public static OrderDetailsFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.orderDetails_recyclerView_detailsList)
    RecyclerView detailsList;
    @BindView(R.id.orderDetails_txtView_details)
    TextView details;
    @BindView(R.id.orderDetails_txtView_total)
    TextView total;
    @BindView(R.id.orderDetails_linearLayout_bottomContainer)
    LinearLayout bottomContainer;
    @BindView(R.id.orderDetails_scrollView_container)
    ScrollView container;
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
        sessionManager = new SessionManager(activity);
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
        container.setVisibility(View.GONE);
        MainActivity.setupAppbar("", true, true);

        order = (OrderModel) getArguments().getSerializable("order");

        orderDetailsAdapter = new OrderDetailsAdapter(activity, orderDetailsArrList,
                order);
        layoutManager = new LinearLayoutManager(activity);
        detailsList.setLayoutManager(layoutManager);
        detailsList.setAdapter(orderDetailsAdapter);

            if (orderDetailsArrList.size() > 0)
                loading.setVisibility(View.GONE);
            else
                orderByIdApi();

    }

    @OnClick(R.id.orderDetails_btn_reorder)
    public void reorderClick() {
        reorder();
    }

    @OnClick(R.id.orderDetails_btn_pdfInvoice)
    public void pdfInvoiceClick() {
        if (order.orderStatus.equalsIgnoreCase(Constants.ORDER_COMPLETE_STATUS) &&
                order.paymentStatus.equalsIgnoreCase(Constants.PAYMENT_STATUS_PAID)) {

            if(GlobalFunctions.isWriteExternalStorageAllowed(activity)) {

                String filename = Calendar.getInstance().getTimeInMillis() + ".pdf";
                String outPath = Environment.getExternalStorageDirectory()
                        + File.separator
                        + "Download"
                        + File.separator
                        + filename;
                startDownload(Constants.DOWNLOAD_INVOICE.replace("{orderId}", String.valueOf(order.id)), outPath);

                return;
            }

            if (!GlobalFunctions.isWriteExternalStorageAllowed(activity)) {

                GlobalFunctions.requestWriteExternalStoragePermission(activity);

            }
        }

    }


    private void startDownload(String fileUrl, final String filePath) {
        loading.setVisibility(View.VISIBLE);

        DownloadAPIInterface downloadService = DownloadApiConfig.getClient().create(DownloadAPIInterface.class);

        Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrlSync(
                Constants.AUTHORIZATION_VALUE,
                "application/x-www-form-urlencoded",
                fileUrl);

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            loading.setVisibility(View.GONE);
                            File file = new File(filePath);
                            Intent target = new Intent(Intent.ACTION_VIEW);
                            Uri uri = FileProvider.getUriForFile
                                    (activity, "app.atelier.fileprovider",file);
                            target.setDataAndType(uri,"application/pdf");
                            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            Intent intent = Intent.createChooser(target, "Open File");
                            try {
                                activity.startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                // Instruct the user to install a PDF reader here, or something
                            }
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {

                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), filePath);
                            return null;

                        }

                    }.execute();

                } else {

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }

        });

    }


    private boolean writeResponseBodyToDisk(ResponseBody body, String filePath) {
        try {
            // todo change the file location/name according to your needs
            File f = new File(Environment.getExternalStorageDirectory(), File.separator + "Download");

            f.mkdirs();


            File futureStudioIconFile = new File(filePath);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("startDownload 1 ->", "file download: " + fileSizeDownloaded + " of " + fileSize);

                    if (futureStudioIconFile.exists())
                        Log.d("startDownload 1 ->", "file path: " + futureStudioIconFile.getAbsolutePath());
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public void orderByIdApi() {
        AtelierApiConfig.getCallingAPIInterface().orderById(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
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
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error, loading);
                    }
                });
    }

    private void reorder() {

        loading.setVisibility(View.VISIBLE);


        AtelierApiConfig.getCallingAPIInterface().reorder(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
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
                        GlobalFunctions.showErrorMessage(error, loading);
                    }

                });

    }

    private void setData() {

        if (order != null) {

            String htmlContent = "";
            htmlContent = "<h3><b>" + activity.getString(R.string.order) + "&nbsp;#" + order.id + "</b></h3>";
            if (order.createdOnUtc != null) {
                if (order.createdOnUtc.length() > 0) {
                    htmlContent = htmlContent + "" + activity.getString(R.string.order_date) + ":&nbsp;" + convertDateToString(order.createdOnUtc);
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
                            + ":&nbsp;<b>" + order.orderTotal + " " + order.customerCurrencyCode + "" + "</b>";
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

            if (Build.VERSION.SDK_INT >= 24) {
                details.setText(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY));
            } else {
                details.setText(Html.fromHtml(htmlContent));

            }

        }

        total.setText(activity.getString(R.string.total_price) + ": " +
                order.orderTotal + " " + order.customerCurrencyCode);


        pdfInvoice.setVisibility(View.GONE);

        bottomContainer.setVisibility(View.VISIBLE);

        if (order.orderStatus.equalsIgnoreCase(Constants.ORDER_COMPLETE_STATUS) &&
                order.paymentStatus.equalsIgnoreCase(Constants.PAYMENT_STATUS_PAID)) {

            pdfInvoice.setVisibility(View.VISIBLE);

            reorder.setVisibility(View.VISIBLE);
        }

        container.setVisibility(View.VISIBLE);

    }

    public static String convertDateToString(String date) {

        String dateResult = "";
        Locale locale = null;
        if (MainActivity.isEnglish)
            locale = new Locale("en");
        else
            locale = new Locale("ar");

        SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale);

        dateFormatter1.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat dateFormatter2 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa", locale);

        int index = date.lastIndexOf('/');

        try {

            dateResult = dateFormatter2.format(dateFormatter1.parse(date.substring(index + 1)));

        } catch (ParseException e) {

            e.printStackTrace();

        }

        return dateResult;

    }


}
