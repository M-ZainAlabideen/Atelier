package app.atelier.webservices.responses.countries;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCountries {
        @SerializedName("countries")
        @Expose
        public List<CountryModel> countries;
}
