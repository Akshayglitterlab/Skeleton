package com.glitterlabs.skeleton.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.glitterlabs.home.skeleton1.R;
import com.glitterlabs.skeleton.model.Users;
import com.glitterlabs.skeleton.utility.Constant;
import com.glitterlabs.skeleton.utility.MainApplication;

import com.glitterlabs.skeleton.activity.HomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.net.InternetDomainName;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class EnterProfilePictureFragment extends Fragment {

    private static final int SELECT_FILE = 1001;
    private static final int REQUEST_CAMERA = 1002;
    private static final int PERMISSION_REQUEST_CODE = 105;

    private DatabaseReference databaseReference;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private Button btnSubmitProfile;
    private ImageView selectImageView;

    private FragmentManager fm;
    private FragmentTransaction ft;

    private ProgressDialog pd;

    private Users user;
    private MainApplication mainApplication;

    private String userChoosenTask;
    private Bitmap bitmap;

    private Uri croppedImgUri = null;
    private String urlPic = null;
    private InternetDomainName storageRef;
    private UploadTask uploadTask;
    private StorageReference sRef;
    private String strPicUrl;

    SharedPreferences sharedpreferences;
    public static final Integer MyPREFERENCES = 1 ;


    public EnterProfilePictureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_enter_photo_demo, container, false);

        initFirebase();
        initViews(view);
        registerEvents();

        return view;
    }

    private void registerEvents() {

        btnSubmitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = ProgressDialog.show(getActivity(), "", "Signup...");
                mainApplication = MainApplication.getInstance();
                user = mainApplication.getUser();

                if (urlPic != null) {
                    user.setmPicUrl(urlPic);
                }else{
                    user.setmPicUrl("null");
                }
                saveDataFirebase(user);
            }
        });


        selectImageView.setOnClickListener(new View.OnClickListener() {
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

    private void saveDataFirebase(Users user) {

        Map<String,Object> childUpdates = new HashMap<>();
        if(String.valueOf(user.getmMobile())!="null")
            childUpdates.put("mUserId",user.getmUserID());
        childUpdates.put("mMobile", user.getmMobile());
        childUpdates.put("mName",user.getmName());
        childUpdates.put("mAddress", user.getmAddress());
        childUpdates.put("mPicUrl",strPicUrl);
        databaseReference.child(user.getmUserID()).updateChildren(childUpdates);
        //databaseReference = FirebaseDatabase.getInstance().getReference().child();
        Toast.makeText(getActivity(), "User Created", Toast.LENGTH_SHORT).show();

        Prefs.putString("userID", user.getmUserID());
        Prefs.putInt("LoginStatus",1);
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA){
                bitmap = (Bitmap) data.getExtras().get("data");
                //imgProfilePic.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Uri tempUri = getImageUri(getActivity(), bitmap);
                //File finalFile = new File(getRealPathFromURI1(getActivity(),tempUri));
                /*CropImage.activity(Uri.fromFile(new File(String.valueOf(finalFile))))
                        .start(getActivity());*/
                //croppedImgUri = result.getUri();
                uploadFile(tempUri);

            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == getActivity().RESULT_OK) {
                    croppedImgUri = result.getUri();
                    uploadFile(croppedImgUri);
                    Log.d("img URI", croppedImgUri.toString());


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.d("error", error.getMessage().toString());
                }
            }
        }
    }

    private void uploadFile(Uri filePath) {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            sRef = storageReference.child(Constant.STORAGE_PATH_UPLOADS + System.currentTimeMillis());

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();
                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();
                                 //   Log.d(TAG, "onSuccess: uri= "+ uri.toString());
                                    strPicUrl = uri.toString();
                                    //user.setmPicURL(strPicUrl);

                                    Log.e("onSuccess: ", ""+strPicUrl);
                                    Glide.with(getActivity()).load(strPicUrl).centerCrop()
                                            .bitmapTransform(new CropCircleTransformation(getActivity()))
                                            .into(selectImageView);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            progressDialog.dismiss();
                        }
                    });
        } else {
            //display an error if no file is selected
        }
    }


    /*public String getRealPathFromURI1(Context context,Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }*/

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            Uri selectedImageUri = data.getData();
            String path = getRealPathFromURI(selectedImageUri);
            // selectedImg = new File(selectedImageUri.getPath());
            CropImage.activity(Uri.fromFile(new File(path)))
                    .start(getActivity());
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void requestPermission() {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void initViews(View view) {

        btnSubmitProfile = (Button)view.findViewById(R.id.btnSubmitProfile);
        selectImageView = (ImageView)view.findViewById(R.id.profile_image);
        //sharedpreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        fm = getFragmentManager();
        ft = fm.beginTransaction();

    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
        if(Prefs.getString(Constant.MODE,null).equals("live")){
            storageReference = FirebaseStorage.getInstance().getReference(Constant.LIVE);
            mDatabase = FirebaseDatabase.getInstance().getReference(Constant.LIVE).child(Constant.DATABASE_PATH_UPLOADS);
            databaseReference = database.getReference(Constant.LIVE).child(Constant.USERS);
        }else{
            storageReference = FirebaseStorage.getInstance().getReference(Constant.TEST);
            mDatabase = FirebaseDatabase.getInstance().getReference(Constant.TEST).child(Constant.DATABASE_PATH_UPLOADS);
            databaseReference = database.getReference(Constant.TEST).child(Constant.USERS);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        //If the app does have this permission, then return true//

        if (result == PackageManager.PERMISSION_GRANTED && result1==PackageManager.PERMISSION_GRANTED && result3==PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }



}
