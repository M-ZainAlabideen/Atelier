package app.atelier.webservices;

import java.util.ArrayList;

import app.atelier.classes.Constants;
import app.atelier.webservices.responses.cities.GetCities;
import app.atelier.webservices.responses.contact.GetContactUsRequest;
import app.atelier.webservices.responses.contact.GetContactUsResponse;
import app.atelier.webservices.responses.addresses.GetAddresses;
import app.atelier.webservices.responses.brands.GetBrands;
import app.atelier.webservices.responses.cart.GetCartProducts;
import app.atelier.webservices.responses.categories.GetCategories;
import app.atelier.webservices.responses.countries.GetCountries;
import app.atelier.webservices.responses.coupon.GetCouponOrderTotal;
import app.atelier.webservices.responses.customers.GetCustomers;
import app.atelier.webservices.responses.orders.GetOrders;
import app.atelier.webservices.responses.products.Delivery;
import app.atelier.webservices.responses.products.GetProducts;
import app.atelier.webservices.responses.cart.CartItem;
import app.atelier.webservices.responses.settings.GetSettings;
import app.atelier.webservices.responses.shipping.GetShippingMethods;
import app.atelier.webservices.responses.sliders.GetSlider;
import app.atelier.webservices.responses.states.GetStates;
import app.atelier.webservices.responses.stores.GetStores;
import app.atelier.webservices.responses.topics.GetTopics;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
public interface AtelierApiInterface {

    @GET("/current_store")
    void currentStore(@Header(Constants.AUTHORIZATION) String Authorization,
                      Callback<GetStores> response);

    @POST("/guestcustomers")
    void createGuestCustomer(@Header(Constants.AUTHORIZATION) String Authorization,
                             @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                             @Header(Constants.CONTENT_TYPE) String contentType,
                             Callback<GetCustomers> response);

    @PUT("/customers/{id}")
    void updateCustomer(@Header(Constants.AUTHORIZATION) String Authorization,
                        @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                        @Path("id") String id,
                        @Header(Constants.CONTENT_TYPE) String contentType,
                        @Body GetCustomers customer,
                        Callback<GetCustomers> response);

    @GET("/customers/login")
    void login(@Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
               @Header(Constants.AUTHORIZATION) String Authorization,
               @Query("username_or_email") String usernameOrEmail,
               @Query("password") String password,
               @Query("current_customer_id") String currentCustomerId,
               Callback<GetCustomers> response);


    @GET("/customers/{id}")
    void customerById(@Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                      @Header(Constants.AUTHORIZATION) String Authorization,
                      @Path("id") String id,
                      Callback<GetCustomers> response);

    @GET("/sliders")
    void sliders(@Header(Constants.AUTHORIZATION) String Authorization,
                 @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                 @Query("category_id") String category_id,
                 Callback<GetSlider> getSlidersCallback);

    //*****************************CATEGORIES***********************************

    @GET("/categories?fields=id,localized_names,image,has_sub_categories")
    void categories(@Header(Constants.AUTHORIZATION) String Authorization,
                    @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                    Callback<GetCategories> response);

    @GET("/manufacturers")
    void brands(@Header(Constants.AUTHORIZATION) String Authorization,
                @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                @Query("category_id") String category_id,
                Callback<GetBrands> response);

    @GET("/categories?fields=id,localized_names,image,has_sub_categories,parent_category_id")
    void subcategories(@Header(Constants.AUTHORIZATION) String Authorization,
                       @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                       @Query("parent_category_id") String parentCategoryId,
                       Callback<GetCategories> getGetCategories);


    //*****************************PRODUCTS***********************************
    @GET("/products?fields=id,localized_names,localized_full_descriptions,images,attributes,price,Is_AddedToWishList,vendor_id,old_price,available_end_date_time_utc,show_timer,prices_percentage,formatted_old_price,formatted_price")
    void products(@Header(Constants.AUTHORIZATION) String Authorization,
                  @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                  @Query("customer_id") String customerId,
                  @Query("category_id") String categoryId,
                  @Query("manufacturer_id") String manufacturerId,
                  @Query("limit") String limit,
                  @Query("page") String page,
                  @Query("show_new_products") String showNewProducts,
                  @Query("show_recently_viewed") String showRecentlyViewed,
                  @Query("tag_id") String tagId,
                  Callback<GetProducts> response);


