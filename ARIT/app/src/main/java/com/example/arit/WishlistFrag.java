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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class WishlistFrag extends Fragment {
    public WishlistFrag() {
        // Required empty public constructor
    }


    String currentId;
    String currentName;

    DatabaseReference mPostReference;
    DatabaseReference wishReference;
    ArrayList<String> wish_list;
    ArrayList<ProductItem> products;
    ProductAdapter productAdapter;
    ListView recent;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_wishlist, container, false);


        Bundle extra = this.getArguments();

        if(extra != null){
            currentId = extra.getString("currentId");
            currentName = extra.getString("currentName");

        }

        recent = view.findViewById(R.id.product_list);
        products = new ArrayList<>();
        wish_list = new ArrayList<>();

        TextView name_tv = view.findViewById(R.id.name);
        name_tv.setText(currentName + "'s ");

        Bundle bundle;
        getFirebaseDatabase(view, currentId);


        return view;
    }


    public void getFirebaseDatabase(View view, String userID) {

        wishReference = FirebaseDatabase.getInstance().getReference().child("wish").child(userID);
        Log.d("ref: ", wishReference.toString());
        wishReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wish_list.clear();

                for(DataSnapshot wish : dataSnapshot.getChildren()){
                    Log.d("wish: ", wish.toString());
                    String product = wish.getKey();
                    Log.d("product: ", product);

                    if(product != null) wish_list.add(0, product);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mPostReference = FirebaseDatabase.getInstance().getReference().child("product");
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                products.clear();

                Log.d("wish list: ", wish_list.toString());

                for(DataSnapshot item : dataSnapshot.getChildren()){

                    String image_name = item.getValue(ProductItem.class).getImagename();
                    image_name = image_name.substring(0, 21);
                    if(wish_list.contains(image_name)){
                        products.add(item.getValue(ProductItem.class));
                        Log.d("products: ", image_name);
                    }
                }



                productAdapter = new ProductAdapter(view.getContext(), products);
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
