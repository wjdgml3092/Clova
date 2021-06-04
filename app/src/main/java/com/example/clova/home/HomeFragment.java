package com.example.clova.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clova.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    TextView load, record_title, message, number_write, happy_txt, funny_txt,
            sad_txt, angry_txt, hash1, hash2, hash3, rate;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user;

    String user_id = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        id_init(view);

        user = firebaseAuth.getCurrentUser();
        user_id = user.getUid();

        call_feel(user_id); // 감정 통계

        call_user(user_id); // 작성횟수, 해시태그 통계, 달성률

        return view;

    }

    void id_init(View view) {
        load = view.findViewById(R.id.load);
        record_title = view.findViewById(R.id.record_title);
        message = view.findViewById(R.id.message);
        number_write = view.findViewById(R.id.number_write);
        happy_txt = view.findViewById(R.id.happy_txt);
        funny_txt = view.findViewById(R.id.funny_txt);
        sad_txt = view.findViewById(R.id.sad_txt);
        angry_txt = view.findViewById(R.id.angry_txt);
        hash1 = view.findViewById(R.id.hash1);
        hash2 = view.findViewById(R.id.hash2);
        hash3 = view.findViewById(R.id.hash3);
        rate = view.findViewById(R.id.rate);
    }

    void call_user(String user_id) {
        DocumentReference docRef = firebaseFirestore.collection("User").document(user_id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> user_info = new HashMap<>(); //message 스트링 얻어오기위한 map
                        user_info = document.getData();
                        String get_nick = (String) user_info.get("nickname");
                        String get_mess = (String) user_info.get("message");
                        String get_cnt = (String) user_info.get("count");

                        Log.d("diary-yes", "DocumentSnapshot data: " + document.getData());
                        record_title.setText(get_nick + " 님의 기록");
                        message.setText(get_mess);
                        number_write.setText(get_cnt + " 회");

                        call_rate(get_cnt); // 달성률

                        call_hash(user_id, get_cnt);
                    } else {
                        Log.d("diary-null", "No such document");
                    }
                } else {
                    Log.d("diary-no", "get failed with ", task.getException());
                }
            }
        });
    }

    void call_rate(String write_cnt){
        Log.d("rate", "들어옴");
        Log.d("hash-cnt", write_cnt);

        int count = Integer.parseInt(write_cnt);
        int result = count % 30;

        //초기상태의 0프로를 위해서, 왜냐면 60일일 때 result가 0이 되는데 그때는 100프로여야 하니깐
        //count만 제어만 -1로 해서 0%출력
        if(count == 0)
            result = -1;

        if(result == -1)
            rate.setText("0%");
        else if(result == 0){
            rate.setTextColor(Color.RED);
            rate.setText("100%");
        }
        else if(result == 1)
            rate.setText("1%");
        else if(result <= 3)
            rate.setText("3%");
        else if(result <= 5)
            rate.setText("10%");
        else if (result <= 8)
            rate.setText("20%");
        else if (result <= 10)
            rate.setText("30%");
        else if (result <= 13)
            rate.setText("40%");
        else if (result <= 15) {
            rate.setTextColor(Color.rgb(67,116,217));
            rate.setText("50%");
        }
        else if (result <= 17) {
            rate.setTextColor(Color.rgb(67,116,217));
            rate.setText("60%");
        }
        else if (result <= 20) {
            rate.setTextColor(Color.BLUE);
            rate.setText("70%");
        }
        else if (result <= 23) {
            rate.setTextColor(Color.BLUE);
            rate.setText("80%");
        }
        else if (result <= 25) {
            rate.setTextColor(Color.rgb(71,200,62));
            rate.setText("90%");
        }
        else if (result <= 27) {
            rate.setTextColor(Color.rgb(71,200,62));
            rate.setText("95%");
        }
        else if (result <= 29) {
            rate.setTextColor(Color.GREEN);
            rate.setText("98%");
        }
        else if(result <= 30){
            rate.setTextColor(Color.RED);
            rate.setText("100%");
        }
    }

    void call_hash(String user_id, String get_cnt) {
        Log.d("hash", "들어옴");
        Log.d("hash-cnt", get_cnt);
        DocumentReference docRef = firebaseFirestore.collection("Diary").document(user_id)
                .collection(get_cnt).document(user_id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> user_hash = new HashMap<>(); //message 스트링 얻어오기위한 map
                        user_hash = document.getData();
                        String get_hash1 = (String) user_hash.get("hashtag1");
                        String get_hash2 = (String) user_hash.get("hashtag2");
                        String get_hash3 = (String) user_hash.get("hashtag3");

                        Log.d("hash-1", get_hash1);
                        Log.d("hash-2", get_hash2);
                        Log.d("hash-3", get_hash3);

                        //hash1,2,3 setText
                        hash1.setText("# " + get_hash1);
                        hash2.setText("# " + get_hash2);
                        hash3.setText("# " + get_hash3);

                    } else {
                        Log.d("diary-null", "No such document");
                    }
                } else {
                    Log.d("diary-no", "get failed with ", task.getException());
                }
            }
        });
    }

    void call_feel(String user_id) {
        DocumentReference happyRef = firebaseFirestore.collection("Feel").document(user_id)
                .collection("행복").document(user_id);

        DocumentReference funRef = firebaseFirestore.collection("Feel").document(user_id)
                .collection("웃김").document(user_id);

        DocumentReference sadRef = firebaseFirestore.collection("Feel").document(user_id)
                .collection("슬픔").document(user_id);

        DocumentReference angryRef = firebaseFirestore.collection("Feel").document(user_id)
                .collection("화남").document(user_id);

        happyRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        load.setVisibility(View.INVISIBLE);
                        Map<String, Object> user_feel = new HashMap<>(); //message 스트링 얻어오기위한 map
                        user_feel = document.getData();
                        String get_feel = (String) user_feel.get("feel_count");

                        Log.d("feel cnt", get_feel);

                        happy_txt.setText(get_feel);

                        user_feel.clear();

                    } else {
                        Log.d("diary-null", "No such document");
                    }
                } else {
                    Log.d("diary-no", "get failed with ", task.getException());
                }
            }
        });

        funRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> user_feel = new HashMap<>(); //message 스트링 얻어오기위한 map
                        user_feel = document.getData();
                        String get_feel = (String) user_feel.get("feel_count");

                        Log.d("feel cnt", get_feel);

                        funny_txt.setText(get_feel);

                        user_feel.clear();

                    } else {
                        Log.d("diary-null", "No such document");
                    }
                } else {
                    Log.d("diary-no", "get failed with ", task.getException());
                }
            }
        });

        sadRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> user_feel = new HashMap<>(); //message 스트링 얻어오기위한 map
                        user_feel = document.getData();
                        String get_feel = (String) user_feel.get("feel_count");

                        Log.d("feel cnt", get_feel);


                        sad_txt.setText(get_feel);

                        user_feel.clear();

                    } else {
                        Log.d("diary-null", "No such document");
                    }
                } else {
                    Log.d("diary-no", "get failed with ", task.getException());
                }
            }
        });

        angryRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> user_feel = new HashMap<>(); //message 스트링 얻어오기위한 map
                        user_feel = document.getData();
                        String get_feel = (String) user_feel.get("feel_count");

                        Log.d("feel cnt", get_feel);

                        angry_txt.setText(get_feel);

                        user_feel.clear();

                    } else {
                        Log.d("diary-null", "No such document");
                    }
                } else {
                    Log.d("diary-no", "get failed with ", task.getException());
                }
            }
        });

    }
}