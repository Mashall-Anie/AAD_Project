package com.example.android_food_app.Fragment.FragmentBottomNavigationUser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android_food_app.ActivityUser.UserFeedbackActivity;
import com.example.android_food_app.Model.Feedback;
import com.example.android_food_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedbackUserFragment extends Fragment {

    private EditText comment, email;
    private Button btnAddFeedback;
    private ProgressDialog progressDialog;
    FloatingActionButton user_feedback;

    public FeedbackUserFragment() {
        // Required empty public constructor
    }

    public static FeedbackUserFragment newInstance(String param1, String param2) {
        FeedbackUserFragment fragment = new FeedbackUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // handle arguments
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback_user, container, false);

        comment = view.findViewById(R.id.comment);
        email = view.findViewById(R.id.email);
        user_feedback = view.findViewById(R.id.user_feedback);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Đang kiểm tra...");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            email.setText(userEmail);
        }
        btnAddFeedback = view.findViewById(R.id.btn_add_feedback);

        btnAddFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFeedback();
            }
        });
        user_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserFeedbackActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void addFeedback() {
        String userComment = comment.getText().toString().trim();

        if (userComment.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference feedbacksRef = FirebaseDatabase.getInstance().getReference("feedbacks");

            // Get the current date and time
            String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            // Tạo một push reference để lấy ID tự động
            DatabaseReference newFeedbackRef = feedbacksRef.push();
            String feedbackId = newFeedbackRef.getKey();

            // Tạo đối tượng feedback với ID tự động và currentDate
            Feedback feedback = new Feedback(feedbackId, userId, userComment, currentDate);

            // Lưu phản hồi vào Firebase với ID tự động
            newFeedbackRef.setValue(feedback).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Phản hồi đã được gửi", Toast.LENGTH_SHORT).show();
                    comment.setText(""); // Xóa nội dung trong EditText comment sau khi gửi thành công
                } else {
                    Toast.makeText(getContext(), "Gửi phản hồi thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}
