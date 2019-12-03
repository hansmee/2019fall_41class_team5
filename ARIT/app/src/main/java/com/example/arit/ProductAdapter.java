package com.example.arit;

import android.content.Context;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    private ArrayList<ProductItem> items;

    public ProductAdapter (Context context, ArrayList<ProductItem> items) {
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

            view.setTag(productViewHolder);
        }
        else{
            productViewHolder = (ProductViewHolder) view.getTag();
        }

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

        return view;
    }
}
