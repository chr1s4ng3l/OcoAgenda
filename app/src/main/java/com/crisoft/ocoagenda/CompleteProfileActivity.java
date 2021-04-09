package com.crisoft.ocoagenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteProfileActivity extends AppCompatActivity {

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    TextInputEditText mTextInputName, mTextInputPhone;
    Button mButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mTextInputName = findViewById(R.id.textCompleteName);
        mTextInputPhone = findViewById(R.id.textCompletePhone);
        mButtonRegister = findViewById(R.id.btnCompleteRegister);
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register() {
        String name = mTextInputName.getText().toString();
        String phone = mTextInputPhone.getText().toString();

        if (!name.isEmpty()) {
            if (!phone.isEmpty()) {
                UpdateUser(name, phone);
            } else {
                mTextInputPhone.setError("Campo obligatorio");
            }
        } else {
            mTextInputName.setError("Campo obligatorio");
        }

    }

    private void UpdateUser(final String name, final String phone) {
        //get id user
        String id = mAuth.getCurrentUser().getUid();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone", phone);
        mFirestore.collection("Users").document(id).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CompleteProfileActivity.this, "No se pudo almacenar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}