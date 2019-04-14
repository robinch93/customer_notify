package com.example.restaurantmanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {

    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};
    String meals = "[" +
            "{id:0,menuImg:'coffee.jpg',menuName:'Chiken Biryani',menuDesc:'Chiken and Rice',menuPrice:10.0,menuQty:10}," +
            "{id:1,menuImg:'coffee.jpg',menuName:'Chiken Biryani',menuDesc:'Chiken and Rice',menuPrice:10.0,menuQty:10}," +
            "{id:2,menuImg:'coffee.jpg',menuName:'Chiken Biryani',menuDesc:'Chiken and Rice',menuPrice:10.0,menuQty:10}" +
            "]";
    private ListView listView;
    private MealAdapter mAdapter;
    ArrayList<Meal> mealsList;
    private static final int EditACTIVITY_REQUEST_CODE = 0;
    private JSONArray mealResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        String json = MyJSON.getData(getBaseContext());
        if( json.isEmpty()){
            MyJSON.saveData(getBaseContext(), meals);
            json = meals;
        }

//        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.menuitems_layout, mobileArray);
//
//        ListView listView = (ListView) findViewById(R.id.menuList);
//        listView.setAdapter(adapter);

        listView = (ListView) findViewById(R.id.menuList);

        updateListView();

        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Meal setItem = (Meal) listView.getItemAtPosition(position); //
                String value = setItem.getmenuName(); //getter method
                Integer val = setItem.getid();
//                Toast.makeText(Menu.super.getBaseContext(), val.toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), MenuDetail.class);
                intent.putExtra("id", val.toString());
                intent.putExtra("item", setItem);
                startActivityForResult(intent, EditACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (EditACTIVITY_REQUEST_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    Meal item = (Meal)data.getSerializableExtra("item");
                    Integer id = Integer.parseInt(data.getStringExtra("id"));
                    View v = listView.getChildAt(id-1);

                    if(v == null)
                        return;
                    try {
                        for (int i=0; i < mealResult.length(); i++){
                            JSONObject itemArr = (JSONObject)mealResult.get(i);
                            if(itemArr.get("id").equals(id)){
                                itemArr.put("menuImg", item.getmenuImg());
                                itemArr.put("menuName", item.getmenuName());
                                itemArr.put("menuDesc", item.getmenuDesc());
                                itemArr.put("menuPrice", item.getmenuPrice());
                                itemArr.put("menuQty", item.getmenuQty());
                            }
                        }
                    }
                    catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }
                    MyJSON.saveData(getBaseContext(), mealResult.toString());
                    updateListView();
//                    TextView someText = (TextView) v.findViewById(R.id.sometextview);
//                    someText.setText("Hi! I updated you manually!");
                }
                break;
            }
        }
    }
    public void updateListView(){
        mealsList = new ArrayList<>();
        try {
            String json = MyJSON.getData(getBaseContext());
            mealResult = new JSONArray(json);
            for (int i=0; i<mealResult.length(); i++) {
                JSONObject meal = mealResult.getJSONObject(i);
                Integer id = meal.getInt("id");
                String menuImg = meal.getString("menuImg");
                String menuName = meal.getString("menuName");
                String menuDesc = meal.getString("menuDesc");
                Double menuPrice = meal.getDouble("menuPrice");
                Integer menuQty = meal.getInt("menuQty");
                mealsList.add(new Meal(id, menuImg, menuName, menuDesc, menuPrice, menuQty));
            }
            mAdapter = new MealAdapter(this,mealsList);
            listView.setAdapter(mAdapter);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}