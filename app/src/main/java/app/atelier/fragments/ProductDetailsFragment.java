package app.atelier.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import app.atelier.adapters.AttributesSizesAdapter;
import app.atelier.adapters.ProductsAdapter;
import app.atelier.classes.Constants;
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

    @BindView(R.id.productDetails_recycler_attributes)
    RecyclerView attributes;
    @BindView(R.id.productDetails_recycler_relatedProducts)
    RecyclerView relatedProducts;
    @BindView(R.id.productDetails_tv_relatedTitle)
    TextView relatedTitle;
    @BindView(R.id.productDetails_slider)
    SliderLayout slider;
    @BindView(R.id.productDetails_txt_name)
    TextView productName;
    @BindView(R.id.productDetails_txt_description)
    TextView productDescription;
    @BindView(R.id.productDetails_txt_price)
    TextView productPrice;
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
    @BindView(R.id.productDetails_editTxt_value1)
    EditText value1;
    @BindView(R.id.productDetails_editTxt_value2)
    EditText value2;
    @BindView(R.id.productDetails_editTxt_value3)
    EditText value3;
    @BindView(R.id.productDetails_editTxt_value4)
    EditText value4;
    @BindView(R.id.productDetails_tv_measure1)
    TextView measure1;
    @BindView(R.id.productDetails_tv_measure2)
    TextView measure2;
    @BindView(R.id.productDetails_tv_measure3)
    TextView measure3;
    @BindView(R.id.productDetails_tv_measure4)
    TextView measure4;


    @BindView(R.id.view3)
    View view3;


    public static ArrayList<AttributeModel> attributesList = new ArrayList<>();
    public static ArrayList<AttributeModel> actualAttributesArrayList = new ArrayList<>();
    public static ArrayList<AttributeModel> fixedAttributesArrayList = new ArrayList<>();
    public static ArrayList<AttributeModel> finalAttributesArrayList = new ArrayList<>();
    int maxNum;

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
    private AttributesSizesAdapter adapter;
    private boolean ShowFixedView1 = false;
    private boolean ShowFixedView2 = false;

    public static ProductDetailsFragment newInstance(FragmentActivity activity, ProductModel product) {
        fragment = new ProductDetailsFragment();
        ProductDetailsFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        Bundle b = new Bundle();
        b.putSerializable("product", product);
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

        relatedTitle.setVisibility(View.GONE);
        relatedProducts.setVisibility(View.GONE);

        view1.setVisibility(View.GONE);
        sizeOption1.setVisibility(View.GONE);

        view2.setVisibility(View.GONE);
        radioSize2.setVisibility(View.GONE);
        sizeOptions2.setVisibility(View.GONE);

        view3.setVisibility(View.GONE);

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

        product = (ProductModel) getArguments().getSerializable("product");
        setData();
        relatedProducts();

        relatedProductsAdapter = new ProductsAdapter(activity, "relatedProduct", relatedProductsList, null, new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Navigator.loadFragment(activity, ProductDetailsFragment.newInstance(activity, relatedProductsList.get(position)),
                        R.id.main_frameLayout_Container, true);
            }

            @Override
            public void onAddCartClick(int position) {
                CartItem_ cartItem_ = new CartItem_();
                cartItem_.productId = relatedProductsList.get(position).id;
                cartItem_.customerId = Integer.valueOf(sessionManager.getUserId());
                cartItem_.quantity = 1;
                cartItem_.shoppingCartType = "1";
                CartItem cartItem = new CartItem();
                cartItem.shoppingCartItem = cartItem_;
                addCartOrFavoriteApi("addToCart", cartItem, null);
            }

            @Override
            public void onAddFavoriteClick(int position, ImageView addFavorite) {
                if (addFavorite.getDrawable().getConstantState() ==
                        getResources().getDrawable(R.mipmap.icon_fav_sel).getConstantState()) {
                    deleteFavoriteApi(position, addFavorite);
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
        });
        horizLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        relatedProducts.setLayoutManager(horizLayoutManager);
        relatedProducts.setAdapter(relatedProductsAdapter);
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

    private void setData() {
        setupSlider();
        productName.setText(product.getLocalizedName());
        productPrice.setText(product.formattedPrice);
        productDescription.setText(product.getFullDescription());

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
                    if (value.product_attribute_id == 10)
                        ShowFixedView1 = true;
                    if (value.product_attribute_id == 13)
                        ShowFixedView2 = true;
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
                } else {
                    view2.setVisibility(View.GONE);
                    radioSize2.setVisibility(View.GONE);
                    sizeOptions2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
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
                            adapter = new AttributesSizesAdapter(activity, fixedAttributesArrayList.get(0).attribute_values, active);
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
                                        radioSize2.setTextColor(Color.parseColor("#9E9E9E"));
                                        title1.setTextColor(Color.parseColor("#9E9E9E"));
                                        title2.setTextColor(Color.parseColor("#9E9E9E"));
                                        title3.setTextColor(Color.parseColor("#9E9E9E"));
                                        title4.setTextColor(Color.parseColor("#9E9E9E"));
                                        measure1.setTextColor(Color.parseColor("#9E9E9E"));
                                        measure2.setTextColor(Color.parseColor("#9E9E9E"));
                                        measure3.setTextColor(Color.parseColor("#9E9E9E"));
                                        measure4.setTextColor(Color.parseColor("#9E9E9E"));

                                        radioSize1.setTextColor(Color.parseColor("#000000"));
                                        active = true;
                                        adapter = new AttributesSizesAdapter(activity, fixedAttributesArrayList.get(0).attribute_values, active);
                                        horizLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                                        sizeOptionsList1.setLayoutManager(horizLayoutManager);
                                        sizeOptionsList1.setAdapter(adapter);
                                    }
                                }
                            });

                        }

                        if (ShowFixedView2) {
                            for (AttributeModel value : fixedAttributesArrayList) {

                            }
                            int count;
                            if (!ShowFixedView1)
                                count = 0;
                            else
                                count = 1;
                            title1.setText(fixedAttributesArrayList.get(count).localized_names.get(0).localizedName);
                            title2.setText(fixedAttributesArrayList.get(++count).localized_names.get(0).localizedName);
                            title3.setText(fixedAttributesArrayList.get(++count).localized_names.get(0).localizedName);
                            title4.setText(fixedAttributesArrayList.get(++count).localized_names.get(0).localizedName);


                            if (!ShowFixedView1)
                                count = 0;
                            else
                                count = 1;
                            ArrayList<AttributeValueModel> textBoxValues1 = new ArrayList<>();
                            AttributeValueModel textBox1 = new AttributeValueModel();
                            textBox1.is_pre_selected = false;
                            textBoxValues1.add(textBox1);
                            ProductDetailsFragment.fixedAttributesArrayList.get(count).attribute_values = textBoxValues1;

                            ArrayList<AttributeValueModel> textBoxValues2 = new ArrayList<>();
                            AttributeValueModel textBox2 = new AttributeValueModel();
                            textBox2.is_pre_selected = false;
                            textBoxValues2.add(textBox2);
                            ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values = textBoxValues2;

                            ArrayList<AttributeValueModel> textBoxValues3 = new ArrayList<>();
                            AttributeValueModel textBox3 = new AttributeValueModel();
                            textBox3.is_pre_selected = false;
                            textBoxValues3.add(textBox3);
                            ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values = textBoxValues3;

                            ArrayList<AttributeValueModel> textBoxValues4 = new ArrayList<>();
                            AttributeValueModel textBox4 = new AttributeValueModel();
                            textBox4.is_pre_selected = false;
                            textBoxValues4.add(textBox4);
                            ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values = textBoxValues4;


                            if (!ShowFixedView1)
                                count = 0;
                            else
                                count = 1;
                            value1.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    String value = s.toString();
                                    if (value == null || value.matches("")) {
                                        ProductDetailsFragment.fixedAttributesArrayList.get(count).attribute_values.get(0).is_pre_selected = false;
                                    } else {
                                        ProductDetailsFragment.fixedAttributesArrayList.get(count).attribute_values.get(0).is_pre_selected = true;
                                        ProductDetailsFragment.fixedAttributesArrayList.get(count).attribute_values.get(0).name = value;


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
                                        ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values.get(0).is_pre_selected = false;
                                    } else {
                                        ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values.get(0).is_pre_selected = true;
                                        ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values.get(0).name = value;

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
                                        ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values.get(0).is_pre_selected = false;
                                    } else {
                                        ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values.get(0).is_pre_selected = true;
                                        ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values.get(0).name = value;


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
                                        ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values.get(0).is_pre_selected = false;
                                    } else {
                                        ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values.get(0).is_pre_selected = true;
                                        ProductDetailsFragment.fixedAttributesArrayList.get(++count).attribute_values.get(0).name = value;


                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });




                            if (!ShowFixedView1)
                                radioSize2.setChecked(true);
                            else {
                                radioSize2.setChecked(false);
                                value1.setFocusableInTouchMode(false);
                                value1.setFocusable(false);
                                value2.setFocusableInTouchMode(false);
                                value2.setFocusable(false);
                                value3.setFocusableInTouchMode(false);
                                value3.setFocusable(false);
                                value4.setFocusableInTouchMode(false);
                                value4.setFocusable(false);
                                radioSize2.setTextColor(Color.parseColor("#9E9E9E"));
                                title1.setTextColor(Color.parseColor("#9E9E9E"));
                                title2.setTextColor(Color.parseColor("#9E9E9E"));
                                title3.setTextColor(Color.parseColor("#9E9E9E"));
                                title4.setTextColor(Color.parseColor("#9E9E9E"));
                                measure1.setTextColor(Color.parseColor("#9E9E9E"));
                                measure2.setTextColor(Color.parseColor("#9E9E9E"));
                                measure3.setTextColor(Color.parseColor("#9E9E9E"));
                                measure4.setTextColor(Color.parseColor("#9E9E9E"));
                                radioSize2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            radioSize1.setChecked(false);
                                            radioSize1.setTextColor(Color.parseColor("#9E9E9E"));
                                            active = false;
                                            adapter = new AttributesSizesAdapter(activity, fixedAttributesArrayList.get(0).attribute_values, active);
                                            horizLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                                            sizeOptionsList1.setLayoutManager(horizLayoutManager);
                                            sizeOptionsList1.setAdapter(adapter);

                                            radioSize2.setTextColor(Color.parseColor("#000000"));
                                            title1.setTextColor(Color.parseColor("#000000"));
                                            title2.setTextColor(Color.parseColor("#000000"));
                                            title3.setTextColor(Color.parseColor("#000000"));
                                            title4.setTextColor(Color.parseColor("#000000"));
                                            measure1.setTextColor(Color.parseColor("#000000"));
                                            measure2.setTextColor(Color.parseColor("#000000"));
                                            measure3.setTextColor(Color.parseColor("#000000"));
                                            measure4.setTextColor(Color.parseColor("#000000"));

                                            value1.setFocusableInTouchMode(true);
                                            value1.setFocusable(true);
                                            value2.setFocusableInTouchMode(true);
                                            value2.setFocusable(true);
                                            value3.setFocusableInTouchMode(true);
                                            value3.setFocusable(true);
                                            value4.setFocusableInTouchMode(true);
                                            value4.setFocusable(true);
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
            }
        }
        if (product.IsAddedToWishList) {
            addFavorite.setImageResource(R.mipmap.icon_add_fav_sel);
        }
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

    public void addCartOrFavoriteApi(final String type, CartItem cartItem,
                                     final ImageView addFavorite) {
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
                                            .setTitle(activity.getString(R.string.message))
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
