package com.example.arit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MypageFrag extends Fragment {

    public MypageFrag() {
        // Required empty public constructor
    }

    String currentId;
    String currentName;
    String phoneNum;

    TextView phoneTV;

    DatabaseReference mPostReference;
    ArrayList<ProductItem> products;
    MypageAdapter productAdapter;
    ListView recent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        Bundle extra = this.getArguments();


        if(extra != null){
            currentId = extra.getString("currentId");
            currentName = extra.getString("currentName");
        }


        TextView idTV = view.findViewById(R.id.mypage_idTV);
        TextView nameTV = view.findViewById(R.id.mypage_nameTV);
        phoneTV = view.findViewById(R.id.mypage_phoneTV);

        recent = view.findViewById(R.id.product_list);
        products = new ArrayList<>();

        Bundle bundle;


        getPhoneNum();

        idTV.setText("ID: " + currentId);
        nameTV.setText("NAME: " + currentName);

        getFirebaseDatabase(view);


        return view;
    }

    public void getPhoneNum() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(currentId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot item : dataSnapshot.getChildren()){

                    if(item.getKey().equals("numphone")){
                        phoneNum = item.getValue().toString();
                        phoneTV.setText("PHONE: " + phoneNum);
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void getFirebaseDatabase(View view) {
        mPostReference = FirebaseDatabase.getInstance().getReference().child("product");
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                products.clear();

                for(DataSnapshot item : dataSnapshot.getChildren()){

                    ProductItem tmp = item.getValue(ProductItem.class);
                    String uname = tmp.getUname();
                    if(uname.equals(currentName)){
                        products.add(0, tmp);
                    }
                }

                Log.d("product list: ", products.toString());

                productAdapter = new MypageAdapter(view.getContext(), products);
                Log.d("productAdapter: ", productAdapter.toString());
                Log.d("recent: ", recent.toString());

                recent.setAdapter(productAdapter);
                recent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                        // 해당 글 클릭하면 상세 정보 화면으로 넘어가도록 (ProductDetail.java)
                        Bundle productInfo = new Bundle();

                        productInfo.putString("currentID", currentId);
                        productInfo.putString("currentName", currentName);
                        productInfo.putString("title", products.get(i).getTitle());
                        productInfo.putString("pname", products.get(i).getPname());
                        productInfo.putString("uname", products.get(i).getUname());
                        productInfo.putString("price", products.get(i).getPrice());
                        productInfo.putString("how", products.get(i).getHow());
                        productInfo.putString("contact", products.get(i).getContact());
                        productInfo.putString("detail",products.get(i).getDetail());
                        productInfo.putString("imagename", products.get(i).getImagename());
                        productInfo.putString("category", products.get(i).getCategory());


                        ((FrameLayout)getActivity()).changeFragment(ProductFrag.newInstance(), productInfo);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
