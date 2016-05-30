package com.robchava.picnicapp.product;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * This object represents any product.
 */
public class Product implements Serializable {

    public static final String PRODUCT_LIST_KEY = "products";

    @SerializedName("product_id")
    protected String productId;

    @SerializedName("name")
    protected String name  ;

    @SerializedName("price")
    protected int price;

    @SerializedName("image")
    protected String image;

    @SerializedName("description")
    protected String description;


    /**
     * Returns the product ID.
     * @return Product ID
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Returns the product name.
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the product price as retrieved from the API.
     * @return price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Returns a nicely formatted price.
     * @return Nicely formatted price.
     */
    public String getPriceAsString() {
        int productPrice = getPrice();
        float priceFloat = (float)productPrice / 100f;
        return "€" + priceFloat;
    }

    /**
     * Returns the product image URL.
     * @return image URL.
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns the product description.
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "productId:" + productId + " " +
                "name:" + name + " " +
                "price:" + price + " " +
                "description:" + description;
    }
}
