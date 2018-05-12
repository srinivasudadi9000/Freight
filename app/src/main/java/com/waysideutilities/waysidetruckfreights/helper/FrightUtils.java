package com.waysideutilities.waysidetruckfreights.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.waysideutilities.waysidetruckfreights.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by XenoSoft2 on 1/3/2017.
 */
public class FrightUtils {
    public static String getFormattedDate(String srcDateFormat, String targetDateFormat, Object dateString, String timeZone) {
        String targetDate = null;
        SimpleDateFormat targetDtformat = new SimpleDateFormat(targetDateFormat);
        if (timeZone != null)
            targetDtformat.setTimeZone(TimeZone.getTimeZone(timeZone));
        if (srcDateFormat != null) {
            SimpleDateFormat srcDtformat = new SimpleDateFormat(srcDateFormat);
            //srcDtformat.setTimeZone(TimeZone.getDefault());
            try {
                Date date = srcDtformat.parse((String) dateString);
                targetDate = targetDtformat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else
            targetDate = targetDtformat.format((Date) dateString);
        return targetDate;
    }

    public static void setLanguage(String languageToLoad,Context context){
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    public static boolean hasActiveInternetConnection(Activity context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static void SetChoosenLanguage(String languageToLoad,Context context) {
        switch (languageToLoad) {
            case "English":
                FrightUtils.setLanguage("en", context);
                break;
            case "বাঙালি":
                FrightUtils.setLanguage("bn", context);
                break;
            case "ગુજરાતી":
                FrightUtils.setLanguage("gu", context);
                break;
            case "हिंदी":
                FrightUtils.setLanguage("hi", context);
                break;
            case "ಕನ್ನಡ":
                FrightUtils.setLanguage("kn", context);
                break;
            case "മലയാളം":
                FrightUtils.setLanguage("ml", context);
                break;
            case "ਪੰਜਾਬੀ ਦੇ":
                FrightUtils.setLanguage("pa", context);
                break;
            case "తెలుగు":
                FrightUtils.setLanguage("te", context);
                break;
            case "தமிழ்":
                FrightUtils.setLanguage("ta", context);
                break;
            default:
                FrightUtils.setLanguage("en", context);
                break;
        }
    }


    public static String getDirectionsUrl(String origin, String destination,Context context) {
        // Origin of route
        String str_origin = "origin=" + origin.replace(" ", "%20");
        // Destination of route
        String str_dest = "destination=" + destination.replace(" ", "%20");
        // Sensor enabled
        String key = "key=" + context.getResources().getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
        Log.e("googleapis URL ", url);
        return url;
    }

    public static String getDistanceUrl(String origin, String destination,Context context) {
        // Origin of route
        String str_origin = "origins=" + origin.replace(" ", "%20");
        // Destination of route
        String str_dest = "destinations=" + destination.replace(" ", "%20");
        // Sensor enabled
        String key = "key=" + context.getResources().getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&mode=driving?" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?" + parameters;
        Log.e("Distance URL ", url);
        return url;
    }

    public static Spanned getFormatedHtmlString(String text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml("<font color='red'>"+text+"</font>" , Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml("<font color='red'>"+text+"</font>");
        }
    }
}
