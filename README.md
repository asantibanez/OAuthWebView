OAuthWebView
============

WebViews for OAuth Authentication

First release.
Tested with Vimeo API OAuth 1.0

Example included in extras. To build the project, add a Constants class with your API parameters:

```java
public class Constants {
    public static final String API_CONSUMER_KEY = "KEY_HERE";
    public static final String API_CONSUMER_SECRET = "SECRET_HERE";
    public static final String API_REQUEST_TOKEN_URL = "https://vimeo.com/oauth/request_token";
    public static final String API_AUTHORIZE_URL = "https://vimeo.com/oauth/authorize?permission=write";
    public static final String API_ACCESS_TOKEN_URL = "https://vimeo.com/oauth/access_token";
    public static final String API_CALLBACK_URL = "oauth://vimeo";
}
```

This widget has to be used with the following configuration on ActivityManifest.xml

```java
android:configChanges="orientation|keyboardHidden|screenSize
```

Work in progress. Feedback appreciated.
