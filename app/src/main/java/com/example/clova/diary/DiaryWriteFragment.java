package com.example.clova.diary;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clova.LoginActivity;
import com.example.clova.MainActivity;
import com.example.clova.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class DiaryWriteFragment extends Fragment {

    EditText content, title, hashtag1, hashtag2, hashtag3;
    TextView date;
    Button btn_save, btn_choose, btn_upload;
    ImageView ivPreview;
    RadioGroup radioGroup;
    RadioButton radio1, radio2, radio3, radio4;

    private Uri filePath;

    String str_date, str_content; // 입력 텍스트 string으로
    String user_id; // user_id string
    int count; // user테이블에 count 얻어오는변수
    String feelcheck = null; // 감정 레이오 스트링
    String chooseDate = null; // 날짜 데이터 스트링
    String titlecheck = null;
    String hash1check = null;
    String hash2check = null;
    String hash3check = null;
    String urlcheck = null;

    //파이어베이스 변수
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    Map<String , Object> user_count = new HashMap<>();

    StorageReference storageRef; // 파이어스토리지 url 담는 객체

    View view;

    public DiaryWriteFragment() {
        // Required empty public constructor
    }

    public static DiaryWriteFragment newInstance() {
        DiaryWriteFragment fragment = new DiaryWriteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_diary_write, container, false);

        date = view.findViewById(R.id.date);
        title = view.findViewById(R.id.title);
        hashtag1 = view.findViewById(R.id.hashtag1);
        hashtag2 = view.findViewById(R.id.hashtag2);
        hashtag3 = view.findViewById(R.id.hashtag3);
        content = view.findViewById(R.id.content);
        radioGroup = view.findViewById(R.id.radioGroup);
        radio1 = view.findViewById(R.id.rg_btn1);
        radio2 = view.findViewById(R.id.rg_btn2);
        radio3 = view.findViewById(R.id.rg_btn3);
        radio4 = view.findViewById(R.id.rg_btn4);

        btn_save =view.findViewById(R.id.save_btn);
        btn_choose = view.findViewById(R.id.bt_choose);
        btn_upload = view.findViewById(R.id.bt_upload);
        ivPreview = view.findViewById(R.id.iv_preview);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // user_id 값 얻어오기
        if (firebaseUser != null) {
            user_id = firebaseUser.getUid(); // 아이디 넣기
        }
        else{
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month + 1;
                                chooseDate = year + "/" + month + "/" + day;
                                date.setText(chooseDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            //라디오 버튼 상태 변경값을 감지한다.
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i){
                if(i == R.id.rg_btn1){
                    Toast.makeText(getContext(),"당신의 오늘의 감정은 행복입니다.",Toast.LENGTH_SHORT).show();
                    feelcheck = radio1.getText().toString();//라디오 버튼의 텍스트 값을 string에 담아준것
                } else if(i == R.id.rg_btn2){
                    Toast.makeText(getContext(),"당신의 오늘의 감정은 웃김입니다.",Toast.LENGTH_SHORT).show();
                    feelcheck = radio2.getText().toString();//라디오 버튼의 텍스트 값을 string에 담아준것
                }
                else if(i == R.id.rg_btn3){
                    Toast.makeText(getContext(),"당신의 오늘의 감정은 슬픔입니다.",Toast.LENGTH_SHORT).show();
                    feelcheck = radio3.getText().toString();//라디오 버튼의 텍스트 값을 string에 담아준것
                }
                else if(i == R.id.rg_btn4){
                    Toast.makeText(getContext(),"당신의 오늘의 감정은 화남입니다.",Toast.LENGTH_SHORT).show();
                    feelcheck = radio4.getText().toString();//라디오 버튼의 텍스트 값을 string에 담아준것
                }
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //제목
                titlecheck = title.getText().toString();

                //해시태그
                //널체크 해주기
                hash1check = hashtag1.getText().toString();
                hash2check = hashtag2.getText().toString();
                hash3check = hashtag3.getText().toString();

              /*  if(hash1check == null)
                    hash1check = "";
                if(hash2check == null)
                    hash2check = "";
                if(hash3check == null)
                    hash3check = "";*/

                //내용
                str_content = content.getText().toString();

                //이미지 url
                if(storageRef == null)
                    urlcheck = "";
                else
                    urlcheck = storageRef.toString();

                //User에서 Count 부르는 함수 call
                call_count(user_id, firebaseFirestore);
                Log.d("write", "DocumentSnapshot data: " + count);

                //디비 내용 저장 함수 call
                diary_save(firebaseFirestore, user_id);
                //count User 업데이트 함수
            }
        });

        //버튼 클릭 이벤트
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //업로드
                uploadFile();
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.voice);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                VoiceTask voiceTask = new VoiceTask();
                voiceTask.execute();
            }
        });

        return view;
    }

    private void call_count(String user_id, FirebaseFirestore firebaseFirestore){
        DocumentReference docRef = firebaseFirestore.collection("User").document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_count = document.getData();

                        String str_count = (String)user_count.get("count");
                        count = Integer.parseInt(str_count) + 1;

                        setcount(user_id, firebaseFirestore);

                    } else {
                        Log.d("write", "No such document");
                    }
                } else {
                    Log.d("diary", "get failed with ", task.getException());
                }
            }
        });
    }

    private void diary_save(FirebaseFirestore firebaseFirestore, String user_id) {
        // Create a new user with a first and last name
        Map<String, Object> diary_cotent = new HashMap<>();
        diary_cotent.put("date", chooseDate);
        diary_cotent.put("title", titlecheck);
        diary_cotent.put("feel", feelcheck);

        Log.d("diary-write-hash", hash1check);

        diary_cotent.put("hashtag1", hash1check);
        diary_cotent.put("hashtag2", hash2check);
        diary_cotent.put("hashtag3", hash3check);
        diary_cotent.put("img", urlcheck);
        diary_cotent.put("content", str_content);

        firebaseFirestore.collection("Diary").document(user_id)
                .collection(Integer.toString(count)).document(user_id)
                .set(diary_cotent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("diary-write", "성공" + chooseDate + str_content);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("diary-write", "Error writing document", e);
                    }
                });

    }

    //결과 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if (requestCode == 0 && resultCode == RESULT_OK) {
            filePath = data.getData();
            Log.d("diary-write-sucess", "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                ivPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (resultCode == RESULT_OK) { // 데이터를 result에서 받아

            ArrayList<String> results = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String str = results.get(0);

            EditText tv = view.findViewById(R.id.content);
            String checktext = tv.getText().toString();

            if(checktext != null) { // 내용에 머가 써져있다.
                checktext = checktext.concat(" " + str);
            }
            else{ // 내용이 아무것도 없다
                checktext = str;
            }
            Toast.makeText(getActivity().getBaseContext(), str, Toast.LENGTH_SHORT).show();

            tv.setText(checktext);
        }
    }

    //upload the file
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            storageRef = storage.getReferenceFromUrl("gs://clova-102e8.appspot.com").child("images/" + filename);
            //올라가거라...
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기

                            Toast.makeText(getContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }
    public class VoiceTask extends AsyncTask<String, Integer, String> {
        String str = null;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                getVoice();
            } catch (Exception e) {
                // TODO: handle exception
            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

            } catch (Exception e) {
                Log.d("onActivityResult", "getImageURL exception");
            }
        }
    }

    private void getVoice() {

        Intent intent = new Intent();
        intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        String language = "ko-KR";

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        startActivityForResult(intent, 2);
    }
    void setcount(String user_id, FirebaseFirestore firebaseFirestore){
        String setcnt = Integer.toString(count);

        Map<String, Object> cnt = new HashMap<>();
        cnt.put("count", setcnt);

        firebaseFirestore.collection("User").document(user_id)
                .set(cnt)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("User", "count setting finish!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("User", "Error writing document", e);
                    }
                });
    }

}