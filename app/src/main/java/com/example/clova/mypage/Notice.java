package com.example.clova.mypage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clova.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Notice#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Notice extends Fragment {
    public Notice() {
    }

    public static Notice newInstance(String param1, String param2) {
        Notice fragment = new Notice();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);

        return view;
    }
}