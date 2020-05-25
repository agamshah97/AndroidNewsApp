package com.example.mywebapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DetailedCardActivity extends AppCompatActivity {

    private RequestQueue MyRequestQueue;
    private String id;
    private Newscard newscard;
    private String urlnews = "https://newsapp-backend-99.appspot.com/article?paper=0&id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setContentView(R.layout.activity_detailed_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        id = intent.getExtras().getString("ArticleId");

        //Toast.makeText(getApplicationContext(), "Article ID : "+id, Toast.LENGTH_SHORT).show();
        urlnews = urlnews + id;
        ProgressBar spinner = (ProgressBar) findViewById(R.id.progress_bar_article);
        findViewById(R.id.detailed_article_card).setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
        findViewById(R.id.progress_text_article).setVisibility(View.VISIBLE);
        getData();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProgressBar spinner = (ProgressBar) findViewById(R.id.progress_bar_article);
        findViewById(R.id.detailed_article_card).setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
        findViewById(R.id.progress_text_article).setVisibility(View.VISIBLE);
        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_nav_menu, menu);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        if(pref.getString(id, null) != null) {
            MenuItem menuItem = menu.findItem(R.id.article_bookmark);
            menuItem.setIcon(R.drawable.ic_bookmark_filled);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.article_bookmark:
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                if(pref.getString(id, null) != null) {
                    editor.remove(id);
                    editor.commit();
                    item.setIcon(R.drawable.ic_bookmark_empty);
                    Toast.makeText(getApplicationContext(), newscard.getTitle() + " was removed from bookmarks", Toast.LENGTH_SHORT).show();
                }
                else {
                    Gson gson = new Gson();
                    editor.putString(id, gson.toJson(newscard));
                    editor.commit();
                    item.setIcon(R.drawable.ic_bookmark_filled);
                    Toast.makeText(getApplicationContext(), newscard.getTitle() + " was added to bookmarks", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.article_twitter:
                //Toast.makeText(getApplicationContext(), "Twitter selected", Toast.LENGTH_SHORT).show();
                Intent tweet = new Intent(Intent.ACTION_VIEW);
                tweet.setData(Uri.parse("https://twitter.com/intent/tweet"+"?url="+newscard.getUrl()+"&hashtags=CSCI_571_NewsApp&text=Check out this link : "));
                startActivity(tweet);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getData()
    {
        MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequestNews = new JsonObjectRequest(Request.Method.GET, urlnews, null, new Response.Listener<JSONObject>()  {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("Home", response.toString());
                try {

                    JSONObject article = response.getJSONObject("article");
                    String title = article.get("title").toString();
                    String image_url = article.get("image").toString();
                    String section = article.get("section").toString();
                    String description = article.get("description").toString();
                    String date = article.get("date").toString();
                    String id = article.get("id").toString();
                    String url = article.get("url").toString();

                    newscard = new Newscard(title, image_url, section, date, url, id);

                    TextView article_title = (TextView) findViewById(R.id.detailed_title);
                    TextView article_section = (TextView) findViewById(R.id.detailed_section);
                    TextView article_date = (TextView) findViewById(R.id.detailed_date);
                    TextView article_desc = (TextView) findViewById(R.id.detailed_desc);
                    TextView article_url = (TextView) findViewById(R.id.detailed_link);
                    ImageView article_img = (ImageView) findViewById(R.id.detailed_img);

                    LocalDateTime published = LocalDateTime.ofInstant(Instant.parse(date), ZoneId.of("America/Los_Angeles"));
                    DateTimeFormatter write_date_format = DateTimeFormatter.ofPattern("dd MMM yyyy");

                    article_title.setText(title);
                    article_section.setText(section);
                    article_date.setText(write_date_format.format(published));
                    article_desc.setText(description);
                    Picasso.with(getApplicationContext()).load(image_url).fit().into(article_img);

                    article_url.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    article_url.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_SHORT).show();
                            Intent open_article = new Intent(Intent.ACTION_VIEW);
                            open_article.setData(Uri.parse(newscard.getUrl()));
                            startActivity(open_article);
                        }
                    });

                    getSupportActionBar().setTitle(title);

                    ProgressBar spinner = (ProgressBar) findViewById(R.id.progress_bar_article);
                    spinner.setVisibility(View.INVISIBLE);
                    findViewById(R.id.progress_text_article).setVisibility(View.INVISIBLE);
                    findViewById(R.id.detailed_article_card).setVisibility(View.VISIBLE);

                }catch (JSONException e) {
                    Log.d("Error", "Exception : "+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("Error", "Error in Home Fragment (News) : "+error);
            }
        });
        MyRequestQueue.add(jsonObjectRequestNews);
    }
}
