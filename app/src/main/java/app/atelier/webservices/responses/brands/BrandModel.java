package app.atelier.webservices.responses.brands;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.atelier.webservices.responses.products.ImageModel;
import app.atelier.webservices.responses.products.LocalizedNameModel;

public class BrandModel {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("localized_names")
    @Expose
    public List<LocalizedNameModel> localizedNames;

    @SerializedName("image")
    @Expose
    public ImageModel image;


    public String getLocalizedName(){

        if(localizedNames != null){

            if(localizedNames.size()>0){

                name = localizedNames.get(0).localizedName;

            }
        }

        return name;

    }

    public String getPhoto(){

        String url="";

        if(image != null){

            if(image.src != null){

                url =  image.src;

            }

        }

        return url;

    }
}
