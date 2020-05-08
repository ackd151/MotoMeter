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
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
    private ImageView imageView;
    private EditText yearValue, makeValue, modelValue, hoursValue;

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
        imageView = findViewById(R.id.edit_profile_iv);
        yearValue = findViewById(R.id.edit_profile_year_value_et);
        makeValue = findViewById(R.id.edit_profile_make_value_et);
        modelValue = findViewById(R.id.edit_profile_model_value_et);
        hoursValue = findViewById(R.id.edit_profile_hours_value_et);

        // Set UI components
        imageView.setImageBitmap(profileImage.getImage());
        imageView.setClickable(true);
        yearValue.setText(profile.getYear());
        yearValue.setSelectAllOnFocus(true);
        makeValue.setText(profile.getMake());
        makeValue.setSelectAllOnFocus(true);
        modelValue.setText(profile.getModel());
        modelValue.setSelectAllOnFocus(true);
        hoursValue.setText(profile.getHours());
        hoursValue.setSelectAllOnFocus(true);
        // Set imageView onClick to show dialog prompt for photo selection (camera/gallery)
        imageView.setOnClickListener(new View.OnClickListener() {
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
                yearValue.setText("");
                makeValue.setText("");
                modelValue.setText("");
                hoursValue.setText("");
                yearValue.requestFocus();
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                        android.R.drawable.ic_menu_gallery));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Helper method to validate all edittexts in edit profile activity
    private boolean validateEditProfileForm() {
        if (validateEditText(yearValue) && validateEditText(makeValue)
                && validateEditText(modelValue) && validateEditText(hoursValue)) {
            return true;
        }
        return false;
    }

    // Helper method to ensure edittext field is not empty
    private boolean validateEditText(EditText editText) {
        if (editText.getText().toString().length() < 1) {
            editText.setError(getResources().getString(R.string.validation_entry_required));
            editText.requestFocus();
            return false;
        }
        return true;
    }

    // Helper method to save profile edits to db
    private void saveProfileEdits() {
        profileRepository.updateProfile(new Profile(profile.getId(), yearValue.getText().toString(),
                makeValue.getText().toString(), modelValue.getText().toString(),
                hoursValue.getText().toString()));
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

    // Helper method to start camera intent
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    // Helper method to start gallery picker intent
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

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext()
                        .getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Save image to db
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        imageView.setImageBitmap(bitmap);
        profileImage.setImage(bitmap);
        imageRepository.updateImage(profileImage);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);

        // Save image to db
        imageView.setImageBitmap(bitmap);
        profileImage.setImage(bitmap);
        imageRepository.updateImage(profileImage);
    }
}