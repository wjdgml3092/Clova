package com.example.clova.diary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
    Button btn_save, btn_choose, btn_upload, btn_delete;
    ImageView ivPreview;
    RadioGroup radioGroup;
    RadioButton radio1, radio2, radio3, radio4;

    private Uri filePath = null;

    String message = null;
    String nickname = null;
    String str_content; // ?????? ????????? string??????
    String user_id; // user_id string
    int count; // user???????????? count ??????????????????
    String feelcheck = ""; // ?????? ????????? ?????????
    String chooseDate = ""; // ?????? ????????? ?????????
    String titlecheck = "";
    String hash1check = "";
    String hash2check = "";
    String hash3check = "";
    String urlcheck = "";

    String filename = ""; // ?????????

    //?????????????????? ??????
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    Map<String, Object> user_count = new HashMap<>();

    StorageReference storageRef; // ????????????????????? url ?????? ??????

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

        id_init(view); //findViewById() ?????????

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // user_id ??? ????????????
        if (firebaseUser != null) {
            user_id = firebaseUser.getUid(); // ????????? ??????
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), 0,
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
            //????????? ?????? ?????? ???????????? ????????????.
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rg_btn1) {
                    Toast.makeText(getContext(), "????????? ????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    feelcheck = radio1.getText().toString();//????????? ????????? ????????? ?????? string??? ????????????
                } else if (i == R.id.rg_btn2) {
                    Toast.makeText(getContext(), "????????? ????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    feelcheck = radio2.getText().toString();//????????? ????????? ????????? ?????? string??? ????????????
                } else if (i == R.id.rg_btn3) {
                    Toast.makeText(getContext(), "????????? ????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    feelcheck = radio3.getText().toString();//????????? ????????? ????????? ?????? string??? ????????????
                } else if (i == R.id.rg_btn4) {
                    Toast.makeText(getContext(), "????????? ????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    feelcheck = radio4.getText().toString();//????????? ????????? ????????? ?????? string??? ????????????
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????
                titlecheck = title.getText().toString();

                //????????????
                //????????? ?????????
                hash1check = hashtag1.getText().toString();
                Log.d("diary-write hash1", hash1check);

                hash2check = hashtag2.getText().toString();

                hash3check = hashtag3.getText().toString();

                //??????
                str_content = content.getText().toString();

                //????????? url
                if (storageRef == null)
                    urlcheck = "";
                else
                    urlcheck = storageRef.toString();

                if (titlecheck.equals("")  || chooseDate.equals("")  ||  str_content.equals("") || feelcheck.equals("") ) {
                    Toast.makeText(getActivity().getBaseContext(), "??????, ??????, ?????? ????????? ????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                }
                else if (filePath != null && storageRef == null) {
                        dialog();
                }
                else {
                    //User?????? Count ????????? ?????? call
                    call_count(user_id);
                    Log.d("write", "DocumentSnapshot data: " + count);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    DiaryFragment diaryFragment = new DiaryFragment();
                    transaction.replace(R.id.main_frame, diaryFragment);
                    transaction.commit();
                }
            }
        });

        //?????? ?????? ?????????
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //???????????? ??????
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "???????????? ???????????????."), 0);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //?????????
                uploadFile();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath == null) {
                    Toast.makeText(getContext(), "?????? ??? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                }
                else {
                    filePath = null;
                    Toast.makeText(getContext(), "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }
                if (storageRef != null) {
                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            storageRef = null;
                            Toast.makeText(getContext(), "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
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

    private void call_count(String user_id) {
        DocumentReference docRef = firebaseFirestore.collection("User").document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_count = document.getData();

                        String str_count = (String) user_count.get("count");
                        message = (String) user_count.get("message");
                        count = Integer.parseInt(str_count) + 1;
                        nickname = (String) user_count.get("nickname");
                        Log.d("write-callcount", Integer.toString(count));

                        setcount(user_id);

                        //?????? ?????? ?????? ?????? call
                        diary_save(user_id);
                    } else {
                        Log.d("write", "No such document");
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
        diary_cotent.put("hashtag1", hash1check);
        diary_cotent.put("hashtag2", hash2check);
        diary_cotent.put("hashtag3", hash3check);
        //diary_cotent.put("img", urlcheck);
        diary_cotent.put("img", filename);
        diary_cotent.put("content", str_content);

        Log.d("diary-write hash", hash1check + " " + hash2check + " " + hash3check);
        Log.d("write-diarycount", Integer.toString(count));

        firebaseFirestore.collection("Diary").document(user_id)
                .collection(Integer.toString(count)).document(user_id)
                .set(diary_cotent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("diary-write", "??????" + chooseDate + str_content);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("diary-write", "Error writing document", e);
                    }
                });

        DocumentReference docRef = firebaseFirestore.collection("Feel").document(user_id)
                .collection(feelcheck).document(user_id);
        Log.d("diary-inner feel", feelcheck);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                         Map<String, Object> feel_info = new HashMap<>();
                        feel_info = document.getData();
                         String get_feelcnt = feel_info.get("feel_count").toString();

                         int feet_cnt = Integer.parseInt(get_feelcnt) + 1;
                         get_feelcnt = Integer.toString(feet_cnt);

                        Map<String, Object> diary_feel = new HashMap<>();
                        diary_feel.put("feel_count", get_feelcnt);

                        firebaseFirestore.collection("Feel").document(user_id)
                                .collection(feelcheck).document(user_id)
                                .set(diary_feel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("diary-write-feel", "??????");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("diary-write", "Error writing document", e);
                                    }
                                });


                    } else {
                        Log.d("diary", "No such document");
                    }
                } else {
                    Log.d("diary", "get failed with ", task.getException());
                }
            }
        });

    }

    void setcount(String user_id) {
        String setcnt = Integer.toString(count);

        Map<String, Object> cnt = new HashMap<>();
        cnt.put("count", setcnt);
        cnt.put("message", message);
        cnt.put("nickname", nickname);

        Log.d("write-setcount", setcnt);
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

    void dialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
        dlg.setTitle("????????? ????????? "); //??????
        dlg.setMessage("???????????? ???????????? ????????? ???????????? ?????? ??? ????????? ????????? ????????? ????????? ??????????????????! \n ?????? ????????? ????????? ???????????? ???????????????! "); // ?????????
        //  dlg.setIcon(R.drawable.deum); // ????????? ??????

        dlg.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //????????? ?????????
                Toast.makeText(getContext(), "????????? ????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
            }
        });
        dlg.show();
    }

    //?????? ??????
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request????????? 0?????? OK??? ???????????? data??? ????????? ?????? ?????????
        if (requestCode == 0 && resultCode == RESULT_OK) {
            filePath = data.getData();
            Log.d("diary-write-sucess", "uri:" + String.valueOf(filePath));
            try {
                //Uri ????????? Bitmap?????? ???????????? ImageView??? ?????? ?????????.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                ivPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK) { // ???????????? result?????? ??????

            ArrayList<String> results = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String str = results.get(0);

            EditText tv = view.findViewById(R.id.content);
            String checktext = tv.getText().toString();

            if (checktext != null) { // ????????? ?????? ????????????.
                checktext = checktext.concat(" " + str);
            } else { // ????????? ???????????? ??????
                checktext = str;
            }
            Toast.makeText(getActivity().getBaseContext(), str, Toast.LENGTH_SHORT).show();

            tv.setText(checktext);
        }
    }

    //upload the file
    private void uploadFile() {
        //???????????? ????????? ????????? ??????
        if (filePath != null) {
            //????????? ?????? Dialog ?????????
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("????????????...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique??? ???????????? ?????????.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            filename = formatter.format(now) + ".png";
            //storage ????????? ?????? ???????????? ????????? ??????.
            storageRef = storage.getReferenceFromUrl("gs://clova-102e8.appspot.com").child("images/" + filename);
            //???????????????...
            storageRef.putFile(filePath)
                    //?????????
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //????????? ?????? Dialog ?????? ??????

                            Toast.makeText(getContext(), "????????? ??????!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //?????????
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "????????? ??????!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //?????????
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //?????? ?????? ?????? ???????????? ????????? ????????????. ??? ??????????
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //dialog??? ???????????? ???????????? ????????? ??????
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getContext(), "????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
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