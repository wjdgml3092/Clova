package com.example.clova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView tex1, tex2;
    EditText me;
    Button bnt_logout, btn_memberdelete, btn_mess;
    String user_id = null;
    private FirebaseAuth user_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tex1 = findViewById(R.id.te1);
        tex2= findViewById(R.id.te2);
        bnt_logout = findViewById(R.id.btn1);
        btn_memberdelete = findViewById(R.id.btn2);
        btn_mess = findViewById(R.id.btn3);
        me = findViewById(R.id.me);        

        user_auth = FirebaseAuth.getInstance(); //로그아웃 위해 필요함

        FirebaseUser user = user_auth.getCurrentUser();

        //firebase 정의
        final FirebaseFirestore user_table = FirebaseFirestore.getInstance();
        
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
           tex1.setText(email);

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
            user_id = uid; // 아이디 변수에 넣기
            tex2.setText(uid);
        }
        else{
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        bnt_logout.setOnClickListener(new View.OnClickListener() { //로그아웃
            @Override
            public void onClick(View v) {
                user_auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_memberdelete.setOnClickListener(new View.OnClickListener() { //회원탈퇴
            @Override
            public void onClick(View v) {
                Log.d("delete-member-btnclick", "버튼을 누르긴 했어,,");
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                                    filed_Delete(user_table, user_id);
                                    Log.d("delete-member", "성공");
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                }
                                else{
                                    Log.d("delete-member-err", "탈퇴 실패");
                                }
                            }
                        });
            }
        });
        
        btn_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = me.getText().toString();

                Map<String, Object> user_mes = new HashMap<>();
                user_mes.put("message", message);

                user_table.collection("User").document(user_id)
                        .set(user_mes)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("User", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("User", "Error writing document", e);
                            }
                        });
            }
        });
    }

    void filed_Delete(FirebaseFirestore user_table, String user_id){
    //    DocumentReference docRef = user_table.collection("User").document(user_id);

        user_table.collection("User").document(user_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("User-delete", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("User-delete", "Error deleting document", e);
                    }
                });
    }
}