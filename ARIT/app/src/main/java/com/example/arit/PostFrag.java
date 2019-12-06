package com.example.arit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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


public class PostFrag extends Fragment {
    public PostFrag() {
        // Required empty public constructor
    }

    View view;

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

    Uri filepath;
    String photoname;
    ImageView imageView;

    FirebaseStorage storage;
    DatabaseReference mPostReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post, container, false);

        Button post_btn = view.findViewById(R.id.post_btn);
        Button get_image = view.findViewById(R.id.get_image);

        final EditText titleET = view.findViewById(R.id.title);
        final EditText pnameET = view.findViewById(R.id.pname);
        final EditText priceET = view.findViewById(R.id.price);
        final EditText contactET = view.findViewById(R.id.contact);
        final EditText detailET = view.findViewById(R.id.detail);

        final Spinner categorySP = view.findViewById(R.id.category);
        final Spinner howSP = view.findViewById(R.id.howto);

        imageView = view.findViewById(R.id.image);


        Bundle extra = this.getArguments();
        if(extra != null){
            currentId = extra.getString("currentId");
            currentName = extra.getString("currentName");
        }

        // 사진 업로드 - 앨범으로 이동
        get_image.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);

            }
        });

        mPostReference = FirebaseDatabase.getInstance().getReference();

        // 게시글 업로드
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
                String image = photoname;


                // 입력 제대로 됐는지 확인
                if(title.equals("")){
                    Toast.makeText(view.getContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(image.equals("no file")){
                    Toast.makeText(view.getContext(), "사진을 첨부해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(pname.equals("")){
                    Toast.makeText(view.getContext(), "제품명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(price.equals("")){
                    Toast.makeText(view.getContext(), "가격을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(contact.equals("")){
                    Toast.makeText(view.getContext(), "연락처를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(detail.equals("")){
                    Toast.makeText(view.getContext(), "제품 상세정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(category.equals("카테고리 선택")){
                    Toast.makeText(view.getContext(), "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    // 업로드 후 홈 화면으로 이동
                    postFirebaseDB(true);

                    StorageReference storageReference = storage.getReferenceFromUrl("gs://arit-37b7d.appspot.com").child("product_image/" + photoname);
                    storageReference.putFile(filepath);

                    Toast.makeText(view.getContext(), "게시글이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), FrameLayout.class);
                    intent.putExtra("currentName", currentName);
                    intent.putExtra("currentId", currentId);
                    startActivity(intent);
                }
            }
        });


        return view;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
