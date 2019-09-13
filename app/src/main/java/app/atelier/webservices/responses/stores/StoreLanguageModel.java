package app.atelier.webservices.responses.stores;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoreLanguageModel {
        @SerializedName("language_id")
        @Expose
        public int languageId;

        @SerializedName("language_name")
        @Expose
        public String languageName;

        @SerializedName("language_code")
        @Expose
        public String languageCode;
}
