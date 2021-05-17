package com.example.clova.diary.Recycler;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clova.R;
import com.example.clova.diary.DiaryLookFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiaryViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryViewFragment extends Fragment {



    public DiaryViewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DiaryViewFragment newInstance() {
        DiaryViewFragment fragment = new DiaryViewFragment();
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
        return inflater.inflate(R.layout.fragment_diary_view, container, false);
    }




}

class DiaryViewHolder extends RecyclerView.ViewHolder {

    public TextView date, title, word1, word2, word3;

    public DiaryViewHolder(View itemView) {
        super(itemView);

        date = (TextView) itemView.findViewById(R.id.date1);
        title = (TextView) itemView.findViewById(R.id.title);
        word1 = (TextView) itemView.findViewById(R.id.word1);
        word2 = (TextView) itemView.findViewById(R.id.word2);
        word3 = (TextView) itemView.findViewById(R.id.word3);

        Context mContext = itemView.getContext();

        itemView.setClickable(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    FragmentTransaction transaction =
                            ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
                    DiaryLookFragment fragment1 = new DiaryLookFragment();
                    transaction.replace(R.id.main_frame, fragment1);
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            }
        });
    }

}
