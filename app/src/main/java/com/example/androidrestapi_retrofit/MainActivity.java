package com.example.androidrestapi_retrofit;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.androidrestapi_retrofit.model.GitUser;
import com.example.androidrestapi_retrofit.model.GitUsersResponse;
import com.example.androidrestapi_retrofit.model.UsersListViewModel;
import com.example.androidrestapi_retrofit.service.GitRepoServiceAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    List<GitUser> data=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final EditText editTextQuery = findViewById(R.id.editeTextQuery);
        Button buttonSearch = findViewById(R.id.buttonSearche);
        ListView listViewUsers=findViewById(R.id.listViewUsers);
       // final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,data);
        UsersListViewModel listViewModel = new UsersListViewModel(this,R.layout.users_list_view_layout,data);
        listViewUsers.setAdapter(listViewModel);
        final Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String query= editTextQuery.getText().toString();
                GitRepoServiceAPI gitRepoServiceAPI= retrofit.create(GitRepoServiceAPI.class);
                Call<GitUsersResponse> callGitUsers = gitRepoServiceAPI.searchUsers(query);
                callGitUsers.enqueue(new Callback<GitUsersResponse>() {

                    @Override
                    public void onResponse(Call<GitUsersResponse> call, Response<GitUsersResponse> response) {
                        Log.i("info",call.request().url().toString());
                        if(!response.isSuccessful()){
                            Log.i("info",String.valueOf(response.code()));
                            return;
                        }
                        GitUsersResponse gitUsersResponse=response.body();
                        for (GitUser user:gitUsersResponse.users){
                            data.add(user);
                        }
                        listViewModel.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<GitUsersResponse> call, Throwable t) {
                        Log.e("error","Error");

                    }
                });
            }
        });
    }
}