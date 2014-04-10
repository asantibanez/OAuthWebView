package com.andressantibanez.extras;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andressantibanez.oauthwebview.OAuth10WebView;
import com.andressantibanez.oauthwebview.R;

public class MainFragment extends Fragment implements OAuth10WebView.OAuthWebViewCallbacks {

    public static final String tag = MainFragment.class.getSimpleName();

    private OAuth10WebView oAuth10WebView;
    private ProgressDialog progressDialog;

    public MainFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        oAuth10WebView = (OAuth10WebView) v.findViewById(R.id.oauth_webview);

        //Setup OAuth10WebView
        if(savedInstanceState == null) {
            oAuth10WebView.setApiParameters(
                    Constants.API_CONSUMER_KEY,
                    Constants.API_CONSUMER_SECRET
            );
            oAuth10WebView.setEndpoints(
                    Constants.API_REQUEST_TOKEN_URL,
                    Constants.API_AUTHORIZE_URL,
                    Constants.API_ACCESS_TOKEN_URL,
                    Constants.API_CALLBACK_URL
            );
            oAuth10WebView.setCallbacksListener(this);
            oAuth10WebView.startOAuthDance();
        }

        return v;
    }

    public void onOAuthProgress(int progress) {
        if(progress != 100)
            getActivity().setProgressBarIndeterminateVisibility(true);
        else
            getActivity().setProgressBarIndeterminateVisibility(false);
    }

    public void onOAuthError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Please try again")
                .setTitle("Error during authentication");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onOAuthSuccess(String accessToken, String tokenSecret) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("-Token: " + accessToken + "\n-Secret: " + tokenSecret + "\n\nSAVE THESE!!!")
                .setTitle("Dude, you are awesome!");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
