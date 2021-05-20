package com.example.clova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail, editTextPassword;
    private Button btn_join;
    private TextView email, password;

    //이메일 인증
    FirebaseUser user;

    //비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) findViewById(R.id.editText_email);
        editTextPassword = (EditText) findViewById(R.id.editText_passWord);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pwd);
        btn_join = (Button) findViewById(R.id.btn_join);

        user = firebaseAuth.getCurrentUser(); //이메일 인증 함수 내에서 사용할 객체체

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    // 이메일과 비밀번호가 공백이 아닌 경우
                    createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), password, email);
                } else {
                    // 이메일과 비밀번호가 공백인 경우
                    Toast.makeText(SignUpActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createUser(String email, String password, TextView check_pwd, TextView check_email) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공시
                            check_pwd.setText("비밀번호가 유효합니다.");
                            Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            // 계정이 중복된 경우
                            if(!isValidPasswd())
                                check_pwd.setText("비밀번호는 4-16자와 특수문자를 포함해야합니다.");
                            check_email.setText("이메일이 이미 가입되어있습니다. 다른 이메일을 사용해주세요");
                            // Toast.makeText(SignUpActivity.this, "비밀번호 형식에 알맞지 않습니다. 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        String check_password = editTextPassword.toString();
        if (!PASSWORD_PATTERN.matcher(check_password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

}