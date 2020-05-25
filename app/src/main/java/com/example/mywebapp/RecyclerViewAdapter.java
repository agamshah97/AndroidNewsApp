package com.example.mywebapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    Dialog mydialog;
    List<Newscard> mNewscard;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public RecyclerViewAdapter(Context mContext, List<Newscard> mNewscard) {
        this.mContext = mContext;
        this.mNewscard = mNewscard;
        pref = this.mContext.getSharedPreferences("MyPref", 0);
        editor = pref.edit();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.newscard, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mydialog = new Dialog(mContext);
        mydialog.setContentView(R.layout.newsdialog);

        final Gson gson = new Gson();

        vHolder.iv_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Bookmark clicked : "+String.valueOf(vHolder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                Newscard news_item = mNewscard.get(vHolder.getAdapterPosition());
                String id = news_item.getId();

                if(pref.getString(id, null) != null) {

                    editor.remove(id);
                    editor.commit();

                    Newscard news_item_copy = gson.fromJson(pref.getString(id, null), Newscard.class);
                    Log.d("Bookmarks", "Removing from bookmarks : "+news_item_copy);
                    Toast.makeText(mContext, news_item.getTitle() + " was removed from bookmarks", Toast.LENGTH_SHORT).show();

                    notifyDataSetChanged();
                    vHolder.iv_bookmark.setImageResource(R.drawable.ic_bookmark_empty);
                }
                else {

                    editor.putString(id, gson.toJson(news_item));
                    editor.commit();

                    Log.d("Bookmarks","Adding to bookmarks : "+gson.toJson(news_item));
                    Toast.makeText(mContext, news_item.getTitle() + " was added to bookmarks", Toast.LENGTH_SHORT).show();

                    notifyDataSetChanged();
                    vHolder.iv_bookmark.setImageResource(R.drawable.ic_bookmark_filled);
                }
            }
        });

        vHolder.newscard_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "News card clicked : "+String.valueOf(vHolder.getAdapterPosition()), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, DetailedCardActivity.class);
                intent.putExtra("ArticleId", mNewscard.get(vHolder.getAdapterPosition()).getId());
                mContext.startActivity(intent);
            }
        });

        vHolder.newscard_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Toast.makeText(mContext, "News card Long Clicked : "+String.valueOf(vHolder.getAdapterPosition()), Toast.LENGTH_LONG).show();

                TextView dialog_title = (TextView) mydialog.findViewById(R.id.dialog_title);
                ImageView dialog_img = (ImageView) mydialog.findViewById(R.id.dialog_img);
                ImageView dialog_bookmark = (ImageView) mydialog.findViewById(R.id.dialog_bookmark);

                Newscard news_item = mNewscard.get(vHolder.getAdapterPosition());
                dialog_title.setText(news_item.getTitle());
                Picasso.with(mContext).load(news_item.getImage()).fit().into(dialog_img);

                if(pref.getString(news_item.getId(), null) != null) {
                    dialog_bookmark.setImageResource(R.drawable.ic_bookmark_filled);
                }

                else {
                    dialog_bookmark.setImageResource(R.drawable.ic_bookmark_empty);
                }

                ImageView twitter_button = (ImageView) mydialog.findViewById(R.id.dialog_twitter);
                ImageView bookmark_button = (ImageView) mydialog.findViewById(R.id.dialog_bookmark);

                twitter_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(mContext, "Twitter Button clicked on : "+vHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        Intent tweet = new Intent(Intent.ACTION_VIEW);
                        tweet.setData(Uri.parse("https://twitter.com/intent/tweet"+"?url="+mNewscard.get(vHolder.getAdapterPosition()).getUrl()+"&hashtags=CSCI_571_NewsApp&text=Check out this link : "));
                        mContext.startActivity(tweet);
                    }
                });

                bookmark_button.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View view) {
                        //Toast.makeText(mContext, "Bookmark Button clicked : "+vHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        try {

                            Newscard news_item = mNewscard.get(vHolder.getAdapterPosition());

                            if (pref.getString(news_item.getId(), null) != null) {

                                editor.remove(news_item.getId());
                                editor.commit();

                                Toast.makeText(mContext, news_item.getTitle() + " was removed from bookmarks", Toast.LENGTH_SHORT).show();
                                Log.d("Bookmarks", "Removing from bookmarks : " + news_item);

                                vHolder.iv_bookmark.setImageResource(R.drawable.ic_bookmark_empty);
                                ImageView bookmark_button = (ImageView) mydialog.findViewById(R.id.dialog_bookmark);
                                bookmark_button.setImageResource(R.drawable.ic_bookmark_empty);

                                notifyDataSetChanged();
                            } else {

                                editor.putString(news_item.getId(), gson.toJson(news_item));
                                editor.commit();

                                Log.d("Bookmarks", "Adding to bookmarks : " + gson.toJson(news_item));
                                Toast.makeText(mContext, news_item.getTitle() + " was added to bookmarks", Toast.LENGTH_SHORT).show();

                                vHolder.iv_bookmark.setImageResource(R.drawable.ic_bookmark_filled);
                                ImageView bookmark_button = (ImageView) mydialog.findViewById(R.id.dialog_bookmark);
                                bookmark_button.setImageResource(R.drawable.ic_bookmark_filled);

                                notifyDataSetChanged();
                            }
                        }catch (Exception e) {
                            Toast.makeText(mContext, "Article not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mydialog.show();
                return true;
            }
        });

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.tv_title.setText(mNewscard.get(position).getTitle());
        holder.tv_section.setText(mNewscard.get(position).getSection());

        String date = mNewscard.get(position).getTime();

        LocalDateTime published = LocalDateTime.ofInstant(Instant.parse(date), ZoneId.of("America/Los_Angeles"));
        LocalDateTime current = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(published, current);
        long hours = ChronoUnit.HOURS.between(published, current);
        long seconds = ChronoUnit.SECONDS.between(published, current);
        long days = ChronoUnit.DAYS.between(published, current);

        String write_date = "";
        if (days > 0)
            write_date = days + "d ago";
        else if(hours > 0)
            write_date = hours + " h ago";
        else if(minutes > 0)
            write_date = minutes + " m ago";
        else
            write_date = seconds + " s ago";

        //Log.d("Date", "Article : "+published+", Current : "+current+" Difference : ("+hours+","+minutes+","+seconds+")");

        holder.tv_time.setText(write_date);
        Picasso.with(mContext).load(mNewscard.get(position).getImage()).fit().into(holder.iv_image);

        SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0);
        if(pref.getString(mNewscard.get(position).getId(), null) != null){
            holder.iv_bookmark.setImageResource(R.drawable.ic_bookmark_filled);
        }

        else {
            holder.iv_bookmark.setImageResource(R.drawable.ic_bookmark_empty);
        }

    }



    @Override
    public int getItemCount() {
        return mNewscard.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout newscard_item;
        private TextView tv_title;
        private TextView tv_section;
        private TextView tv_time;
        private ImageView iv_image;
        private ImageView iv_bookmark;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            newscard_item = (LinearLayout) itemView.findViewById(R.id.newscard_id);
            tv_title = (TextView) itemView.findViewById(R.id.newscard_title);
            tv_section = (TextView) itemView.findViewById(R.id.newscard_section);
            tv_time = (TextView) itemView.findViewById(R.id.newscard_time);
            iv_image = (ImageView) itemView.findViewById(R.id.newscard_img);
            iv_bookmark = (ImageView) itemView.findViewById(R.id.newscard_bookmark);
        }
    }

}
