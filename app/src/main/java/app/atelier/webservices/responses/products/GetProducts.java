package app.atelier.webservices.responses.products;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetProducts {
    @SerializedName("products")
    @Expose
    public ArrayList<ProductModel> products;

}
