package app.atelier.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.topics.GetTopics;
import app.atelier.webservices.responses.topics.TopicModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopicsPageFragment extends Fragment {
    public static FragmentActivity activity;
    public static TopicsPageFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.aboutUs_webView_content)
    WebView content;
    @BindView(R.id.loading)
    ProgressBar loading;

    List<TopicModel> topicsArrList = new ArrayList<>();
    String fontName;

    public static TopicsPageFragment newInstance(FragmentActivity activity,String topicId) {
        fragment = new TopicsPageFragment();
        TopicsPageFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        Bundle b = new Bundle();
        b.putString("topicId",topicId);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_topics_page, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText("");
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.GONE);
        MainActivity.setupAppbar("",false,false);

        //WebView should execute JavaScript
        content.getSettings().setJavaScriptEnabled(true);
        /*when enabling this Property is that it would then allow ANY website
        that takes advantage of DOM storage to use said storage options on the device*/
        content.getSettings().setDomStorageEnabled(true);
        content.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        if(topicsArrList.size() > 0){
            setData();
        }
        else{
            aboutUsCall();
        }
        content.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("vnd.youtube")) {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                    return true;
                } else {
                    return false;
                }
            }

            public void onPageFinished(WebView view, String url) {
                // hide progress of Loading after finishing
                loading.setVisibility(View.GONE);
            }
        });

    }



    public void aboutUsCall()
    {
        AtelierApiConfig.getCallingAPIInterface().topics(
                Constants.AUTHORIZATION_VALUE,
                sessionManager.getUserLanguage(),
                getArguments().getString("topicId"),
                new Callback<GetTopics>() {
                    @Override
                    public void success(GetTopics getTopics, Response response) {
                        if(getTopics != null){
                            if(getTopics.topics.size() > 0){
                                topicsArrList.addAll(getTopics.topics);
                                setData();
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

    public void setData() {
        MainActivity.title.setText(topicsArrList.get(0).titles.get(0).localized_title);
        String contentStr = topicsArrList.get(0).bodies.get(0).localized_body;
        contentStr = contentStr.replace("font","f");
        contentStr = contentStr.replace("color","c");
        contentStr = contentStr.replace("size","s");
        if(MainActivity.isEnglish)
            fontName = "montserrat_regular.ttf";
        else
            fontName = "droid_arabic_kufi.ttf";
        String head = "<head><style>@font-face {font-family: 'verdana';src: url('file:///android_asset/"+fontName+"');}body {font-family: 'verdana';}</style></head>";
        String htmlData = "<html>" + head + (MainActivity.isEnglish ? "<body dir=\"ltr\"" : "<body dir=\"rtl\"") + " style=\"font-family: verdana\">" +
                contentStr + "</body></html>";
        //Content.loadData(htmlData, "text/html; charset=utf-8", "utf-8");
        content.loadDataWithBaseURL("", htmlData, "text/html; charset=utf-8", "utf-8", "");
        loading.setVisibility(View.GONE);

    }

}
