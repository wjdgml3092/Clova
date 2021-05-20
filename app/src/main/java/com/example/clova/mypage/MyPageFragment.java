package com.example.clova.mypage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clova.LoginActivity;
import com.example.clova.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyPageFragment extends Fragment {

    TextView tex1, tex2;
    EditText me;
    Button bnt_logout, btn_memberdelete, btn_mess;
    String user_id = null;
    private FirebaseAuth user_auth;
    FirebaseFirestore firebaseFirestore;
    Map<String , Object> user_count = new HashMap<>();
    String str_count = null;

    public MyPageFragment() {
        // Required empty public constructor
    }

    public static MyPageFragment newInstance() {
       MyPageFragment myPageFragment = new MyPageFragment();
       return myPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        tex1 = view.findViewById(R.id.te1);
        tex2= view.findViewById(R.id.te2);
        bnt_logout = view.findViewById(R.id.btn1);
        btn_memberdelete = view.findViewById(R.id.btn2);
        btn_mess = view.findViewById(R.id.btn3);
        me = view.findViewById(R.id.me);

        user_auth = FirebaseAuth.getInstance(); //로그아웃 위해 필요함

        FirebaseUser user = user_auth.getCurrentUser(); //현재 사용자 받아오기

        //firebase 정의
        firebaseFirestore = FirebaseFirestore.getInstance();

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
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            //finish();
        }

        bnt_logout.setOnClickListener(new View.OnClickListener() { //로그아웃
            @Override
            public void onClick(View v) {
                user_auth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();  //finish() 사용법 -> 현재 조각이 연결된 활동을 완료한다!
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
                                    Toast.makeText(getActivity(), "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                                    filed_Delete(user_id);
                                    Log.d("delete-member", "성공");
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                    getActivity().finish();
                                }
                                else{
                                    Log.d("delete-member-err", "탈퇴 실패");
                                }
                            }
                        });
            }
        });

        call_count(user_id); // count 값 가져오기

        btn_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = me.getText().toString();

                Map<String, Object> user_info = new HashMap<>();
                user_info.put("message", message);
                user_info.put("count", str_count);

                firebaseFirestore.collection("User").document(user_id)
                        .set(user_info)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("User", "DocumentSnapshot successfully written!");
                                Toast.makeText(getActivity(), "응원메시지가 입력되었습니다!", Toast.LENGTH_SHORT).show();
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


        return view;
    }

    void call_count(String user_id){
        DocumentReference docRef = firebaseFirestore.collection("User").document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_count = document.getData();

                        str_count = (String)user_count.get("count");
                        Log.d("mypage-count", str_count);
                    } else {
                        Log.d("write", "No such document");
                    }
                } else {
                    Log.w("diary", "get failed with ", task.getException());
                }
            }
        });
    }

    void filed_Delete(String user_id){
        //    DocumentReference docRef = user_table.collection("User").document(user_id);

        firebaseFirestore.collection("User").document(user_id)
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