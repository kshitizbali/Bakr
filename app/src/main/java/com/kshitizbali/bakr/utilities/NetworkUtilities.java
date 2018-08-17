package com.kshitizbali.bakr.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.kshitizbali.bakr.AppExecutors;
import com.kshitizbali.bakr.database.AppDatabase;
import com.kshitizbali.bakr.database.RecipeEntry;
import com.kshitizbali.bakr.database.RecipeIngredientsEntry;
import com.kshitizbali.bakr.database.RecipeStepsEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class NetworkUtilities {

    private static final String TAG = NetworkUtilities.class.getSimpleName();


    // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
    //Checks for a working internet connection
    public static boolean isOnlineB() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Builds the URL used to talk to the server
     *
     * @param sortBy    The sort order that will be queried for.
     * @param voteCount number of votes (top rated movies).
     * @return The URL to use to query the  server.
     */
    public static URL buildUrl(String sortBy, String voteCount) {
        Uri builtUri = Uri.parse(ConstantUtilities.BASE_URL).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL buildLaunchUrl() {
        Uri builtUri = Uri.parse(ConstantUtilities.BASE_URL).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }


    /**
     * This method returns the string array of movie posters parsed from the main json HTTP response.
     * <p>
     * //@param jsonResponse The HTTP response.
     *
     * @return string array of movie posters parsed from the main json HTTP response.
     */
    /*public static String[] getRecipeIngredientsSteps(String jsonResponse) {

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray)
                JSONObject completeJsonObject = jsonArray.getJSONObject(i);
            JSONArray resultsJsonArray = completeJsonObject.getJSONArray(ConstantUtilities.RECIPE_INGREDIENTS);

            String[] moviePosters = new String[resultsJsonArray.length()];

            for (int i = 0; i < resultsJsonArray.length(); i++) {

                JSONObject movieItem = resultsJsonArray.getJSONObject(i);


                moviePosters[i] = ConstantUtilities.IMAGE_PART_URL + "" + movieItem.getString(ConstantUtilities.POSTER_PATH);
            }

            return moviePosters;
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }


    }*/
    public static void saveRecipeEntryIngreSteps(String jsonReponse, Context mAppCongext) {
        try {
            String FAVOURITE_RECIPE = ConstantUtilities.FAV_NO;
            String RECIPE_IMAGE = ConstantUtilities.TEST_IMAGE;
            final AppDatabase appDatabase = AppDatabase.getInstance(mAppCongext);
            JSONArray jsonArray = new JSONArray(jsonReponse);

            for (int i = 0; i < jsonArray.length(); i++) {


                //Main Array Objects
                JSONObject jsonObjectMain = jsonArray.getJSONObject(i);
                //Store in DB
                final RecipeEntry recipeEntry = new RecipeEntry(
                        jsonObjectMain.getString(ConstantUtilities.NAME),
                        jsonObjectMain.getString(ConstantUtilities.IMAGE),
                        jsonObjectMain.getString(ConstantUtilities.SERVINGS),
                        /*jsonObjectMain.getString(ConstantUtilities.FAVOURITE)*/
                        FAVOURITE_RECIPE
                );

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        appDatabase.recipeDao().insertRecipe(recipeEntry);
                    }
                });
                //Ingredients Array Objects
                JSONArray jsonIngredientsArray = jsonObjectMain.getJSONArray(ConstantUtilities.RECIPE_INGREDIENTS);
                //Store in DB

                for (int j = 0; j < jsonIngredientsArray.length(); j++) {
                    JSONObject jsonObjectIngredients = jsonIngredientsArray.getJSONObject(j);

                    final RecipeIngredientsEntry recipeIngredientsEntry = new RecipeIngredientsEntry(
                            jsonObjectIngredients.getInt(ConstantUtilities.QUANTITY),
                            jsonObjectIngredients.getString(ConstantUtilities.MEASURE),
                            jsonObjectIngredients.getString(ConstantUtilities.INGREDIENTS),
                            jsonObjectMain.getInt(ConstantUtilities.ID)
                    );

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            appDatabase.recipeIngredientsDao().insertRecipeIngredients(recipeIngredientsEntry);
                        }
                    });
                }

                //Steps Array Objects
                JSONArray jsonStepsArray = jsonObjectMain.getJSONArray(ConstantUtilities.RECIPE_STEPS);
                //Store in DB
                for (int k = 0; k < jsonStepsArray.length(); k++) {
                    JSONObject jsonObjectSteps = jsonStepsArray.getJSONObject(k);

                    final RecipeStepsEntry recipeStepsEntry = new RecipeStepsEntry(
                            jsonObjectSteps.getString(ConstantUtilities.SHORT_DESC),
                            jsonObjectSteps.getString(ConstantUtilities.DESCRIPTION),
                            jsonObjectSteps.getString(ConstantUtilities.VIDEO_URL),
                            jsonObjectSteps.getString(ConstantUtilities.THUMBNAIL_URL),
                            jsonObjectMain.getInt(ConstantUtilities.ID));

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            appDatabase.recipeStepsDao().insertRecipeSteps(recipeStepsEntry);
                        }
                    });
                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method returns the a particular json values parsed from the main json HTTP response. for ex title, rating etc
     * <p>
     * //@param jsonResponse The HTTP response.
     * //@param position     position of the item in the json array.
     * //@param jsonKey      key of the item to be extracted from the json response.
     *
     * @return string array of movie posters parsed from the main json HTTP response.
     */
   /* public static String getJsonValue(String jsonResponse, int position, String jsonKey) {


        try {
            JSONObject completeJsonObject = new JSONObject(jsonResponse);
            JSONArray resultsJsonArray = completeJsonObject.getJSONArray(ConstantUtilities.RESULTS);
            JSONObject movieItem = resultsJsonArray.getJSONObject(position);

            return movieItem.getString(jsonKey);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

    }*/
    public static void initializeStetho(Context context) {

        Stetho.initialize(Stetho.newInitializerBuilder(context)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                .build());
    }


    /*
     * Checks if WiFi or 3G is enabled or not. server
     */
    public static boolean isInternetAvailable(Context context) {
        return isWiFiAvailable(context) || isMobileDateAvailable(context);
    }

    /**
     * Checks if the WiFi is enabled on user's device
     */
    public static boolean isWiFiAvailable(Context context) {
        // ConnectivityManager is used to check available wifi network.
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = connectivityManager.getActiveNetworkInfo();
        // Wifi network is available.
        return network_info != null
                && network_info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Checks if the mobile data is enabled on user's device
     */
    public static boolean isMobileDateAvailable(Context context) {
        // ConnectivityManager is used to check available 3G network.
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        // 3G network is available.
        return networkInfo != null
                && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }


}
