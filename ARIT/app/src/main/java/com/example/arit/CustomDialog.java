package com.example.arit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomDialog extends DialogFragment{
    public interface OnCompleteListener{
        void onInputedData(String id, String pass);
    }

    private OnCompleteListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnCompleteListener) activity;
        }
        catch (ClassCastException e) {
            Log.d("DialogFragmentExample", "Activity doesn't implement the OnCompleteListener interface");
        }
    }

    String name;
    String phone;
    String password;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(view);
        final Button submit = (Button) view.findViewById(R.id.edit);
        final EditText nameET = (EditText) view.findViewById(R.id.name);
        final EditText phoneET = (EditText) view.findViewById(R.id.phone);
        final EditText passwordET = (EditText) view.findViewById(R.id.password);


        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = nameET.getText().toString();
                phone = phoneET.getText().toString();
                password = passwordET.getText().toString();

                Intent data = new Intent();
                data.putExtra ("name", name );
                data.putExtra("phone", phone);
                data.putExtra ("password", password );

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);

                dismiss();
            }
        });

        return builder.create();
    }
}
