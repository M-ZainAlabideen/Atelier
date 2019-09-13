package app.atelier.webservices.responses.categories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetCategories {

    @SerializedName("categories")
    @Expose
    public ArrayList<CategoryModel> categories;
}
