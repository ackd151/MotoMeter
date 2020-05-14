package com.slack.motometer.ui.fragments;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.slack.motometer.R;

public class CollapsibleInfoPanel extends Fragment {

    private boolean infoVisible;
    private ConstraintLayout infoCollapseView;
    private Button infoButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoVisible = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_collapsible_info_panel, container, false);
        // Set info button listener/handler
        infoButton = view.findViewById(R.id.info_btn);
        infoCollapseView = view.findViewById(R.id.collapsing_info_cl);
        infoButton.setOnClickListener(v -> {
            if (!infoVisible) {
                infoCollapseView.setVisibility(View.VISIBLE);
                infoButton.setBackgroundResource(R.drawable.ic_arrow_down);
                infoVisible = true;
            } else {
                infoCollapseView.setVisibility(View.GONE);
                infoButton.setBackgroundResource(R.drawable.ic_info);
                infoVisible = false;
            }
        });
        return view;
    }
}
