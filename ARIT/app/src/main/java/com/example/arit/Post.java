package com.example.arit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Post extends AppCompatActivity {

    String currentId, currentName;
    String title;       // 제목
    String pname;       // 제품명
    String uname;       // 작성자
    String price;       // 가격
    String how;         // 거래방식
    String contact;     // 연락처
    String detail;      // 상세정보
    String category;    // 카테고리
    String image;

    Intent intent;

    Button post_btn;
    Button get_image;

    EditText titleET;      // 제목
    EditText pnameET;      // 제품명
    EditText priceET;      // 가격
    EditText contactET;    // 연락처
    EditText detailET;     // 상세정보

    Spinner categorySP;    // 카테고리
    Spinner howSP;         // 거래방식

    ImageView imageView;
    Uri filepath;
    String photoname;

    FirebaseStorage storage;

    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        intent = getIntent();
        currentId = intent.getStringExtra("currentId");
        currentName = intent.getStringExtra("currentName");
        uname = currentName;

        // xml 입력 가져오기
        titleET = findViewById(R.id.title);
        pnameET = findViewById(R.id.pname);
        priceET = findViewById(R.id.price);
        contactET = findViewById(R.id.contact);
        detailET = findViewById(R.id.detail);
        howSP = findViewById(R.id.howto);
        categorySP = findViewById(R.id.category);
        imageView = findViewById(R.id.image);

        // 사진 업로드 - 앨범으로 이동
        get_image = findViewById(R.id.get_image);
        get_image.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);

            }
        });



        mPostReference = FirebaseDatabase.getInstance().getReference();


        // 게시글 업로드
        post_btn = findViewById(R.id.post_btn);
        post_btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                // 사용자 입력 가져오기
                title = titleET.getText().toString();
                pname = pnameET.getText().toString();
                price = priceET.getText().toString();
                contact = contactET.getText().toString();
                detail = detailET.getText().toString();
                how = howSP.getSelectedItem().toString();
                category = categorySP.getSelectedItem().toString();

                // 사진 가져오기

                if (filepath != null) {
                    storage = FirebaseStorage.getInstance();
                    SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd-HH-mm-ss-SSS", Locale.KOREA);
                    Date tmp_date = new Date();
                    String CurrentTime = formatter.format(tmp_date);
                    photoname = CurrentTime+"."+title;
                }
                else{
                    photoname = "no file";
                }
                image = photoname;


                // 입력 제대로 됐는지 확인
                if(title.equals("")){
                    Toast.makeText(Post.this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(image.equals("no file")){
                    Toast.makeText(Post.this, "사진을 첨부해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(pname.equals("")){
                    Toast.makeText(Post.this, "제품명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(price.equals("")){
                    Toast.makeText(Post.this, "가격을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(contact.equals("")){
                    Toast.makeText(Post.this, "연락처를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(detail.equals("")){
                    Toast.makeText(Post.this, "제품 상세정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(category.equals("카테고리 선택")){
                    Toast.makeText(Post.this, "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    // 업로드 후 홈 화면으로 이동
                    postFirebaseDB(true);

                    StorageReference storageReference = storage.getReferenceFromUrl("gs://arit-37b7d.appspot.com").child("product_image/" + photoname);
                    storageReference.putFile(filepath);

                    Toast.makeText(Post.this, "게시글이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Post.this, Home.class);
                    intent.putExtra("currentName", currentName);
                    intent.putExtra("currentId", currentId);
                    startActivity(intent);
                }
            }
        });
    }

    // 파이어베이스 실시간 DB에 업로드
    public void postFirebaseDB(boolean add){
        Map<String, Object> child = new HashMap<>();
        Map<String, Object> value = null;

        if(add){
            ProductItem product = new ProductItem(title, pname, uname, price, how, contact, detail, category, photoname);
            value = product.toMap();
        }
        SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd-HH-mm-ss-SSS", Locale.KOREA);
        Date tmp_date = new Date();
        String CurrentTime = formatter.format(tmp_date);
        child.put("/product/"+CurrentTime+"/", value);
        mPostReference.updateChildren(child);
    }

    // 사진 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
