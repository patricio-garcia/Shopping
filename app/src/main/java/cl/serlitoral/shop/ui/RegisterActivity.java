package cl.serlitoral.shop.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import cl.serlitoral.shop.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Button btnSignUp;
    private EditText etName, etPhone, etEmail, etPassword, etConfirmPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSignUp = findViewById(R.id.btn_registerSignUp);
        etName = findViewById(R.id.et_registerName);
        etPhone = findViewById(R.id.et_registerPhone);
        etEmail = findViewById(R.id.et_registerEmail);
        etPassword = findViewById(R.id.et_registerPassword);
        etConfirmPassword = findViewById(R.id.et_registerConfirmPassword);
        loadingBar = new ProgressDialog(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Ingrese su nombre");
            return;
        }
        else if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Ingrese su número de contactao");
            return;
        } else if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese su correo electrónico");
            return;
        }
        else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingrese su contraseña");
            return;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirme su contraseña");
            return;
        } else if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas son diferentes");
            return;
        } else if (password.length()<6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        } else if (!isValidEmail(email)) {
            etEmail.setError("Correo electrónico inválido");
            return;
        }

       loadingBar.setTitle("Creando Cuenta");
       loadingBar.setMessage("Espere mientras se validan las credenciales");
       loadingBar.setCanceledOnTouchOutside(false);
       loadingBar.show();


       //Crear cuenta usando correo electrónico
       mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()) {
                   Toast.makeText(RegisterActivity.this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();

                   //Almacenar Datos en CloudSotre
                   AddUser(name, phone, email, password);

                   startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                   finish();
               }
               else {
                   Toast.makeText(RegisterActivity.this, "Error al crear la cuenta, intente más tarde", Toast.LENGTH_SHORT).show();
               }
               loadingBar.dismiss();
           }
       });
    }

    private void AddUser(String name, String phone, String email, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("phone", phone);
        user.put("email", email);
        user.put("password", password);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RegisterActivity.this, "Datos almacenados de forma correcta", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "No se pudieron almacenar los datos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


}