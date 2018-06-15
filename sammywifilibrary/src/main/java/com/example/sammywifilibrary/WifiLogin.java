package com.example.sammywifilibrary;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;

/**
 * Created by maduranga on 7/20/17.
 */

public class WifiLogin {
    public static String uamip;
    public static String uamport;
    public static String sip;
    public static String vender;
    public static String qv;
    public static String deviceMac;
    public static String apmac;
    public static String ssid , nasId , clientMac;
    public static String networkSSID = "Cambium_test";

    private static final String BASE_URL = "https://www.vamps.vedicsoft.net/portal/mobile/index.php";

    public static String AutoConectToSSID(Context context, String netssid){
        networkSSID=netssid;
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        //conf.preSharedKey = "\""+ networkPass +"\"";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        deviceMac = getMacAddr().toUpperCase();//info.getMacAddress();
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if (list!=null){
            for( WifiConfiguration i : list ) {
                System.out.print(i);
                if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    //This is the time for hotel check-in and connecting to open wifi and auto log to access point
                    //((Hotel) context).setStatus(0);
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }
        }
        try {
            String value = new ApGateway().execute("").get();
            URL redirectURL = new URL(value);
            Map<String , String> param = splitQuery(redirectURL);
            uamip = param.get("uamip");
            uamport = param.get("uamport");
            sip = param.get("sip");
            vender = param.get("vendor");
            qv = param.get("Qv");
            apmac = param.get("called");
            if(vender.equalsIgnoreCase("cambium")){
                uamip = param.get("ga_srvr");
                ssid = param.get("ga_ssid");
                apmac = param.get("ga_ap_mac");
                nasId = param.get("ga_nas_id");
                clientMac = param.get("ga_cmac");
                qv = param.get("ga_Qv");
            }else if (vender.equalsIgnoreCase("zebra")){
                uamip = param.get("hs_server");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceMac;
    }

    public static boolean isMacExsist(String devicemac){
        boolean result = false;
        final JSONObject jsonObj = new JSONObject();
        //JSONObject loginInfo = new JSONObject();
        try{
            jsonObj.put("action", "checkmac");
            jsonObj.put("apMAC", devicemac);
        }catch (Exception e){
            System.out.println(e);
        }

        ApiOkhttp api = new ApiOkhttp();
        //postCall for login
        api.postCall( BASE_URL, jsonObj.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.print(e);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println(response.body());
                    String responseStr = response.body().string();
                    System.out.println(responseStr);
                    if (responseStr.equalsIgnoreCase("0")){
                        //return true
                    }else {
                        //return false
                    }
                    //loginToGateway("mac".getBytes(), "maddu");

                    // Do what you want to do with the response.
                } else {
                    System.out.print("error");
                    // Request not successful
                }
            }
        });
        return result;
    }

    public static void Login(final JSONObject logindetail){
         try{
            logindetail.put("apMAC", apmac);
            logindetail.put("deviceMAC", normalizeMAC(deviceMac));
        }catch (Exception e){
            System.out.println(e);
        }

        ApiOkhttp api = new ApiOkhttp();
        //postCall for login
        api.postCall( BASE_URL, logindetail.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.print(e);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    //loginToGateway("mac".getBytes(), "maddu");
                    try{
                        JSONObject resObj = new JSONObject(responseStr);
                        JSONObject user =resObj.getJSONObject("user");
                        String username = logindetail.getString("deviceMAC");
                        String password = logindetail.getString("password");
                        loginToGateway(username, password , vender);
                    }catch (JSONException e){
                        System.out.print(e);
                    }
                    System.out.print(responseStr);
                    // Do what you want to do with the response.
                } else {
                    System.out.print("error");
                    // Request not successful
                }
            }
        });
    }

    public static void Signup(final JSONObject signupdetail){

        ApiOkhttp api = new ApiOkhttp();
        //postCall for login
        api.postCall( BASE_URL, signupdetail.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.print(e);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    //loginToGateway("mac".getBytes(), "maddu");
                    try{
                        JSONObject resObj = new JSONObject(responseStr);
                        if(resObj.getString("reason").equalsIgnoreCase("allowed")){
                            String username = normalizeMAC(deviceMac);
                            String password = deviceMac.replace(":","");
                            loginToGateway(username, password , vender);
                        }
                    }catch (JSONException e){
                        System.out.print(e);
                    }
                    System.out.print(responseStr);
                    // Do what you want to do with the response.
                } else {
                    System.out.print("error");
                    // Request not successful
                }
            }
        });
    }

    static void loginToGateway(String username, String password, String vender) {
        try {
            String loginurl ="";
            if (vender.equalsIgnoreCase("ruckus7372") || vender.equalsIgnoreCase("ruckus")){
                URI l1 = appendUri("http://" + uamip+":"+uamport + "/login", "username="+username);
                URI l2 = appendUri(l1.toString(), "password="+password);
                URI l3 = appendUri(l2.toString() , "dst=http://www.vedicsoft.com/");
                loginurl = l3.toString();
                loginRadiusGet(loginurl);
            }else if (vender.equalsIgnoreCase("mkt")){
                URI l1 = appendUri("http://" + sip+ "/login", "username="+username);
                URI l2 = appendUri(l1.toString(), "password="+password);
                URI l3 = appendUri(l2.toString() , "dst=https://www.vamps.vedicsoft.net/portal/0/demo/landing/landing.php");
                loginurl = l3.toString();
                loginRadiusGet(loginurl);
            }else if (vender.equalsIgnoreCase("zebra")){
                URI l1 = appendUri("http://" + uamip+":"+uamport + "/cgi-bin/hslogin.cgi", "username="+username);
                URI l2 = appendUri(l1.toString(), "password="+password);
                URI l4 = appendUri(l2.toString() , "uamip="+uamip);
                URI l3 = appendUri(l4.toString() , "qv="+qv);
                loginurl = l3.toString();
                loginRadiusGet(loginurl);
            }else if(vender.equalsIgnoreCase("cambium")){
                URI l1 = appendUri("http://" + uamip+":880" + "/cgi-bin/hotspot_login.cgi", "ga_ssid="+ssid);
                URI l2 = appendUri(l1.toString(), "ga_ap_mac="+ apmac);
                URI l3 = appendUri(l2.toString() , "ga_nas_id="+nasId);
                URI l4 = appendUri(l3.toString() , "ga_srvr="+uamip);
                URI l5 = appendUri(l4.toString() , "ga_cmac="+clientMac);
                URI l6 = appendUri(l5.toString() , "ga_Qv="+qv);
                URI l7 = appendUri(l6.toString() , "ga_user="+username);
                URI l8 = appendUri(l7.toString() , "ga_pass="+password);

                JSONObject payload = new JSONObject();
                payload.put("password", password);
                payload.put("username", username);
                loginurl = l8.toString();
                loginRadiusPost(l6.toString(), payload);
                //loginRadiusPost(loginurl , payload);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public static void loginRadiusGet(String url){
        ApiOkhttp api = new ApiOkhttp();
        api.getCall(url , new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.print(e);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }

    public static void loginRadiusPost(String url , JSONObject payload){
        ApiOkhttp api = new ApiOkhttp();
        api.postFormData(url, payload , new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.print(e);
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static URI appendUri(String uri, String appendQuery) throws URISyntaxException {
        URI oldUri = new URI(uri);

        String newQuery = oldUri.getQuery();
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery += "&" + appendQuery;
        }

        URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(),
                oldUri.getPath(), newQuery, oldUri.getFragment());

        return newUri;
    }

    public static String normalizeMAC(String mac) {
        byte[] bmac = mac.getBytes();
        if (bmac.length == 12) {
            String x = Arrays.copyOfRange(bmac, 0, 2) + "-" + Arrays.copyOfRange(bmac, 2, 4) + "-" + Arrays.copyOfRange(bmac, 4, 6) + "-" + Arrays.copyOfRange(bmac, 6, 8) + "-" +
                    Arrays.copyOfRange(bmac, 8, 10) + "-" + Arrays.copyOfRange(bmac, 10, 12).toString().toUpperCase();
            return x;
        } else {
            return mac.replaceAll(":", "-").toUpperCase();
        }
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
}


