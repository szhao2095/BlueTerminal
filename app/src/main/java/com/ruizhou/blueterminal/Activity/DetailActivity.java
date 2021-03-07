package com.ruizhou.blueterminal.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ruizhou.blueterminal.BLE_Service;
import com.ruizhou.blueterminal.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    public static String read_data;

    private TextView responseView;
    private EditText cmdView;
    private Button submitBut;
    //    private Button graphBut;
    private Button receiveBut;
    private Button dumpBut;
    private Button connectBut;

    private BLE_Service ble;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private Uri mFileUri;

    private HashMap<String, String> cachedFiles;
    private static final int FILE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setUpUI();
        responseView.setMovementMethod(new ScrollingMovementMethod());
        ble = MainActivity.ble;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    // user is signed in
                }else{
                    //user is signed out

                }
            }
        };

        cachedFiles = MainActivity.cachedFiles;


        submitBut.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                String content = cmdView.getText().toString();
                try {
                    ble.writeData(content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        receiveBut.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
                ble.readData();
                responseView.setText(ble.response.toString());
            }
        });

//        graphBut.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void onClick(View v) {
////                ble.setFileListName("filelist.txt");
////                ble.setFile(context); // Delete file if it exists and create new file
////                try {
////                    ble.writeData("NAMES#");
////                } catch (UnsupportedEncodingException e) {
////                    e.printStackTrace();
////                }
//                ble.readData();
//                read_data = ble.response.toString();
//                openList("test.txt");
//            }
//        });
        dumpBut.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                if (!cachedFiles.containsKey(ble.anchorName)) {
                    ble.setFileListName(ble.anchorName);
                    try {
                        Log.d("COMMANDDDD", "command: #LIST:a#");
                        ble.writeData("#LIST:a:123456#");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    // Insert into hashmap
                    cachedFiles.put(ble.anchorName, "foo");

                    // Wait for onCharacteristicChanged to finish running
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                openList("data.txt");

            }
        });

        connectBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < 19) {
                    Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    fileIntent.setType("image/*");
                    startActivityForResult(fileIntent,FILE_CODE);
                }else{
                    Intent fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    fileIntent.setType("iamge/*");
                    startActivityForResult(fileIntent,FILE_CODE);
                }
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("file/*");
                startActivityForResult(fileIntent,FILE_CODE);
//                upload();
            }
        });

    }

    private void upload(){
        mDatabaseReference = database.getReference().child("files");
        mDatabaseReference.keepSynced(true);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        for (Map.Entry<String,String> entry : ble.cachedPaths.entrySet()){
            Uri file = Uri.fromFile(new File(entry.getValue()));
            StorageReference filepath = mStorageReference.child("files_content").child(file.getLastPathSegment());
            filepath.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloaduri = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    downloaduri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DatabaseReference newPost = mDatabaseReference.push();
                            Map<String, String> dataToSave = new HashMap<>();
                            dataToSave.put("userId", mUser.getUid());
                            dataToSave.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));
                            dataToSave.put("file",uri.toString());

                        }
                    });

                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILE_CODE && resultCode == RESULT_OK){
            mFileUri = data.getData();
            mDatabaseReference = database.getReference().child("files");
            mDatabaseReference.keepSynced(true);
            mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference filepath = mStorageReference.child("files_content").child(mFileUri.getLastPathSegment());
            filepath.putFile(mFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloaduri = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    downloaduri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DatabaseReference newPost = mDatabaseReference.push();
                            Map<String, String> dataToSave = new HashMap<>();
                            dataToSave.put("userId", mUser.getUid());
                            dataToSave.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));
                            dataToSave.put("file",uri.toString());

                        }
                    });

                }
            });


        }
    }

    private void setUpUI(){
        responseView = (TextView)findViewById(R.id.resultView);
        cmdView = (EditText)findViewById(R.id.cmdInput);
        submitBut = (Button)findViewById(R.id.submitBut);
//        graphBut = (Button)findViewById(R.id.graphBut);
        receiveBut = (Button)findViewById(R.id.receiveBut);
        dumpBut = (Button)findViewById(R.id.dump);
        connectBut = (Button) findViewById(R.id.connectButton);
    }

    public void openList(String filename) {
        Intent intent = new Intent(this, FileList.class);
        intent.putExtra("filename", filename);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}