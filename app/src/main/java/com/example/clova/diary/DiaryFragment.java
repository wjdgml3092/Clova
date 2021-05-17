package com.example.clova.diary;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clova.LoginActivity;
import com.example.clova.R;
import com.example.clova.diary.Recycler.DiaryAdapter;
import com.example.clova.diary.Recycler.DiaryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryFragment extends Fragment {

    private RecyclerView mVerticalView;
    private DiaryAdapter mAdapter;
    // private StaggeredGridLayoutManager mLayoutManager;
    private LinearLayoutManager mLayoutManager;

    private int MAX_ITEM_COUNT = 0; // 개수 제한

    //글 리스트 데이터베이스 변수 관련
    private FirebaseAuth user_auth;
    String date, head , hash1 , hash2 , hash3;
    Map<String, Object> list = new HashMap<>();
    String uid = null;

    public DiaryFragment() {
        // Required empty public constructor
    }

    public static DiaryFragment newInstance() {
        DiaryFragment diaryFragment = new DiaryFragment();
        return diaryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        // RecyclerView binding
        mVerticalView = (RecyclerView) view.findViewById(R.id.diary_list);

        //firebase 정의
        final FirebaseFirestore user_table = FirebaseFirestore.getInstance();
        // init Data
        ArrayList<DiaryData> data = new ArrayList<>();

        user_auth = FirebaseAuth.getInstance(); //로그아웃 위해 필요함

        FirebaseUser user = user_auth.getCurrentUser(); //현재 사용자 받아오기

        // user_id 값 얻어오기
        if (user != null) {
            uid = user.getUid(); // 아이디 넣기
        }
        else{
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        Log.d("diary-call", "call 함");
        Log.d("diary-user", uid);
        call_list(uid, user_table, data);
        Log.d("diary-user", "comeback");

    /*    int i = 0;
        while (i < 2) {
            Log.d("diary", "뒤질래?");
            data.add(new DiaryData(date, head,"#" + hash1, "#" + hash2,"#" + hash3));
            i++;
        }*/

        // init LayoutManager
        mLayoutManager = new LinearLayoutManager(getContext());

        // setLayoutManager
        mVerticalView.setLayoutManager(mLayoutManager);

        // init Adapter
        mAdapter = new DiaryAdapter(getContext(), data);

        // set Data
        mAdapter.setData(data);

        // set Adapter
        mVerticalView.setAdapter(mAdapter);

        FloatingActionButton fab = view.findViewById(R.id.write);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                DiaryWriteFragment writeFragment = new DiaryWriteFragment();
                transaction.replace(R.id.main_frame, writeFragment);
                transaction.commit();
            }
        });


        return view;
    }

    private void call_list(String user_id, FirebaseFirestore user_table, ArrayList<DiaryData> data) { //리스트 불러오기

        DocumentReference docRef = user_table.collection("Diary").document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        list = document.getData();

                        MAX_ITEM_COUNT = list.size();
                        date = (String)list.get("story_date");
                        head = (String)list.get("story_headline");
                        hash1 = (String)list.get("story_hashtag1");
                        hash2 = (String)list.get("story_hashtag2");
                        hash3 = (String)list.get("story_hashtag3");
                        data.add(new DiaryData(date, head,"#" + hash1, "#" + hash2,"#" + hash3));
                        Log.d("diary", "DocumentSnapshot data: " + date + " : " + head + " : " + hash1  + hash2 + hash3);
                        mAdapter.notifyDataSetChanged();
                        //fill_data(data, date, head, hash1, hash2, hash3);

                    } else {
                        Log.d("diary", "No such document");
                    }
                } else {
                    Log.d("diary", "get failed with ", task.getException());
                }
            }
        });
    }

    void fill_data(ArrayList<DiaryData> data, String date, String head, String hash1, String hash2, String hash3){

    }
}