package com.example.arit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    public ArrayList myItems = new ArrayList();
    private DatabaseReference commentDatabase;
    String pname = "";
    String checkID = "";
    String checkText = "";
    String updateText = "";
    ViewHolder holder;
    String uID = "";
    String comName = "";
    int i = 0;

    public MyAdapter(Context context, String pname2, ArrayList dataList, String userID, String comName2 ) {
        pname = pname2;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myItems = dataList;
        uID = userID;
        comName = comName2;

        notifyDataSetChanged();
    }

    public int getCount() {
        return myItems.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.list_comment, parent, false);

            holder = new ViewHolder();

            holder.caption =  convertView.findViewById(R.id.comments);
            holder.edit = convertView.findViewById(R.id.editText);
            holder.butt = convertView.findViewById(R.id.replyButton);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        holder.caption.setText(myItems.get(position).toString());
        holder.caption.setId(position);



        Button temp = convertView.findViewById(R.id.replyButton);

        commentDatabase = FirebaseDatabase.getInstance().getReference("Comment"+"/"+comName);


        final View finalConvertView = convertView;
        temp.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){


                Log.e("work", myItems.get(position).toString());
                String[] split = myItems.get(position).toString().split(": ");
                checkID = split[0];
                checkText = split[1];

                holder.edit = finalConvertView.findViewById(R.id.editText);
                updateText = holder.edit.getText().toString();

                if(updateText.length() > 1)
                {
                    holder.edit.setText("");
                    addReply();
                }
                else{
                    Toast.makeText(finalConvertView.getContext(), "Comments must be longer than 1 character", Toast.LENGTH_SHORT).show();

                }




            }
        });

        return convertView;
    }

    private void addReply() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //Log.e("get", "please: " + snapshot.toString().substring(21));
                    String[] temp = snapshot.toString().substring(21).split(", ");
                    String currentID = temp[0];

                    String[] temp2 = checkText.split("\n");
                    checkText = temp2[0];

                    String tempID=snapshot.child("id").getValue().toString();
                    String tempText=snapshot.child("text").getValue().toString();


                    if(checkID.equals(tempID) && checkText.equals(tempText))
                    {
                        String replyString = "";


                        while(true)
                        {
                            DataSnapshot tempSnap = snapshot.child(replyString);
                            if(tempSnap.hasChild("reply")) {
                                replyString += "/reply";
                            }
                            else{
                                replyString += "/reply";
                                break;

                            }

                        }


                        Map<String, Object> userUpdates = new HashMap<>();
                        Map<String, Object> postValues = new HashMap<>();

                        postValues.put("text", updateText);
                        postValues.put("id", " L "+ uID);
                        userUpdates.put("/" + currentID + replyString, postValues);
                        commentDatabase.updateChildren(userUpdates);

                        return;
                    }



                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        commentDatabase.addListenerForSingleValueEvent(postListener);

    }
}

class ViewHolder {
    TextView caption;
    TextView replyText;
    EditText edit;
    Button butt;
}
