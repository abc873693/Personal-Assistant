package rainvisitor.personal_assistant.libs;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rainvisitor.personal_assistant.R;

/**
 * Created by Ray on 2016/12/20.
 */

public class Utils {
    public static int REQUEST_LOCATION = 9487;
    public static int RESULT_LOCATION = 87;

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void openCustomTabs(Context context, Resources resources, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        Bitmap icon = BitmapFactory
                .decodeResource(resources, R.drawable.ic_menu_share);
        builder.setActionButton(icon, "share", Utils.createSharePendingIntent(context, url));
        builder.setToolbarColor(
                ContextCompat.getColor(context, R.color.blue_300));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context
                , Uri.parse(url));
    }

    public static PendingIntent createSharePendingIntent(Context context, String content) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        return PendingIntent.getActivity(context, 0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public static Drawable getSelectableItemBackgroundDrawable(Context context) {
        return ContextCompat.getDrawable(context, getSelectableItemBackgroundResource(context));
    }

    public static int getSelectableItemBackgroundResource(Context context) {
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int resourceId = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        return resourceId;
    }

    public static Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static boolean checkGPSisOpen(Context context) {
        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static void startNavigationActivity(Context context, double startLatitude,
                                               double startLongitude, double endLatitude,
                                               double endLongitude) {
        String saddr = "saddr=" + startLatitude + "," + startLongitude;
        String daddr = "daddr=" + endLatitude + "," + endLongitude;
        String uriString = "http://maps.google.com/maps?" + saddr + "&" + daddr;

        Uri uri = Uri.parse(uriString);

        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            intent.setClassName("com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity");
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }

    public static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();
        os.close();
        return state;
    }

    public static boolean isNear(LatLng a, LatLng b) {
        Double pa = 0.025;
        if (Math.sqrt(Math.pow(a.latitude - b.latitude, 2.0) + Math.pow(a.longitude - b.longitude, 2.0)) <= pa) {
            return true;
        }
        else return false;
    }
}
