package com.crisoft.ocoagenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    CircleImageView mCircleImageViewBack;
    TextInputEditText mTextInputName, mTextInputEmail, mTextInputPhone, mTextInputPassword, mTextInputConfirmPassword;
    Button mButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mTextInputName = findViewById(R.id.textViewUserName);
        mTextInputEmail = findViewById(R.id.textViewRegisterEmail);
        mTextInputPhone = findViewById(R.id.textInputPhone);
        mTextInputPassword = findViewById(R.id.textViewRegisterPassword);
        mTextInputConfirmPassword = findViewById(R.id.textViewConfirmPassword);
        mButtonRegister = findViewById(R.id.btnRegister);
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void register() {
        String name = mTextInputName.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String phone = mTextInputPhone.getText().toString();
        String password = mTextInputPassword.getText().toString();
        String confirmPassword = mTextInputConfirmPassword.getText().toString();

        if (!name.isEmpty()) {
            if (!email.isEmpty()) {
                if (!phone.isEmpty()) {
                    if (!password.isEmpty()) {
                        if (!confirmPassword.isEmpty()) {
                            if (password.length() >= 6) {
                                if (password.equals(confirmPassword)) {
                                    if (isEmailValid(email)) {

                                        createUser(name, email, phone, password);

                                    } else {
                                        mTextInputEmail.setError("Correo no valido");
                                    }

                                } else {
                                    mTextInputConfirmPassword.setError("Las contraseñas no coinciden");
                                }
                            } else {
                                mTextInputPassword.setError("Contraseña muy corta");
                            }
                        } else {
                            mTextInputConfirmPassword.setError("Confirme su contraseña");
                        }
                    } else {
                        mTextInputPassword.setError("Campo obligatorio");
                    }
                } else {
                    mTextInputPhone.setError("Campo obligatorio");
                }
            } else {
                mTextInputEmail.setError("Campo obligatorio");
            }
        } else {
            mTextInputName.setError("Campo obligatorio");
        }

    }

    private void createUser(final String name, final String email, final String phone, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //get id user
                    String id = mAuth.getCurrentUser().getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("phone", phone);
                    mFirestore.collection("Users").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Usuario almacenado correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "No se pudo almacenar el usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Toast.makeText(RegisterActivity.this, "Se registro el usuario", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Ocurrio un error, No se registro el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //VALIDAR QUE SEA UN CORREO VALIDO
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}