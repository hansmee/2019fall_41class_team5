package com.example.arit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;


public class Home extends AppCompatActivity {

    Button post_btn;
    String currentId;
    String currentName;
    Intent intent;

    ListView recent;
    ArrayList<ProductItem> products;
    ProductAdapter productAdapter;

    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        intent = getIntent();
        currentId = intent.getStringExtra("currentId");
        currentName = intent.getStringExtra("currentName");

        recent = findViewById(R.id.recent_list);
        products = new ArrayList<>();

        // 최신 글 띄우기 (리스트 뷰 업데이트)
        mPostReference = FirebaseDatabase.getInstance().getReference().child("product");
        mPostReference.limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot item : dataSnapshot.getChildren()){
                    products.add(0,item.getValue(ProductItem.class));
                }
                productAdapter = new ProductAdapter(getApplicationContext(), products);
                recent.setAdapter(productAdapter);
                recent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                        // 해당 글 클릭하면 상세 정보 화면으로 넘어가도록 (ProductDetail.java)
                        intent = new Intent(getApplicationContext(), ProductDetail.class);
                        intent.putExtra("currentID", currentId);
                        intent.putExtra("currentName", currentName);
                        intent.putExtra("title", products.get(i).getTitle());
                        intent.putExtra("pname", products.get(i).getPname());
                        intent.putExtra("uname", products.get(i).getUname());
                        intent.putExtra("price", products.get(i).getPrice());
                        intent.putExtra("how", products.get(i).getHow());
                        intent.putExtra("contact", products.get(i).getContact());
                        intent.putExtra("detail",products.get(i).getDetail());
                        intent.putExtra("imagename", products.get(i).getImagename());
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // POST 버튼
        post_btn = findViewById(R.id.post);
        post_btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                intent = new Intent(Home.this, Post.class);
                intent.putExtra("currentId", currentId);
                intent.putExtra("currentName", currentName);
                startActivity(intent);
            }
        });
    }

}
