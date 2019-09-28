package app.atelier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;

import app.atelier.classes.Constants;
import app.atelier.classes.FixControl;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.LocaleHelper;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.fragments.TopicsPageFragment;
import app.atelier.fragments.CartFragment;
import app.atelier.fragments.CategoriesFragment;
import app.atelier.fragments.ContactUsFragment;
import app.atelier.fragments.FavoritesFragment;
import app.atelier.fragments.HomeFragment;
import app.atelier.fragments.LoginFragment;
import app.atelier.fragments.MainCategoriesFragment;
import app.atelier.fragments.MyAccountFragment;
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
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_navigationView)
    NavigationView navigationView;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_frameLayout_Container)
    FrameLayout Container;
    @BindView(R.id.main_imgView_back)
    ImageView back;


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
    public static ImageView notification;
    public static TextView accountOrLogin;
    public static SearchView search;
    public static ImageView menu;

    public static SessionManager sessionManager;
    //sharedPreference and Editor used to check and save the value of language

    //this variable is used to check the current selected language in the app
    public static boolean isEnglish;
    public static boolean emptyCart = true;
    public static String mainCategoryId;
    public static String brandId;
    private String language;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(LocaleHelper.onAttach(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupStatusBar();
        //this line must be added to use butterKnife in the activity
        ButterKnife.bind(this);
        setupAppBar();
        GlobalFunctions.setUpFont(this);

        sessionManager = new SessionManager(this);
        language = sessionManager.getUserLanguage();
        LocaleHelper.setLocale(this, language);
        sessionManager.setUserLanguage(language);

        appbar = (AppBarLayout) findViewById(R.id.main_appbar);
        bottomAppbar = (LinearLayout) findViewById(R.id.main_linearLayout_bottomAppbar);
        title = (TextView) findViewById(R.id.main_txtView_title);
        home = (ImageView) findViewById(R.id.main_imgView_home);
        categories = (ImageView) findViewById(R.id.main_imgView_categories);
        favorite = (ImageView) findViewById(R.id.main_imgView_favorite);
        cart = (ImageView) findViewById(R.id.main_imgView_cart);
        notification = (ImageView) findViewById(R.id.main_imgView_notification);
        accountOrLogin = (TextView) findViewById(R.id.main_txtView_accountOrLogin);
        search = (SearchView) findViewById(R.id.main_imgView_search);
        menu = (ImageView) findViewById(R.id.main_imgView_menu);

        if (language.equals("en")) {
            isEnglish = true;
            language = "en";
            Typeface enBold = Typeface.createFromAsset(getAssets(), "montserrat_medium.ttf");
            title.setTypeface(enBold);
        } else {
            isEnglish = false;
            language = "ar";
            Typeface arBold = Typeface.createFromAsset(getAssets(), "droid_arabic_kufi_bold.ttf");
            title.setTypeface(arBold);
        }

        if (sessionManager.getUser().get("id") == null || sessionManager.getUser().get("id").matches("")) {
            createGuestCustomerApi();
        }
        if (sessionManager.isGuest() || (sessionManager.getUser().get("userName") != null
                && !sessionManager.getUser().get("userName").matches(""))) {
            Navigator.loadFragment(this, MainCategoriesFragment.newInstance(this), R.id.main_frameLayout_Container, false);
        } else {
            Navigator.loadFragment(this, LoginFragment.newInstance(this, "mainActivity"), R.id.main_frameLayout_Container, false);
        }

        if (sessionManager.getUser().get("userName") != null
                && !sessionManager.getUser().get("userName").matches("")) {
            accountOrLogin.setText(getResources().getString(R.string.account));
        }

        shoppingCartItemsCount(this);

        //change the color of editText in searchView
        EditText searchEditText = (EditText) search.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search.onActionViewCollapsed();
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
        if (!search.isIconified()) {
            search.onActionViewCollapsed();
        } else
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
        if (sessionManager.getUser().get("userName") != null
                && !sessionManager.getUser().get("userName").matches("")) {
            Navigator.loadFragment(this, MyAccountFragment.newInstance(this), R.id.main_frameLayout_Container, true);
        } else {
            Navigator.loadFragment(this, LoginFragment.newInstance(this, "mainActivity"), R.id.main_frameLayout_Container, true);
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
        Navigator.loadFragment(this, TopicsPageFragment.newInstance(this, "1"), R.id.main_frameLayout_Container, true);
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
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
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
        sessionManager.setUserLanguage(language);

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
                            sessionManager.setUser(
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
                sessionManager.getUserLanguage(),
                sessionManager.getUser().get("id"),
                new Callback<GetCartProducts>() {
                    @Override
                    public void success(GetCartProducts outResponse, retrofit.client.Response response) {

                        if (outResponse != null) {
                            if (outResponse.CartProducts.size() > 0) {
                                notification.setVisibility(View.VISIBLE);
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


    public static void setupAppbar(String selection, boolean hasSearch, boolean hasSideMenu) {
        bottomAppbar.setVisibility(View.VISIBLE);
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
            bottomAppbar.setVisibility(View.GONE);
        }

        if (!selection.equals("cart") && !emptyCart) {
            MainActivity.notification.setVisibility(View.VISIBLE);
        }
        else{
            MainActivity.notification.setVisibility(View.INVISIBLE);
        }

        if (!hasSearch) {
            search.setVisibility(View.GONE);
        } else {
            search.setVisibility(View.VISIBLE);
        }
        if (!hasSideMenu) {
            menu.setVisibility(View.GONE);
        } else {
            menu.setVisibility(View.VISIBLE);
        }
    }

}
