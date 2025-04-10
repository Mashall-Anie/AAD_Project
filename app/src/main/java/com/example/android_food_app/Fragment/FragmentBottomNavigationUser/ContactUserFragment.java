package com.example.android_food_app.Fragment.FragmentBottomNavigationUser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android_food_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView facebook, tiktok, intasgram;

    public ContactUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LienHeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactUserFragment newInstance(String param1, String param2) {
        ContactUserFragment fragment = new ContactUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_user, container, false);

        // Find the views
        View facebook = view.findViewById(R.id.facebook);
        View tiktok = view.findViewById(R.id.tiktok);

        // Set OnClickListener for Facebook
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Facebook link
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/NgocAnh062"));
                startActivity(intent);
            }
        });

        // Set OnClickListener for TikTok
        tiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open TikTok link
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com"));
                startActivity(intent);
            }
        });

        return view;
    }

}
