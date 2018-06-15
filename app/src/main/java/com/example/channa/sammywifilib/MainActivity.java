package com.example.channa.sammywifilib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sammywifilibrary.WifiLogin;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JSONObject obj = new JSONObject();
        try {
            obj.put("location", "mirissa.");
            obj.put("ssid", "office");
            obj.put("userid", 145);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println();
        final String devicemac =WifiLogin.AutoConectToSSID(getApplicationContext() , "Cambium_test");
        new Thread( new Runnable() {
            public void run(){
       //         WifiLogin.isMacExsist(devicemac);
                return; // to stop the thread
            }
        }).start();
       // boolean ismacExsist =WifiLogin.isMacExsist(devicemac);
       // System.out.println(ismacExsist);
        final JSONObject jsonObj = new JSONObject();
        //JSONObject loginInfo = new JSONObject();
        try{
            jsonObj.put("action", "login");
            jsonObj.put("username", "chamil@gmail.com");
            jsonObj.put("password", "0714814700");
        }catch (Exception e){
            System.out.println(e);
        }
        WifiLogin.Login(jsonObj);

        JSONObject sinupJson = new JSONObject();
        JSONObject profile = new JSONObject();
        JSONObject loginInfo = new JSONObject();
        try {
            sinupJson.put("profile", profile);
            sinupJson.put("loginInfo", loginInfo);
            sinupJson.put("action", "signup");
            profile.put("mobileNumber", "0714814700");
            profile.put("password", "0714814700");
            profile.put("email", "chamil@gmail.com");
            profile.put("username", "chamil@gmail.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //WifiLogin.Signup(sinupJson);
    }
}
