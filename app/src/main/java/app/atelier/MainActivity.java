package app.atelier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import app.atelier.classes.Constants;
import app.atelier.classes.LocaleHelper;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.fragments.AboutUsFragment;
import app.atelier.fragments.CartFragment;
import app.atelier.fragments.CategoriesFragment;
import app.atelier.fragments.ContactUsFragment;
import app.atelier.fragments.FavoritesFragment;
import app.atelier.fragments.HomeFragment;
import app.atelier.fragments.LoginFragment;
import app.atelier.fragments.MainCategoriesFragment;
import app.atelier.fragments.MyAccountFragment;
import app.atelier.fragments.ProductsFragment;
import app.atelier.fragments.SearchResultFragment;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.customers.CustomerModel;
import app.atelier.webservices.responses.customers.GetCustomers;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_navigationView)
    NavigationView navigationView;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_frameLayout_Container)
    FrameLayout Container;

    @BindView(R.id.main_imgView_menu)
    ImageView menu;
    @BindView(R.id.main_imgView_back)
    ImageView back;
    @BindView(R.id.main_imgView_search)
    SearchView search;


    /*butterKnife don't work with static or private
     if there is needing of static or private >> use the normal way
    * */
    public static AppBarLayout appbar;
    public static LinearLayout bottomAppbar;
    public static TextView title;
    public static ImageView home;
    public static ImageView categories;
    public static ImageView favorite;
    public static ImageView cart;
    public static TextView accountOrLogin;
    public ImageView closeButton;

    //sharedPreference and Editor used to check and save the value of language

    //this variable is used to check the current selected language in the app
    public static boolean isEnglish;
    public static String language;
    public static boolean emptyCart = true;
    public static String mainCategoryId;
    public static String brandId;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupStatusBar();
        //this line must be added to use butterKnife in the activity
        ButterKnife.bind(this);
        setupAppBar();
        language = SessionManager.getUserLanguage(this);
        LocaleHelper.setLocale(this, language);
        if (language.equals("en")) {
            isEnglish = true;
        } else if (language.equals("ar")) {
            isEnglish = false;
        }

        appbar = (AppBarLayout) findViewById(R.id.main_appbar);
        bottomAppbar = (LinearLayout) findViewById(R.id.main_linearLayout_bottomAppbar);

        title = (TextView) findViewById(R.id.main_txtView_title);
        home = (ImageView) findViewById(R.id.main_imgView_home);
        categories = (ImageView) findViewById(R.id.main_imgView_categories);
        favorite = (ImageView) findViewById(R.id.main_imgView_favorite);
        cart = (ImageView) findViewById(R.id.main_imgView_cart);
        accountOrLogin = (TextView) findViewById(R.id.main_txtView_accountOrLogin);

        if (SessionManager.getUser(this).get("id") == null || SessionManager.getUser(this).get("id").matches("")) {
            createGuestCustomerApi();
        }
        if (SessionManager.checkSkip(this)) {
            Navigator.loadFragment(this, MainCategoriesFragment.newInstance(this), R.id.main_frameLayout_Container, false);
        } else {
            Navigator.loadFragment(this, LoginFragment.newInstance(this), R.id.main_frameLayout_Container, false);
        }

        if (SessionManager.getUser(this).get("userName") != null
                && !SessionManager.getUser(this).get("userName").matches("")) {
            accountOrLogin.setText(getResources().getString(R.string.account));
        }

        shoppingCartItemsCount(this);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Navigator.loadFragment((FragmentActivity) MainActivity.this, SearchResultFragment.newInstance((FragmentActivity) MainActivity.this, query), R.id.main_frameLayout_Container, true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }


    @OnClick(R.id.main_imgView_menu)
    public void menuClick() {
        //open and close the sideMenu when the navigationIcon clicked
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers();
        } else {
            drawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    //the back button action of all the app
    @OnClick(R.id.main_imgView_back)
    public void back() {
        onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawers();
            } else {
                onBackPressed();
            }
        }
        return true;
    }


    @OnClick(R.id.main_imgView_home)
    public void homeClick() {
        Navigator.loadFragment(this, HomeFragment.newInstance(this, MainActivity.mainCategoryId), R.id.main_frameLayout_Container, true);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_imgView_categories)
    public void categoriesClick() {
        Navigator.loadFragment(this, CategoriesFragment.newInstance(this, getString(R.string.categories), mainCategoryId), R.id.main_frameLayout_Container, true);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_imgView_favorite)
    public void favoriteClick() {
        Navigator.loadFragment(this, FavoritesFragment.newInstance(this), R.id.main_frameLayout_Container, true);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_imgView_cart)
    public void cartClick() {
        Navigator.loadFragment(this, CartFragment.newInstance(this), R.id.main_frameLayout_Container, true);
        drawerLayout.closeDrawers();
    }

    //SideMenu items Clicks

    //myAccount click
    @OnClick(R.id.main_linearLayout_accountOrLogin)
    public void accountClick() {
        if (SessionManager.getUser(this).get("userName") != null
                && !SessionManager.getUser(this).get("userName").matches("")) {
            Navigator.loadFragment(this, MyAccountFragment.newInstance(this), R.id.main_frameLayout_Container, true);
        } else {
            Navigator.loadFragment(this, LoginFragment.newInstance(this), R.id.main_frameLayout_Container, true);
        }
        drawerLayout.closeDrawers();
    }

    //contactUs Click
    @OnClick(R.id.main_linearLayout_contactUs)
    public void contactUsClick() {
        Navigator.loadFragment(this, ContactUsFragment.newInstance(this), R.id.main_frameLayout_Container, true);
        drawerLayout.closeDrawers();
    }

    //aboutUs Click
    @OnClick(R.id.main_linearLayout_aboutUs)
    public void aboutUsClick() {
        Navigator.loadFragment(this, AboutUsFragment.newInstance(this), R.id.main_frameLayout_Container, true);
        drawerLayout.closeDrawers();
    }

    //language Click
    @OnClick(R.id.main_linearLayout_language)
    public void languageClick() {
        changeLanguage();
    }


    public void setupStatusBar() {
        //change the color of status Bar and make it light >> but it work from lollipop version and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void setupAppBar() {
        //set the actionBar to the activity
        setSupportActionBar(toolbar);
        //the content of toolbar Leaves a space on left or right hand so this function remove the spaces
        toolbar.setContentInsetsAbsolute(0, 0);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open
                , R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //adding animation when the content of activity change (fragment navigation)
                float moveFactor = navigationView.getWidth() * slideOffset;
                Container.setTranslationX(-moveFactor);
            }
        };
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(null);
    }

    public void changeLanguage() {
        /*for changing the language of App
        1- check the value of language in sharedPreference and Reflects the language
         2- set the new value of language in local and change the value of sharedPreference to new value
         3- restart the mainActivity with noAnimation
        * */

        if (language.equals("ar")) {
            language = "en";
            isEnglish = true;
        } else if (language.equals("en")) {
            language = "ar";
            isEnglish = false;
        }

        LocaleHelper.setLocale(this, language);
        SessionManager.setUserLanguage(this, language);

        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, MainActivity.class));
    }

    public void createGuestCustomerApi() {
        AtelierApiConfig.getCallingAPIInterface().createGuestCustomer(
                Constants.AUTHORIZATION_VALUE, language, Constants.CONTENT_TYPE_VALUE,
                new Callback<GetCustomers>() {
                    @Override
                    public void success(GetCustomers getCustomers, Response response) {
                        if (getCustomers != null) {
                            CustomerModel customer = getCustomers.customers.get(0);
                            SessionManager.setUser(MainActivity.this,
                                    customer.id,
                                    customer.userName,
                                    customer.firstName,
                                    customer.lastName,
                                    customer.phone,
                                    customer.email,
                                    customer.password);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Snackbar.make(Container, getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                    }
                }
        );
    }


    public static void shoppingCartItemsCount(Context context) {

        AtelierApiConfig.getCallingAPIInterface().shoppingCartItemsCount(
                Constants.AUTHORIZATION_VALUE,
                MainActivity.language,
                SessionManager.getUser(context).get("id"),
                new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts outResponse, retrofit.client.Response response) {

                        if (outResponse != null) {
                            if (outResponse.CartProducts.size() > 0) {
                                cart.setImageResource(R.mipmap.icon_cart_with_notifi);
                                emptyCart = false;
                            } else {
                                emptyCart = true;
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }

                });

    }


    public static void setupBottomAppbar(String selection) {
        if (selection.equals("home")) {
            home.setImageResource(R.mipmap.icon_home_sel);
            categories.setImageResource(R.mipmap.icon_cate_unsel);
            favorite.setImageResource(R.mipmap.icon_fav_unsel);
            cart.setImageResource(R.mipmap.icon_cart_unsel);
        } else if (selection.equals("categories")) {
            home.setImageResource(R.mipmap.icon_home_unsel);
            categories.setImageResource(R.mipmap.icon_cate_sel);
            favorite.setImageResource(R.mipmap.icon_fav_unsel);
            cart.setImageResource(R.mipmap.icon_cart_unsel);
        } else if (selection.equals("favorite")) {
            home.setImageResource(R.mipmap.icon_home_unsel);
            categories.setImageResource(R.mipmap.icon_cate_unsel);
            favorite.setImageResource(R.mipmap.icon_fav_sel);
            cart.setImageResource(R.mipmap.icon_cart_unsel);
        } else if (selection.equals("cart")) {
            home.setImageResource(R.mipmap.icon_home_unsel);
            categories.setImageResource(R.mipmap.icon_cate_unsel);
            favorite.setImageResource(R.mipmap.icon_fav_unsel);
            cart.setImageResource(R.mipmap.icon_cart_sel);
        } else {
            home.setImageResource(R.mipmap.icon_home_unsel);
            categories.setImageResource(R.mipmap.icon_cate_unsel);
            favorite.setImageResource(R.mipmap.icon_fav_unsel);
            cart.setImageResource(R.mipmap.icon_cart_unsel);
        }

        if (!selection.equals("cart") && !emptyCart) {
            cart.setImageResource(R.mipmap.icon_cart_with_notifi);
        }
    }
}
