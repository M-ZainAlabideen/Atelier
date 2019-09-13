package app.atelier.webservices.responses.states;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetStates {
    @SerializedName("state_provinces")
    @Expose
    public List<StateModel> states;

}
