package com.slack.motometer.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.ProfileImage;
import com.slack.motometer.domain.repositories.ImageRepository;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ImageService;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.utilities.PhotoUtility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfile extends AppCompatActivity {

    // UI components
    private ImageView profileImageIV;
    private EditText yearValueET, makeValueET, modelValueET, hoursValueET;
    private TextView infoPanelTV;

    // Logic components
    private String profileId;
    private ProfileRepository profileRepository;
    private Profile profile;
    private ImageRepository imageRepository;
    private ProfileImage profileImage;
    // Image
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Set toolbar
        Toolbar myToolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_edit_profile_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set Logic components
        profileId = getIntent().getExtras().getString("profileId");
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
        imageRepository = new ImageService(this);
        profileImage = imageRepository.getImageByProfileId(profileId);

        // Get handle on UI components
        profileImageIV = findViewById(R.id.edit_profile_iv);
        yearValueET = findViewById(R.id.edit_profile_year_value_et);
        makeValueET = findViewById(R.id.edit_profile_make_value_et);
        modelValueET = findViewById(R.id.edit_profile_model_value_et);
        hoursValueET = findViewById(R.id.edit_profile_hours_value_et);
        infoPanelTV = findViewById(R.id.information_tv);

        // Set UI components
        profileImageIV.setImageBitmap(profileImage.getImage());
        profileImageIV.setClickable(true);
        yearValueET.setText(profile.getYear());
        yearValueET.setSelectAllOnFocus(true);
        makeValueET.setText(profile.getMake());
        makeValueET.setSelectAllOnFocus(true);
        modelValueET.setText(profile.getModel());
        modelValueET.setSelectAllOnFocus(true);
        hoursValueET.setText(profile.getHours());
        hoursValueET.setSelectAllOnFocus(true);
        infoPanelTV.setText(R.string.activity_edit_profile_information);
        // Set imageView onClick to show dialog prompt for photo selection (camera/gallery)
        profileImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoSelectDialog();
            }
        });
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Set toolbar icon actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back button - return to previous activity
            case android.R.id.home:
                finish();
                return true;
            // Save profile edits to db
            case R.id.toolbar_edit_profile_icon_save:
                if (validateEditProfileForm()) {
                    saveProfileEdits();
                    finish();
                }
                return true;
            // Clear edit profile fields
            case R.id.toolbar_edit_profile_icon_clear:
                yearValueET.setText("");
                makeValueET.setText("");
                modelValueET.setText("");
                hoursValueET.setText("");
                yearValueET.requestFocus();
                return true;
            case R.id.toolbar_delete_profile:
                AlertDialog profileDeleteDialog = createDeleteProfileDialog(profile);
                profileDeleteDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Validate all EditTexts in edit profile activity
    private boolean validateEditProfileForm() {
        if (validateEditText(yearValueET) && validateEditText(makeValueET)
                && validateEditText(modelValueET) && validateEditText(hoursValueET)) {
            return true;
        }
        return false;
    }

    // Ensure EditText field is not empty
    private boolean validateEditText(EditText editText) {
        if (editText.getText().toString().length() < 1) {
            editText.setError(getResources().getString(R.string.validation_entry_required));
            editText.requestFocus();
            return false;
        }
        return true;
    }

    // Save profile edits to db
    private void saveProfileEdits() {
        profileRepository.updateProfile(new Profile(profile.getId(), yearValueET.getText().toString(),
                makeValueET.getText().toString(), modelValueET.getText().toString(),
                hoursValueET.getText().toString()));
    }

    // Build profile delete alert dialog
    private AlertDialog createDeleteProfileDialog(Profile profileToDelete) {
        return new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alert_dialog_delete_profile_title))
                .setMessage(getResources().getString(R.string.alert_dialog_delete_profile_message))
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(getResources().getString(R.string.alert_dialog_confirm),
                        (dialogInterface, i) -> {
                            profileRepository.deleteProfile(profile);
                            dialogInterface.dismiss();
                            // Go to main activity
                            Intent homeIntent = new Intent(this, MainActivity.class);
                            startActivity(homeIntent);
                        })
                .setNegativeButton(getResources().getString(R.string.alert_dialog_cancel),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
    }

    // Helper method to build/show photo select dialog
    private void showPhotoSelectDialog() {
        final CharSequence[] options = { "Take Photo", "Pick from Gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        builder.setTitle("Add Photo");
        builder.setItems(options, (dialog, option) -> {
            boolean result = PhotoUtility.checkPermission(EditProfile.this);

            if (options[option].equals("Take Photo")) {
                userSelection = "Take Photo";
                if(result)
                    cameraIntent();
            } else if (options[option].equals("Pick from Gallery")) {
                userSelection = "Pick from Gallery";
                if(result)
                    galleryIntent();
            } else if (options[option].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // Start camera intent
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    // Start gallery picker intent
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PhotoUtility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (userSelection.equals("Take Photo"))
                    cameraIntent();
                else if (userSelection.equals("Choose from Library"))
                    galleryIntent();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    // User selected Gallery. Take bitmap from intent data, compress, set image view and store in
    // file/db via ImageRepository
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext()
                        .getContentResolver(), data.getData());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                // Set profile image view
                profileImageIV.setImageBitmap(bitmap);
                // Save image to db
                profileImage.setImage(bitmap);
                imageRepository.updateImage(profileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // User selected take photo. Take bitmap from intent data, compress, set image view and store in
    // file/db via ImageRepository
    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        // Set profile image view
        profileImageIV.setImageBitmap(bitmap);
        // Save image to db
        profileImage.setImage(bitmap);
        imageRepository.updateImage(profileImage);
    }
}
