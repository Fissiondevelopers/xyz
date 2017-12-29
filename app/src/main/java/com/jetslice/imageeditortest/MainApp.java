package com.jetslice.imageeditortest;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

public class MainApp extends Application implements IAdobeAuthClientCredentials{
    private static final String CREATIVE_SDK_CLIENT_ID      = "912555b983d14495bfa836d3e4101041";
    private static final String CREATIVE_SDK_CLIENT_SECRET  = "633c8716-b48c-45f1-bf21-cb46af4546ea";
    private static final String CREATIVE_SDK_REDIRECT_URI   = "ams+6ccad231732fa28fc495a008b72257ae6e5a1d76://adobeid/912555b983d14495bfa836d3e4101041";
    private static final String[] CREATIVE_SDK_SCOPES       = {"email", "profile", "address"};

    @Override
    public void onCreate() {
        super.onCreate();
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }


    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String[] getAdditionalScopesList() {
        return CREATIVE_SDK_SCOPES;
    }

    @Override
    public String getRedirectURI() {
        return CREATIVE_SDK_REDIRECT_URI;
    }
}
