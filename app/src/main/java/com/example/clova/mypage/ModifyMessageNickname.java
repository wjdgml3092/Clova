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
import android.widget.TextView;
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

public class ModifyMessageNickname extends Fragment {
    TextView text_mess, text_nick;
    EditText modify_message, modify_nickname;
    Button registerMessBtn, registerNickBtn ;

    FirebaseAuth user_auth;
    FirebaseFirestore firebaseFirestore;
    Map<String, Object> user_count = new HashMap<>();
    String str_count = null;
    String user_id = null;
    String str_nickname = null;
    String str_message = null;

    public ModifyMessageNickname() {
    }

    public static ModifyMessageNickname newInstance() {
        ModifyMessageNickname fragment = new ModifyMessageNickname();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_messagenickname, container, false);

        id_init(view);

        user_auth = FirebaseAuth.getInstance(); //로그아웃 위해 필요함
        FirebaseUser user = user_auth.getCurrentUser(); //현재 사용자 받아오기
        firebaseFirestore = FirebaseFirestore.getInstance();

        String uid = user.getUid();
        user_id = uid; // 아이디 변수에 넣기

        call_count(user_id); // 현재 닉네임, 메시지 받아오기

        registerMessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = modify_message.getText().toString();

                if (message.equals("")) {
                    Toast.makeText(getActivity(), "변경 메시지를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(message.equals(str_message)){
                    Toast.makeText(getActivity(), "변경 전과 후 메시지가 동일합니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Map<String, Object> user_info = new HashMap<>();

                    user_info.put("message", message);
                    user_info.put("count", str_count);
                    user_info.put("nickname", str_nickname);

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
                                    Toast.makeText(getActivity(), "응원메시지가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("User", "Error writing document", e);
                                }
                            });
                }
            }
        });

        registerNickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = modify_nickname.getText().toString();

                if (nickname.equals("")) {
                    Toast.makeText(getActivity(), "변경 닉네임을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(nickname.equals(str_nickname)){
                    Toast.makeText(getActivity(), "변경 전과 후 닉네임이 동일합니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Map<String, Object> user_info = new HashMap<>();

                    user_info.put("message", str_message);
                    user_info.put("count", str_count);
                    user_info.put("nickname", nickname);

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
                                    Toast.makeText(getActivity(), "닉네임이 변경되었습니다!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("User", "Error writing document", e);
                                }
                            });
                }
            }
        });
        return view;
    }

    void id_init(View view){
        text_mess = view.findViewById(R.id.text_mess);
        text_nick = view.findViewById(R.id.text_nick);

        modify_message = view.findViewById(R.id.modify_message);
        modify_nickname = view.findViewById(R.id.modify_nickname);
        registerMessBtn = view.findViewById(R.id.registerMessBtn);
        registerNickBtn = view.findViewById(R.id.registerNickBtn);
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
                        str_nickname = (String) user_count.get("nickname");
                        str_count = (String) user_count.get("count");
                        str_message = (String) user_count.get("message");

                        text_mess.setText(str_message);
                        text_nick.setText(str_nickname);

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