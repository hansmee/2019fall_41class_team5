package com.example.arit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

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
import java.util.Map;

public class MypageAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    private ArrayList<ProductItem> items;
    private OnItemClick mCallback;
    DatabaseReference mPostReference;
    int commentCount = 0;
    TextView Comments;
    int flag = 0;

    public MypageAdapter (Context context, ArrayList<ProductItem> items, OnItemClick listener) {
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.mCallback = listener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ProductItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final MypageViewHolder productViewHolder;
        ProductItem item = items.get(i);

        if ( view == null ) {

            view = layoutInflater.inflate(R.layout.my_list_item, viewGroup, false);

            productViewHolder = new MypageViewHolder();

            productViewHolder.iv1 = (ImageView) view.findViewById(R.id.image);
            productViewHolder.tv1 = (TextView) view.findViewById(R.id.category);
            productViewHolder.tv2 = (TextView)view.findViewById(R.id.title);
            productViewHolder.tv3 = (TextView)view.findViewById(R.id.pname);
            productViewHolder.tv4 = (TextView)view.findViewById(R.id.price);
            productViewHolder.tv5 = (TextView)view.findViewById(R.id.howto);
            productViewHolder.tv6 = (TextView)view.findViewById(R.id.commentCount);
            productViewHolder.btn = (Button)view.findViewById(R.id.delete);

            view.setTag(productViewHolder);
        }
        else{
            productViewHolder = (MypageViewHolder) view.getTag();
        }

        commentCount = 0;
        mPostReference = FirebaseDatabase.getInstance().getReference().child("Comment").child(item.getImagename().split("\\.")[0]);
        Log.e("no", mPostReference.toString());
        getCommentCount(productViewHolder.tv6);

        productViewHolder.tv1.setText("["+item.getCategory()+"]");
        productViewHolder.tv2.setText(item.getTitle());
        productViewHolder.tv3.setText(item.getPname());
        productViewHolder.tv4.setText(item.getPrice() + "원");
        productViewHolder.tv5.setText(item.getHow());
        productViewHolder.iv1.setImageResource(R.drawable.picture);

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://arit-37b7d.appspot.com").child("product_image");

        final String fileName = item.getImagename();
        storageRef = storageRef.child(fileName);
        StorageReference pathReference = storageRef;

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri u) {
                Glide.with(productViewHolder.iv1.getContext()).load(u.toString()).into(productViewHolder.iv1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        productViewHolder.btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String imagename = item.getImagename();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("product");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        for(DataSnapshot item : dataSnapshot.getChildren()){

                            ProductItem tmp = item.getValue(ProductItem.class);
                            String imgname = tmp.getImagename();
                            if(imgname.equals(imagename)){
                                mCallback.onClick(item.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        return view;
    }

    public void getCommentCount(TextView tv6) {

        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flag = 0;
                commentCount = 0;


                for(int i=0; i < dataSnapshot.toString().length(); i++)
                {    if(dataSnapshot.toString().charAt(i) == '{')
                    commentCount++;
                }

                commentCount -= 2;
                if(commentCount < 0){ commentCount = 0; }
                Log.e("yes", dataSnapshot.toString());
                String setString = Integer.toString(commentCount);
                Log.e("yes", setString);
                tv6.setText(setString);
                flag = 1;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }
}
