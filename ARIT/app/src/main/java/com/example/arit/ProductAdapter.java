package com.example.arit;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

public class ProductAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    private ArrayList<ProductItem> items;
    DatabaseReference mPostReference;
    int commentCount = 0;
    TextView Comments;
    int flag = 0;

    public ProductAdapter(Context context, ArrayList<ProductItem> items) {
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
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

        final ProductViewHolder productViewHolder;
        ProductItem item = items.get(i);

        if ( view == null ) {

            view = layoutInflater.inflate(R.layout.list_item, viewGroup, false);

            productViewHolder = new ProductViewHolder();

            productViewHolder.iv1 = (ImageView) view.findViewById(R.id.image);
            productViewHolder.tv1 = (TextView) view.findViewById(R.id.category);
            productViewHolder.tv2 = (TextView)view.findViewById(R.id.title);
            productViewHolder.tv3 = (TextView)view.findViewById(R.id.pname);
            productViewHolder.tv4 = (TextView)view.findViewById(R.id.price);
            productViewHolder.tv5 = (TextView)view.findViewById(R.id.howto);
            productViewHolder.tv6 = (TextView)view.findViewById(R.id.commentCount);
            //Comments = productViewHolder.tv6;



            view.setTag(productViewHolder);
        }
        else{
            productViewHolder = (ProductViewHolder) view.getTag();
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
        //productViewHolder.tv6.setText(Integer.toString(commentCount));
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
