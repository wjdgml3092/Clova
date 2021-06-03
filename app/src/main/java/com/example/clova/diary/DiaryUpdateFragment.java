package com.example.clova.diary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import com.bumptech.glide.Glide;
import com.example.clova.LoginActivity;
import com.example.clova.R;
import com.example.clova.diary.Recycler.DiaryData;
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

public class DiaryUpdateFragment extends Fragment {

    String count; // look에서 받아온 count

    EditText content, title, hashtag1, hashtag2, hashtag3;
    TextView date;
    Button btn_save, btn_choose, btn_upload, btn_delete;
    ImageView ivPreview;
    RadioGroup radioGroup;
    RadioButton radio1, radio2, radio3, radio4;

    //db 불러올 때 사용
    String read_date, read_title, read_feel, read_hash1, read_hash2, read_hash3, read_img, read_content;

    Map<String, Object> list = new HashMap<>(); // diary_setting에서 사용
    ArrayList<DiaryData> data = new ArrayList<>();

    //db 저장 시 사용
    String str_content; // 입력 텍스트 string으로
    String feelcheck = ""; // 감정 레이오 스트링
    String chooseDate = ""; // 날짜 데이터 스트링
    String titlecheck = "";
    String hash1check = "";
    String hash2check = "";
    String hash3check = "";
    String urlcheck = "";
    String filename = ""; // 파일명

    View view;

    //이미지 uri
    private Uri filePath = null;
    StorageReference storageRef; // 파이어스토리지 url 담는 객체

    //파이어베이스 변수
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    Map<String, Object> diary = new HashMap<>();
    String user_id; // user_id string

    public DiaryUpdateFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static DiaryUpdateFragment newInstance() {
        DiaryUpdateFragment diaryUpdateFragment = new DiaryUpdateFragment();
        return diaryUpdateFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_diary_update, container, false);

        //id init
        id_init(view); //findViewById() 초기화

