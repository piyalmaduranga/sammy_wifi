package com.example.sammywifilibrary;

/**
 * Created by maduranga on 7/18/17.
 */

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;


public class ApGateway extends AsyncTask<String, Void, String> {

    private Exception exception;

    protected String doInBackground(String... urls) {

        try {

            String url = "http://adaderana.lk/";

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setReadTimeout(5000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");

            boolean redirect = false;

            // normally, 3xx is redirect
            int status = conn.getResponseCode();
            if (status !=  HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status ==  HttpURLConnection.HTTP_MOVED_PERM
                        || status ==  HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            System.out.println("Response Code ... " + status);

            if (redirect) {

                String requestUrl = conn.getHeaderField("Location");
                String cookies = conn.getHeaderField("Set-Cookie");

                // open the new connnection again
                conn = (HttpURLConnection) new URL(requestUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                conn.addRequestProperty("User-Agent", "Mozilla");
                conn.addRequestProperty("Referer", "google.com");

                System.out.println("Redirect to URL : " + requestUrl);
                return requestUrl;

            }

            String requestUrl =  conn.getURL().toString();

            return requestUrl;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }

}