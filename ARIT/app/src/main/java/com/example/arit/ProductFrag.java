package com.example.arit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    Double plength, pheight, pwidth;

    
    TextView titleTV;       // 제목
    TextView pnameTV;       // 제품명
    TextView unameTV;       // 작성자
    TextView priceTV;       // 가격
    TextView howTV;         // 거래방식
    TextView contactTV;     // 연락처
    TextView detailTV;      // 상세정보
    TextView categoryTV;    // 카테고리
    TextView listComments;  // 코멘트
    TextView SizeTV;        // 사이즈
    
    ImageView imageIV;

    ListView coms;


    EditText comment;   //Comment

    Button postCom;     //Comment Post button

    Button arMode;     //Comment Post button

    ImageButton wish;    // 위시리스트 추가하기

    Intent intent;

    ArrayList<String> comments;
    ArrayAdapter<String> adapter;
    List<String> dataList;
    ArrayAdapter<String> arrayAdapter;

    private MyAdapter myAdapter;
    private DatabaseReference commentDatabase;
    private DatabaseReference wishDatabase;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product, container, false);
        Bundle bundle = getArguments();

        if (bundle != null){
            currentId = bundle.getString("currentID");
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
            plength = bundle.getDouble("length");
            pheight = bundle.getDouble("height");
            pwidth = bundle.getDouble("width");            
        }

        String SizeInfo = "길이: " + plength + "높이: " + pheight + "길이: " + pwidth;


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
        coms = view.findViewById(R.id.commentList);
        arMode = view.findViewById(R.id.arMode);
        SizeTV = view.findViewById(R.id.size);
        wish = view.findViewById(R.id.wishbtn);

        // intent로부터 받은 정보 텍스트뷰에 넣기
        titleTV.setText(title);
        unameTV.setText("작성자: " + uname);
        pnameTV.setText(pname);
        priceTV.setText(price + "원");
        howTV.setText("거래 방법: " + how);
        categoryTV.setText("카테고리: " + category);
        contactTV.setText("연락처: " + contact);
        detailTV.setText("상세 정보: " + detail);

        SizeTV.setText(SizeInfo);

        // Instance of product comment section
        commentDatabase = FirebaseDatabase.getInstance().getReference("Comment"+"/"+imagename.split("\\.")[0]);

        // wishlist database
        wishDatabase = FirebaseDatabase.getInstance().getReference("wish/");

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

        dataList = new ArrayList<>();
        dataList.clear();


        //Get comments from Firebase
        getComments();

        //Post to Firebase
        postCom.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){

                if(comment.getText().toString().length() > 0)
                {

                    postComment(pname, comment.getText().toString(), currID);
                    getComments();
                }


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


        // 위시리스트에 추가하기
        wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("currentId: ", currentId + " id");
                addWishItem(currentId, imagename);
            }
        });

        return view;
    }


    public void getComments() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataList.clear();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String ID=snapshot.child("id").getValue().toString();
                    String text=snapshot.child("text").getValue().toString();



                    String incSize = "";
                    String allComm = "";
                    String space = " ";
                    while(true) {
                        DataSnapshot tempSnap = snapshot.child(incSize);

                        if(tempSnap.hasChild("reply")) {

                            String replyID = tempSnap.child("reply").child("id").getValue().toString();
                            String replytext = tempSnap.child("reply").child("text").getValue().toString();
                            allComm += "\n" + space + replyID + ": " + replytext + "\n ";

                            incSize += "/reply";
                            space += "    ";

                        }
                        else{
                            break;
                        }

                    }
                    if(allComm.length() > 2)
                        dataList.add(ID + ": " + text + "\n  " + allComm);
                    else{
                        dataList.add(ID+": "+text);

                    }

                }

                Log.e("tag", dataList.toString());

                myAdapter = new MyAdapter(getActivity(), pname, (ArrayList) dataList, currID, imagename.split("\\.")[0]);
                coms.setAdapter(myAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        commentDatabase.addValueEventListener(postListener);

    }


    //////////////////////Post new user info to Firebase////////////////////////////////////////////
    public void postComment(final String product, final String text, final String user) {
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

    // 위시리스트 추가
    public void addWishItem(String userId, String imagename){
        //wishDatabase = wishDatabase.child(userID);

        Log.d("currentId: ", currentId + " id");
        Log.d("userId: ", userId + " id");

        String key = imagename.substring(0, 21);
        Log.d("key: ", key);

        wishDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("datasnapshot: ", dataSnapshot.toString());

                boolean flag = false;
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    Log.d("item: ", item.getKey());

                    if(item.getKey().equals(currentId)){
                        flag = true;
                        break;
                    }
                }

                if(flag){
                    Log.d("user: ", "존재");
                    DatabaseReference wishDB = FirebaseDatabase.getInstance().getReference("wish/" + currentId);
                    wishDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot Snapshot) {

                            boolean flag2 = false;
                            for(DataSnapshot itm : Snapshot.getChildren()){
                                Log.d("itm: ", itm.getKey());

                                if(itm.getKey().equals(key)){
                                    flag2 = true;
                                    break;
                                }
                            }

                            Map<String, Object> update = new HashMap<>();
                            Map<String, Object> post = new HashMap<>();
                            post.put("id", currentId);
                            post.put("wish", key);


                            if(flag2){
                                post = null;
                                update.put("/" + key, post);
                                wishDB.updateChildren(update);
                                Toast.makeText(view.getContext(), "위시리스트에서 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                            }else{
                                update.put("/" + key, post);
                                wishDB.updateChildren(update);
                                Toast.makeText(view.getContext(), "위시리스트에 추가하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    Map<String, Object> update = new HashMap<>();
                    Map<String, Object> post = new HashMap<>();
                    post.put("id", currentId);
                    post.put("wish", key);
                    update.put("/"+currentId + "/" + key, post);
                    wishDatabase.updateChildren(update);
                    Toast.makeText(view.getContext(), "위시리스트에 추가하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