        Bundle bundle = getArguments();  //번들 받기. getArguments() 메소드로 받음.
        if (bundle != null) {
            count = bundle.getString("count"); //count 받기
            Log.d("diary-update : ", count); //확인
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // user_id 값 얻어오기
        if (firebaseUser != null) {
            user_id = firebaseUser.getUid(); // 아이디 넣기
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        //db diary 읽어와서 setting
        diary_setting(user_id, data);

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),0 ,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month + 1;
                                if(day < 10 && month <10) {
                                    chooseDate = year + "/0" + month + "/0" + day;
                                }
                                else if (day < 10){
                                    chooseDate = year + "/" + month + "/0" + day;
                                }
                                else if(month < 10){
                                    chooseDate = year + "/0" + month + "/" + day;
                                }
                                else{
                                    chooseDate = year + "/" + month + "/" + day;
                                }
                                date.setText(chooseDate);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            //라디오 버튼 상태 변경값을 감지한다.
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rg_btn1) {
                    Toast.makeText(getContext(), "당신의 오늘의 감정은 행복입니다.", Toast.LENGTH_SHORT).show();
                    feelcheck = radio1.getText().toString();//라디오 버튼의 텍스트 값을 string에 담아준것
                } else if (i == R.id.rg_btn2) {
                    Toast.makeText(getContext(), "당신의 오늘의 감정은 웃김입니다.", Toast.LENGTH_SHORT).show();
                    feelcheck = radio2.getText().toString();//라디오 버튼의 텍스트 값을 string에 담아준것
                } else if (i == R.id.rg_btn3) {
                    Toast.makeText(getContext(), "당신의 오늘의 감정은 슬픔입니다.", Toast.LENGTH_SHORT).show();
                    feelcheck = radio3.getText().toString();//라디오 버튼의 텍스트 값을 string에 담아준것
                } else if (i == R.id.rg_btn4) {
                    Toast.makeText(getContext(), "당신의 오늘의 감정은 화남입니다.", Toast.LENGTH_SHORT).show();
                    feelcheck = radio4.getText().toString();//라디오 버튼의 텍스트 값을 string에 담아준것
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //제목
                titlecheck = title.getText().toString();
                Log.d("diary_update", titlecheck);

                //내용
                str_content = content.getText().toString();

                //이미지 url
                if (storageRef == null)
                    urlcheck = "";
                else
                    urlcheck = storageRef.toString();

                if (titlecheck.equals("")  || chooseDate.equals("")  ||  str_content.equals("") || feelcheck.equals("") ) {
                    Toast.makeText(getActivity().getBaseContext(), "날짜, 제목, 감정 그리고 내용은 필수 입력입니다.", Toast.LENGTH_LONG).show();
                }
                else if (filePath != null && storageRef == null) {
                    dialog();
                }
                else {
                    //디비 내용 저장 함수 call
                    diary_save(user_id);
                    Log.d("write", "DocumentSnapshot data: " + count);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    DiaryFragment diaryFragment = new DiaryFragment();
                    transaction.replace(R.id.main_frame, diaryFragment);
                    transaction.commit();
                }
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

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath == null) {
                    Toast.makeText(getContext(), "삭제 할 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    filePath = null;
                    Toast.makeText(getContext(), "이미지를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                if (storageRef != null) {
                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            storageRef = null;
                            Toast.makeText(getContext(), "업로드가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });
                }
                ivPreview.setImageBitmap(null);

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

    void id_init(View view) {
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

        btn_save = view.findViewById(R.id.save_btn);
        btn_choose = view.findViewById(R.id.bt_choose);
        btn_upload = view.findViewById(R.id.bt_upload);
        ivPreview = view.findViewById(R.id.iv_preview);
        btn_delete = view.findViewById(R.id.bt_delete);
    }

    private void diary_setting(String user_id, ArrayList<DiaryData> data) { //리스트 불러오기
        Log.d("diary-call_list-count", count);

        DocumentReference docRef = firebaseFirestore.collection("Diary").document(user_id)
                .collection(count).document(user_id);
        Log.d("diary-inner cnt", count);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        list = document.getData();

                        read_date = (String) list.get("date");
                        read_title = (String) list.get("title");
                        read_feel = (String) list.get("feel");
                        read_hash1 = (String) list.get("hashtag1");
                        read_hash2 = (String) list.get("hashtag2");
                        read_hash3 = (String) list.get("hashtag3");
                        read_img = (String) list.get("img");
                        str_content = (String) list.get("content");

                        date.setText(read_date);
                        chooseDate = read_date; // 날짜 choosedate로 통일

                        title.setText(read_title);

                        //라디오 버튼 선택
                        if(read_feel.equals("행복"))
                            radioGroup.check(R.id.rg_btn1);
                        else if(read_feel.equals("웃김"))
                            radioGroup.check(R.id.rg_btn2);
                        else if(read_feel.equals("슬픔"))
                            radioGroup.check(R.id.rg_btn3);
                        else if(read_feel.equals("화남"))
                            radioGroup.check(R.id.rg_btn4);
                        feelcheck = read_feel;

                        hashtag1.setText(read_hash1);
                        hashtag2.setText(read_hash2);
                        hashtag3.setText(read_hash3);

                        if (!read_img.equals("")) {
                            filePath = Uri.parse(read_img);
                            //Firebase Storage관리 객체 얻어오기
                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

                            //최상위노드 참조객체 얻어오기
                            StorageReference rootRef = firebaseStorage.getReference();

                            //읽어오길 원하는 파일의 참조객체 얻어오기
                            StorageReference imgRef = rootRef.child("images/" + read_img); //파일 이름을 가져오는 중
                            //하위 폴더가 있다면 폴더명까지 포함하여
                            //imgRef = rootRef.child("photo/bikewheel.png");

                            if (imgRef != null) {
                                //참조객체로부터 이미지의 다운로드 URL을 얻어오기.
                                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { ////Task로 리턴, 이 자체가 객체이기에 바로 . 하고 사용가능
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //다운로드 URL이 파라미터로 전달되어 옴.
                                        Glide.with(content.getContext()).load(uri).into(ivPreview);
                                    }
                                });
                            }
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

    private void diary_save(String user_id) {
        // Create a new user with a first and last name
        Map<String, Object> diary_cotent = new HashMap<>();

        diary_cotent.put("date", chooseDate);
        diary_cotent.put("title", titlecheck);
        diary_cotent.put("feel", feelcheck);

        hash1check = hashtag1.getText().toString();
        hash2check = hashtag2.getText().toString();
        hash3check = hashtag3.getText().toString();

        diary_cotent.put("hashtag1", hash1check);
        diary_cotent.put("hashtag2", hash2check);
        diary_cotent.put("hashtag3", hash3check);
        //diary_cotent.put("img", urlcheck);
        diary_cotent.put("img", filename);
        diary_cotent.put("content", str_content);

        Log.d("diary-write hash", hash1check + " " + hash2check + " " + hash3check);
        Log.d("write-diarycount", count);

        firebaseFirestore.collection("Diary").document(user_id)
                .collection(count).document(user_id)
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

    void dialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
        dlg.setTitle("이미지 업로드 "); //제목
        dlg.setMessage("이미지를 이용해서 일기를 작성하고 싶을 시 이미지 업로드 버튼이 필수로 눌러야합니다! \n 아직 업로드 버튼을 누르시지 않으셨어요! "); // 메시지
        //  dlg.setIcon(R.drawable.deum); // 아이콘 설정

        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //토스트 메시지
                Toast.makeText(getContext(), "이미지 업로드 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        dlg.show();
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
        } else if (resultCode == RESULT_OK) { // 데이터를 result에서 받아

            ArrayList<String> results = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String str = results.get(0);

            EditText tv = view.findViewById(R.id.content);
            String checktext = tv.getText().toString();

            if (checktext != null) { // 내용에 머가 써져있다.
                checktext = checktext.concat(" " + str);
            } else { // 내용이 아무것도 없다
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
            filename = formatter.format(now) + ".png";
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

}