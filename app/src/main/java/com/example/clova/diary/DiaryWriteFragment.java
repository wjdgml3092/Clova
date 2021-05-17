package com.example.clova.diary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clova.R;

public class DiaryWriteFragment extends Fragment {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary_write, container, false);
    }
}