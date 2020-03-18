package com.learn2crack.imageupload;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int INTENT_REQUEST_CODE = 100;

    public static final String URL = "https://esellers.againwish.com/";

    private String mImageUrl = "";

    private InputStream is;

    private int id;
    private EditText nameET, emailET, contactET, addressET;
    private TextView outputTV;
    private String token, name, email, contact, stringPhoto, address;
    private Button btnLogout, choseBtn, editBtn;
    private ImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        contactET = findViewById(R.id.contactET);
        addressET = findViewById(R.id.addressET);
        choseBtn = findViewById(R.id.choseBtn);
        editBtn = findViewById(R.id.editBtn);
        profileImg = findViewById(R.id.profileIV);

        id = 14;
        token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9lc2VsbGVycy5hZ2Fpbndpc2guY29tXC9hcGlcL2F1dGhcL3VzZXJfbG9naW4iLCJpYXQiOjE1ODQ1NTIxODYsImV4cCI6MTYxNjA4ODE4NiwibmJmIjoxNTg0NTUyMTg2LCJqdGkiOiI3WE5KMGpWYWZJaVJMMGh1Iiwic3ViIjoxNCwicHJ2IjoiODdlMGFmMWVmOWZkMTU4MTJmZGVjOTcxNTNhMTRlMGIwNDc1NDZhYSJ9.StWychN32GgK0HPGFE26Cz0QcCNMLOgI2L4oxb0Pk08";

        choseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Photo"), INTENT_REQUEST_CODE);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameET.getText().toString();
                email = emailET.getText().toString();
                contact = contactET.getText().toString();
                address = addressET.getText().toString();
                try {
                    uploadImage(getBytes(is),name, email, contact, address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                uploadImage(name, email, contact, stringPhoto, address);
            }
        });

//        initViews();

    }

//    private void initViews() {
//
//        mBtImageSelect.setOnClickListener((View view) -> {
//
//
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/jpeg");
//
//            try {
//                startActivityForResult(intent, INTENT_REQUEST_CODE);
//
//            } catch (ActivityNotFoundException e) {
//
//                e.printStackTrace();
//            }
//
//        });
//
//        mBtImageShow.setOnClickListener(view -> {
//
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(mImageUrl));
//            startActivity(intent);
//
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                try {

                    is = getContentResolver().openInputStream(data.getData());

//                    uploadImage(getBytes(is));

                    Uri filePath = data.getData();
                    RequestOptions myOptions = new RequestOptions()
                            .centerCrop() // or centerCrop
                            .override(195, 195);

                    Glide
                            .with(this)
                            .asBitmap()
                            .apply(myOptions)
                            .load(filePath)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            stringPhoto = (getStringImage(resource));
                                    profileImg.setImageBitmap(resource);
                                    Log.d(TAG, "onResourceReady: called");
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }


    private void uploadImage(byte[] imageBytes, String name, String email, String contact, String address) {
        Log.e(TAG, "uploadImage: "+name+" "+email+" "+contact+" "+imageBytes.length+" "+address );
        Log.e(TAG, "uploadImage: "+token+" "+id );
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "bearer" + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);

        MultipartBody.Part body = MultipartBody.Part.createFormData("user_profile_photo", "user_profile_photo.jpg", requestFile);

        Call<ProfileUpdateRes> call = retrofitInterface.updateProfile(body, name, email, contact, address, id);

        call.enqueue(new Callback<ProfileUpdateRes>() {
            @Override
            public void onResponse(Call<ProfileUpdateRes> call, Response<ProfileUpdateRes> response) {
                if (response.isSuccessful()){
                    ProfileUpdateRes updateRes=response.body();
                    if (updateRes.getStatus()==1){
                        Log.e(TAG, "onResponse: "+updateRes.getMessage() );
                    }
                }else
                    Log.e(TAG, "onResponse else: "+response.code() );
            }

            @Override
            public void onFailure(Call<ProfileUpdateRes> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getLocalizedMessage() );
            }
        });


//        Call<Response> call = retrofitInterface.uploadImage(body);
//
//        call.enqueue(new Callback<Response>() {
//            @Override
//            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
//
//
//
//                if (response.isSuccessful()) {
//
//                    Response responseBody = response.body();
//
//                    mImageUrl = URL + responseBody.getPath();
//                    Snackbar.make(findViewById(R.id.content), responseBody.getMessage(),Snackbar.LENGTH_SHORT).show();
//
//                } else {
//
//                    ResponseBody errorBody = response.errorBody();
//
//                    Gson gson = new Gson();
//
//                    try {
//
//                        Response errorResponse = gson.fromJson(errorBody.string(), Response.class);
//                        Snackbar.make(findViewById(R.id.content), errorResponse.getMessage(),Snackbar.LENGTH_SHORT).show();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Response> call, Throwable t) {
//
//                Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
//            }
//        });
    }
}
