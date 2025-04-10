package com.example.android_food_app.AdapterAdmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_food_app.Model.Feedback;
import com.example.android_food_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FeedbackAdminAdapter extends RecyclerView.Adapter<FeedbackAdminAdapter.FeedbackViewHolder> {
    private Context mContext;
    private List<Feedback> mListFeedback;

    public FeedbackAdminAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Feedback> list) {
        this.mListFeedback = list;
        notifyDataSetChanged();
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
                // Handle possible errors
            }
        });

        holder.comment.setText(feedback.getComment());
        holder.date.setText(feedback.getDate()); // Set the date
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
            date = itemView.findViewById(R.id.date); // Initialize the date TextView
        }
    }
}
