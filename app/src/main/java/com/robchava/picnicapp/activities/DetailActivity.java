package com.robchava.picnicapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.robchava.picnicapp.R;
import com.robchava.picnicapp.api.PicnicDataApi;
import com.robchava.picnicapp.product.Product;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This activity requests the data via the API endpoint using an Asynctask and displays
 * the specific product info.
 */
public class DetailActivity extends BaseActivity {

    /**
     * References to the UI widgets being displayed.
     */
    protected TextView nameTv;
    protected TextView descriptionTv;
    protected TextView priceTv;
    protected ImageView imageIv;

    /**
     * The Product that's being displayed.
     */
    protected Product mProduct;

    /**
     * The key used to get the product ID from the extras.
     */
    public static final String PRODUCT_ID_EXTRA = "product_id_extra";

    /**
     * The key used to get the Product from the Bundle.
     */
    private final String PRODUCT_OBJECT = "product_object";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a reference to the widgets
        nameTv = (TextView)findViewById(R.id.product_name);
        descriptionTv = (TextView)findViewById(R.id.product_description);
        priceTv = (TextView)findViewById(R.id.product_price);
        imageIv = (ImageView)findViewById(R.id.product_image);

        // See if we already have the Product object.
        if (savedInstanceState != null) {
            Serializable serializable = savedInstanceState.getSerializable(PRODUCT_OBJECT);
            if (serializable != null && serializable instanceof Product) {
                mProduct = (Product) serializable;
            }
        }

        if (mProduct != null) {

            // If we already had the Product object, just display it.
            updateProductDetail(mProduct);

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
                final String productId = getIntent().getStringExtra(PRODUCT_ID_EXTRA);
                runningAsyncTask = new DetailDownloadAsyncTask(this, productId);
                runningAsyncTask.execute(null, null, null);
            }
        }
    }

    /**
     * If we have a Product object, try to save it so I don't have to reload it.
     * from the network.
     *
     * @param outState Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the existing Product.
        outState.putSerializable(PRODUCT_OBJECT, mProduct);
    }

    /**
     * Updates the UI with the new product info. The image is loaded using Picasso.
     */
    protected void updateProductDetail(Product product) {

        // Assign to mProduct
        mProduct = product;

        // Update the UI.
        nameTv.setText(mProduct.getName());
        descriptionTv.setText(mProduct.getDescription());
        priceTv.setText(mProduct.getPriceAsString());
        priceTv.setVisibility(View.VISIBLE);

        // Load the image using Picasso
        final String imagePath = mProduct.getImage();
        Picasso.with(this).load(imagePath).error(R.drawable.placeholder_error)
                .placeholder(R.drawable.placeholder).into(imageIv);

        // Hide the loading dialog if it's open.
        hideLoadingDialog();
    }

    /**
     * A static method used to create a new DetailActivity activity.
     */
    public static void showDetail(final Context context, final String productId) {
        final Intent showDetail = new Intent(context, DetailActivity.class);
        showDetail.putExtra(DetailActivity.PRODUCT_ID_EXTRA, productId);
        context.startActivity(showDetail);
    }

    /**
     * This is the Asynctask which downloads the product detail.
     */
    protected static class DetailDownloadAsyncTask extends BaseDownloadAsyncTask {

        /**
         * The product ID I want to download.
         */
        protected final String mProductId;

        /**
         * Constructor which assigns the ID and keeps the reference to the activity.
         */
        public DetailDownloadAsyncTask(final DetailActivity detailActivity, final String productId) {
            mProductId = productId;
            attachActivity(detailActivity);
        }

        /**
         * Do long work here. This includes hitting the endpoint and waiting for the data.
         */
        @Override
        protected Product doInBackground(Void ...v) {

            final Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(PicnicDataApi.API_BASE_URL)
                    .build();

            final PicnicDataApi picnicDataApi = retrofit.create(PicnicDataApi.class);

            Object returnObject = null;

            try {
                final Call<Product> call = picnicDataApi.getProductDetail(mProductId);
                final Response<Product> response = call.execute();
                returnObject = response.body();
            } catch(IllegalArgumentException | IOException e) { //Java 7 multicatch
                e.printStackTrace();
            }

            return (Product) returnObject;
        }

        /**
         * Update the UI on the main thread.
         */
        @Override
        protected void onPostExecute(final Object productObject) {

            // Due to some change (for example an orientation change) we currently don't
            // have a valid reference to the activity, so we can't ping back.
            if (mActivity == null) {
                return;
            }

            final Product product = (Product)productObject;

            if (product == null) {

                // If for some reason we're getting a null object, show an error dialog
                // and finish() this activity.
                mActivity.hideLoadingDialog();
                mActivity.showOkDialog(R.string.data_download_error);

            } else {

                // Update the UI.
                ((DetailActivity)mActivity).updateProductDetail(product);
                mActivity.hideLoadingDialog();
            }

            runningAsyncTask = null;

        }

    }
}
