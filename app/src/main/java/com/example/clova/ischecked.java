package com.example.clova;

import android.widget.CheckBox;

public class ischecked {
    private boolean ischecked(CheckBox checkBox, CheckBox checkBox2, CheckBox checkBox3) {
        if (checkBox.isChecked() || checkBox2.isChecked() && checkBox3.isChecked()) {
            return true;
        }
        return false;
    }
}
//회원가입시 체크박스 확인할때 사용하세요.
//                    //체크박스 확인
//                    else if(!ischecked(checkBox,checkBox2,checkBox3)){
//                        Toast.makeText(getApplicationContext(), "이용약관에 동의해주세요.", Toast.LENGTH_SHORT).show();
//                    }

