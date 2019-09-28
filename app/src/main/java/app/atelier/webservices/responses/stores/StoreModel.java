package app.atelier.webservices.responses.stores;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StoreModel {
     @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("url")
        @Expose
        public String url;

        @SerializedName("ssl_enabled")
        @Expose
        public boolean sslEnabled;

        @SerializedName("secure_url")
        @Expose
        public String secureUrl;

        @SerializedName("hosts")
        @Expose
        public String hosts;

        @SerializedName("default_language_id")
        @Expose
        public int defaultLanguageId;

        @SerializedName("store_languages")
        @Expose
        public ArrayList<StoreLanguageModel> storeLanguages;

        @SerializedName("display_order")
        @Expose
        public int displayOrder;

        @SerializedName("company_name")
        @Expose
        public String companyName;

        @SerializedName("company_address")
        @Expose
        public String companyAddress;

        @SerializedName("company_phone_number")
        @Expose
        public String companyPhoneNumber;

        @SerializedName("company_vat")
        @Expose
        public String companyVat;

        @SerializedName("primary_currency_id")
        @Expose
        public int primaryCurrencyId;

        @SerializedName("store_currencies")
        @Expose
        public ArrayList<StoreCurrencyModel> storeCurrencies;

        @SerializedName("store_payment_methods")
        @Expose
        public ArrayList<PaymentModel> storePaymentMethods;

        @SerializedName("id")
        @Expose
        public int id;
    }

