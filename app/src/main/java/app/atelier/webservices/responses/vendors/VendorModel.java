package app.atelier.webservices.responses.vendors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VendorModel {
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("phone_number")
        @Expose
        public String phoneNumber;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("picture_id")
        @Expose
        public Integer pictureId;
        @SerializedName("coordinates")
        @Expose
        public String coordinates;
        @SerializedName("active")
        @Expose
        public Boolean active;
        @SerializedName("deleted")
        @Expose
        public Boolean deleted;
        @SerializedName("id")
        @Expose
        public Integer id;
    }
    
