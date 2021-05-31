package com.example.clova.diary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.clova.LoginActivity;
import com.example.clova.MainActivity;
import com.example.clova.R;
import com.example.clova.diary.Recycler.DiaryAdapter;
import com.example.clova.diary.Recycler.DiaryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class DiaryLookFragment extends Fragment {

    TextView date, title, feel, hashtag1, hashtag2, hashtag3, content, img_des;
    Button update_btn;
    ImageView img;

    String str_date, str_title, str_feel, str_hash1, str_hash2, str_hash3, str_img, str_content;

    String count; // 전 프래그먼트에서 받아오는 count 값
    String current_cnt; // 현재 count값
    String user_id; // 유저 아이디
    String result_cnt; // 현재랑 전 count 계산 결과 값

    //글 리스트 데이터베이스 변수 관련
    private FirebaseAuth user_auth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    //storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Map<String, Object> user_count = new HashMap<>(); // call_count 사용
    Map<String, Object> list = new HashMap<>(); // callLookList에서 사용
    ArrayList<DiaryData> data = new ArrayList<>();

   // ArrayList<DiaryData> data;

    public DiaryLookFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DiaryLookFragment newInstance() {
        DiaryLookFragment fragment = new DiaryLookFragment();
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
        View view = inflater.inflate(R.layout.fragment_diary_look, container, false);

        id_init(view);

        Bundle bundle = getArguments();  //번들 받기. getArguments() 메소드로 받음.
        if (bundle != null) {
            count = bundle.getString("count"); //count 받기
            Log.d("diary-look : ", count); //확인
        }

        //firebase 정의
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_auth = FirebaseAuth.getInstance(); //로그아웃 위해 필요함

        user = user_auth.getCurrentUser(); //현재 사용자 받아오기
        // user_id 값 얻어오기
        if (user != null) {
            user_id = user.getUid(); // 아이디 넣기
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        // 현재 user에 count 값 받아오기 + 현재 count랑 - 받아온 count
        call_count(user_id);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                DiaryUpdateFragment diaryUpdateFragment = new DiaryUpdateFragment();

                //프래그먼트 값 전달
                Bundle bundle = new Bundle();
                bundle.putString("count", result_cnt);
                diaryUpdateFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.main_frame, diaryUpdateFragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    void id_init(View view) {
        date = view.findViewById(R.id.date);
        title = view.findViewById(R.id.title);
        feel = view.findViewById(R.id.feel);
        hashtag1 = view.findViewById(R.id.hashtag1);
        hashtag2 = view.findViewById(R.id.hashtag2);
        hashtag3 = view.findViewById(R.id.hashtag3);
        img = view.findViewById(R.id.img);
        content = view.findViewById(R.id.content);
        update_btn = view.findViewById(R.id.update_btn);
        img_des = view.findViewById(R.id.img_des);
    }

    private void call_count(String user_id) {
        DocumentReference docRef = firebaseFirestore.collection("User").document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_count = document.getData();

                        current_cnt = (String) user_count.get("count");
                        Log.d("diary_look-currentcount", current_cnt);

                        int num_cucnt = Integer.parseInt(current_cnt);
                        int num_cnt = Integer.parseInt(count);

                        result_cnt = Integer.toString(num_cucnt - num_cnt);
                        Log.d("diary_look-계산", result_cnt);

                        callLookList(user_id, data); // 계산된 count로 Diary 컬렉션에서 읽어와서 set해주기

                    } else {
                        Log.d("write", "No such document");
                    }
                } else {
                    Log.d("diary", "get failed with ", task.getException());
                }
            }
        });
    }

    private void callLookList(String user_id, ArrayList<DiaryData> data) { //리스트 불러오기
        Log.d("diary-call_list-count", result_cnt);

        DocumentReference docRef = firebaseFirestore.collection("Diary").document(user_id)
                .collection(result_cnt).document(user_id);
        Log.d("diary-inner cnt", result_cnt);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        list = document.getData();

                        str_date = (String) list.get("date");
                        str_title = (String) list.get("title");
                        str_feel = (String) list.get("feel");
                        str_hash1 = (String) list.get("hashtag1");
                        str_hash2 = (String) list.get("hashtag2");
                        str_hash3 = (String) list.get("hashtag3");
                        str_img = (String) list.get("img");
                        str_content = (String) list.get("content");

                        date.setText(str_date);
                        title.setText(str_title);
                        feel.setText(str_feel);
                        hashtag1.setText("#" + str_hash1);
                        hashtag2.setText("#" + str_hash2);
                        hashtag3.setText("#" + str_hash3);

                        if (!str_img.equals("")) {
                            img.setImageResource(R.drawable.loading);
                            img_des.setVisibility(View.VISIBLE);

                            //Firebase Storage관리 객체 얻어오기
                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

                            //최상위노드 참조객체 얻어오기
                            StorageReference rootRef = firebaseStorage.getReference();

                            //읽어오길 원하는 파일의 참조객체 얻어오기
                            StorageReference imgRef = rootRef.child("images/" + str_img); //파일 이름을 가져오는 중
                            //하위 폴더가 있다면 폴더명까지 포함하여
                            //imgRef = rootRef.child("photo/bikewheel.png");
                            if (imgRef != null) {
                                //참조객체로부터 이미지의 다운로드 URL을 얻어오기.
                                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { ////Task로 리턴, 이 자체가 객체이기에 바로 . 하고 사용가능
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.d("diary-look", "성공");
                                        //다운로드 URL이 파라미터로 전달되어 옴.
                                        Glide.with(content.getContext()).load(uri).into(img);
                                        img_des.setText("Loading success!");
                                    }
                                });
                            }
                        }
                        else{
                            img.setVisibility(View.INVISIBLE);
                        }

                    content.setText(str_content);

                } else {
                    Log.d("diary", "No such document");
                }
            } else {
                Log.d("diary", "get failed with ", task.getException());
            }
         }
        });
    }

}