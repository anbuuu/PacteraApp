package com.example.anbusubramanian.pacteraapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import util.ConnectivityReceiver;
import util.UrlEndpoints;

public class PacteraMainActivity extends AppCompatActivity
{

    private static final String TAG = "PacteraApp";
    private List<JsonDataItem> feedsList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private PacteraRecycleAdapter adapter;
    private ProgressBar progressBar;
    private final static String JSONURL = UrlEndpoints.PACTERA_URL;
    private List<JsonDataItem> jsonDataItemList = new ArrayList<JsonDataItem>();
    private ProgressDialog loadingDialog;
    private String actionBarTitle = String.valueOf(R.string.app_name);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_list);

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver(connectivityManager);

        // If there is no connectivity, display to the user
        if ( !connectivityReceiver.isOnline())
        {
            showNoNetworkDialog();
        }



        // Recycler View Initialization -- used recycler view instead of list view
        // The main advantages are reuse cells while scroll up/down, can put
        // item seasily in the different containers and animations are delegated to itemanimator
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        new AsyncHttpTask().execute(JSONURL);

        // Swipe to Refresh Functionality and sending isRefreshing to true
        // so that while refreshing the UI Thread is not blocked
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshData);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh()
            {
                AsyncHttpTask asyncHttpTask = new AsyncHttpTask();
                asyncHttpTask.isRefreshing = true;
                asyncHttpTask.execute(JSONURL);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Async Task to Fetch data from the provided JSON URL
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer>
    {
        boolean isRefreshing;
        public AsyncHttpTask() {

        }
        @Override
        protected void onPreExecute() {
            if (!isRefreshing) {
                loadingDialog = ProgressDialog.show(PacteraMainActivity.this,
                        "Downloading Content", "Please Wait .....");
            }

        }

        @Override
        protected Integer doInBackground(String... params) {

            InputStream inputStream = null;
            Integer result = 0;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                // Initiate GET Request
                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();

                // Check if the status Code is Ok,
                if (statusCode ==  200) {

                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }

                    parseResult(response.toString());
                    result = 1; // Successful
                }else{
                    result = 0; //"Failed to fetch data!";
                }

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            loadingDialog.dismiss();
            if (mSwipeRefreshLayout.isRefreshing()) {
                isRefreshing = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }

                /* Download complete. Lets update UI */
            if (result == 1) {
                adapter = new PacteraRecycleAdapter(PacteraMainActivity.this, jsonDataItemList);
                mRecyclerView.setAdapter(adapter);
            } else {
                showNoNetworkDialog();
                Log.e(TAG, "Failed to fetch Data from the JSON URL");
            }
        }
    }

    private void parseResult(String result) {
        try {

            JSONObject response = new JSONObject(result);
            Log.d(TAG, "The JSON Response is " + response);
            actionBarTitle = response.optString("title");
            Log.d(TAG, "The Title is " + actionBarTitle);
            updateTitle(actionBarTitle);


            JSONArray posts = response.optJSONArray("rows");

            // Initialize if the Array is Null
            if (null == jsonDataItemList) {
                jsonDataItemList = new ArrayList<JsonDataItem>();
            }

            // Loop through JSON Object to fetch Item Title and Description and image Ref
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);

                JsonDataItem item = new JsonDataItem();

                if (!post.optString("title").equals("null")) {
                    item.setItemTitle(post.optString("title"));
                    item.setItemThumbnail(post.optString("imageHref"));
                    item.setDescription(post.optString("description"));
                    Log.d(TAG, "The Title is " + post.optString("title") +
                                    " and thumbnail is " + post.optString("imageHref") +
                                    " and Description is " + post.optString("description")
                    );
                    jsonDataItemList.add(item);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Update Action Bar Title ..
    private void updateTitle(final String actionBarTitle)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                getSupportActionBar().setTitle(actionBarTitle);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_about)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void showNoNetworkDialog() {
           // If No Network is present / Timeout
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Connection")
                    .setMessage("No Internet Connection Found .. Exiting ")
                    .setCancelable(false)
                    .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            System.exit(0);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
    }


    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        Log.d(TAG, "onSavedInstance");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstance");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
