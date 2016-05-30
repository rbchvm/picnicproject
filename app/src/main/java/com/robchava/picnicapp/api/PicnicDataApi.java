package com.robchava.picnicapp.api;

import com.robchava.picnicapp.product.Product;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Here I've defined the endpoints for use with Retrofit.
 */
public interface PicnicDataApi {

    /**
     * The base URL.
     */
    String API_BASE_URL = "https://s3-eu-west-1.amazonaws.com/developer-application-test/cart/";

    /**
     * The URLs for the full listing.
     */
    String PRODUCT_LISTING_ENDPOINT = "list";
    @GET(PRODUCT_LISTING_ENDPOINT)
    Call<HashMap<String, ArrayList<Product>>> getAllProducts();

    /**
     * The URLs for the product detail.
     */
    String PRODUCT_DETAIL_ENDPOINT = "{product_id}/detail";
    @GET(PRODUCT_DETAIL_ENDPOINT)
    Call<Product> getProductDetail(@Path("product_id") String product_id);

}
