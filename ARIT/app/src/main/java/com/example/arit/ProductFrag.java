package com.example.arit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class ProductFrag extends Fragment {

    public static ProductFrag newInstance() {
        return new ProductFrag();
    }

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
    String currID;      //사용자

    TextView titleTV;       // 제목
    TextView pnameTV;       // 제품명
    TextView unameTV;       // 작성자
    TextView priceTV;       // 가격
    TextView howTV;         // 거래방식
    TextView contactTV;     // 연락처
    TextView detailTV;      // 상세정보
    TextView categoryTV;    // 카테고리
    TextView listComments;  // 코멘트
    ImageView imageIV;


    EditText comment;   //Comment

    Button postCom, arMode;     //Comment Post button

    Intent intent;

    private DatabaseReference commentDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_product, container, false);
        Bundle bundle = getArguments();

        if (bundle != null){
            currentId = bundle.getString("currentId");
            currentName = bundle.getString("currentName");
            title = bundle.getString("title");
            uname = bundle.getString("uname");
            pname = bundle.getString("pname");
            price = bundle.getString("price");
            how = bundle.getString("how");
            category = bundle.getString("category");
            contact = bundle.getString("contact");
            detail = bundle.getString("detail");
            imagename = bundle.getString("imagename");
            currID = bundle.getString("currentID");
        }



        titleTV = view.findViewById(R.id.title);
        unameTV = view.findViewById(R.id.uname);
        pnameTV = view.findViewById(R.id.pname);
        priceTV = view.findViewById(R.id.price);
        howTV = view.findViewById(R.id.howto);
        categoryTV = view.findViewById(R.id.category);
        contactTV = view.findViewById(R.id.contact);
        detailTV = view.findViewById(R.id.detail);
        imageIV = view.findViewById(R.id.image);
        listComments = view.findViewById(R.id.comments);
        comment = view.findViewById(R.id.commentEdit);
        postCom = view.findViewById(R.id.commentButton);
        arMode = view.findViewById(R.id.arMode);


        // intent로부터 받은 정보 텍스트뷰에 넣기
        titleTV.setText(title);
        unameTV.setText(uname);
        pnameTV.setText(pname);
        priceTV.setText(price);
        howTV.setText(how);
        categoryTV.setText(category);
        contactTV.setText(contact);
        detailTV.setText(detail);

        // Instance of product comment section
        commentDatabase = FirebaseDatabase.getInstance().getReference("Comment"+"/"+pname);

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


        //Get comments from Firebase
        getComments();





        //Post to Firebase
        postCom.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                postComment(pname, comment.getText().toString(), currID, pname);
                getComments();
            }
        });

        //AR 모드로 넘어가기
        arMode.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ARMode.class);
                startActivity(intent);
            }
        });
        return view;
    }


    public void getComments() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String allComments = "";

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> map = snapshot.child("").getValue(genericTypeIndicator);

                    String tempStorage = map.toString();

                    String tempArray[] = tempStorage.replaceAll("[{}]", "").split(",");

                    String text = "", ID = "";

                    for(int i = 0; i < 2; i++)
                    {
                        if(tempArray[i].charAt(0) == 't'){                                           text = tempArray[i].substring(5);                        }
                        else if((tempArray[i].charAt(0) == ' ' && tempArray[i].charAt(1) == 't')){   text = tempArray[i].substring(6);                         }

                        if(tempArray[i].charAt(0) == 'i'){                                           ID = tempArray[i].substring(3);                        }
                        else if((tempArray[i].charAt(0) == ' ' && tempArray[i].charAt(1) == 'i')){   ID = tempArray[i].substring(4);                         }
                    }

                    allComments += ID+": "+text+"\n";

                }
                listComments.setText(allComments);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        commentDatabase.addValueEventListener(postListener);

    }


    //////////////////////Post new user info to Firebase////////////////////////////////////////////
    public void postComment(final String product, final String text, final String user, final String pname) {
        commentDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                StringBuilder tempUser = new StringBuilder(user);


                //If user has already posted a comment, make unique ID by adding *
                if (snapshot.hasChild(user)) {
                    while(snapshot.hasChild(String.valueOf(tempUser))) {                        tempUser.append('*');                    }
                }

                //post to Firebase
                Map<String, Object> userUpdates = new HashMap<>();
                Map<String, Object> postValues = new HashMap<>();

                postValues.put("text", text);
                postValues.put("id", user);
                userUpdates.put("/" + tempUser, postValues);
                commentDatabase.updateChildren(userUpdates);
                comment.setText("");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    //////////////////////Post new user info to Firebase////////////////////////////////////////////
}
