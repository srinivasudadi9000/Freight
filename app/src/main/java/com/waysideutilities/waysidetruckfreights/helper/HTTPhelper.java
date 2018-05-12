package com.waysideutilities.waysidetruckfreights.helper;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Archana on 8/29/2016.
 */
public class HTTPhelper {
    private InputStream inputStream;

    public String makeHttpPostRequest(String url, String number, int code) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        String _response = null;
        try {
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("number", number));
            nameValuePair.add(new BasicNameValuePair("message", "Verification code for WaysideTruckFreight for login is " + code));
            System.out.println(url+" "+nameValuePair.toString());
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            // write response to log
            _response = EntityUtils.toString(resEntity);
          //  Log.d("Http Post Response:", response.getEntity().toString());
            Log.d("Http Post Response:", _response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return _response;
    }

    public String makeHttpPostRequest(String url, String number, String message) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        String _response = null;
        try {
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("number", number));
            nameValuePair.add(new BasicNameValuePair("message",message));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            // write response to log
            Log.e("Http Post Response:", response.getEntity().toString());
            _response = EntityUtils.toString(resEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return _response;
    }

    public String generateCheckSumPostRequest(String amount, String number, String orderId, String email) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Constants.CHECKSUM_GENERATION_URL);

        String _response = null;
        try {
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);

            nameValuePair.add(new BasicNameValuePair("ORDER_ID",orderId));
            nameValuePair.add(new BasicNameValuePair("TXN_AMOUNT",amount));
            nameValuePair.add(new BasicNameValuePair("EMAIL", email));
            nameValuePair.add(new BasicNameValuePair("MOBILE_NO",number));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            // write response to log
            _response = EntityUtils.toString(resEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
       // Log.e("CheckSum Response:", _response);

        return _response;
    }

    public InputStream makeHttpRequest(String url, String method, String requeststring) throws JSONException {
        try {
            if (method == "GET") {
                HttpClient httpclient = new DefaultHttpClient();
                url += "?" + requeststring;
                Log.e("GET =======>", url);
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpclient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
            }
        } catch (UnsupportedEncodingException e) {
            Log.e("Unsupportedception", e.getMessage().toString());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", e.getMessage().toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException", e.getMessage().toString());
            e.printStackTrace();
        }
        return inputStream;
    }
}