    @GET("/products/search?fields=id,localized_names,localized_full_descriptions,images,attributes,price,Is_AddedToWishList,vendor_id,old_price,available_end_date_time_utc,show_timer,prices_percentage,formatted_old_price,formatted_price")
    void searchProducts(@Header(Constants.AUTHORIZATION) String Authorization,
                        @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                        @Query("customer_id") String customerId,
                        @Query("category_id") String categoryId,
                        @Query("manufacturer_id") String manufacturerId,
                        @Query("limit") String limit,
                        @Query("page") String page,
                        @Query("query") String query,
                        @Query("include_sub_categories") String includeSubCategories,
                        @Query("vendor_id") String vendorId,
                        @Query("price_from") String priceFrom,
                        @Query("price_to") String priceTo,
                        @Query("search_in_descriptions") String searchInDescriptions,
                        Callback<GetProducts> response);


    @GET("/products/{id}/related?fields=id,localized_names,localized_full_descriptions,images,attributes,price,Is_AddedToWishList,vendor_id,old_price,available_end_date_time_utc,show_timer,prices_percentage,formatted_old_price,formatted_price")
    void relatedProducts(@Header(Constants.AUTHORIZATION) String Authorization,
                         @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                         @Query("customer_id") String customerId,
                         @Path("id") String id,
                         Callback<GetProducts> response);

    @GET("/products/{id}")
    void productById(@Header(Constants.AUTHORIZATION) String Authorization,
                     @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                     @Query("customer_id") String customerId,
                     @Path("id") String id,
                     Callback<GetProducts> response);


    //*****************************CART***********************************
    @POST("/shopping_cart_items")
    void createShoppingCart(@Header(Constants.AUTHORIZATION) String Authorization,
                            @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                            @Header(Constants.CONTENT_TYPE) String contentType,
                            @Body CartItem cartItem,
                            Callback<GetCartProducts> response);


    @GET("/shopping_cart_items/{id}")
    void shoppingCartItems(@Header(Constants.AUTHORIZATION) String Authorization,
                           @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                           @Path("id") String id,
                           @Query("shopping_cart_type_id") String shoppingCartTypeId,
                           Callback<GetCartProducts> response);


    @PUT("/shopping_cart_items/{id}")
    void updateShoppingCart(@Header(Constants.AUTHORIZATION) String Authorization,
                            @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                            @Header(Constants.CONTENT_TYPE) String contentType,
                            @Path("id") String id,
                            @Body CartItem cartItem,
                            Callback<GetCartProducts> response);


    @DELETE("/shopping_cart_items/{id}")
    void deleteShoppingCartItem(@Header(Constants.AUTHORIZATION) String Authorization,
                                @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                                @Path("id") String id,
                                Callback<Response> response);


    @DELETE("/shopping_cart_items/{shopping_cart_type}/{id}/{customer_id}")
    void deleteFavoriteItem(@Header(Constants.AUTHORIZATION) String Authorization,
                            @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                            @Path("shopping_cart_type") String shoppingCartType,
                            @Path("id") String productId,
                            @Path("customer_id") String customerId,
                            Callback<Response> response);


    @GET("/shopping_cart_items/{id}?fields=quantity&shopping_cart_type_id=1")
    void shoppingCartItemsCount(@Header(Constants.AUTHORIZATION) String Authorization,
                                @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                                @Path("id") String id,
                                Callback<GetCartProducts> response);


    @GET("/shopping_cart_items/{id}?fields=id,quantity,product_id,product_attributes,&shopping_cart_type_id=1")
    void shoppingCartItemsForOrder(@Header(Constants.AUTHORIZATION) String Authorization,
                                   @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                                   @Path("id") String id,
                                   Callback<GetCartProducts> response);

    //*****************************ORDERS***********************************
    @GET("/orders?fields=id,created_on_utc,customer_currency_code,order_status,order_total")
    void orders(@Header(Constants.AUTHORIZATION) String Authorization,
                @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                @Query("customer_id") String customerId,
                @Query("page") String page,
                @Query("limit") String limit,
                Callback<GetOrders> getOrders);

    @GET("/orders/{id}")
    void orderById(@Header(Constants.AUTHORIZATION) String Authorization,
                   @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                   @Path("id") String id,
                   Callback<GetOrders> getOrders);

    @GET("/orders/{id}/reorder")
    void reorder(@Header(Constants.AUTHORIZATION) String Authorization,
                 @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                 @Path("id") String id,
                 Callback<GetCartProducts> getProducts);

    @POST("/orders")
    void createOrders(@Header(Constants.AUTHORIZATION) String Authorization,
                      @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                      @Header(Constants.CONTENT_TYPE) String content_type,
                      @Body GetOrders orders,
                      Callback<Response> response);


