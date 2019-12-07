package com.example.arit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    EditText idEdit;
    EditText passEdit;
    EditText nameEdit;
    EditText phoneEdit;

    Button singupButton;
    Button cancelButton;


    private DatabaseReference uDatabase;
    int exists = 0, flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);
        actionBar.setElevation(0);

        idEdit = findViewById(R.id.idEdit2);
        passEdit = findViewById(R.id.passEdit2);
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        singupButton = findViewById(R.id.signupButton);
        cancelButton = findViewById(R.id.cancelButton);
        uDatabase = FirebaseDatabase.getInstance().getReference("user");



        final Intent intent = new Intent(SignUp.this, MainActivity.class);
        final Intent intent2 = new Intent(SignUp.this, FrameLayout.class);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        singupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exists = 0; flag = 0;
                checkandPost(idEdit.getText().toString(), passEdit.getText().toString(), nameEdit.getText().toString(), phoneEdit.getText().toString(), intent2);
            }
        });
    }



    public void checkandPost(final String testID, final String PW, final String NAME, final String PHONE, final Intent intent2) {
        uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(testID) && testID.length() >= 2) {
                    Toast.makeText(SignUp.this, "ID already exits", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int check = checkSignup(testID, PW, NAME, PHONE);

                    if(check == 5){
                        Toast.makeText(SignUp.this, "Congratulations you have signed up to ARIT", Toast.LENGTH_SHORT).show();

                        String idFinal = idEdit.getText().toString();
                        String nameFinal = nameEdit.getText().toString();
                        postUserInfo(idFinal, passEdit.getText().toString(), nameFinal, phoneEdit.getText().toString());
                        flag = 1;
                        intent2.putExtra("currentId", idFinal);
                        intent2.putExtra("currentName", nameFinal);
                        startActivity(intent2);
                        finish();
                    }
                    else{
                        if(check == 0){  Toast.makeText(SignUp.this, "ID must be at least 2 characters long", Toast.LENGTH_SHORT).show();    }
                        if(check == 1){  Toast.makeText(SignUp.this, "PW must be at least 8 characters and contain Capital, Lowercase, Number and Special Character", Toast.LENGTH_SHORT).show();    }
                        if(check == 2){  Toast.makeText(SignUp.this, "Please check Name and enter again", Toast.LENGTH_SHORT).show();    }
                        if(check == 3){  Toast.makeText(SignUp.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();    }
                    }

                }



            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private int checkSignup(String ID, String PW, String NAME, String PHONE) {

        int id = 0, pass = 0, name = 0, phone = 0;



        ///////////////////////Regex for PW and phone number requirements///////////////////////////
        Pattern pw = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()])(?=.{8,})");
        Matcher pwMatch = pw.matcher(PW);

        Pattern phoneCheck = Pattern.compile("^((010|011|016|017|018|019)[0-9][0-9][0-9][0-9][0-9][0-9][0-9]([0-9])?|(010|011|016|017|018|019)( )[0-9][0-9][0-9]([0-9])?( )[0-9][0-9][0-9][0-9]|(010|011|016|017|018|019)-[0-9][0-9][0-9]([0-9])?-[0-9][0-9][0-9][0-9]|(010|011|016|017|018|019) - [0-9][0-9][0-9]([0-9])? - [0-9][0-9][0-9][0-9])$");
        Matcher phoneMatch = phoneCheck.matcher(PHONE);
        ///////////////////////Regex for PW and phone number requirements///////////////////////////






        /////////////////////////////////Check Requirements/////////////////////////////////////////
        if(idEdit.length() >= 2)                        {           id++;          }
        if(passEdit.length() >= 2 && pwMatch.find())    {           pass++;        }
        if(nameEdit.length() >= 2 )                     {           name++;        }
        if(phoneEdit.length() >= 2 && phoneMatch.find()){           phone++;       }
        /////////////////////////////////Check Requirements/////////////////////////////////////////






        /////////////////////////Return check value/////////////////////////////////////////////////
        if(id > 0 && pass > 0 && name > 0 && phone > 0){            return 5;        }

        else{
            if(id == 0){                return 0;            }
            else if(pass == 0){         return 1;             }
            else if(name == 0){         return 2;            }
            else if(phone == 0){        return 3;            }
        }
        return 5;
        /////////////////////////Return check value/////////////////////////////////////////////////
    }





    //////////////////////Post new user info to firebase////////////////////////////////////////////
    public void postUserInfo(String one, String two, String three, String four) {

        Map<String, Object> userUpdates = new HashMap<>();
        Map<String, Object> postValues = new HashMap<>();

        String User = one;
        String Password = two;
        String Name = three;
        String PhoneNum = four;

        String All = "{"+User+","+Password+","+Name+","+PhoneNum+"}";
        Log.e("tag", "WOTTT: "+All);

        postValues.put("id", User);
        postValues.put("pw", Password);
        postValues.put("name", Name);
        postValues.put("numphone", PhoneNum);

        userUpdates.put("/"+User, postValues);
        uDatabase.updateChildren(userUpdates);
        idEdit.setText("");
        passEdit.setText("");
        nameEdit.setText("");
        phoneEdit.setText("");
    }
    //////////////////////Post new user info to firebase////////////////////////////////////////////




}
