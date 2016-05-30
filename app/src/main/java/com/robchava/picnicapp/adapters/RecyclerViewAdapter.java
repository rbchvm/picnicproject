package com.robchava.picnicapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.robchava.picnicapp.R;
import com.robchava.picnicapp.product.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * The RecyclerView's adapter.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter {

    /**
     * Reference to the activity.
     */
    final protected Context mContext;

    /**
     * Reference to the OnFragmentInteractionListener.
     */
    final protected View.OnClickListener mOnClickListener;

    /**
     * List of all the products to display.
     */
    final protected List<Product> allProducts = new ArrayList<>();

    /**
     * The adapter constructor.
     * @param context The Activity
     * @param listener The OnFragmentInteractionListener
     */
    public RecyclerViewAdapter(final Context context, final View.OnClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    /**
     * Adds data to the product list.
     * @param newData List of Product objects.
     */
    public void addData(List<Product> newData) {
        allProducts.addAll(newData);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View layout = View.inflate(mContext, R.layout.product_listview_item, null);
        return new MyRecyclerViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Product product = allProducts.get(position);
        final String imagePath = product.getImage();

        final MyRecyclerViewHolder myRecyclerViewHolder = (MyRecyclerViewHolder)holder;
        myRecyclerViewHolder.nameTv.setText(product.getName());
        myRecyclerViewHolder.priceTv.setText(product.getPriceAsString());

        // Download the image using Picasso.
        Picasso.with(mContext).load(imagePath).error(R.drawable.placeholder_error).placeholder(R.drawable.placeholder)
                .into(myRecyclerViewHolder.imageIv);

        myRecyclerViewHolder.wrapper.setTag(product);
        myRecyclerViewHolder.wrapper.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return allProducts.size();
    }


    /**
     * The holder class for the Products displayed in the RecyclerView.
     */
    protected class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

        public final ViewGroup wrapper;
        public final TextView nameTv;
        public final TextView priceTv;
        public final ImageView imageIv;

        public MyRecyclerViewHolder(View itemView) {
            super(itemView);

            wrapper = (ViewGroup)itemView.findViewById(R.id.item_wrapper);
            nameTv = (TextView)itemView.findViewById(R.id.product_name);
            priceTv = (TextView)itemView.findViewById(R.id.product_price);
            imageIv = (ImageView)itemView.findViewById(R.id.product_image);
        }
    }


}