package com.example.android_food_app.AdapterUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_food_app.Model.Feedback;
import com.example.android_food_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.stream.Collectors;

public class FeedbackUserAdapter extends RecyclerView.Adapter<FeedbackUserAdapter.FeedbackViewHolder> {
    private Context mContext;
    private List<Feedback> mListFeedback;

    public FeedbackUserAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Feedback> list) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            this.mListFeedback = list.stream()
                    .filter(feedback -> feedback.getUserID().equals(currentUserId))
                    .collect(Collectors.toList());
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_admin, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = mListFeedback.get(position);
        if (feedback == null) {
            return;
        }

        // Lấy email của người dùng từ Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(feedback.getUserID());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String email = snapshot.child("email").getValue(String.class);
                    holder.email.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });

        holder.comment.setText(feedback.getComment());
        holder.date.setText(feedback.getDate()); // Hiển thị ngày
    }

    @Override
    public int getItemCount() {
        if (mListFeedback != null) {
            return mListFeedback.size();
        }
        return 0;
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        private TextView email, comment, date;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.email);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.date); // Khởi tạo TextView cho ngày
        }
    }
}
