package com.example.clova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.clova.diary.DiaryFragment;
import com.example.clova.home.HomeFragment;
import com.example.clova.mypage.MyPageFragment;
import com.example.clova.picture.PictureFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STROAGE = 1001;

    private BottomNavigationView bottomNavigationView; //바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private HomeFragment frag1;
    private DiaryFragment frag2;
    private PictureFragment frag3;
    private MyPageFragment frag4;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getCurrentUser(); //현재 유저 받아와서

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //권한 허용
                //Toast.makeText(MainActivity.this, "당신의 일상을 기록하세요 ^_^", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //권한 거부
                Toast.makeText(MainActivity.this, "권한 거부되서 이용이 불가 합니다.", Toast.LENGTH_SHORT).show();

                ActivityCompat.finishAffinity(MainActivity.this);

                System.runFinalization(); //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.

                System.exit(0) ;// 현재 액티비티를 종료시킨다.

            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("[필수]\n그림 그리기, 그림일기를 위해서는 권한이 필요합니다.")
                .setDeniedMessage("권한이 없으셔서 이용이 불가합니다.\n- [닫기] 누르면 앱이 종료됩니다.\n- [설정] > [권한] 에서 권한을 허용할 수 있습니다")
                .setPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE})
                .check();

        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        setFrag(0);
                        break;
                    case R.id.diary_lists:
                        setFrag(1);
                        break;
                    case R.id.draw_picture:
                        setFrag(2);
                        break;
                    case R.id.action_mypage:
                        setFrag(3);
                        break;
                }
                return false;
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavi);
        frag1 = new HomeFragment();
        frag2 = new DiaryFragment();
        frag3 = new PictureFragment();
        frag4 = new MyPageFragment();

        setFrag(0); //첫 프래그먼트 화면을 무엇으로 지정해줄 것인지 결정
    }

    private void exitProgram() {
        // 종료

        // 태스크를 백그라운드로 이동
        // moveTaskToBack(true);

        if (Build.VERSION.SDK_INT >= 21) {
            // 액티비티 종료 + 태스크 리스트에서 지우기
            finishAndRemoveTask();
            user = null;
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            // 액티비티 종료


        }


        //System.exit(0);

        System.exit(0);
    }

    //프래그먼트 교체가 일어나는 실행문이다.
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, frag1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, frag2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, frag3);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, frag4);
                ft.commit();
                break;
        }
    }

}