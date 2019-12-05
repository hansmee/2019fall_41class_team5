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

    EditText idEdit;    //User ID
    EditText passEdit;  //User PW

    Button loginButton;
    Button signupButton;
    String userId;
    String userPass;
    String userName;


    private DatabaseReference uDatabase;
    SharedPreferences loginPref;
    boolean check = false;
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


        final Intent intent = new Intent(MainActivity.this, Home.class);
        final Intent intent2 = new Intent(MainActivity.this, SignIn.class);



        ///////////////////////////////Check preference/////////////////////////////////////////////
        final SharedPreferences.Editor editor = loginPref.edit();
        String idValue = loginPref.getString("userid", null);
        if (getIntent().getStringExtra("logout") != null && getIntent().getStringExtra("logout").equals("Logout")) {
            editor.clear();
            editor.commit();
        }
        if (idValue != null) {
            intent.putExtra("currentId", idValue);
            intent.putExtra("currentName", idValue);
            startActivity(intent);
            finish();
        }
        ///////////////////////////////Check preference/////////////////////////////////////////////




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = false;
                //v.startAnimation(buttonClick);
                if (idEdit.getText() != null && passEdit.getText() != null) {
                    userId = idEdit.getText().toString();
                    userPass = passEdit.getText().toString();
                    getUserDatabase(intent, editor);

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
    public void getUserDatabase(final Intent intent, final SharedPreferences.Editor editor) {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> map = snapshot.child("").getValue(genericTypeIndicator);

                    String tempStorage = map.toString();
                    String tempArray[] = tempStorage.replaceAll("[{}]", "").split(",");

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
                        editor.putString("userid", ID);
                        editor.putString("userName", userName);
                        editor.commit();

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
