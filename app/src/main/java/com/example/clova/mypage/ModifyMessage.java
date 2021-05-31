package com.example.clova.mypage;

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
import android.widget.Toast;

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

public class ModifyMessage extends Fragment {
EditText modify_message;
Button registerBtn;
FirebaseAuth user_auth;
FirebaseFirestore firebaseFirestore;
Map<String , Object> user_count = new HashMap<>();
String str_count = null;
String user_id = null;
public ModifyMessage() {
    }

    public static ModifyMessage newInstance(String param1, String param2) {
        ModifyMessage fragment = new ModifyMessage();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_message, container, false);

        modify_message = view.findViewById(R.id.modify_message);
        registerBtn = view.findViewById(R.id.registerBtn);

        user_auth = FirebaseAuth.getInstance(); //로그아웃 위해 필요함
        FirebaseUser user = user_auth.getCurrentUser(); //현재 사용자 받아오기
        firebaseFirestore = FirebaseFirestore.getInstance();

        String uid = user.getUid();
        user_id = uid; // 아이디 변수에 넣기
        call_count(user_id);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> user_info = new HashMap<>();
                String message = modify_message.getText().toString();

                user_info.put("message", message);
                user_info.put("count", str_count);

                firebaseFirestore.collection("User").document(user_id)
                        .set(user_info)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("User", "DocumentSnapshot successfully written!");
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                MyPageFragment myPageFragment = new MyPageFragment();
                                transaction.replace(R.id.main_frame, myPageFragment);
                                transaction.commit();
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
    void call_count(String user_id) {
        DocumentReference docRef = firebaseFirestore.collection("User").document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_count = document.getData();

                        str_count = (String) user_count.get("count");
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
    }