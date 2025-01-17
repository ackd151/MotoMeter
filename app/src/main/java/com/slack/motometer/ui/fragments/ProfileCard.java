package com.slack.motometer.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ChecklistLogic;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.logic.TaskLogic;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.Task;
import com.slack.motometer.domain.repositories.ChecklistRepository;
import com.slack.motometer.domain.repositories.ImageRepository;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ChecklistService;
import com.slack.motometer.domain.services.ImageService;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.ui.activities.EditProfile;
import com.slack.motometer.ui.activities.Notes;
import com.slack.motometer.ui.activities.PostRide;
import com.slack.motometer.ui.activities.PreRide;
import com.slack.motometer.ui.activities.TasksOverview;

import java.util.Objects;

public class ProfileCard extends Fragment {

    private Context context;
    private String profileId;

    public static ProfileCard newInstance(String profileId) {
        ProfileCard fragment = new ProfileCard();
        Bundle bundle = new Bundle();
        bundle.putString("profileId", profileId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_profile_card, container, false);

        // UI components
        TextView profileTitleTV, profileHoursTV, maintenanceIconTV, nextTaskTV, inspStatusTV,
                taskHoursTV, nextTaskProximityTV;
        ImageView profileImageIV;

        // Logic components
        ProfileRepository profileRepository;
        ChecklistRepository checklistRepository;
        Profile profile;
        TaskLogic taskLogic;
        ChecklistLogic checklistLogic;
        Task nextTask;
        // Get handle on context.getResources to reduce calls below
        Resources resources = Objects.requireNonNull(context).getResources();

        // Set Logic components
        profileRepository = new ProfileService(context);
        checklistRepository = new ChecklistService(context);
        profileId = getArguments().getString("profileId");
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
        taskLogic = new TaskLogic(context, profileId);
        checklistLogic = new ChecklistLogic(context);
        nextTask = taskLogic.getNextDue(Integer.parseInt(profileId));

        // Get handle on UI components
        profileTitleTV = view.findViewById(R.id.profile_card_title_tv);
        profileImageIV = view.findViewById(R.id.profile_card_image_iv);
        profileHoursTV = view.findViewById(R.id.profile_card_hrs_value_tv);
        maintenanceIconTV = view.findViewById(R.id.profile_card_wrench_tv);
        nextTaskTV = view.findViewById(R.id.profile_card_task_tv);
        inspStatusTV = view.findViewById(R.id.profile_card_insp_status_tv);
        taskHoursTV = view.findViewById(R.id.profile_card_task_hrs_value_tv);
        nextTaskProximityTV = view.findViewById(R.id.profile_card_upcoming_tv);

        // Set UI components
        profileTitleTV.setText(new ProfileLogic(context).getProfileTitle(profile));
        ImageRepository imageRepository = new ImageService(context);
        profileImageIV.setImageBitmap(imageRepository.getImageByProfileId(profileId).getImage());
        profileHoursTV.setText(profile.getHours());
        // Set maintenance indicator (i.e. wrench icon color/visibility)
        TaskLogic.MaintenanceDue due =
                new TaskLogic(context, profile.getId()).isMaintenanceDue();
        if (due == TaskLogic.MaintenanceDue.NOT) {
            maintenanceIconTV.setVisibility(View.GONE);
        } else if (due == TaskLogic.MaintenanceDue.SOON) {
            maintenanceIconTV.getBackground().setColorFilter(resources
                    .getColor(R.color.caution), PorterDuff.Mode.SRC_ATOP);
        } else {
            maintenanceIconTV.getBackground().setColorFilter(resources
                    .getColor(R.color.danger), PorterDuff.Mode.SRC_ATOP);
        }
        // combine into if
        if (nextTask == null) {
            nextTaskTV.setText(R.string.profile_card_no_tasks);
            taskHoursTV.setVisibility(View.GONE);
        } else {
            nextTaskTV.setText(nextTask.getTaskTitle());
            taskHoursTV.setVisibility(View.VISIBLE);
            taskHoursTV.setText(taskLogic.getRemainingHours(nextTask));
            taskHoursTV.setTextColor(due == TaskLogic.MaintenanceDue.NOT ?
                    resources.getColor(R.color.accent_pressed) :
                    due == TaskLogic.MaintenanceDue.SOON ?
                            resources.getColor(R.color.caution) :
                            resources.getColor(R.color.danger));
            nextTaskProximityTV.setText(taskLogic.getDueProximityString(nextTask));
        }
        inspStatusTV.setBackgroundColor(
            checklistLogic.isReady(checklistRepository
                    .getProfileChecklistItems(Integer.parseInt(profileId))) ?
                    resources.getColor(R.color.accent_pressed) : resources.getColor(R.color.danger)
        );

        // Set view onClickListener to start TasksOverview activity
        view.setClickable(true);
        view.setOnClickListener(v -> {
            Intent tasksOverviewIntent = new Intent(context, TasksOverview.class);
            tasksOverviewIntent.putExtra("profileId", profileId);
            startActivity(tasksOverviewIntent);
        });

        // Set action buttons - edit, tasks, pre-ride, post-ride, notes
        TextView editProfileTV, tasksTV, preRideTV, postRideTV, notesTV;
        editProfileTV = view.findViewById(R.id.profile_card_edit_tv);
        tasksTV = view.findViewById(R.id.profile_card_tasks_btn);
        preRideTV = view.findViewById(R.id.profile_card_pre_ride_btn);
        postRideTV = view.findViewById(R.id.profile_card_post_ride_btn);
        notesTV = view.findViewById(R.id.profile_card_notes_btn);
        setActionButton(editProfileTV, EditProfile.class);
        setActionButton(tasksTV, TasksOverview.class);
        setActionButton(preRideTV, PreRide.class);
        setActionButton(postRideTV, PostRide.class);
        setActionButton(notesTV, Notes.class);

        return view;
    }

    private void setActionButton(TextView textView, Class cls) {
        textView.setClickable(true);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(context, cls);
            intent.putExtra("profileId", profileId);
            startActivity(intent);
        });
    }
}
