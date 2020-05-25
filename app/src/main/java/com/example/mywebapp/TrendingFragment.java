package com.example.mywebapp;

import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {

    View v;
    private RequestQueue MyRequestQueue;
    private String base_url = "https://newsapp-backend-99.appspot.com/trends/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.trending_fragment, container, false);

        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.progress_bar_trending);
        v.findViewById(R.id.trending_chart).setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
        v.findViewById(R.id.progress_text_trending).setVisibility(View.VISIBLE);
        handleInput("Coronavirus");

        final EditText editText = (EditText) v.findViewById(R.id.textField);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    Log.d("Trends", "Edit Text : "+editText.getText().toString());

                    ProgressBar spinner = (ProgressBar) v.findViewById(R.id.progress_bar_trending);
                    v.findViewById(R.id.trending_chart).setVisibility(View.INVISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                    v.findViewById(R.id.progress_text_trending).setVisibility(View.VISIBLE);

                    handleInput(editText.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void handleInput(final String inputText)
    {
        MyRequestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequestNews = new JsonObjectRequest(Request.Method.GET, base_url + inputText, null, new Response.Listener<JSONObject>()  {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray trends = response.getJSONArray("trends");
                    Log.d("Trends", trends.toString());

                    List<Entry> trendvalues = new ArrayList<Entry>();
                    for(int i = 0; i < trends.length(); i++) {
                        trendvalues.add(new Entry(i, Integer.parseInt(trends.get(i).toString())));
                    }

                    LineDataSet dataset = new LineDataSet(trendvalues, "Trending Chart for "+inputText);
                    dataset.setAxisDependency(YAxis.AxisDependency.LEFT);
                    dataset.setCircleColor(R.color.colorPrimary);
                    dataset.setDrawCircleHole(false);
                    dataset.setColor(R.color.colorPrimary);
                    dataset.setValueTextColor(R.color.colorPrimary);

                    List<ILineDataSet> datasets = new ArrayList<ILineDataSet>();
                    datasets.add(dataset);

                    LineData data = new LineData(datasets);
                    LineChart lineChart = (LineChart) v.findViewById(R.id.trending_chart);
                    lineChart.setData(data);
                    lineChart.invalidate();

                    lineChart.getXAxis().setDrawGridLines(false);
                    lineChart.getAxisRight().setDrawGridLines(false);
                    lineChart.getAxisLeft().setDrawGridLines(false);

                    Legend legend = lineChart.getLegend();
                    legend.setTextSize(15);

                    ProgressBar spinner = (ProgressBar) v.findViewById(R.id.progress_bar_trending);
                    spinner.setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.progress_text_trending).setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.trending_chart).setVisibility(View.VISIBLE);

                }catch (JSONException e) {
                    Log.d("Error", "Exception : "+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("Error", "Error in Trending Fragment");
            }
        });
        MyRequestQueue.add(jsonObjectRequestNews);
    }
}
