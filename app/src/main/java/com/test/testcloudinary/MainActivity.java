package com.test.testcloudinary;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cloudinary.android.MediaManager;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class MainActivity extends AppCompatActivity {
    Map config = new HashMap();

    //for request permission
    private boolean isReadPermissionGranted = false;
    private boolean isWritePermissionGranted = false;
    private boolean isLocationPermissionGranted = false;
    ActivityResultLauncher<String[]> mPermissionResultLauncher;

    //mongodb
    String Appid = "employeems-mcwma";
    MongoDatabase mongoDatabase;
    MongoClient mongoClient;
    MongoCollection<Document> mongoCollection;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);

        button = findViewById(R.id.button);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {

                if (result.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
                    isReadPermissionGranted = Boolean.TRUE.equals(result.get(android.Manifest.permission.READ_EXTERNAL_STORAGE));
                }
                if (result.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                    isWritePermissionGranted = Boolean.TRUE.equals(result.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
                }
                if (result.get(android.Manifest.permission.ACCESS_FINE_LOCATION) != null) {
                    isLocationPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                }

            }

        });

        App app = new App(new AppConfiguration.Builder(Appid).build());
        app.loginAsync(Credentials.emailPassword("everyoneusingtheapp@gmail.com", "Ems2023"), new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if (result.isSuccess()) {
                    Log.v("user", "database now accessible");
                    User user = app.currentUser();
                    mongoClient = user.getMongoClient("mongodb-atlas");
                    mongoDatabase = mongoClient.getDatabase("CourseData");
                } else {
                    Log.v("user", "Cannot Access Database");
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nye = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(nye);
            }
        });

        requestPermission();

    }//end of oncreate
    private void initCongif() {

        config.put("cloud_name", "dyp8ikyqu");
        config.put("api_key","141177131558868");
        config.put("api_secret","yi-CT6D5awMVZWGQ1IZUmXzweOY");
//        config.put("secure", true);
        MediaManager.init(this, config);
    }
    private void requestPermission() {

        Log.v("Result", "Requesting Permission");

        boolean minSDK = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

        isReadPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        isLocationPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = isWritePermissionGranted || minSDK;

        List<String> permissionRequest = new ArrayList<String>();

        if (!isReadPermissionGranted) {

            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        }
        if (!isWritePermissionGranted) {

            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!isLocationPermissionGranted) {

            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionRequest.isEmpty()) {

            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }

    }
}