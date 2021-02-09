package cl.serlitoral.shop.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import cl.serlitoral.shop.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnSignIn;
    private EditText etEmail, etPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_loginEmail);
        etPassword = findViewById(R.id.et_loginPassword);
        btnSignIn = findViewById(R.id.btn_loginSignIn);
        loadingBar = new ProgressDialog(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese su correo electrónico");
            return;
        }
        else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingrese su contraseña");
            return;
        }  else if (password.length()<4) {
            etPassword.setError("La contraseña debe tener al menos 4 caracteres");
            return;
        } else if (!isValidEmail(email)) {
            etEmail.setError("Correo electrónico inválido");
            return;
        }

        loadingBar.setTitle("Validación de Usuario");
        loadingBar.setMessage("Espere mientras se validan las credenciales");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        //Login usando correo electrónico
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Usuario validado", Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error, no se pudo validar el usuario", Toast.LENGTH_LONG).show();
            }
        });
        loadingBar.dismiss();
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}