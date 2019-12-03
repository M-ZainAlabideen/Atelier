package app.atelier.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.DefaultSliderView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.adapters.AttributesAdapter;
import app.atelier.adapters.AttributesSelectedSizesAdapter;
import app.atelier.adapters.ProductsAdapter;
import app.atelier.classes.AppController;
import app.atelier.classes.Constants;
import app.atelier.classes.FixControl;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.cart.CartItem;
import app.atelier.webservices.responses.cart.CartItem_;
import app.atelier.webservices.responses.cart.CartProductModel;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.attributes.AttributeModel;
import app.atelier.webservices.responses.attributes.AttributeValueModel;
import app.atelier.webservices.responses.attributes.OrderAttributeModel;
import app.atelier.webservices.responses.products.GetProducts;
import app.atelier.webservices.responses.products.ImageModel;
import app.atelier.webservices.responses.products.ProductModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

public class ProductDetailsFragment extends Fragment {
    public static FragmentActivity activity;
    public static ProductDetailsFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.productDetails_cl_container)
    ConstraintLayout container;
    @BindView(R.id.productDetails_linear_addCart)
    LinearLayout addCartContainer;
    @BindView(R.id.productDetails_recycler_attributes)
    RecyclerView attributes;
    @BindView(R.id.productDetails_recycler_relatedProducts)
    RecyclerView relatedProducts;
    @BindView(R.id.productDetails_tv_relatedTitle)
    TextView relatedTitle;
    @BindView(R.id.productDetails_slider)
    SliderLayout slider;
    @BindView(R.id.productDetails_img_placeHolder)
    ImageView placeholder;
    @BindView(R.id.productDetails_txt_name)
    TextView productName;
    @BindView(R.id.productDetails_txt_description)
    WebView productDescription;
    @BindView(R.id.productDetails_txt_price)
    TextView productPrice;
    @BindView(R.id.productDetails_txt_oldPrice)
    TextView oldPrice;
    @BindView(R.id.productDetails_txt_quantity)
    TextView productQuantity;
    @BindView(R.id.productDetails_editTxt_notes)
    EditText notes;
    @BindView(R.id.productDetails_img_addFavorite)
    ImageView addFavorite;
    @BindView(R.id.loading)
    ProgressBar loading;


    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.productDetails_linear_sizeOption1)
    LinearLayout sizeOption1;
    @BindView(R.id.productDetails_recycler_sizeOptionsList1)
    RecyclerView sizeOptionsList1;
    @BindView(R.id.productDetails_radio_radioSize1)
    RadioButton radioSize1;

    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.productDetails_radio_radioSize2)
    RadioButton radioSize2;
    @BindView(R.id.productDetails_linear_sizeOptions2)
    LinearLayout sizeOptions2;

    @BindView(R.id.productDetails_tv_title1)
    TextView title1;
    @BindView(R.id.productDetails_tv_title2)
    TextView title2;
    @BindView(R.id.productDetails_tv_title3)
    TextView title3;
    @BindView(R.id.productDetails_tv_title4)
    TextView title4;
    @BindView(R.id.productDetails_tv_title5)
    TextView title5;
    @BindView(R.id.productDetails_tv_title6)
    TextView title6;
    @BindView(R.id.productDetails_tv_title7)
    TextView title7;
    @BindView(R.id.productDetails_tv_title8)
    TextView title8;
    @BindView(R.id.productDetails_tv_title9)
    TextView title9;

    @BindView(R.id.productDetails_editTxt_value1)
    EditText value1;
    @BindView(R.id.productDetails_editTxt_value2)
    EditText value2;
    @BindView(R.id.productDetails_editTxt_value3)
    EditText value3;
    @BindView(R.id.productDetails_editTxt_value4)
    EditText value4;
    @BindView(R.id.productDetails_editTxt_value5)
    EditText value5;
    @BindView(R.id.productDetails_editTxt_value6)
    EditText value6;
    @BindView(R.id.productDetails_editTxt_value7)
    EditText value7;
    @BindView(R.id.productDetails_editTxt_value8)
    EditText value8;
    @BindView(R.id.productDetails_editTxt_value9)
    EditText value9;

    @BindView(R.id.productDetails_tv_measure1)
    TextView measure1;
    @BindView(R.id.productDetails_tv_measure2)
    TextView measure2;
    @BindView(R.id.productDetails_tv_measure3)
    TextView measure3;
    @BindView(R.id.productDetails_tv_measure4)
    TextView measure4;
    @BindView(R.id.productDetails_tv_measure5)
    TextView measure5;
    @BindView(R.id.productDetails_tv_measure6)
    TextView measure6;
    @BindView(R.id.productDetails_tv_measure7)
    TextView measure7;
    @BindView(R.id.productDetails_tv_measure8)
    TextView measure8;
    @BindView(R.id.productDetails_tv_measure9)
    TextView measure9;


    @BindView(R.id.view3)
    View view3;


    public static ArrayList<AttributeModel> attributesList = new ArrayList<>();
    public static ArrayList<AttributeModel> actualAttributesArrayList = new ArrayList<>();
    public static ArrayList<AttributeModel> fixedAttributesArrayList = new ArrayList<>();
    public static ArrayList<AttributeModel> finalAttributesArrayList = new ArrayList<>();
    int maxNum;
    int sizeSelectedPosition = -1;
    int counter;


    public static ProductModel product;
    CartItem cartItem = new CartItem();
    int quantity = 1;
    public AttributesAdapter attributesAdapter;
    public LinearLayoutManager layoutManagerAttributes;
    public ArrayList<String> sliderList = new ArrayList<>();

    public ArrayList<ProductModel> relatedProductsList = new ArrayList<>();
    ProductsAdapter relatedProductsAdapter;
    LinearLayoutManager horizLayoutManager;

    private boolean active = true;
    private AttributesSelectedSizesAdapter adapter;
    private boolean ShowFixedView1 = false;
    private boolean ShowFixedView2 = false;
    private boolean withFiveValue = false;
    private boolean withSixValue = false;
    private boolean withSevenValue = false;
    private boolean withEightValue = false;
    private boolean withNineValue = false;


    public static ProductDetailsFragment newInstance(FragmentActivity activity, int productId) {
        fragment = new ProductDetailsFragment();
        ProductDetailsFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        Bundle b = new Bundle();
        b.putInt("productId", productId);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_product_details, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupAppbar("", true, true);
        MainActivity.title.setText(getString(R.string.product_details));
        container.setVisibility(View.GONE);
        addCartContainer.setVisibility(View.GONE);
        FixControl.setupUI(container, activity);


        relatedTitle.setVisibility(View.GONE);
        relatedProducts.setVisibility(View.GONE);

        view1.setVisibility(View.GONE);
        sizeOption1.setVisibility(View.GONE);

        view2.setVisibility(View.GONE);
        radioSize2.setVisibility(View.GONE);
        sizeOptions2.setVisibility(View.GONE);

        view3.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        placeholder.getLayoutParams().height = (int) (width * 1.25);

        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        if (sessionManager.getUserLanguage().equals("en")) {
            Typeface enBold = Typeface.createFromAsset(activity.getAssets(), "montserrat_medium.ttf");
            productName.setTypeface(enBold);
            radioSize1.setTypeface(enBold);
            radioSize2.setTypeface(enBold);

        } else {
            Typeface arBold = Typeface.createFromAsset(activity.getAssets(), "droid_arabic_kufi_bold.ttf");
            productName.setTypeface(arBold);
            radioSize1.setTypeface(arBold);
            radioSize2.setTypeface(arBold);
        }

        initialize();
        productByIdApi();


        relatedProductsAdapter = new ProductsAdapter(activity, "relatedProduct", relatedProductsList, null, new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Navigator.loadFragment(activity, ProductDetailsFragment.newInstance(activity, relatedProductsList.get(position).id),
                        R.id.main_frameLayout_Container, true);
            }

            @Override
            public void onAddCartClick(int position) {
                if (relatedProductsList.get(position).attributes != null && relatedProductsList.get(position).attributes.size() > 0) {
                    Navigator.loadFragment(activity, ProductDetailsFragment.newInstance(activity, relatedProductsList.get(position).id), R.id.main_frameLayout_Container, true);
                } else {
                    CartItem_ cartItem_ = new CartItem_();
                    cartItem_.productId = relatedProductsList.get(position).id;
                    cartItem_.customerId = Integer.valueOf(sessionManager.getUserId());
                    cartItem_.quantity = 1;
                    cartItem_.shoppingCartType = "1";
                    CartItem cartItem = new CartItem();
                    cartItem.shoppingCartItem = cartItem_;
                    addCartOrFavoriteApi("addToCart", cartItem, null);
                }
            }

            @Override
            public void onAddFavoriteClick(int position, ImageView addFavorite) {
                if (addFavorite.getDrawable().getConstantState() ==
                        getResources().getDrawable(R.mipmap.icon_fav_sel).getConstantState()) {
                    deleteFavoriteApi(position, addFavorite);
                } else {
                    if (relatedProductsList.get(position).attributes != null && relatedProductsList.get(position).attributes.size() > 0) {
                        Navigator.loadFragment(activity, ProductDetailsFragment.newInstance(activity, relatedProductsList.get(position).id), R.id.main_frameLayout_Container, true);
                    } else {
                        CartItem_ favoriteItem_ = new CartItem_();
                        favoriteItem_.productId = relatedProductsList.get(position).id;
                        favoriteItem_.customerId = Integer.valueOf(sessionManager.getUserId());
                        favoriteItem_.quantity = 1;
                        favoriteItem_.shoppingCartType = "2";
                        CartItem favoriteItem = new CartItem();
                        favoriteItem.shoppingCartItem = favoriteItem_;
                        addCartOrFavoriteApi("addToFavorite", favoriteItem, addFavorite);
                    }
                }
            }
        });
        horizLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        relatedProducts.setLayoutManager(horizLayoutManager);
        relatedProducts.setAdapter(relatedProductsAdapter);
    }


    public void initialize() {
        attributesList.clear();
        actualAttributesArrayList.clear();
        fixedAttributesArrayList.clear();
        finalAttributesArrayList.clear();
        value1.setText("");
        value2.setText("");
        value3.setText("");
        value4.setText("");
        value5.setText("");
        value6.setText("");
        value7.setText("");
        value8.setText("");
        value9.setText("");

        maxNum = 0;
        product = null;
        cartItem = new CartItem();
        quantity = 1;
        sliderList.clear();
        relatedProductsList.clear();
        active = true;
        ShowFixedView1 = false;
        ShowFixedView2 = false;
    }

    @OnClick(R.id.productDetails_img_addFavorite)
    public void addFavoriteClick() {
        if (addFavorite.getDrawable().getConstantState() ==
                getResources().getDrawable(R.mipmap.icon_add_fav_sel).getConstantState()) {
            deleteFavoriteApi(-1, addFavorite);
        } else {
            CartItem_ favoriteItem_ = new CartItem_();
            favoriteItem_.productId = product.id;
            favoriteItem_.customerId = Integer.valueOf(sessionManager.getUserId());
            favoriteItem_.quantity = quantity;
            favoriteItem_.shoppingCartType = "2";
            CartItem favoriteItem = new CartItem();
            favoriteItem.shoppingCartItem = favoriteItem_;
            addCartOrFavoriteApi("addToFavorite", favoriteItem, addFavorite);

        }
    }

    @OnClick(R.id.productDetails_img_plus)
    public void plusClick() {
        quantity = quantity + 1;
        productQuantity.setText(quantity + "");
    }

    @OnClick(R.id.productDetails_img_minus)
    public void minusClick() {
        if (quantity > 1) {
            quantity = quantity - 1;
            productQuantity.setText(quantity + "");
        }
    }

    @OnClick(R.id.productDetails_linear_addCart)
    public void addCartClick() {
        if (radioSize1.isChecked()) {
            boolean b = false;
            for (int i = 0; i < fixedAttributesArrayList.get(0).attribute_values.size(); i++) {
                if (fixedAttributesArrayList.get(0).attribute_values
                        .get(i).is_pre_selected) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                Snackbar.make(loading, getString(R.string.select_size), Snackbar.LENGTH_SHORT).show();
                return;
            }
        } else if (radioSize2.isChecked()) {
            if (ShowFixedView1)
                counter = 1;
            else
                counter = 0;
            for (int i = counter; i < fixedAttributesArrayList.size(); i++) {
                if (fixedAttributesArrayList.get(i).attribute_values.get(0).name == null
                        || fixedAttributesArrayList.get(i).attribute_values.get(0).name.matches("")) {
                    Snackbar.make(loading, getString(R.string.enter_size), Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        boolean required = true;
        String s = "";
        for (AttributeModel a : attributesList) {
            if (a.is_required) {
                boolean f = false;
                for (AttributeValueModel av : a.attribute_values) {
                    if (av.is_pre_selected) {
                        f = true;
                    }
                }
                if (!f) {
                    if (a.localized_names != null) {
                        s += a.localized_names.get(0).localizedName + ", ";
                    } else {
                        s += "" + ", ";
                    }
                }
                required = f;
            }
        }

        if (required) {
            prepareShoppingCartData(true);
        } else {
            Snackbar.make(loading, getString(R.string.DataMissing) + "\n" + s.substring(0, s.length() - 2), Snackbar.LENGTH_LONG).show();
        }
    }


    public void productByIdApi() {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().productById(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                String.valueOf(getArguments().getInt("productId")),
                new Callback<GetProducts>() {
                    @Override
                    public void success(GetProducts getProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);
                        addCartContainer.setVisibility(View.VISIBLE);
                        product = getProducts.products.get(0);
                        setData();
                        relatedProducts();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error, loading);
                    }
                }
        );
    }

    private void setData() {
        AppController.getInstance().trackEvent(product.getLocalizedName(), "Details", "Mobile");
        AppController.getInstance().trackEvent(product.getLocalizedName(), "Details", "Android");
        setupSlider();
        productName.setText(product.getLocalizedName());
        productPrice.setText(product.formattedPrice);
        if (product.oldPrice == 0) {
            oldPrice.setVisibility(View.GONE);
        } else {
            oldPrice.setText(product.formattedOldPrice);
        }


        String contentStr = product.getFullDescription();
        if (contentStr != null && !contentStr.isEmpty()) {
            String fontName;
            contentStr = contentStr.replace("font", "f");
            contentStr = contentStr.replace("color", "c");
            contentStr = contentStr.replace("size", "s");
            if (MainActivity.isEnglish)
                fontName = "montserrat_regular.ttf";
            else
                fontName = "droid_arabic_kufi.ttf";
            String head = "<head><style>@font-face {font-family: 'verdana';src: url('file:///android_asset/" + fontName + "');}body {font-family: 'verdana';}</style></head>";
            String htmlData = "<html>" + head + (MainActivity.isEnglish ? "<body dir=\"ltr\"" : "<body dir=\"rtl\"") + " style=\"font-family: verdana\">" +
                    contentStr + "</body></html>";
            productDescription.loadDataWithBaseURL("", htmlData, "text/html; charset=utf-8", "utf-8", "");
        }
        if (product.attributes != null) {

            if (product.attributes.size() > 0) {
                attributesList.clear();
                actualAttributesArrayList.clear();
                fixedAttributesArrayList.clear();
                finalAttributesArrayList.clear();

                attributesList.addAll(product.attributes);
                Collections.sort(attributesList);
                actualAttributesArrayList.addAll(attributesList);

                for (AttributeModel value : attributesList) {
                    if (value.product_attribute_id == 10 || value.product_attribute_id == 27)
                        ShowFixedView1 = true;
                    if (value.product_attribute_id == 13)
                        ShowFixedView2 = true;
                    if (value.product_attribute_id == 19)
                        withFiveValue = true;
                    if (value.product_attribute_id == 20)
                        withSixValue = true;
                    if (value.product_attribute_id == 21)
                        withSevenValue = true;
                    if (value.product_attribute_id == 22)
                        withEightValue = true;
                    if (value.product_attribute_id == 23)
                        withNineValue = true;
                }
                if (ShowFixedView1) {
                    view1.setVisibility(View.VISIBLE);
                    sizeOption1.setVisibility(View.VISIBLE);
                    maxNum = maxNum + 1;
                } else {
                    view1.setVisibility(View.GONE);
                    sizeOption1.setVisibility(View.GONE);
                }
                if (ShowFixedView2) {
                    view2.setVisibility(View.VISIBLE);
                    radioSize2.setVisibility(View.VISIBLE);
                    sizeOptions2.setVisibility(View.VISIBLE);
                    view3.setVisibility(View.VISIBLE);
                    maxNum = maxNum + 4;

                    if (withFiveValue) {
                        maxNum = maxNum + 1;
                    }

                    if (withSixValue) {
                        maxNum = maxNum + 1;
                    }


                    if (withSevenValue) {
                        maxNum = maxNum + 1;
                    }


                    if (withEightValue) {
                        maxNum = maxNum + 1;
                    }
                    if (withNineValue) {
                        maxNum = maxNum + 1;
                    }

                    if ((ShowFixedView1 && maxNum == 6) || (!ShowFixedView1 && maxNum == 5)) {
                        value5.setVisibility(View.VISIBLE);
                        measure5.setVisibility(View.VISIBLE);
                        title5.setVisibility(View.VISIBLE);
                    } else if ((ShowFixedView1 && maxNum == 7) || (!ShowFixedView1 && maxNum == 6)) {
                        value5.setVisibility(View.VISIBLE);
                        measure5.setVisibility(View.VISIBLE);
                        title5.setVisibility(View.VISIBLE);

                        value6.setVisibility(View.VISIBLE);
                        measure6.setVisibility(View.VISIBLE);
                        title6.setVisibility(View.VISIBLE);
                    } else if ((ShowFixedView1 && maxNum == 8) || (!ShowFixedView1 && maxNum == 7)) {
                        value5.setVisibility(View.VISIBLE);
                        measure5.setVisibility(View.VISIBLE);
                        title5.setVisibility(View.VISIBLE);

                        value6.setVisibility(View.VISIBLE);
                        measure6.setVisibility(View.VISIBLE);
                        title6.setVisibility(View.VISIBLE);

                        value7.setVisibility(View.VISIBLE);
                        measure7.setVisibility(View.VISIBLE);
                        title7.setVisibility(View.VISIBLE);
                    } else if ((ShowFixedView1 && maxNum == 9) || (!ShowFixedView1 && maxNum == 8)) {
                        value5.setVisibility(View.VISIBLE);
                        measure5.setVisibility(View.VISIBLE);
                        title5.setVisibility(View.VISIBLE);

                        value6.setVisibility(View.VISIBLE);
                        measure6.setVisibility(View.VISIBLE);
                        title6.setVisibility(View.VISIBLE);

                        value7.setVisibility(View.VISIBLE);
                        measure7.setVisibility(View.VISIBLE);
                        title7.setVisibility(View.VISIBLE);

                        value8.setVisibility(View.VISIBLE);
                        measure8.setVisibility(View.VISIBLE);
                        title8.setVisibility(View.VISIBLE);

                    } else if ((ShowFixedView1 && maxNum == 10) || (!ShowFixedView1 && maxNum == 9)) {
                        value5.setVisibility(View.VISIBLE);
                        measure5.setVisibility(View.VISIBLE);
                        title5.setVisibility(View.VISIBLE);

                        value6.setVisibility(View.VISIBLE);
                        measure6.setVisibility(View.VISIBLE);
                        title6.setVisibility(View.VISIBLE);

                        value7.setVisibility(View.VISIBLE);
                        measure7.setVisibility(View.VISIBLE);
                        title7.setVisibility(View.VISIBLE);

                        value8.setVisibility(View.VISIBLE);
                        measure8.setVisibility(View.VISIBLE);
                        title8.setVisibility(View.VISIBLE);

                        value9.setVisibility(View.VISIBLE);
                        measure9.setVisibility(View.VISIBLE);
                        title9.setVisibility(View.VISIBLE);
                    }

                }
            }
        }
        if (maxNum != 0) {
            if (attributesList.size() >= maxNum) {
                fixedAttributesArrayList.clear();
                for (int i = 0; i < maxNum; i++) {
                    fixedAttributesArrayList.add(attributesList.get(i));
                }
                int attributeCount = attributesList.size();

                attributesList.clear();
                if (attributeCount > maxNum) {
                    for (int i = maxNum; i < attributeCount; i++) {
                        attributesList.add(actualAttributesArrayList.get(i));
                    }
                }

                if (ShowFixedView1) {
                    radioSize1.setChecked(true);
                    adapter = new AttributesSelectedSizesAdapter(activity, fixedAttributesArrayList.get(0).attribute_values, active);
                    horizLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                    sizeOptionsList1.setLayoutManager(horizLayoutManager);
                    sizeOptionsList1.setAdapter(adapter);
                    radioSize1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {

                                radioSize2.setChecked(false);
                                value1.setFocusableInTouchMode(false);
                                value1.setFocusable(false);
                                value2.setFocusableInTouchMode(false);
                                value2.setFocusable(false);
                                value3.setFocusableInTouchMode(false);
                                value3.setFocusable(false);
                                value4.setFocusableInTouchMode(false);
                                value4.setFocusable(false);
                                value5.setFocusableInTouchMode(false);
                                value5.setFocusable(false);

                                value6.setFocusableInTouchMode(false);
                                value6.setFocusable(false);
                                value7.setFocusableInTouchMode(false);
                                value7.setFocusable(false);
                                value8.setFocusableInTouchMode(false);
                                value8.setFocusable(false);
                                value9.setFocusableInTouchMode(false);
                                value9.setFocusable(false);

                                radioSize2.setTextColor(Color.parseColor("#9E9E9E"));
                                title1.setTextColor(Color.parseColor("#9E9E9E"));
                                title2.setTextColor(Color.parseColor("#9E9E9E"));
                                title3.setTextColor(Color.parseColor("#9E9E9E"));
                                title4.setTextColor(Color.parseColor("#9E9E9E"));
                                title5.setTextColor(Color.parseColor("#9E9E9E"));
                                title6.setTextColor(Color.parseColor("#9E9E9E"));
                                title7.setTextColor(Color.parseColor("#9E9E9E"));
                                title8.setTextColor(Color.parseColor("#9E9E9E"));
                                title9.setTextColor(Color.parseColor("#9E9E9E"));

                                measure1.setTextColor(Color.parseColor("#9E9E9E"));
                                measure2.setTextColor(Color.parseColor("#9E9E9E"));
                                measure3.setTextColor(Color.parseColor("#9E9E9E"));
                                measure4.setTextColor(Color.parseColor("#9E9E9E"));
                                measure5.setTextColor(Color.parseColor("#9E9E9E"));
                                measure6.setTextColor(Color.parseColor("#9E9E9E"));
                                measure7.setTextColor(Color.parseColor("#9E9E9E"));
                                measure8.setTextColor(Color.parseColor("#9E9E9E"));
                                measure9.setTextColor(Color.parseColor("#9E9E9E"));

                                radioSize1.setTextColor(Color.parseColor("#000000"));
                                active = true;
                                if (sizeSelectedPosition != -1) {
                                    fixedAttributesArrayList.get(0).attribute_values.get(sizeSelectedPosition).is_pre_selected = true;
                                }
                                adapter = new AttributesSelectedSizesAdapter(activity, fixedAttributesArrayList.get(0).attribute_values, active);
                                horizLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                                sizeOptionsList1.setLayoutManager(horizLayoutManager);
                                sizeOptionsList1.setAdapter(adapter);
                            }
                        }
                    });

                }

                if (ShowFixedView2) {
                    if (!ShowFixedView1)
                        counter = 0;
                    else
                        counter = 1;
                    title1.setText(fixedAttributesArrayList.get(counter).localized_names.get(0).localizedName);
                    title2.setText(fixedAttributesArrayList.get(++counter).localized_names.get(0).localizedName);
                    title3.setText(fixedAttributesArrayList.get(++counter).localized_names.get(0).localizedName);
                    title4.setText(fixedAttributesArrayList.get(++counter).localized_names.get(0).localizedName);
                    if (withFiveValue) {
                        title5.setText(fixedAttributesArrayList.get(++counter).localized_names.get(0).localizedName);
                    }
                    if (withSixValue) {
                        title6.setText(fixedAttributesArrayList.get(++counter).localized_names.get(0).localizedName);
                    }
                    if (withSevenValue) {
                        title7.setText(fixedAttributesArrayList.get(++counter).localized_names.get(0).localizedName);
                    }
                    if (withEightValue) {
                        title8.setText(fixedAttributesArrayList.get(++counter).localized_names.get(0).localizedName);
                    }
                    if (withNineValue) {
                        title9.setText(fixedAttributesArrayList.get(++counter).localized_names.get(0).localizedName);
                    }

                    if (!ShowFixedView1)
                        counter = 0;
                    else
                        counter = 1;
                    ArrayList<AttributeValueModel> textBoxValues1 = new ArrayList<>();
                    AttributeValueModel textBox1 = new AttributeValueModel();
                    textBox1.is_pre_selected = false;
                    textBoxValues1.add(textBox1);
                    fixedAttributesArrayList.get(counter).attribute_values = textBoxValues1;

                    ArrayList<AttributeValueModel> textBoxValues2 = new ArrayList<>();
                    AttributeValueModel textBox2 = new AttributeValueModel();
                    textBox2.is_pre_selected = false;
                    textBoxValues2.add(textBox2);
                    fixedAttributesArrayList.get(++counter).attribute_values = textBoxValues2;

                    ArrayList<AttributeValueModel> textBoxValues3 = new ArrayList<>();
                    AttributeValueModel textBox3 = new AttributeValueModel();
                    textBox3.is_pre_selected = false;
                    textBoxValues3.add(textBox3);
                    fixedAttributesArrayList.get(++counter).attribute_values = textBoxValues3;

                    ArrayList<AttributeValueModel> textBoxValues4 = new ArrayList<>();
                    AttributeValueModel textBox4 = new AttributeValueModel();
                    textBox4.is_pre_selected = false;
                    textBoxValues4.add(textBox4);
                    fixedAttributesArrayList.get(++counter).attribute_values = textBoxValues4;


                    if (withFiveValue) {
                        ArrayList<AttributeValueModel> textBoxValues5 = new ArrayList<>();
                        AttributeValueModel textBox5 = new AttributeValueModel();
                        textBox5.is_pre_selected = false;
                        textBoxValues5.add(textBox5);
                        fixedAttributesArrayList.get(++counter).attribute_values = textBoxValues5;
                    }

                    if (withSixValue) {
                        ArrayList<AttributeValueModel> textBoxValues6 = new ArrayList<>();
                        AttributeValueModel textBox6 = new AttributeValueModel();
                        textBox6.is_pre_selected = false;
                        textBoxValues6.add(textBox6);
                        fixedAttributesArrayList.get(++counter).attribute_values = textBoxValues6;
                    }

                    if (withSevenValue) {
                        ArrayList<AttributeValueModel> textBoxValues7 = new ArrayList<>();
                        AttributeValueModel textBox7 = new AttributeValueModel();
                        textBox7.is_pre_selected = false;
                        textBoxValues7.add(textBox7);
                        fixedAttributesArrayList.get(++counter).attribute_values = textBoxValues7;
                    }

                    if (withEightValue) {
                        ArrayList<AttributeValueModel> textBoxValues8 = new ArrayList<>();
                        AttributeValueModel textBox8 = new AttributeValueModel();
                        textBox8.is_pre_selected = false;
                        textBoxValues8.add(textBox8);
                        fixedAttributesArrayList.get(++counter).attribute_values = textBoxValues8;
                    }

                    if (withNineValue) {
                        ArrayList<AttributeValueModel> textBoxValues9 = new ArrayList<>();
                        AttributeValueModel textBox9 = new AttributeValueModel();
                        textBox9.is_pre_selected = false;
                        textBoxValues9.add(textBox9);
                        fixedAttributesArrayList.get(++counter).attribute_values = textBoxValues9;
                    }

                    if (!ShowFixedView1) {
                        counter = 0;
                        value1.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(0).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(0).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(0).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        value2.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(1).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(1).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(1).attribute_values.get(0).name = value;

                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        value3.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(2).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(2).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(2).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        value4.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(3).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(3).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(3).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        value5.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(4).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(4).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(4).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        value6.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(5).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(5).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(5).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        value7.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(6).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(6).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(6).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        value8.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(8).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(8).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(8).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        value9.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(8).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(8).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(8).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                    } else {
                        counter = 1;
                        value1.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(1).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(1).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(1).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        value2.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(2).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(2).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(2).attribute_values.get(0).name = value;

                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        value3.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(3).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(3).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(3).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        value4.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(4).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(4).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(4).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        value5.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(5).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(5).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(5).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        value6.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(6).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(6).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(6).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        value7.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(7).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(7).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(7).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        value8.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(8).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(8).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(8).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        value9.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String value = s.toString();
                                if (value == null || value.matches("")) {
                                    fixedAttributesArrayList.get(9).attribute_values.get(0).is_pre_selected = false;
                                } else {
                                    fixedAttributesArrayList.get(9).attribute_values.get(0).is_pre_selected = true;
                                    fixedAttributesArrayList.get(9).attribute_values.get(0).name = value;


                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                    }

                    if (!ShowFixedView1)
                        radioSize2.setChecked(true);
                    else {
                        disableSecondSizeOption();
                        radioSize2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    enableSecondSizeOption();
                                    radioSize1.setChecked(false);
                                    for (int i = 0; i < fixedAttributesArrayList.get(0).attribute_values.size(); i++) {
                                        if (fixedAttributesArrayList.get(0).attribute_values.get(i).is_pre_selected) {
                                            sizeSelectedPosition = i;
                                            fixedAttributesArrayList.get(0).attribute_values.get(i).is_pre_selected = false;
                                        }
                                    }
                                    radioSize1.setTextColor(Color.parseColor("#9E9E9E"));
                                    active = false;
                                    adapter = new AttributesSelectedSizesAdapter(activity, fixedAttributesArrayList.get(0).attribute_values, active);
                                    horizLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                                    sizeOptionsList1.setLayoutManager(horizLayoutManager);
                                    sizeOptionsList1.setAdapter(adapter);

                                }
                            }
                        });
                    }
                }
            }
        }

        attributesAdapter = new AttributesAdapter(activity, attributesList);
        layoutManagerAttributes = new LinearLayoutManager(activity);
        attributes.setLayoutManager(layoutManagerAttributes);
        attributes.setAdapter(attributesAdapter);

        if (product.IsAddedToWishList) {
            addFavorite.setImageResource(R.mipmap.icon_add_fav_sel);
        }

    }

    public void disableSecondSizeOption() {
        radioSize2.setChecked(false);
        radioSize2.setTextColor(Color.parseColor("#9E9E9E"));

        value1.setFocusableInTouchMode(false);
        value1.setFocusable(false);

        value2.setFocusableInTouchMode(false);
        value2.setFocusable(false);

        value3.setFocusableInTouchMode(false);
        value3.setFocusable(false);

        value4.setFocusableInTouchMode(false);
        value4.setFocusable(false);

        value5.setFocusableInTouchMode(false);
        value5.setFocusable(false);


        value6.setFocusableInTouchMode(false);
        value6.setFocusable(false);

        value7.setFocusableInTouchMode(false);
        value7.setFocusable(false);

        value8.setFocusableInTouchMode(false);
        value8.setFocusable(false);

        value9.setFocusableInTouchMode(false);
        value9.setFocusable(false);

        title1.setTextColor(Color.parseColor("#9E9E9E"));
        title2.setTextColor(Color.parseColor("#9E9E9E"));
        title3.setTextColor(Color.parseColor("#9E9E9E"));
        title4.setTextColor(Color.parseColor("#9E9E9E"));
        title5.setTextColor(Color.parseColor("#9E9E9E"));
        title6.setTextColor(Color.parseColor("#9E9E9E"));
        title7.setTextColor(Color.parseColor("#9E9E9E"));
        title8.setTextColor(Color.parseColor("#9E9E9E"));
        title9.setTextColor(Color.parseColor("#9E9E9E"));


        measure1.setTextColor(Color.parseColor("#9E9E9E"));
        measure2.setTextColor(Color.parseColor("#9E9E9E"));
        measure3.setTextColor(Color.parseColor("#9E9E9E"));
        measure4.setTextColor(Color.parseColor("#9E9E9E"));
        measure5.setTextColor(Color.parseColor("#9E9E9E"));
        measure6.setTextColor(Color.parseColor("#9E9E9E"));
        measure7.setTextColor(Color.parseColor("#9E9E9E"));
        measure8.setTextColor(Color.parseColor("#9E9E9E"));
        measure9.setTextColor(Color.parseColor("#9E9E9E"));

    }

    public void enableSecondSizeOption() {
        radioSize2.setChecked(true);
        radioSize2.setTextColor(Color.parseColor("#000000"));

        value1.setFocusableInTouchMode(true);
        value1.setFocusable(true);

        value2.setFocusableInTouchMode(true);
        value2.setFocusable(true);

        value3.setFocusableInTouchMode(true);
        value3.setFocusable(true);

        value4.setFocusableInTouchMode(true);
        value4.setFocusable(true);

        value5.setFocusableInTouchMode(true);
        value5.setFocusable(true);

        value6.setFocusableInTouchMode(true);
        value6.setFocusable(true);

        value7.setFocusableInTouchMode(true);
        value7.setFocusable(true);

        value8.setFocusableInTouchMode(true);
        value8.setFocusable(true);

        value9.setFocusableInTouchMode(true);
        value9.setFocusable(true);

        title1.setTextColor(Color.parseColor("#000000"));
        title2.setTextColor(Color.parseColor("#000000"));
        title3.setTextColor(Color.parseColor("#000000"));
        title4.setTextColor(Color.parseColor("#000000"));
        title5.setTextColor(Color.parseColor("#000000"));
        title6.setTextColor(Color.parseColor("#000000"));
        title7.setTextColor(Color.parseColor("#000000"));
        title8.setTextColor(Color.parseColor("#000000"));
        title9.setTextColor(Color.parseColor("#000000"));


        measure1.setTextColor(Color.parseColor("#000000"));
        measure2.setTextColor(Color.parseColor("#000000"));
        measure3.setTextColor(Color.parseColor("#000000"));
        measure4.setTextColor(Color.parseColor("#000000"));
        measure5.setTextColor(Color.parseColor("#000000"));
        measure6.setTextColor(Color.parseColor("#000000"));
        measure7.setTextColor(Color.parseColor("#000000"));
        measure8.setTextColor(Color.parseColor("#000000"));
        measure9.setTextColor(Color.parseColor("#000000"));

    }

    public void setupSlider() {
        for (ImageModel image : product.images) {
            sliderList.add(image.src);
            createSliderViews(image.src);
        }
    }

    public void createSliderViews(String sliderViewUrl) {
        DefaultSliderView sliderView = new DefaultSliderView(activity);
        sliderView.image(sliderViewUrl).setRequestOption(new RequestOptions().centerCrop());
        slider.addSlider(sliderView);
        //add slider duration for every View(Advertisement)
        slider.setDuration(5000);

        sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView baseSlider) {
                Navigator.loadFragment2(activity, GestureImageFragment.newInstance(activity, sliderList, slider.getCurrentPosition()), R.id.main_frameLayout_Container, true);
            }
        });

    }

    private void prepareShoppingCartData(boolean isAddToCart) {

        CartItem_ cartItem_ = new CartItem_();
        cartItem_.customerId = Integer.parseInt(sessionManager.getUserId());
        cartItem_.productId = product.id;
        cartItem_.quantity = quantity;
        cartItem_.shoppingCartType = isAddToCart ? "1" : "2";

        List<OrderAttributeModel> attributeList = new ArrayList<>();
        finalAttributesArrayList.clear();
        if (actualAttributesArrayList.size() > maxNum) {
            finalAttributesArrayList.addAll(fixedAttributesArrayList);
            finalAttributesArrayList.addAll(attributesList);
        } else {
            finalAttributesArrayList.addAll(attributesList);
        }
        int i = 0;
        for (AttributeModel a : finalAttributesArrayList) {
            if (a.attribute_values != null) {
                for (AttributeValueModel av : a.attribute_values) {
                    if (av.is_pre_selected) {
                        i++;
                        OrderAttributeModel p = new OrderAttributeModel();
                        p.id = i;
                        p.code = a.id;
                        if (a.attribute_control_type_name.equals("MultilineTextbox") ||
                                a.attribute_control_type_name.equals("TextBox")) {
                            p.value = av.name;
                        } else {
                            p.value = String.valueOf(av.id);
                        }
                        attributeList.add(p);
                    }
                }
            }
        }

        cartItem_.product_attributes = attributeList;


        cartItem.shoppingCartItem = cartItem_;
        addCartOrFavoriteApi("addToCart", cartItem, null);

    }

    public void relatedProducts() {
        AtelierApiConfig.getCallingAPIInterface().relatedProducts(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                sessionManager.getUserId(),
                String.valueOf(product.id),
                new Callback<GetProducts>() {
                    @Override
                    public void success(GetProducts getProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getProducts != null) {
                            if (getProducts.products != null) {
                                if (getProducts.products.size() > 0) {
                                    relatedProducts.setVisibility(View.VISIBLE);
                                    relatedTitle.setVisibility(View.VISIBLE);
                                    relatedProductsList.addAll(getProducts.products);
                                    relatedProductsAdapter.notifyDataSetChanged();
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

    public void addCartOrFavoriteApi(final String type, CartItem cartItem, final ImageView addFavorite) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().createShoppingCart(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                Constants.CONTENT_TYPE_VALUE,
                cartItem,
                new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts getCartProducts, Response response) {
                        loading.setVisibility(View.GONE);
                        if (getCartProducts != null) {
                            if (getCartProducts.CartProducts.size() > 0) {
                                if (type.equals("addToFavorite")) {
                                    Snackbar.make(loading, getString(R.string.product_favorites_added), Snackbar.LENGTH_SHORT).show();
                                    addFavorite.setImageResource(R.mipmap.icon_add_fav_sel);
                                } else {
                                    new AlertDialog.Builder(activity)
                                            .setTitle(activity.getString(R.string.app_name))
                                            .setMessage(activity.getString(R.string.product_added))
                                            .setPositiveButton(R.string.continue_shopping, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton(activity.getString(R.string.checkout), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Navigator.loadFragment(activity, CartFragment.newInstance(activity), R.id.main_frameLayout_Container, true);
                                                }
                                            })
                                            .setIcon(R.mipmap.logo)
                                            .show();
                                    MainActivity.emptyCart = false;
                                    MainActivity.notification.setVisibility(View.VISIBLE);
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

    private void deleteFavoriteApi(final int position, final ImageView addFavorite) {
        int productId = 0;
        if (position == -1)
            productId = product.id;
        else
            productId = relatedProductsList.get(position).id;
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().deleteFavoriteItem(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                "2",
                String.valueOf(productId),
                sessionManager.getUserId(),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

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
                                Snackbar.make(loading, getString(R.string.unfavorite), Snackbar.LENGTH_SHORT).show();
                                addFavorite.setImageResource(R.mipmap.icon_add_fav_unsel);
                            }
                        }

                        loading.setVisibility(View.GONE);
                    }


                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error, loading);
                    }
                }
        );

    }


}