    //************************ADDRESSES*********************************

    @POST("/customers/{id}/addresses")
    void addAddress(@Header(Constants.AUTHORIZATION) String Authorization,
                    @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                    @Header(Constants.CONTENT_TYPE) String contentType,
                    @Body GetAddresses address,
                    @Path("id") String id,
                    Callback<GetAddresses> getAddress);


    @PUT("/customers/{id}/addresses/{addressId}")
    void editAddress(@Header(Constants.AUTHORIZATION) String Authorization,
                     @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                     @Header(Constants.CONTENT_TYPE) String contentType,
                     @Body GetAddresses address,
                     @Path("id") String customerId,
                     @Path("addressId") String addressId,
                     Callback<GetAddresses> getAddress);

    @DELETE("/customers/{id}/addresses/{addressId}")
    void deleteAddress(@Header(Constants.AUTHORIZATION) String Authorization,
                       @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                       @Path("id") String customerId,
                       @Path("addressId") String addressId,
                       Callback<Response> response);


    @GET("/customers/{id}?fields=addresses,billing_address")
    void customerAddresses(@Header(Constants.AUTHORIZATION) String Authorization,
                           @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                           @Path("id") String id,
                           Callback<GetCustomers> response);


    @GET("/countries/{id}/StateProvinces?fields=id,name,country_id")
    void stateProvinces(@Header(Constants.AUTHORIZATION) String Authorization,
                        @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                        @Path("id") String countryId,
                        Callback<GetStates> getStates);

    @GET("/countries?fields=id,name")
    void countries(@Header(Constants.AUTHORIZATION) String Authorization,
                   @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                   Callback<GetCountries> getCountries);

    @GET("/cities/{id}?fields=id,name")
    void cities(@Header(Constants.AUTHORIZATION) String Authorization,
                @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                @Path("id") String countryId,
                Callback<GetCities> getCities);

    //************************PASSWORD*********************************

    @FormUrlEncoded
    @POST("/customers/recoverypassword")
    void forgetPassword(@Header(Constants.AUTHORIZATION) String Authorization,
                        @Field("email") String email,
                        Callback<Response> response);

    //@FormUrlEncoded
    @POST("/customers/changepassword")
    void changePassword(@Header(Constants.AUTHORIZATION) String Authorization,
                        @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                        @Query("customer_id") String customer_id,
                        @Query("old_password") String old_password,
                        @Query("new_password") String new_password,
                        Callback<GetCustomers> getGetCustomer);

    //************************CONTENT*********************************

    @POST("/contact_us")
    void contactUs(@Header(Constants.AUTHORIZATION) String Authorization,
                   @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                   @Header(Constants.CONTENT_TYPE) String contentType,
                   @Body GetContactUsRequest contactUs,
                   Callback<GetContactUsResponse> getGetContactUs);

    @GET("/topics/{id}")
    void topics(@Header(Constants.AUTHORIZATION) String Authorization,
                @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                @Path("id") String id,
                Callback<GetTopics> getTopics);

    @GET("/settings")
    void settings(@Header(Constants.AUTHORIZATION) String Authorization,
                  @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                  Callback<GetSettings> getProductsCallback);

    //********************************************************************
    @GET("/ApplyDiscountCoupon")
    void applyCoupon(@Header(Constants.AUTHORIZATION) String Authorization,
               @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
               @Query("customerId") String customerId,
               @Query("discountcouponcode") String discountCouponCode,
               Callback<GetCouponOrderTotal> getProductsCallback);

    @DELETE("/RemoveDiscountCoupon")
    void removeCoupon(@Header(Constants.AUTHORIZATION) String Authorization,
                      @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                      @Query("customerId") String customerId,
                      @Query("discountcouponcode") String discountCouponCode,
                      Callback<Response> response);

    @GET("/shipping/{id}")
    void deliveryCost(@Header(Constants.AUTHORIZATION) String Authorization,
                      @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                      @Path("id") String id,
                      Callback<Delivery> response);

    @GET("/shippingV2/{customerId}")
    void shipping(@Header(Constants.AUTHORIZATION) String Authorization,
                  @Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                  @Path("customerId") String customer_id,
                  @Query("countryId") int countryId,
                  @Query("city") String city,
                  Callback<GetShippingMethods> getStateProvince);

    @POST("/customers/devicetoken")
    void insertToken(@Header("Authorization") String Authorization,
                     @Query("token") String token,
                     @Query("device_type") String deviceType,
                     @Query("device_id") String deviceId,
                     @Query("customer_id") String userId,
                     Callback<Response> response);
}
