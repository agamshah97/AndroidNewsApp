package com.example.mywebapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BookmarksFragment extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    View v;
    private RecyclerView myrecyclerview;
    private List<Newscard> listcards;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        pref = getContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        Log.d("Bookmarks", "No. of items bookmarked : "+pref.getAll().keySet().size());
        v = inflater.inflate(R.layout.bookmarks_fragment, container, false);

        RenderCards();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        RenderCards();
        super.onResume();
    }


    public void RenderCards() {

        Gson gson = new Gson();

        listcards = new ArrayList<Newscard>();
        Set<String> ids = pref.getAll().keySet();

        for(String id : ids)
        {
            Newscard news_item = gson.fromJson(pref.getString(id, null), Newscard.class);
            listcards.add(news_item);
        }

        myrecyclerview = (RecyclerView) v.findViewById(R.id.bookmark_recyclerview);

        RecyclerViewAdapterBM recyclerViewAdapter = new RecyclerViewAdapterBM(getContext(), listcards, v.findViewById(R.id.bookmark_text));
        myrecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        myrecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        myrecyclerview.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        if(listcards.size() > 0) {
            v.findViewById(R.id.bookmark_text).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.bookmark_recyclerview).setVisibility(View.VISIBLE);
        }

        else {
            v.findViewById(R.id.bookmark_text).setVisibility(View.VISIBLE);
            v.findViewById(R.id.bookmark_recyclerview).setVisibility(View.INVISIBLE);
        }


    }
}
