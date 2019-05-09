package com.glitterlabs.skeleton.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.glitterlabs.skeleton.R;
import com.glitterlabs.skeleton.model.Users;
import com.glitterlabs.skeleton.utility.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pixplicity.easyprefs.library.Prefs;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UpdateProfile extends AppCompatActivity {

    private static final int SELECT_FILE = 1001;
    private static final int REQUEST_CAMERA = 1002;
    private static final int PERMISSION_REQUEST_CODE = 105;


    private DatabaseReference databaseReference;

    public Button btnChangeSubmit;

    private String userChoosenTask;

    private Users user;

    private Bitmap bitmap;

    Toolbar toolbar;
    public TextView phoneNumber;
    public EditText userName, userAddress;
    public TextView editProfile;

    public ImageView UserProfile;
    private Uri mImageUri = null;
    private FirebaseDatabase database;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private String strPicUrl;

    FirebaseUser firebaseUser;

    String userId;
    private StorageReference sRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        initFirebase();
        initViews();
        registerEvents();

        databaseReference.child(Constant.TEST).child(Constant.USERS).child(Prefs.getString("userID",null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Users.class);
                phoneNumber.setText(user.getmMobile());
                userName.setText(user.getmName());

                userAddress.setText(user.getmAddress());

                if (String.valueOf(user.getmPicUrl()) != null) {
                    Glide.with(UpdateProfile.this)
                            .load(user.getmPicUrl())
                            .into(UserProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            Uri selectedImageUri = data.getData();
            String path = getRealPathFromURI(selectedImageUri);
            // selectedImg = new File(selectedImageUri.getPath());
            CropImage.activity(Uri.fromFile(new File(path)))
                    .start(UpdateProfile.this);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = UpdateProfile.this.managedQuery(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) {
                bitmap = (Bitmap) data.getExtras().get("data");
                //imgProfilePic.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Uri tempUri = getImageUri(UpdateProfile.this, bitmap);

                uploadFile(tempUri);

            }

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void registerEvents() {

        btnChangeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newUserName = userName.getText().toString().trim();
                String newUserAddress = userAddress.getText().toString().trim();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("mName", newUserName);
                childUpdates.put("mAddress", newUserAddress);

                databaseReference.child(Constant.TEST).child(Constant.USERS)
                        .child(Prefs.getString("userID",null)).updateChildren(childUpdates);
                Intent intent = new Intent(UpdateProfile.this, ProfileActivity.class)
                        .putExtra("userID",Prefs.getString("userID",null));
                startActivity(intent);

            }
        });

        UserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        selectImage();
                        Log.e("permission", "Permission already granted.");
                    } else {
                        //If your app doesn’t have permission to access external storage, then call requestPermission//
                        requestPermission();
                    }
                }else {
                    selectImage();
                }
            }
        });

    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(UpdateProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void galleryIntent() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(UpdateProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(UpdateProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(UpdateProfile.this, Manifest.permission.CAMERA);
        //If the app does have this permission, then return true//

        if (result == PackageManager.PERMISSION_GRANTED && result1==PackageManager.PERMISSION_GRANTED && result3==PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private void uploadFile(Uri uri){
        if (uri != null){
            /*MainApplication mainApplication = MainApplication.getInstance();
            User user =mainApplication.getUser();
            user.getmUserID();*/
            final ProgressDialog progressDialog = new ProgressDialog(UpdateProfile.this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            sRef = mStorageRef.child(Prefs.getString("userID",null)+"."+getFileExtension(uri));

            sRef.putFile(uri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();

                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();
                                    //   Log.d(TAG, "onSuccess: uri= "+ uri.toString());
                                    strPicUrl = uri.toString();
                                    //user.setmPicURL(strPicUrl);

                                    Log.e("onSuccess: ", "" + strPicUrl);
                                    Glide.with(UpdateProfile.this).load(strPicUrl).centerCrop()
                                            .bitmapTransform(new CropCircleTransformation(UpdateProfile.this))
                                            .into(UserProfile);

                                    Toast.makeText(UpdateProfile.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                }
                            });
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                                        }
                                    });

                    }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = UpdateProfile.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void initFirebase() {

        databaseReference = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("user");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("user");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.getUid();

    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Update Profile");
        setSupportActionBar(toolbar);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = getIntent().getStringExtra("user");
        UserProfile = (ImageView)findViewById(R.id.profile_image);
        //editProfile = (TextView)findViewById(R.id.editUserProfile);
        phoneNumber = (TextView)findViewById(R.id.phone_number);
        userName = (EditText) findViewById(R.id.userName);
        userAddress = (EditText) findViewById(R.id.userAddress);
        btnChangeSubmit = (Button)findViewById(R.id.changeSubmit);
    }
}


/*private void writeNewUser(String name, String address) {
        User user = new User(name, address);

        mDatabase.child("users").child(userId).setValue(user);
    }*/

    /*private void saveDataFirebase(User user) {
        Map<String,Object> childUpdates = new HashMap<>();
        if(String.valueOf(user.getmMobile())!="null")
            childUpdates.put("mUserId",user.getmUserID());
        //childUpdates.put("mMobile", user.getmMobile());
        childUpdates.put("mName",user.getmName());
        childUpdates.put("mAddress", user.getmAddress());
        childUpdates.put("mPicUrl",user.getmPicUrl());
        databaseReference.child(user.getmUserID()).updateChildren(childUpdates);
        Toast.makeText(UpdateProfile.this, "User Created", Toast.LENGTH_SHORT).show();

    }
*/