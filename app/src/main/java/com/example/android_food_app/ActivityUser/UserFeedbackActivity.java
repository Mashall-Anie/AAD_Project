package com.example.android_food_app.ActivityUser;

import android.os.Bundle;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.AdapterUser.FeedbackUserAdapter;
import com.example.android_food_app.Model.Feedback;
import com.example.android_food_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserFeedbackActivity extends AppCompatActivity {

    private RecyclerView rcvFeedback;
    private FeedbackUserAdapter mAdapter;
    private List<Feedback> mListFeedback;
    private ProgressDialog progressDialog;
    private ImageButton imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);

        // Set up EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading feedback...");
        progressDialog.show();

        rcvFeedback = findViewById(R.id.rcv_feedback);
        mAdapter = new FeedbackUserAdapter(this);
        rcvFeedback.setAdapter(mAdapter);
        rcvFeedback.setLayoutManager(new LinearLayoutManager(this));

        fetchDataFromFirebase();
        imgBack = findViewById(R.id.imgBack);  // Sửa lỗi ID ở đây
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void fetchDataFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks");
            feedbackRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mListFeedback = new ArrayList<>();
                    progressDialog.dismiss();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Feedback feedback = dataSnapshot.getValue(Feedback.class);
                        if (feedback != null && feedback.getUserID().equals(currentUserId)) {
                            mListFeedback.add(feedback);
                        }
                    }
                    mAdapter.setData(mListFeedback);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle potential errors here
                    progressDialog.dismiss();
                }
            });
        } else {
            progressDialog.dismiss();
            // Handle case when user is not logged in
        }
    }
}
