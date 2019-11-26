package com.example.arit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    EditText idEdit, passEdit, nameEdit, phoneEdit;
    Button singupButton, cancelButton;
    private DatabaseReference uDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        idEdit = findViewById(R.id.idEdit2);
        passEdit = findViewById(R.id.passEdit2);
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);

        singupButton = findViewById(R.id.signupButton);
        cancelButton = findViewById(R.id.cancelButton);

        uDatabase = FirebaseDatabase.getInstance().getReference("user");



        final Intent intent = new Intent(SignIn.this, MainActivity.class);
        final Intent intent2 = new Intent(SignIn.this, Home.class);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        singupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check = checkSignup();
                if(check == 5){
                    Toast.makeText(SignIn.this, "Correct", Toast.LENGTH_SHORT).show();

                    postUserInfo(idEdit.getText().toString(), passEdit.getText().toString(), nameEdit.getText().toString(), phoneEdit.getText().toString());
                    startActivity(intent);


                }
                else{
                    if(check == 0){  Toast.makeText(SignIn.this, "Please check ID and enter again", Toast.LENGTH_SHORT).show();    }
                    if(check == 1){  Toast.makeText(SignIn.this, "Please check PW and enter again", Toast.LENGTH_SHORT).show();    }
                    if(check == 2){  Toast.makeText(SignIn.this, "Please check Name and enter again", Toast.LENGTH_SHORT).show();    }
                    if(check == 3){  Toast.makeText(SignIn.this, "Please check Phone Number and enter again", Toast.LENGTH_SHORT).show();    }

                }



            }
        });






    }

    private int checkSignup() {

        int id = 0, pass = 0, name = 0, phone = 0;
        Log.e("tag", "WOT: " + idEdit.length() );

        if(idEdit.length() >= 2)
        {
            id++;
        }
        if(passEdit.length() >= 2)
        {
            pass++;
        }
        if(nameEdit.length() >= 2)
        {
            name++;
        }
        if(phoneEdit.length() >= 2)
        {
            phone++;
        }


        if(id > 0 && pass > 0 && name > 0 && phone > 0)
        {
            return 5;
        }

        else{

            if(id == 0){                return 0;            }
            else if(pass == 0){         return 1;             }
            else if(name == 0){         return 2;            }
            else if(phone == 0){        return 3;            }
        }

        return 0;
    }


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
        Log.e("tag", "WORK: "+userUpdates.toString());
        idEdit.setText("");
        passEdit.setText("");
        nameEdit.setText("");
        phoneEdit.setText("");
    }





}
