package com.example.android.facebookloginsample;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.*;
import com.google.gson.Gson;

import java.util.Arrays;

public class LoginActivity extends Activity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private TextView btnLogin;
    private ProgressDialog progressDialog;
    private LoginResult logr;
    Gson gsonClass = new Gson();

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(PrefUtils.getCurrentUser(LoginActivity.this) != null){

            Intent homeIntent = new Intent(LoginActivity.this, welcome.class);

            startActivity(homeIntent);

            finish();
        }

        logr = gsonClass.fromJson("{\"accessToken\":{\"applicationId\":\"287372341602615\",\"declinedPermissions\":[],\"expires\":\"Jul 20, 2016 11:57:36 PM\",\"lastRefresh\":\"May 22, 2016 7:14:16 PM\",\"permissions\":[\"user_friends\",\"contact_email\",\"email\",\"public_profile\",\"publish_actions\"],\"source\":\"FACEBOOK_APPLICATION_WEB\",\"token\":\"EAAEFXRZAW6TcBALxF2vI3JNu0itzYkOHSzBUEau6NPMNzintSfgFSmIigV7aWhLVXCqugK7NgNJEfJDDrnOHd9wpBN2GwUeQCCuvnJFhQ1y9zHsZCE6vFLXiMdreJpvUlTh5lVxOtAT6YE7bqUfiEsxYZCQlrirjZA7ePC9ZCNSFE6GjU2tnshi2oJ2T1SislxdCOGpbnoAZDZD\",\"userId\":\"125237697891733\"},\"recentlyDeniedPermissions\":[],\"recentlyGrantedPermissions\":[\"user_friends\",\"contact_email\",\"email\",\"public_profile\",\"publish_actions\"]}", LoginResult.class);
//        GraphRequestAsyncTask request = GraphRequest.newMeRequest(
//                logr.getAccessToken(),
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(
//                            JSONObject object,
//                            GraphResponse response) {
//                        Log.w("object: ", object.toString());
//                        Log.w("response: ", response + "");
//                        try {
//                            user = new User();
//                            user.facebookID = object.getString("id").toString();
//                            user.email = object.getString("email").toString();
//                            user.name = object.getString("name").toString();
//                            user.gender = object.getString("gender").toString();
//                            PrefUtils.setCurrentUser(user, LoginActivity.this);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        Toast.makeText(LoginActivity.this, "welcome " + user.name, Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(LoginActivity.this, LogoutActivity.class);
//                        startActivity(intent);
//                        finish();
//
//                    }
//
//                }).executeAsync();
//        loginButton= (LoginButton)findViewById(R.id.login_button);
//        loginButton.setPublishPermissions(Arrays.asList("email", "public_profile", "publish_actions"));



///Code for posting
            Bundle parameters = new Bundle();
            parameters.putString("message","Nailed It!!!!123");
            GraphRequest request = new GraphRequest(
                    logr.getAccessToken(),
                    "/me/feed",
                    parameters,
                    HttpMethod.POST,
                    new GraphRequest.Callback(){
                        public void onCompleted(GraphResponse response)
                        {
                            Log.e("response",response.toString());
                        }
                    }

);      request.executeAsync();
        Intent intent = new Intent(LoginActivity.this, welcome.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();


        callbackManager=CallbackManager.Factory.create();
        loginButton= (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email","user_friends","read_mailbox");
        //loginButton.setPublishPermissions("publish_actions");
        btnLogin= (TextView) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                loginButton.performClick();

                loginButton.setPressed(true);

                loginButton.invalidate();

                loginButton.registerCallback(callbackManager, mCallBack);

                loginButton.setPressed(false);

                loginButton.invalidate();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            progressDialog.dismiss();
            Log.e("onSuccess: OH YEAH", gsonClass.toJson(loginResult));
            // App code
//            GraphRequest request = GraphRequest.newMeRequest(
//            loginResult.getAccessToken(),
//                    new GraphRequest.GraphJSONObjectCallback() {
//                        @Override
//                        public void onCompleted(
//                                JSONObject object,
//                                GraphResponse response) {
//                            Log.e("object: ", object.toString());
//                            Log.e("response: ", response + "");
//                                try {
//                                    user = new User();
//                                    user.facebookID = object.getString("id").toString();
//                                    user.email = object.getString("email").toString();
//                                    user.name = object.getString("name").toString();
//                                    user.gender = object.getString("gender").toString();
//                                    PrefUtils.setCurrentUser(user,LoginActivity.this);
//
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
//                              Toast.makeText(LoginActivity.this,"welcome "+user.name,Toast.LENGTH_LONG).show();
//                                Intent intent=new Intent(LoginActivity.this,LogoutActivity.class);
//                                startActivity(intent);
//                                finish();
//
//                        }
//
//                    });
//
//            Bundle parameters = new Bundle();
//            parameters.putString("fields", "id,name,email,gender, birthday");
//            request.setParameters(parameters);
//            request.executeAsync();

            ///////////////////////////////////////////////

//            Bundle parameters = new Bundle();
//            parameters.putString("message","Nailed It!!!!");
//            GraphRequest request = new GraphRequest(
//                    logr.getAccessToken(),
//                    "/me/feed",
//                    parameters,
//                    HttpMethod.POST,
//                    new GraphRequest.Callback(){
//                        public void onCompleted(GraphResponse response)
//                        {
//                            Log.e("response",response.toString());
//                        }
//                    }
//);
//
//            request.executeAsync();

///////////////////////////////////////////////////////////////////////////////

        }

        @Override
        public void onCancel() {
            progressDialog.dismiss();
        }

        @Override
        public void onError(FacebookException e) {
            progressDialog.dismiss();
        }
    };

}
