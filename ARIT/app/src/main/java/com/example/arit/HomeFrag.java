package com.example.arit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;


public class HomeFrag extends Fragment {

    public static HomeFrag newInstance() {
        return new HomeFrag();
    }

    String currentId;
    String currentName;

    String category = "NEW";

    DatabaseReference mPostReference;
    ArrayList<ProductItem> products;
    ProductAdapter productAdapter;
    ListView recent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        Bundle extra = this.getArguments();


        if(extra != null){
            currentId = extra.getString("currentId");
            currentName = extra.getString("currentName");
        }

        recent = view.findViewById(R.id.recent_list);
        products = new ArrayList<>();
        //final ProductAdapter productAdapter;

        Bundle bundle;
        getFirebaseDatabase(view, category);

        // ****** Navigation Bar ****** //
        DrawerLayout drawerLayout = view.findViewById(R.id.drawer);
        TextView category_tv = view.findViewById(R.id.main_text);

        // 메뉴 버튼 클릭 시 사이드 바(카테고리) 나타내기
        ImageButton list_btn = view.findViewById(R.id.list);
        list_btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // 카테고리 클릭 시 리스트 변경
        final String[] categories = {"NEW", "노트북", "태블릿PC", "데스크탑/본체", "모니터", "CPU/메인보드/RAM", "키보드/마우스/스피커", "기타"};
        ArrayAdapter adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1, categories);
        ListView category_menu = view.findViewById(R.id.drawerlist);
        category_menu.setAdapter(adapter);
        category_menu.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        category_tv.setText(categories[0]);
                        category = "NEW";
                        break;
                    case 1:
                        category_tv.setText(categories[1]);
                        category = "노트북";
                        break;
                    case 2:
                        category_tv.setText(categories[2]);
                        category = "태블릿PC";
                        break;
                    case 3:
                        category_tv.setText(categories[3]);
                        category = "데스크탑/본체";
                        break;
                    case 4:
                        category_tv.setText(categories[4]);
                        category = "모니터";
                        break;
                    case 5:
                        category_tv.setText(categories[5]);
                        category = "CPU/메인보드/RAM";
                        break;
                    case 6:
                        category_tv.setText(categories[6]);
                        category = "키보드/마우스/스피커";
                        break;
                    case 7:
                        category_tv.setText(categories[7]);
                        category = "기타";
                        break;

                }

                getFirebaseDatabase(view, category);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });



        return view;
    }

    public void getFirebaseDatabase(View view, String _category) {
        mPostReference = FirebaseDatabase.getInstance().getReference().child("product");
        mPostReference.limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                products.clear();

                for(DataSnapshot item : dataSnapshot.getChildren()){

                    ProductItem tmp = item.getValue(ProductItem.class);
                    Log.d("products: ", tmp.getCategory());
                    if(_category.equals("NEW")) products.add(0,tmp);
                    else {
                        if (tmp.getCategory().equals(_category)) {
                            products.add(0, item.getValue(ProductItem.class));
                        }
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
                        productInfo.putDouble("length", products.get(i).getLength_size());
                        productInfo.putDouble("height", products.get(i).getHeight_size());
                        productInfo.putDouble("width", products.get(i).getWidth_size());


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
