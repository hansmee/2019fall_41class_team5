package com.example.arit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    EditText idEdit, passEdit;
    Button loginButton, signupButton;
    String userId, userPass, userName;
    private DatabaseReference uDatabase;
    boolean check = false;
    SharedPreferences loginPref;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idEdit = (EditText) findViewById(R.id.idEdit);
        passEdit = (EditText) findViewById(R.id.passEdit);
        loginButton = (Button) findViewById(R.id.loginButton);
        signupButton = (Button) findViewById(R.id.signupButton);
        uDatabase = FirebaseDatabase.getInstance().getReference("user");
        loginPref = this.getPreferences(Context.MODE_PRIVATE);

        final Intent intent = new Intent(MainActivity.this, FrameLayout.class);
        final Intent intent2 = new Intent(MainActivity.this, SignIn.class);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = false;
                //v.startAnimation(buttonClick);
                if (idEdit.getText() != null && passEdit.getText() != null) {
                    userId = idEdit.getText().toString();
                    userPass = passEdit.getText().toString();
                    getUserDatabase(intent);

                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(intent2);
            }
        });


    }
    public void getUserDatabase(final Intent intent) {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> map = snapshot.child("").getValue(genericTypeIndicator);

                    String tempStorage = map.toString();
                    String temp2Storage = tempStorage.replaceAll("[{}]", "");
                    String tempArray[] = temp2Storage.split(",");

                    String PW = "", ID = "";

                    for(int i = 0; i < 4; i++)
                    {
                        if(tempArray[i].charAt(0) == 'p'){                                             PW = tempArray[i].substring(3);                        }
                        else if((tempArray[i].charAt(0) == ' ' && tempArray[i].charAt(1) == 'p')){       PW = tempArray[i].substring(4);                      }

                        else if(tempArray[i].charAt(0) == 'i'){                                        ID = tempArray[i].substring(3);                        }
                        else if((tempArray[i].charAt(0) == ' ' && tempArray[i].charAt(1) == 'i')){   ID = tempArray[i].substring(4);                         }

                        else if((tempArray[i].charAt(2) == 'a')){   userName = tempArray[i].substring(6);                         }


                    }

                    Log.e("TAG", "zero: " + PW + " one: " + ID);

                    if (userId.equals(ID) && userPass.equals(PW)) {
                        check = true;
                        intent.putExtra("currentId", userId);
                        intent.putExtra("currentName", userName);
                        startActivity(intent);
                        break;
                    }





                }
                if(!check)
                    Toast.makeText(MainActivity.this, "Please check your id and password or try again", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("loger", "faile to read value");
                Log.v("loger", databaseError.toString());
            }
        };
        uDatabase.addValueEventListener(postListener);
    }


}
