package com.example.clova.mypage;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    TextView fixtext, write_count;
    Button modify_info, modify_message, notice, QandA ,discard_account ,logout;
    String user_id = null;
    Map<String , Object> user_count = new HashMap<>();
    private FirebaseAuth user_auth;
    FirebaseFirestore firebaseFirestore;
    String str_count = null;
    String email = null;
    final String[] items = {"YES", "NO"};
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
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        fixtext = view.findViewById(R.id.fixText);
        modify_info = view.findViewById(R.id.modify_info);
        modify_message = view.findViewById(R.id.modify_message);
        notice = view.findViewById(R.id.notice);
        QandA = view.findViewById(R.id.QandA);
        write_count = view.findViewById(R.id.write_count);
        discard_account = view.findViewById(R.id.discard_account);
        logout = view.findViewById(R.id.logout);
        user_auth = FirebaseAuth.getInstance(); //로그아웃 위해 필요함
        FirebaseUser user = user_auth.getCurrentUser(); //현재 사용자 받아오기
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (user != null) {
            email = user.getEmail();
            fixtext.setText("- " + email+"님");

            String uid = user.getUid();
            user_id = uid; // 아이디 변수에 넣기
        }
        else{
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            //finish();
        }
        call_count(user_id);

        modify_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = email;
                user_auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("password", "Email sent.");
                                    Toast myToast = Toast.makeText(getContext(),"이메일을 확인하세요.", Toast.LENGTH_SHORT);
                                    myToast.show();
                                }
                            }
                        });
            }
        });
        modify_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                ModifyMessage modifyMessage = new ModifyMessage();
                transaction.replace(R.id.main_frame, modifyMessage);
                transaction.commit();
            }
        });
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Notice notice = new Notice();
                transaction.replace(R.id.main_frame, notice);
                transaction.commit();
            }
        });
        QandA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                QandAFragment QandAFragment = new QandAFragment();
                transaction.replace(R.id.main_frame, QandAFragment);
                transaction.commit();
            }
        });
        discard_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("정말 탈퇴 하시겠습니까?")
                        .setMessage("탈퇴에 관해, ")
                        .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                MyPageFragment myPageFragment = new MyPageFragment();
                                transaction.replace(R.id.main_frame, myPageFragment);
                                transaction.commit();
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                member_delete(user);
                                Log.d("discard", "탈퇴완료");
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                MyPageFragment myPageFragment = new MyPageFragment();
                                transaction.replace(R.id.main_frame, myPageFragment);
                                transaction.commit();
                            }
                        })
                        .show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_auth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();  //finish() 사용법 -> 현재 조각이 연결된 활동을 완료한다!
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
                        write_count.setText("기록일수 : " + str_count + "일");
                    } else {
                        Log.d("write", "No such document");
                    }
                } else {
                    Log.w("diary", "get failed with ", task.getException());
                }
            }
        });
    }

    void member_delete(FirebaseUser user){ //회원탈퇴
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                            filed_Delete(user_id); // 회원 내용 지우기
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

