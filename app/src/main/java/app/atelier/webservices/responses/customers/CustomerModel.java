package app.atelier.webservices.responses.customers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.atelier.webservices.responses.addresses.AddressModel;

public class CustomerModel {
    @SerializedName("reward_points_balance")
    @Expose
    public String rewardPointsBalance;

    @SerializedName("reward_points_amount")
    @Expose
    public String rewardPointsAmount;

    @SerializedName("min_reward_points_balance")
    @Expose
    public String minRewardPointsBalance;

    @SerializedName("min_reward_points_amount")
    @Expose
    public String minRewardPointsAmount;

    @SerializedName("shopping_cart_items")
    @Expose
    public List<Object> shoppingCartItems;

    @SerializedName("billing_address")
    @Expose
    public AddressModel billingAddress;

    @SerializedName("shipping_address")
    @Expose
    public String shippingAddress;

    @SerializedName("addresses")
    @Expose
    public List<AddressModel> addresses;

    @SerializedName("customer_guid")
    @Expose
    public String customerGuid;

    @SerializedName("username")
    @Expose
    public String userName;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("first_name")
    @Expose
    public String firstName;

    @SerializedName("last_name")
    @Expose
    public String lastName;

    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("password")
    @Expose
    public String password;

    @SerializedName("language_id")
    @Expose
    public String languageId;

    @SerializedName("date_of_birth")
    @Expose
    public String dateOfBirth;

    @SerializedName("gender")
    @Expose
    public String gender;

    @SerializedName("admin_comment")
    @Expose
    public String adminComment;

    @SerializedName("is_tax_exempt")
    @Expose
    public boolean isTaxExempt;

    @SerializedName("vendor_id")
    @Expose
    public int vendorId;

    @SerializedName("has_shopping_cart_items")
    @Expose
    public boolean hasShoppingCartItems;

    @SerializedName("active")
    @Expose
    public boolean active;

    @SerializedName("deleted")
    @Expose
    public boolean deleted;

    @SerializedName("is_system_account")
    @Expose
    public boolean isSystemAccount;

    @SerializedName("system_name")
    @Expose
    public String systemName;

    @SerializedName("last_ip_address")
    @Expose
    public String lastIpAddress;

    @SerializedName("created_on_utc")
    @Expose
    public String createdOnUtc;

    @SerializedName("last_login_date_utc")
    @Expose
    public String lastLoginDateUtc;

    @SerializedName("last_activity_date_utc")
    @Expose
    public String lastActivityDateUtc;

    @SerializedName("registered_in_store_id")
    @Expose
    public int registeredInStoreId;

    @SerializedName("subscribed_to_newsletter")
    @Expose
    public boolean subscribedToNewsletter;

    @SerializedName("role_ids")
    @Expose
    public List<Integer> roleIds;

    @SerializedName("id")
    @Expose
    public int id;

}
