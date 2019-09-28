package app.atelier.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.Navigator;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.contact.ContactUs;
import app.atelier.webservices.contact.GetContactUsRequest;
import app.atelier.webservices.contact.GetContactUsResponse;
import app.atelier.webservices.responses.settings.GetSettings;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ContactUsFragment extends Fragment {
    public static FragmentActivity activity;
    public static ContactUsFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.contactUs_editTxt_name)
    EditText name;
    @BindView(R.id.contactUs_editTxt_subject)
    EditText subject;
    @BindView(R.id.contactUs_editTxt_email)
    EditText email;
    @BindView(R.id.contactUs_editTxt_message)
    EditText message;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static Map<String,String> User;
    private String facebookLink;
    private String instagramLink;
    private String twitterLink;
    private String youtubeLink;

    public static ContactUsFragment newInstance(FragmentActivity activity) {
        fragment = new ContactUsFragment();
        ContactUsFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        User = sessionManager.getUser();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_contact_us, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(activity.getResources().getString(R.string.contact));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupAppbar("",true,true);
        if(User.get("userName") !=null && !User.get("userName").matches("")){
            name.setText(User.get("userName"));
            email.setText(User.get("email"));
        }
        if(facebookLink == null && youtubeLink == null && twitterLink == null && twitterLink == null) {
            SettingsApi();
        }
        else{
            loading.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.contactUs_imgView_youtube)
    public void youtubeCLick(){
        if(youtubeLink != null && !youtubeLink.matches("")){
        Navigator.loadFragment(activity,UrlsFragment.newInstance(activity,youtubeLink),R.id.main_frameLayout_Container,true);
    }
    else{

        }
    }
    @OnClick(R.id.contactUs_imgView_instagram)
    public void instagramCLick(){
        if(instagramLink != null && !instagramLink.matches("")) {

            Navigator.loadFragment(activity, UrlsFragment.newInstance(activity, instagramLink), R.id.main_frameLayout_Container, true);
        }
        else{}
    }
    @OnClick(R.id.contactUs_imgView_twitter)
    public void twitterCLick(){
        if(twitterLink != null && !twitterLink.matches("")) {

            Navigator.loadFragment(activity, UrlsFragment.newInstance(activity, twitterLink), R.id.main_frameLayout_Container, true);
        }
        else{}
    }
    @OnClick(R.id.contactUs_imgView_facebook)
    public void facebookCLick(){
        if(facebookLink != null && !facebookLink.matches("")) {

            Navigator.loadFragment(activity, UrlsFragment.newInstance(activity, facebookLink), R.id.main_frameLayout_Container, true);
        }
        else{

        }
    }
    @OnClick(R.id.contactUs_btn_send)
    public void sendClick(){
        String nameStr = name.getText().toString();
        String subjectStr = subject.getText().toString();
        String emailStr = email.getText().toString();
        String messageStr = message.getText().toString();

        if(nameStr == null || nameStr.matches("")){
            Snackbar.make(loading,getString(R.string.name_required),Snackbar.LENGTH_SHORT).show();
        }
        else if(subjectStr == null || subjectStr.matches("")){
            Snackbar.make(loading,getString(R.string.subject_required),Snackbar.LENGTH_SHORT).show();

        }
        else if(emailStr == null || emailStr.matches("")){
            Snackbar.make(loading,getString(R.string.mail_required),Snackbar.LENGTH_SHORT).show();

        }
        else if(messageStr == null || messageStr.matches("")){
            Snackbar.make(loading,getString(R.string.message_required),Snackbar.LENGTH_SHORT).show();

        }
        else{
            GetContactUsRequest getContactUsRequest = new GetContactUsRequest();
            ContactUs contactUs = new ContactUs();
            contactUs.fullName = nameStr;
            contactUs.email = emailStr;
            contactUs.subject  = subjectStr;
            contactUs.enquiry = messageStr;
            getContactUsRequest.contactUs = contactUs;
            contactUsApi(getContactUsRequest);
        }

    }

    public void contactUsApi(GetContactUsRequest contactUs) {
        loading.setVisibility(View.VISIBLE);
        AtelierApiConfig.getCallingAPIInterface().contactUs(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                Constants.CONTENT_TYPE_VALUE,
                contactUs,
                new Callback<GetContactUsResponse>() {
                    @Override
                    public void success(GetContactUsResponse getContactUsResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        if(getContactUsResponse != null){
                            if(getContactUsResponse.contactUsList != null){
                                if(getContactUsResponse.contactUsList.size() > 0){
                                    if(getContactUsResponse.contactUsList.get(0).successfullySent){
                                        Snackbar.make(loading,getString(R.string.message_sent), Snackbar.LENGTH_LONG).show();
                                        getFragmentManager().popBackStack();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error,loading);
                    }
                }
        );
    }

    public void SettingsApi(){
        AtelierApiConfig.getCallingAPIInterface().settings(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                new Callback<GetSettings>() {
                    @Override
                    public void success(GetSettings getSettings, Response response) {
                        loading.setVisibility(View.GONE);
                        if(getSettings != null){
                            if(getSettings.settings.size() > 0){
                                for(int i = 0 ;i<getSettings.settings.size();i++){
                                    if(getSettings.settings.get(i).Name.equals(Constants.FACEBOOK)){
                                        facebookLink = getSettings.settings.get(i).Value;
                                    }
                                    else if(getSettings.settings.get(i).Name.equals(Constants.INSTAGRAM)){
                                        instagramLink = getSettings.settings.get(i).Value;
                                    }
                                    else if(getSettings.settings.get(i).Name.equals(Constants.TWITTER)){
                                        twitterLink = getSettings.settings.get(i).Value;
                                    }
                                    else if(getSettings.settings.get(i).Name.equals(Constants.YOUTUBE)){
                                        youtubeLink = getSettings.settings.get(i).Value;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.showErrorMessage(error,loading);
                    }
                }
        );
    }

}
