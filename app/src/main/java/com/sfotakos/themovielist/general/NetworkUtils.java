package com.sfotakos.themovielist.general;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.sfotakos.themovielist.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by spyridion on 11/10/17.
 */

public class NetworkUtils {
    
    public static final String TMDB_API_KEY_PARAM = "api_key";
    public static final String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;

    public static final String BASE_TMDB_URL = "https://api.themoviedb.org/";
    public static final String TMDB_API_VERSION = "3";

    public static final String BASE_YOUTUBE_URL = "https://www.youtube.com/";
    public static final String YOUTUBE_WATCH_PARAM = "watch";
    public static final String YOUTUBE_VIDEO_ID_PARAM = "v";

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    public static Uri buildYoutubeUri(String videoId){
        return Uri.parse(NetworkUtils.BASE_YOUTUBE_URL).buildUpon()
                .appendPath(YOUTUBE_WATCH_PARAM)
                .appendQueryParameter(YOUTUBE_VIDEO_ID_PARAM, videoId)
                .build();
    }
}
