package com.glitterlabs.home.skeleton1;

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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.MimeTypeFilter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import static android.support.constraint.Constraints.TAG;
import static com.glitterlabs.home.skeleton1.MainApplication.mainApplication;


public class EnterPhoto extends Fragment {


    private DatabaseReference databaseReference;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private User user;

    private DatabaseReference getPhotoUrl ;

    //private ImageView mImageURI;

    private Button btnSubmitProfile;
    private Bitmap bitmap;
    private Uri croppedImgUri = null;

    private Uri mImageUri = null;
    private ImageView selectImageView;
    private static final int REQUEST_CAMERA = 1002;
    private static final int SELECT_FILE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 105;

    private String urlPic = null;
    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    private StorageReference storageReference;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private String userChoosenTask;
    private UploadTask uploadTask;

    public EnterPhoto() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_photo, container, false);


        initFirebase();
        initView(view);
        registerEvents();

        return view;
    }

    private void registerEvents() {
        btnSubmitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainApplication = MainApplication.getInstance();
                user = mainApplication.getUser();
                uploadFile(mImageUri);
                saveDataFirebase(user);
            }
        });
        selectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
        }

    private void uploadFile(Uri uri){
        if (uri != null){
            MainApplication mainApplication = MainApplication.getInstance();
            User user =mainApplication.getUser();
            user.getmUserID();
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference sRef = mStorageRef.child(user.getmUserID()+"."+getFileExtension(uri));

            //StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis());

            sRef.putFile(uri)

            /*StorageReference fileRefrence = mStorageRef.child(System.currentTimeMillis()
            +"."+getFileExtension(mImageUri));
            fileRefrence.putFile(mImageUri)*/
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();

                            //urlPic = taskSnapshot.toString();
                            //urlPic =taskSnapshot.getUploadSessionUri().toString();

                                    //taskSnapshot.getDownloadUrl().toString();
                            /*Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //mProgressBar.setProgress(0);
                                }
                            },5000);*/

                            Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_SHORT).show();
                           // getDownloadUrl(urlPic);
                            //User user = new User(mEditTextFiled)
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            //mProgressBar.SetProgress((int)progress);
                        }
                    });
        }
    }
/*
    private void getDownloadUrl(String urlPic) {
        final StorageReference ref = storageReference.child("images/mountains.jpg");
        uploadTask = ref.putFile(urlPic);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Uri tempUri = getImageUri(getActivity(), bitmap);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                File finalFile = new File(getRealPathFromURI1(getActivity(),tempUri));

                uploadFile(tempUri);
                /*MainApplication mainApplication = MainApplication.getInstance();
                User user =mainApplication.getUser();
                user.setmPicURL(tempUri.toString());
                mainApplication.setUser(user);*/

            }


        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectImageView.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //mImageURI = data.getData(bm.toString());
        selectImageView.setImageBitmap(bm);

    }


    private void initView(View view) {
    btnSubmitProfile = (Button)view.findViewById(R.id.btnSubmitProfile);
    selectImageView = (ImageView)view.findViewById(R.id.profile_image);

    }

    private void saveDataFirebase(User user) {



        Map<String,Object> childUpdates = new HashMap<>();
        if(String.valueOf(user.getmMobile())!="null")
            childUpdates.put("mUserId",user.getmUserID());
            childUpdates.put("mMobile", user.getmMobile());
            childUpdates.put("mName",user.getmName());
            childUpdates.put("mAddress", user.getmAddress());
            childUpdates.put("mPicUrl",user.getmPicURL());
        databaseReference.child(user.getmUserID()).updateChildren(childUpdates);
        //databaseReference = FirebaseDatabase.getInstance().getReference().child();
        Toast.makeText(getActivity(), "User Created", Toast.LENGTH_SHORT).show();

    }

    private void initFirebase() {


        //databaseReference = new DatabaseReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("user");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("user");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.getUid();

        /*mainApplication.setUser(user);
        User user;
        user.getmUserID();
        Map<String,Object> childUpdates = new HashMap<>();*/


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI1(Context context,Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /*private void getPhotoURL(){
        getPhotoUrl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String massage = dataSnapshot.getValue(String.class);
                Picasso.get()
                        .load(massage);
                        //.into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/



}
