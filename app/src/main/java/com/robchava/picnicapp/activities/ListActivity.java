package com.robchava.picnicapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.robchava.picnicapp.R;
import com.robchava.picnicapp.adapters.RecyclerViewAdapter;
import com.robchava.picnicapp.api.PicnicDataApi;
import com.robchava.picnicapp.product.Product;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * This activity requests the data via the API endpoint using an Asynctask and displays
 * it as a grid using a RecyclerView.
 */
public class ListActivity extends BaseActivity implements View.OnClickListener {

    /**
     * The Product list that's being displayed.
     */
    protected ArrayList<Product> mProductList;

    /**
     * The key used to get the Product list from the Bundle.
     */
    private final String PRODUCT_LIST_OBJECT = "product_list_object";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // See if we already have the Product object.
        if (savedInstanceState != null) {
            Serializable serializable = savedInstanceState.getSerializable(PRODUCT_LIST_OBJECT);
            if (serializable != null && serializable instanceof ArrayList) {
                mProductList = (ArrayList<Product>) serializable;
            }
        }

        if (mProductList != null) {

            // If we already had the Product list object, just display it.
            updateProductList(mProductList);

        } else {

            // There's already a running asynctask
            if (runningAsyncTask != null && runningAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING)) {

                // Send over the current activity to use to ping the UI when finished.
                runningAsyncTask.attachActivity(this);

            } else {

                // Show a loading dialog while everything is being downloaded and setup.
                showLoadingDialog();

                // Request the product list via the API. This starts an Asynctask which
                // downloads the data in another thread, then pings the activity when it is done.
                runningAsyncTask = new ListDownloadAsyncTask(this);
                runningAsyncTask.execute(null, null, null);
            }
        }
    }

    /**
     * If we have a Product list object, try to save it so I don't have to reload it.
     * from the network.
     *
     * @param outState Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the existing Product.
        outState.putSerializable(PRODUCT_LIST_OBJECT, mProductList);
    }

    /**
     * Here I update the product list from the retrieved data.
     */
    protected void updateProductList(ArrayList<Product> productList) {

        // Assign to mProductList
        mProductList = productList;

        // Create an adapter, sending as parameters the context and the OnClickListener.
        final RecyclerViewAdapter mRecyclerViewAdapter = new RecyclerViewAdapter(this, this);
        mRecyclerViewAdapter.addData(productList);

        // Get a reference to the RecyclerView
        final RecyclerView mRecyclerview = (RecyclerView)findViewById(R.id.recyclerview);

        // Set the adapter and the layout manager. I'm using a GridLayoutManager.
        mRecyclerview.setAdapter(mRecyclerViewAdapter);
        mRecyclerview.setLayoutManager(new GridLayoutManager(this, 2));

        // Hide the loading dialog if it's open.
        hideLoadingDialog();
    }

    /**
     * Here the RecyclerView clicks are handled. When clicked, check if the view has
     * a Product tag assigned to it and if so start the DetailActivity.
     */
    @Override
    public void onClick(View v) {

        final Object tag = v.getTag();

        if (tag instanceof Product) {
            final Product product = (Product)tag;
            DetailActivity.showDetail(this, product.getProductId());
        }
    }

    /**
     * This is the Asynctask which downloads the product list. I'm using a simple
     * Retrofit method.
     */
    protected static class ListDownloadAsyncTask extends BaseDownloadAsyncTask {

        /**
         * Constructor
         * @param listActivity activity to attach to this Asynctask.
         */
        public ListDownloadAsyncTask(final ListActivity listActivity) {
            attachActivity(listActivity);
        }

        /**
         * Do long work here. This includes hitting the endpoint and waiting for the data.
         */
        @Override
        protected Object doInBackground(Void ...v) {

            final Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(PicnicDataApi.API_BASE_URL)
                    .build();

            final PicnicDataApi picnicDataApi = retrofit.create(PicnicDataApi.class);

            Object returnObject = null;

            try {
                final Call<HashMap<String, ArrayList<Product>>> call = picnicDataApi.getAllProducts();
                final Response<HashMap<String, ArrayList<Product>>> response = call.execute();
                returnObject = response.body();
            } catch(IllegalArgumentException | IOException e) { //Java 7 multicatch
                e.printStackTrace();
            }

            if (returnObject != null) {
                final HashMap<String, ArrayList<Product>> allData = (HashMap<String, ArrayList<Product>>) returnObject;
                return allData.get(Product.PRODUCT_LIST_KEY);
            } else {
                return null;
            }
        }

        /**
         * Update the UI on the main thread.
         */
        @Override
        protected void onPostExecute(final Object productListObject) {

            // Due to some change (for example an orientation change) we currently don't
            // have a valid reference to the activity, so we can't ping back.
            if (mActivity == null) {
                return;
            }

            final ArrayList<Product> productList = (ArrayList<Product>)productListObject;

            if (productList == null) {

                // If for some reason we're getting a null object, show an error dialog
                // and finish() this activity.
                mActivity.hideLoadingDialog();
                mActivity.showOkDialog(R.string.data_download_error);

            } else {

                // Update the UI.
                ((ListActivity)mActivity).updateProductList(productList);
                mActivity.hideLoadingDialog();
            }

            runningAsyncTask = null;

        }
    }

}
