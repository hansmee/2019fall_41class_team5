package com.example.arit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchFrag extends Fragment {

    public SearchFrag() {
        // Required empty public constructor
    }

    String currentId;
    String currentName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        Bundle extra = this.getArguments();

        if(extra != null){
            currentId = extra.getString("currentId");
            currentName = extra.getString("currentName");

        }

        ListView product_list = view.findViewById(R.id.product_list);
        final ArrayList<ProductItem> products = new ArrayList<>();
        //

        DatabaseReference mPostReference;

        Bundle bundle;
        ///////////////////// 시작 시 최신 글 띄우기 /////////////////////
        mPostReference = FirebaseDatabase.getInstance().getReference().child("product");
        mPostReference.limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot item : dataSnapshot.getChildren()){
                    products.add(0,item.getValue(ProductItem.class));
                }
                ProductAdapter productAdapter = new ProductAdapter(view.getContext(), products);
                product_list.setAdapter(productAdapter);
                product_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                        // 해당 글 클릭하면 상세 정보 화면으로 넘어가도록 (ProductFrag.java)
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


                        ((FrameLayout)getActivity()).changeFragment(ProductFrag.newInstance(), productInfo);

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ///////////////////// 시작 시 최신 글 띄우기 /////////////////////

        final EditText searchET = view.findViewById(R.id.search);
        Button searchBtn = view.findViewById(R.id.search_btn);

        searchBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                // ET에서 검색어 가져오기
                ListView product_list = view.findViewById(R.id.product_list);
                final ArrayList<ProductItem> products = new ArrayList<>();

                String keyword = searchET.getText().toString();

                DatabaseReference mPostReference2;

                mPostReference2 = FirebaseDatabase.getInstance().getReference().child("product");
                mPostReference2.limitToLast(20).startAt(keyword).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot item : dataSnapshot.getChildren()){
                            products.add(0, item.getValue(ProductItem.class));
                        }
                        ProductAdapter productAdapter = new ProductAdapter(view.getContext(), products);
                        product_list.setAdapter(productAdapter);
                        product_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

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


                                ((FrameLayout)getActivity()).changeFragment(ProductFrag.newInstance(), productInfo);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


        return view;
    }
}
