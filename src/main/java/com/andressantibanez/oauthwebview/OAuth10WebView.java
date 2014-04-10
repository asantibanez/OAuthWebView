package com.andressantibanez.oauthwebview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class OAuth10WebView extends WebView {

    public static final String tag = OAuth10WebView.class.getSimpleName();

    //API Consumer definitions
    private String API_CONSUMER_KEY;
    private String API_CONSUMER_SECRET;

    //API Endpoints definitions
    private String API_REQUEST_TOKEN_URL;
    private String API_AUTHORIZE_URL;
    private String API_ACCESS_TOKEN_URL;

    //API Callback definition
    private String API_CALLBACK_URL;

    //Signpost Consumer and Provider
    private OAuthConsumer mConsumer;
    private OAuthProvider mProvider;

    //Callbacks Listener
    private OAuthWebViewCallbacks mCallbacksListener;

    //Constructors
    public OAuth10WebView(Context context) {
        super(context);
        init();
    }
    public OAuth10WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public OAuth10WebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init(){
        //Enable Javascript
        getSettings().setJavaScriptEnabled(true);
        //Set OAuthWebViewClient
        setWebViewClient(new OAuthWebViewClient());
        //Set OAuthWebChromeClient
        setWebChromeClient(new OAuthWebChromeClient());
    }

    public void setApiParameters(String consumerKey, String consumerSecret){
        API_CONSUMER_KEY = consumerKey;
        API_CONSUMER_SECRET = consumerSecret;
    }

    public void setEndpoints(String requestTokenUrl, String authorizeUrl, String accessTokenUrl, String callbackUrl){
        API_REQUEST_TOKEN_URL = requestTokenUrl;
        API_ACCESS_TOKEN_URL = accessTokenUrl;
        API_AUTHORIZE_URL = authorizeUrl;
        API_CALLBACK_URL = callbackUrl;
    }

    public void setCallbacksListener(OAuthWebViewCallbacks listener){
        mCallbacksListener = listener;
    }

    public void startOAuthDance(){
        new RequestAuthorizationUrlTask().execute();
    }

    //Custom WebViewClient to handle/parse received Urls
    private class OAuthWebViewClient extends WebViewClient{
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(tag, "Url to Load: " + url);
            boolean isApiCallback = url.startsWith(API_CALLBACK_URL);
            if(isApiCallback){
                Uri uri = Uri.parse(url);
                String verifier = uri.getQueryParameter("oauth_verifier");
                new GetAuthorizedTokenTask().execute(verifier);
            }else {
                view.loadUrl(url);
            }
            return true;
        }
    }
    //Custom WebChromeClient to notify progress
    private class OAuthWebChromeClient extends WebChromeClient{
        public void onProgressChanged(WebView view, int newProgress) {
            Log.d(tag, "Progress: " + newProgress);
            if(mCallbacksListener != null)
                mCallbacksListener.onOAuthProgress(newProgress);
        }
    }

    //BackgroundTasks
    //RequestAuthorizationUrlTask
    private class RequestAuthorizationUrlTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {

            mConsumer = new DefaultOAuthConsumer(
                    API_CONSUMER_KEY,
                    API_CONSUMER_SECRET
            );
            mProvider = new DefaultOAuthProvider(
                    API_REQUEST_TOKEN_URL,
                    API_ACCESS_TOKEN_URL,
                    API_AUTHORIZE_URL
            );

            String authorizationUrl = null;
            try {
                authorizationUrl = mProvider.retrieveRequestToken(mConsumer, API_CALLBACK_URL);
            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
            } catch (OAuthNotAuthorizedException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
            }
            return authorizationUrl;
        }
        protected void onPostExecute(String url) {
            if(url != null){
                loadUrl(url);
            }
            else if(mCallbacksListener != null){
                mCallbacksListener.onOAuthError();
            }

        }
    }
    //GetAuthorizedTokenTask
    private class GetAuthorizedTokenTask extends AsyncTask<String, Void, String[]>{
        protected String[] doInBackground(String... strings) {
            String verifier = strings[0];
            String accessToken = null;
            String tokenSecret = null;
            try {
                mProvider.retrieveAccessToken(mConsumer, verifier);
                accessToken = mConsumer.getToken();
                tokenSecret = mConsumer.getTokenSecret();
            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
            } catch (OAuthNotAuthorizedException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
            }

            String values[] = new String[2];
            values[0] = accessToken;
            values[1] = tokenSecret;
            return values;
        }

        protected void onPostExecute(String[] values) {
            String accessToken = values[0];
            String tokenSecret = values[1];

            if(mCallbacksListener != null)
            if(accessToken != null && tokenSecret != null)
                mCallbacksListener.onOAuthSuccess(accessToken, tokenSecret);
            else
                mCallbacksListener.onOAuthError();
        }
    }

    //Callbacks
    public interface OAuthWebViewCallbacks{
        public void onOAuthProgress(int progress);
        public void onOAuthError();
        public void onOAuthSuccess(String accessToken, String tokenSecret);
    }
}
