package com.example.clova.diary.Recycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.clova.R;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryViewHolder> {

    private ArrayList<DiaryData> arr_data;
    private Context mContext;
    public void setData(ArrayList<DiaryData> list) {
        arr_data = list;
    }

    public DiaryAdapter(Context context, ArrayList<DiaryData> list){
        this.arr_data = list;
        this.mContext = context;
    }

    @Override
    public DiaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

// 사용할 아이템의 뷰를 생성해준다.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diarylist, parent, false);

        DiaryViewHolder holder = new DiaryViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(DiaryViewHolder holder, int position) {
        Log.d("diaryAdapter-position ", Integer.toString(position));
        int pos = getItemCount();
       // if(pos == 0)
        DiaryData data = arr_data.get(position);

        holder.date.setText(data.getDate());
        holder.title.setText(data.getTitle());
        holder.word1.setText(data.getWord1());
        holder.word2.setText(data.getWord2());
        holder.word3.setText(data.getWord3());
    }

    @Override
    public int getItemCount() {
        return arr_data.size();
    }

}


