package com.example.clova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private Button buttonSignUp;

    boolean verified = false; //이메일 인증 확인 boolean 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d("into", "로그인이야");
        firebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 객체 얻기
        firebaseAuth.useAppLanguage(); // 사용자 디바이스 언어에 맞게 이메일 언어 보내기
        FirebaseUser user = firebaseAuth.getCurrentUser(); //현재 사용자 정보

        //Log.d("login-oncreate", firebaseAuth.getCurrentUser().toString());

        editTextEmail = (EditText) findViewById(R.id.edittext_email);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);
        buttonLogIn = (Button) findViewById(R.id.btn_login);

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), firebaseAuth);
                    // loginUser 함수 call -> dialog 제어, db에서 유저정보 찾아서 로그인 success
                } else {
                    Toast.makeText(LoginActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

      /*  FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    //Do anything here which needs to be done after user is set is complete
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                else {
                }
            }
        }; */

      //  firebaseAuth.addAuthStateListener(authStateListener);

    /*   firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("login-user-listener", firebaseAuth.toString());
                if (user != null) { //사용자 정보가 있으면,
                    Log.d("login-user-listener", "not null");
                 //   Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                 //   startActivity(intent);
                }
                else {
                    Log.d("login-user-listener", "null");
                }
            }
        };*/

        buttonSignUp = (Button) findViewById(R.id.btn_signup);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SignUpActivity 연결
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    public void loginUser(String email, String password, FirebaseAuth auth) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("login-loginuser funtion", email);

                        FirebaseUser loginuser = auth.getCurrentUser();

                        if(!task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {   // 로그인 성공
                            verified = loginuser.isEmailVerified(); //이메일 인증 ok - true, no - false
                            Log.d("login-user-veri-in", loginuser.toString());

                            if(!verified) { //인증하러가라
                                dialog(loginuser);
                                Log.d("login-user-veri-no", loginuser.toString());
                            }
                            else { // 이미 인증했다
                                Log.d("login-user-veri ok", loginuser.toString());
                                // firebaseAuth.addAuthStateListener(firebaseAuthListener);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }

    private void dialog(FirebaseUser user){
        AlertDialog.Builder dlg = new AlertDialog.Builder(LoginActivity.this);
        dlg.setTitle("이메일 인증 관련 "); //제목
        dlg.setMessage("안녕하세요 Clova입니다. 어플을 사용하기 앞서 이메일 인증을 해주셔야 사용이 가능합니다. \n 원치 않으신 분은 No를 눌러주세요."); // 메시지
        //  dlg.setIcon(R.drawable.deum); // 아이콘 설정

        dlg.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                //토스트 메시지
                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("login-email veri", "Email sent.");
                                }
                            }
                        });
                Toast.makeText(LoginActivity.this,"해당 이메일을 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
        dlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoginActivity.this,"이메일을 원치 않으십니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dlg.show();
    }

    @Override
    protected void onStart() { //자동로그인
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}