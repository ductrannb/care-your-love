package com.ducanh.care_your_love;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.ducanh.care_your_love.Adapters.PostAdapter;
import com.ducanh.care_your_love.Models.Post;
import com.ducanh.care_your_love.Models.User;
import com.ducanh.care_your_love.commons.Common;
import com.ducanh.care_your_love.commons.Store;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText searchPost;
    private Button btnSearch;
    private Button linkCreatePost;
    private TextView postConsultantName;
    RecyclerView rcvListPost;
    ArrayList<Post> dataList;
    PostAdapter adapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Post.REFERENCE_NAME);

    private TextView account;

    //Đổi mk
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchPost = findViewById(R.id.search_post);
        btnSearch = findViewById(R.id.btn_search);
        linkCreatePost = findViewById(R.id.link_create_post);
        rcvListPost = findViewById(R.id.rcv_list_post);
        rcvListPost.setHasFixedSize(true);
        rcvListPost.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapter = new PostAdapter(dataList, this);
        rcvListPost.setAdapter(adapter);
        if (Store.userLogin == null || Store.userLogin.role == User.ROLE_USER_NORMAL) {
            //Ẩn nút đăng bài nếu người dùng thường đăng nhập
            linkCreatePost.setVisibility(View.INVISIBLE);
        }

        //đây là xử lý nav menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout_main);
        NavigationView navigationView = findViewById(R.id.nav_view_main);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }

        //xử lý onclick cho tìm kiếm, tìm kieesm theo title
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String keyword = searchPost.getText().toString();

                Query query = databaseReference.orderByChild("title").startAt(keyword).endAt(keyword + "\uf8ff");
                query.addValueEventListener(new ValueEventListener() {
                    List<Post> postList = new ArrayList<>();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class); // chuyển dl về kiểu post
                            postList.add(post);
                        }
                        adapter.setDataList(postList);//cập nhật giao diện hiển thị dsach bai viet tim dc
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //////hiển thị danh sách bài viết
        databaseReference.orderByChild("created_at").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    boolean isExist = false;
                    for (int i=0; i<dataList.size(); i++) {
                        if (dataList.get(i).uuid.equals(post.uuid)) {
                            dataList.set(i, post);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        dataList.add(post);
                    }
                }
                Collections.reverse(dataList);
                adapter.setDataList(dataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //xử lý hiển thị tên ng dùng trong nav_header
        if (Store.userLogin != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = headerView.findViewById(R.id.account);
            navUsername.setText(Store.userLogin.name);
        }

        // xử lý sự kiện onCLick cho Tạo bài viết
        linkCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(intent);
            }
        });

    }
    //

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
//                startActivity();
                break;
            case R.id.nav_change_password:
                startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                break;
            case R.id.nav_list_chat:
                startActivity(new Intent(MainActivity.this, ListChatActivity.class));
                break;
            case R.id.nav_logout:
                Common.logout(this);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.getOnBackPressedDispatcher();
        }
    }
}