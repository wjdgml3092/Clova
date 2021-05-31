package com.example.clova.diary;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clova.LoginActivity;
import com.example.clova.R;
import com.example.clova.diary.Recycler.DiaryAdapter;
import com.example.clova.diary.Recycler.DiaryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    SwipeRefreshLayout swipeRefreshLayout; // 추가

    private RecyclerView recyclerView;
    private DiaryAdapter mAdapter;
    // private StaggeredGridLayoutManager mLayoutManager;
    private LinearLayoutManager mLayoutManager;

    //글 리스트 데이터베이스 변수 관련
    private FirebaseAuth user_auth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    Map<String, Object> user_count = new HashMap<>();

    String date, head, hash1, hash2, hash3;
    Map<String, Object> list = new HashMap<>();

    String user_id = null;
    String str_count = null;
    ArrayList<DiaryData> data;

    public DiaryFragment() {
        // Required empty public constructor
    }

    public static DiaryFragment newInstance() {
        DiaryFragment diaryFragment = new DiaryFragment();
        return diaryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        // RecyclerView binding
        recyclerView = (RecyclerView) view.findViewById(R.id.diary_list);

        //firebase 정의
        firebaseFirestore = FirebaseFirestore.getInstance();
        // init Data
        data = new ArrayList<>();

        user_auth = FirebaseAuth.getInstance(); //로그아웃 위해 필요함

        user = user_auth.getCurrentUser(); //현재 사용자 받아오기

        // user_id 값 얻어오기
        if (user != null) {
            user_id = user.getUid(); // 아이디 넣기
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        // init LayoutManager
        mLayoutManager = new LinearLayoutManager(getContext());

        // setLayoutManager
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemViewCacheSize(10);

        // init Adapter
        mAdapter = new DiaryAdapter(getContext(), data);

        // set Data
        mAdapter.setData(data);

        // set Adapter
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //새로고침시 돌아가는 코드

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                DiaryFragment diaryFragment = new DiaryFragment();
                transaction.replace(R.id.main_frame, diaryFragment);
                transaction.commit();

                //새로고침 종료
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //count값 불러오는 함수, 이 안에서 리스트 불러오는 함수(call_list call 된다)
        call_count(user_id);

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

    private void call_count(String user_id) {
        DocumentReference docRef = firebaseFirestore.collection("User").document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_count = document.getData();

                        str_count = (String) user_count.get("count");

                        Log.d("diary-count", str_count);

                        call_list(user_id, data);

                    } else {
                        Log.d("write", "No such document");
                    }
                } else {
                    Log.d("diary", "get failed with ", task.getException());
                }
            }
        });
    }

    private boolean call_list(String user_id, ArrayList<DiaryData> data) { //리스트 불러오기
        Log.d("diary-call_list-count", str_count);

        if(str_count.equals("0")) { // 종료 조건
            return false;
        }

        DocumentReference docRef = firebaseFirestore.collection("Diary").document(user_id)
                .collection(str_count).document(user_id);
        Log.d("diary-inner cnt", str_count);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        list = document.getData();

                        date = (String) list.get("date");
                        head = (String) list.get("title");
                        hash1 = (String) list.get("hashtag1");
                        hash2 = (String) list.get("hashtag2");
                        hash3 = (String) list.get("hashtag3");

                        data.add(new DiaryData(date, head, "#" + hash1, "#"+ hash2, "#" + hash3));
                        Log.d("diary", "DocumentSnapshot data: " + date + " : " + head + " : " + hash1 + hash2 + hash3);

                        //mAdapter.notifyDataSetChanged();
                        mAdapter.notifyItemChanged (mAdapter.getItemCount() -1 ,"click");

                        //아래 주석 풀고 실행 위 주석 지우기
                        int count = Integer.parseInt(str_count);
                        /*if(count == 1){
                            mAdapter.notifyItemChanged (mAdapter.getItemCount() -1 ,"click");
                        }*/
                        count--;
                        str_count = Integer.toString(count);
                        call_list(user_id, data);

                    } else {
                        Log.d("diary", "No such document");
                    }
                } else {
                    Log.d("diary", "get failed with ", task.getException());
                }
            }
        });
        return  true;
    }
}