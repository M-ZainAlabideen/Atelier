package app.atelier.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.atelier.MainActivity;
import app.atelier.R;
import butterknife.ButterKnife;

public class AboutUsFragment extends Fragment {
    public static FragmentActivity activity;
    public static AboutUsFragment fragment;

    public static AboutUsFragment newInstance(FragmentActivity activity) {
        fragment = new AboutUsFragment();
        AboutUsFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_about_us, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.title.setText(activity.getResources().getString(R.string.about));
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.bottomAppbar.setVisibility(View.VISIBLE);
        MainActivity.setupBottomAppbar("");
    }

    public void ApiCall() {
        setData();
    }

    public void setData() {
    }


}
