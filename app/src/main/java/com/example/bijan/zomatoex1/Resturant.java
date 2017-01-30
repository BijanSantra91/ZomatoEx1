package com.example.bijan.zomatoex1;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Resturant extends Fragment {

    EditText editText;
    Button button;
    RecyclerView recyclerView;
    ArrayList<ResturantBean> arrayListResturantBeen;
    MyrecyclerViewAdapter myRecyclerViewAdapter;
    MyTask myTask;
    LinearLayoutManager linearLayoutManager;
    int pos;
    double cualat, curlong;

    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.overflowmenu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ResturantBean resturantBean = arrayListResturantBeen.get(pos);
                switch (item.getItemId()){
                    case R.id.map:
                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        intent.putExtra("latitude", resturantBean.getLatitude());
                        intent.putExtra("longitude", resturantBean.getLongitude());
                        intent.putExtra("name", resturantBean.getName());
                        startActivity(intent);
                        break;
                    case R.id.web:
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.url(resturantBean.getUrl());
                        break;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    public class MyTask extends AsyncTask<String, Void, String>{

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
                    String url = restaurant.getString("url");
                    String imageUrl = restaurant.getString("thumb");
                    JSONObject location = restaurant.getJSONObject("location");
                    String locality = location.getString("locality");
                    String address = location.getString("address");
                    String latitude = location.getString("latitude");
                    String longitude = location.getString("longitude");

                    ResturantBean resturantBean = new ResturantBean(name, locality, address, imageUrl, latitude, longitude, url);
                    resturantBean.setName(name);
                    resturantBean.setAddress(address);
                    resturantBean.setImageUrl(imageUrl);
                    resturantBean.setLatitude(latitude);
                    resturantBean.setLongitude(longitude);
                    resturantBean.setLocality(locality);
                    resturantBean.setUrl(url);

                    arrayListResturantBeen.add(resturantBean);
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.row, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            ResturantBean resturantBean = arrayListResturantBeen.get(position);
            holder.textName.setText(resturantBean.getName());
            holder.textLocation.setText(resturantBean.getLocality());
            holder.textAddress.setText(resturantBean.getAddress());
            holder.overflowImage.setTag(position);
            Glide
                    .with(getActivity())
                    .load(resturantBean.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(holder.resturentImage);
            holder.overflowImage.setOnClickListener(new View.OnClickListener() {
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
            return arrayListResturantBeen.size();
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

    public Resturant() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_resturant, container, false);
        editText = (EditText) v.findViewById(R.id.getlocation);
        button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = editText.getText().toString();
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(address, 10);
                    Address best = addresses.get(0);
                    curlong = best.getLongitude();
                    cualat = best.getLatitude();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HomeActivity homeActivity = (HomeActivity) getActivity();
                if (homeActivity.checkInternet()) {
                    if(myTask.getStatus() == AsyncTask.Status.RUNNING || myTask.getStatus() == AsyncTask.Status.FINISHED) {
                        Toast.makeText(getActivity(), "Please Wait", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                    myTask.execute("https://developers.zomato.com/api/v2.1/geocode?lat=" + cualat + "&lon=" + curlong);
                    }
                }
                else {
                    Toast.makeText(getActivity(), "CHECK INTERNET", Toast.LENGTH_SHORT).show();
                }
                editText.setText("");

            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview1);
        arrayListResturantBeen = new ArrayList<ResturantBean>();
        myRecyclerViewAdapter = new MyrecyclerViewAdapter();
        myTask = new MyTask();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        //17.establist all links
        recyclerView.setAdapter(myRecyclerViewAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        return  v;
    }
}
