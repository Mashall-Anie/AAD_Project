package com.example.android_food_app.FragmentAdmin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.AdapterAdmin.FeedbackAdminAdapter;
import com.example.android_food_app.Model.Feedback;
import com.example.android_food_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FeedbackAdminFragment extends Fragment {
    private RecyclerView rcvFeedback;
    private FeedbackAdminAdapter mAdapter;
    private List<Feedback> mListFeedback;
    private ProgressDialog progressDialog;

    public FeedbackAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_admin, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Đang kiểm tra...");
        rcvFeedback = view.findViewById(R.id.rcv_feedback);
        mAdapter = new FeedbackAdminAdapter(getContext());
        rcvFeedback.setAdapter(mAdapter);
        rcvFeedback.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchDataFromFirebase();

        return view;
    }

    private void fetchDataFromFirebase() {
        progressDialog.show();
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks");
        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListFeedback = new ArrayList<>();
                progressDialog.dismiss();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    mListFeedback.add(feedback);
                }

                // Sắp xếp danh sách theo ngày theo thứ tự giảm dần
                Collections.sort(mListFeedback, new Comparator<Feedback>() {
                    @Override
                    public int compare(Feedback f1, Feedback f2) {
                        return f2.getDate().compareTo(f1.getDate());
                    }
                });

                mAdapter.setData(mListFeedback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi có thể xảy ra.
            }
        });
    }
}
