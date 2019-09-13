package app.atelier.webservices.responses.stores;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoreCurrencyModel {
        @SerializedName("currency_id")
        @Expose
        public int currencyId;

        @SerializedName("currency_name")
        @Expose
        public String currencyName;

        @SerializedName("currency_code")
        @Expose
        public String currencyCode;
}
