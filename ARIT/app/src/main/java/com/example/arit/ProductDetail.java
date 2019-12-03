package com.example.arit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductDetail extends AppCompatActivity {

    String currentId, currentName;
    String title;       // 제목
    String pname;       // 제품명
    String uname;       // 작성자
    String price;       // 가격
    String how;         // 거래방식
    String contact;     // 연락처
    String detail;      // 상세정보
    String category;    // 카테고리
    String imagename;

    TextView titleTV;       // 제목
    TextView pnameTV;       // 제품명
    TextView unameTV;       // 작성자
    TextView priceTV;       // 가격
    TextView howTV;         // 거래방식
    TextView contactTV;     // 연락처
    TextView detailTV;      // 상세정보
    TextView categoryTV;    // 카테고리
    ImageView imageIV;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        intent = getIntent();
        currentId = intent.getStringExtra("currentId");
        currentName = intent.getStringExtra("currentName");
        title = intent.getStringExtra("title");
        uname = intent.getStringExtra("uname");
        pname = intent.getStringExtra("pname");
        price = intent.getStringExtra("price");
        how = intent.getStringExtra("how");
        category = intent.getStringExtra("category");
        contact = intent.getStringExtra("contact");
        detail = intent.getStringExtra("detail");
        imagename = intent.getStringExtra("imagename");

        titleTV = findViewById(R.id.title);
        unameTV = findViewById(R.id.uname);
        pnameTV = findViewById(R.id.pname);
        priceTV = findViewById(R.id.price);
        howTV = findViewById(R.id.howto);
        categoryTV = findViewById(R.id.category);
        contactTV = findViewById(R.id.contact);
        detailTV = findViewById(R.id.detail);
        imageIV = findViewById(R.id.image);

        // intent로부터 받은 정보 텍스트뷰에 넣기
        titleTV.setText(title);
        unameTV.setText(uname);
        pnameTV.setText(pname);
        priceTV.setText(price);
        howTV.setText(how);
        categoryTV.setText(category);
        contactTV.setText(contact);
        detailTV.setText(detail);

        // 파이어베이스 스토리지로부터 해당 이미지 파일 가져오기
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://arit-37b7d.appspot.com").child("product_image");
        storageRef = storageRef.child(imagename);
        StorageReference pathReference = storageRef;
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri u) {
                Glide.with(imageIV.getContext()).load(u.toString()).into(imageIV);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });



    }
}
