package app.atelier.webservices.responses.categories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import app.atelier.webservices.responses.products.ImageModel;
import app.atelier.webservices.responses.products.LocalizedNameModel;
import app.atelier.webservices.responses.products.ProductModel;

public class CategoryModel {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("localized_names")
    @Expose
    public List<LocalizedNameModel> localizedNames;
    @SerializedName("category_template_id")
    @Expose
    public Integer categoryTemplateId;
    @SerializedName("parent_category_id")
    @Expose
    public String parentCategoryId;
    @SerializedName("page_size")
    @Expose
    public Integer pageSize;
    @SerializedName("show_on_home_page")
    @Expose
    public Boolean showOnHomePage;
    @SerializedName("include_in_top_menu")
    @Expose
    public Boolean includeInTopMenu;
    @SerializedName("published")
    @Expose
    public Boolean published;
    @SerializedName("deleted")
    @Expose
    public Boolean deleted;
    @SerializedName("display_order")
    @Expose
    public Integer displayOrder;
    @SerializedName("created_on_utc")
    @Expose
    public String createdOnUtc;
    @SerializedName("updated_on_utc")
    @Expose
    public String updatedOnUtc;
    @SerializedName("role_ids")
    @Expose
    public List<Object> roleIds;
    @SerializedName("discount_ids")
    @Expose
    public List<Object> discountIds;
    @SerializedName("store_ids")
    @Expose
    public List<Object> storeIds;
    @SerializedName("image")
    @Expose
    public ImageModel image;
    @SerializedName("se_name")
    @Expose
    public String seName;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("has_sub_categories")
    @Expose
    public Boolean hasSubCategories;
    @SerializedName("top_products")
    @Expose
    public ArrayList<ProductModel> topProducts;
    @SerializedName("localized_descriptions")
    @Expose
    public List<DescriptionModel> localizedDescriptions;

    boolean IsSelected;
    
    public String getPhoto(){

        String url="";

        if(image != null){

            if(image.src != null){

                url =  image.src;

            }

        }

        return url;

    }

    public String getLocalizedName(){

        String name="";

        if(localizedNames != null){

            if(localizedNames.size()>0){

                name = localizedNames.get(0).localizedName;

            }

        }

        return name;

    }

    public void setLocalizedName(String name){

        LocalizedNameModel localizedName = new LocalizedNameModel();

        localizedName.localizedName =  name;

        if(localizedNames == null){

            localizedNames = new ArrayList<>();

        }

        localizedNames.add(localizedName);

    }

}
