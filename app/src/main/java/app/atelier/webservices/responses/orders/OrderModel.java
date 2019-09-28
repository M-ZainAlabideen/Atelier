package app.atelier.webservices.responses.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import app.atelier.webservices.responses.addresses.AddressModel;
import app.atelier.webservices.responses.customers.CustomerModel;

public class OrderModel implements Serializable {
    @SerializedName("billing_address_id")
    @Expose
    public Integer billingAddressId;
    @SerializedName("payment_by")
    @Expose
    public Integer payment_by;
    @SerializedName("use_reward_points")
    @Expose
    public String use_reward_points;
    @SerializedName("store_id")
    @Expose
    public Integer storeId;
    @SerializedName("pick_up_in_store")
    @Expose
    public Boolean pickUpInStore;
    @SerializedName("payment_method_system_name")
    @Expose
    public String paymentMethodSystemName;

    @SerializedName("Payment_Method_Code")
    @Expose
    public String paymentMethodCode;

    @SerializedName("customer_currency_code")
    @Expose
    public String customerCurrencyCode;
    @SerializedName("currency_rate")
    @Expose
    public Integer currencyRate;
    @SerializedName("customer_tax_display_type_id")
    @Expose
    public Integer customerTaxDisplayTypeId;
    @SerializedName("vat_number")
    @Expose
    public Object vatNumber;
    @SerializedName("order_subtotal_incl_tax")
    @Expose
    public Double orderSubtotalInclTax;
    @SerializedName("order_subtotal_excl_tax")
    @Expose
    public Double orderSubtotalExclTax;
    @SerializedName("order_sub_total_discount_incl_tax")
    @Expose
    public Double orderSubTotalDiscountInclTax;
    @SerializedName("order_sub_total_discount_excl_tax")
    @Expose
    public Double orderSubTotalDiscountExclTax;
    @SerializedName("order_shipping_incl_tax")
    @Expose
    public Double orderShippingInclTax;
    @SerializedName("order_shipping_excl_tax")
    @Expose
    public Double orderShippingExclTax;
    @SerializedName("payment_method_additional_fee_incl_tax")
    @Expose
    public Double paymentMethodAdditionalFeeInclTax;
    @SerializedName("payment_method_additional_fee_excl_tax")
    @Expose
    public Double paymentMethodAdditionalFeeExclTax;
    @SerializedName("tax_rates")
    @Expose
    public String taxRates;
    @SerializedName("order_tax")
    @Expose
    public Double orderTax;
    @SerializedName("order_discount")
    @Expose
    public Double orderDiscount;
    @SerializedName("order_total")
    @Expose
    public Double orderTotal;
    @SerializedName("refunded_amount")
    @Expose
    public Double refundedAmount;
    @SerializedName("reward_points_were_added")
    @Expose
    public Object rewardPointsWereAdded;
    @SerializedName("checkout_attribute_description")
    @Expose
    public String checkoutAttributeDescription;
    @SerializedName("customer_language_id")
    @Expose
    public Integer customerLanguageId;
    @SerializedName("affiliate_id")
    @Expose
    public Integer affiliateId;
    @SerializedName("customer_ip")
    @Expose
    public String customerIp;
    @SerializedName("authorization_transaction_id")
    @Expose
    public String authorizationTransactionId;
    @SerializedName("authorization_transaction_code")
    @Expose
    public Object authorizationTransactionCode;
    @SerializedName("authorization_transaction_result")
    @Expose
    public Object authorizationTransactionResult;
    @SerializedName("capture_transaction_id")
    @Expose
    public Object captureTransactionId;
    @SerializedName("capture_transaction_result")
    @Expose
    public Object captureTransactionResult;
    @SerializedName("subscription_transaction_id")
    @Expose
    public Object subscriptionTransactionId;
    @SerializedName("paid_date_utc")
    @Expose
    public String paidDateUtc;
    @SerializedName("shipping_method")
    @Expose
    public Object shippingMethod;
    @SerializedName("shipping_rate_computation_method_system_name")
    @Expose
    public Object shippingRateComputationMethodSystemName;
    @SerializedName("custom_values_xml")
    @Expose
    public Object customValuesXml;
    @SerializedName("deleted")
    @Expose
    public Boolean deleted;
    @SerializedName("created_on_utc")
    @Expose
    public String createdOnUtc;
    @SerializedName("customer")
    @Expose
    public CustomerModel customer;
    @SerializedName("customer_id")
    @Expose
    public Integer customerId;
    @SerializedName("billing_address")
    @Expose
    public AddressModel billingAddress;
    @SerializedName("shipping_address")
    @Expose
    public Object shippingAddress;
    @SerializedName("order_items")
    @Expose
    public ArrayList<OrderItems> orderItems = null;
    @SerializedName("order_status")
    @Expose
    public String orderStatus;
    @SerializedName("payment_status")
    @Expose
    public String paymentStatus;
    @SerializedName("shipping_status")
    @Expose
    public String shippingStatus;
    @SerializedName("customer_tax_display_type")
    @Expose
    public String customerTaxDisplayType;
    @SerializedName("id")
    @Expose
    public Integer id;
    
}
