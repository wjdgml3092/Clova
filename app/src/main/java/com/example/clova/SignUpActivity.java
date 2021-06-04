package com.example.clova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail, editTextPassword, editTextPassword_check;
    private TextInputLayout password_check, email_check, password_vali_check;
    private Button btn_join;
    private TextView email, password;
    String emailVali = null; //유효성 검사 이메일, 비밀번호
    String passWordVali = null;
    //이메일 정규식
    private String emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private String passwordValidation = "^.*(?=^.{8,20}$)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$";
    //이메일 인증
    FirebaseUser user;
    CheckBox checkBox, checkBox2, checkBox3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser(); //이메일 인증 함수 내에서 사용할 객체체

        //findViewById초기화
        id_init();

        //email정규식 검사
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailVali = editTextEmail.getText().toString();
                if (emailVali.matches(emailValidation) && emailVali.length() > 0) {
                    email_check.setError(null);
                } else {
                    email_check.setError("이메일 형식이 옳바르지 않습니다.");
                }
            }
        });

        //password정규식 검사
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                passWordVali = editTextPassword.getText().toString();
                if (passWordVali.matches(passwordValidation) && passWordVali.length() > 0) {
                    password_vali_check.setError(null);
                } else {
                    password_vali_check.setError("최소 8자이상 숫자,문자,특수문자를 입력해주세요");
                }
            }
        });
        //비밀번호 일치검사
        editTextPassword_check.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("editPass", editTextPassword.toString());
                Log.d("editPass_check", editTextPassword_check.toString());

                if (editTextPassword.getText().toString().equals(editTextPassword_check.getText().toString())) {
                    password_check.setError(null);
                } else {
                    password_check.setError("일치하지않습니다.");

                }
            }
        });
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(true);
                } else {
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                }
            }
        });
        //2 클릭시
        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //만약 전체 클릭이 true 라면 false로 변경
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    //각 체크박스 체크 여부 확인해서  전체동의 체크박스 변경
                } else if (checkBox2.isChecked() && checkBox3.isChecked()) {
                    checkBox.setChecked(true);
                }
            }
        });
        //3 클릭시
        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else if (checkBox2.isChecked() && checkBox3.isChecked()) {
                    checkBox.setChecked(true);
                }
            }
        });

        //이용약관 버튼 - 서비스
        Button btn_agr = findViewById(R.id.btn_agr);
        btn_agr.setText(R.string.underlined_text);
        btn_agr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                //다이얼로그 창의 제목 입력
                builder.setTitle("서비스 이용약관 ");
                //다이얼로그 창의 내용 입력
                builder.setMessage(R.string.app_arg); //이용약관 내용 추가  ,예시는 res-values-string 에 추가해서 사용
                //다이얼로그창에 취소 버튼 추가
                builder.setNegativeButton("닫기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("이용약관 닫기");
                            }
                        });
                //다이얼로그 보여주기
                builder.show();
            }
        });

        //이용약관 버튼2 - 개인정보
        Button btn_agr3 = findViewById(R.id.btn_agr2);
        btn_agr3.setText(R.string.underlined_text);
        btn_agr3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                //다이얼로그 창의 제목 입력
                builder.setTitle("개인정보처리방침 ");
                //다이얼로그 창의 내용 입력
                builder.setMessage(R.string.app_arg3); //이용약관 내용 추가 , 예시는 res-values-string 에 추가해서 사용
                //다이얼로그창에 취소 버튼 추가
                builder.setNegativeButton("닫기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // System.out.println(TAG + "이용약관 닫기");
                                System.out.println("이용약관 닫기");
                            }
                        });
                //다이얼로그 보여주기
                builder.show();
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                    createUser(emailVali, passWordVali);
                } else {
                    // 이메일과 비밀번호가 공백인 경우
                    Toast.makeText(SignUpActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공시
                            Toast.makeText(SignUpActivity.this, "회원가입 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignUpActivity.this, "입력한 정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    void id_init() {
        //전체동의
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        //필수 서비스이용약관
        checkBox2 = (CheckBox) findViewById(R.id.checkbox2);
        //필수 개인정보
        checkBox3 = (CheckBox) findViewById(R.id.checkbox3);

        editTextEmail = (EditText) findViewById(R.id.editText_email);
        email_check = (TextInputLayout) findViewById(R.id.find_loginID_text_input);
        editTextPassword = (EditText) findViewById(R.id.editText_passWord);
        editTextPassword_check = (EditText) findViewById(R.id.editText_passWord_check);
        password_vali_check = (TextInputLayout) findViewById(R.id.find_input_password);
        password_check = (TextInputLayout) findViewById(R.id.find_input_password_check);
        btn_join = (Button) findViewById(R.id.btn_join);
    }
}