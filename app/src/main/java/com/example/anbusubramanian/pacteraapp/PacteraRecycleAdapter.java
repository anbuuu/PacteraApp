package com.example.anbusubramanian.pacteraapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by anbu.subramanian on 16/09/15.
 */
public class PacteraRecycleAdapter extends RecyclerView.Adapter<PacteraRecycleAdapter.MyViewHolder>
{
    private List<JsonDataItem> feedItemList;
    private Context mContext;
    private final static String TAG = PacteraRecycleAdapter.class.getSimpleName();

    public PacteraRecycleAdapter(Context context, List<JsonDataItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
        JsonDataItem feedItem = feedItemList.get(i);



            // Setting Text View Title and Description
            if  ( feedItem.getItemTitle() != null )
            {
                myViewHolder.titleView.setText(Html.fromHtml(feedItem.getItemTitle()));
            }

            if ( feedItem.getDescription() != null) {
                myViewHolder.descriptionView.setText(Html.fromHtml(feedItem.getDescription()));
            }

            //Download image using picasso library
            if ( feedItem.getItemThumbnail() != null )
            {
                Log.d(TAG, "The Thumbnail URL is " + feedItem.getItemThumbnail());
                Picasso.with(mContext).load(feedItem.getItemThumbnail())
                        .error(R.drawable.pactera)
                        .placeholder(null)
                        .into(myViewHolder.imageThumbnailView);
            }

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageThumbnailView;
        protected TextView titleView;
        protected TextView descriptionView;

        public MyViewHolder(View view) {
            super(view);
            this.descriptionView = ( TextView ) view.findViewById(R.id.rowDescription);
            this.imageThumbnailView = ( ImageView ) view.findViewById(R.id.imageHref);
            this.titleView = (TextView) view.findViewById(R.id.rowTitle);

        }
    }

}
