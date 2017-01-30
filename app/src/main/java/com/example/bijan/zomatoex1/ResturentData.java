package com.example.bijan.zomatoex1;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResturentData extends Fragment {

    RecyclerView recyclerView;
    MyDatabase myDatabase;
    MyrecyclerViewAdapter myRecyclerViewAdapter;
    MyTask myTask;
    Cursor cursor;
    int pos;
    double cualat, curlong;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDatabase = new MyDatabase(getActivity());
        myDatabase.open();
    }

    @Override
    public void onDestroy() {
        myDatabase.close();
        super.onDestroy();
    }

    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.overflowmenu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.map:
                        Intent intent = new Intent(getActivity(), MapsActivity.class);
//                        intent.putExtra("latitude",);
//                        intent.putExtra("longitude",);
//                        intent.putExtra("name",);
                        startActivity(intent);
                        break;
                    case R.id.web:
                }
                return false;
            }
        });

        popupMenu.show();
    }

    public class MyTask extends AsyncTask<String, Void, String> {

        URL myURL;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        String line;
        StringBuilder result;

        @Override
        protected String doInBackground(String... strings) {
            try {
                myURL = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) myURL.openConnection();
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("user-key", "7f6ebcad5fb459db8e22080d29eba072");
                httpURLConnection.connect();
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                line = bufferedReader.readLine();
                result = new StringBuilder();
                while (line != null){
                    result.append(line);
                    line = bufferedReader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("B-34", "URL IS IMPROPER");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("B-34", "NETWORK PROBLEM");
            }
            return "SOME THING WENT WRONG";
        }


        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject a = new JSONObject(s);
                JSONArray j = a.getJSONArray("nearby_restaurants");
                for (int i = 0; i<j.length(); i++){
                    JSONObject k = j.getJSONObject(i);
                    JSONObject restaurant = k.getJSONObject("restaurant");
                    String name = restaurant.getString("name");
                    String imageUrl = restaurant.getString("thumb");
                    JSONObject location = restaurant.getJSONObject("location");
                    String locality = location.getString("locality");
                    String address = location.getString("address");
                    String latitude = location.getString("latitude");
                    String longitude = location.getString("longitude");

                    myDatabase.insertResturent(name, locality, imageUrl, address, latitude, longitude);
                }
                myRecyclerViewAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("B-34", "JSON PARSING ERROR");
            }

            super.onPostExecute(s);
        }
    }

    public class MyrecyclerViewAdapter extends RecyclerView.Adapter<MyrecyclerViewAdapter.ViewHolder>{
        @Override
        public MyrecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.row, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyrecyclerViewAdapter.ViewHolder holder, int position) {
            cursor.moveToPosition(position);

            String name = cursor.getString(1);
            String imageUrl = cursor.getString(3);
            String locality = cursor.getString(2);
            String address = cursor.getString(4);
            String longitude = cursor.getString(5);
            String lantitude = cursor.getString(6);

            holder.textName.setText(name);
            holder.textLocation.setText(locality);
            holder.textAddress.setText(address);
            holder.overflowImage.setTag(position);
            Glide
                    .with(getActivity())
                    .load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(holder.resturentImage);
            holder.overflowImage.setOnClickListener (new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = (ImageView) view;
                    pos= (int) imageView.getTag();
                    showPopup(view);

                }
            });
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textName, textAddress, textLocation;
            public ImageView resturentImage, overflowImage;
            public ViewHolder(View itemView) {
                super(itemView);
                textName = (TextView) itemView.findViewById(R.id.textView1);
                textLocation = (TextView) itemView.findViewById(R.id.textView2);
                textAddress = (TextView) itemView.findViewById(R.id.textView3);
                resturentImage = (ImageView) itemView.findViewById(R.id.imageView1);
                overflowImage = (ImageView) itemView.findViewById(R.id.imageView2);
            }
        }
    }

    public ResturentData() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment/d
        View v = inflater.inflate(R.layout.fragment_resturent_data, container, false);
        EditText editTextLocation = (EditText) v.findViewById(R.id.getlocation1);
        Button enter = (Button) v.findViewById(R.id.button1);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                if (homeActivity.checkInternet()) {
                    myTask.execute("https://developers.zomato.com/api/v2.1/geocode?lat=12.9719&lon=77.6412.");
                }
                else {
                    Toast.makeText(getActivity(), "CHECK INTERNET", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview2);
        cursor = myDatabase.quaryResturent();
        myRecyclerViewAdapter = new MyrecyclerViewAdapter();
        myTask = new MyTask();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myRecyclerViewAdapter);

        return  v;
    }
}
