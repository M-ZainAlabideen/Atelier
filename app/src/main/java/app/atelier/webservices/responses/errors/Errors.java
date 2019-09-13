package app.atelier.webservices.responses.errors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Errors {
    @SerializedName("Dto.Email")
    @Expose
    public List<String> dtoEmail;
    @SerializedName("Dto.Password")
    @Expose
    public List<String> dtoPassword;

}
