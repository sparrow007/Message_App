package com.jackandphantom.messageapp.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jackandphantom.messageapp.R;
import com.jackandphantom.messageapp.Utils.DataUtil;
import com.jackandphantom.messageapp.model.MessageModel;
import com.jackandphantom.messageapp.model.UserModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private DatabaseReference userDatabase = firebaseDatabase.getReferenceFromUrl("https://message-app-368c2.firebaseio.com/userDB");

    private List<UserModel> userModels = new ArrayList<>();

    private static final int PERMISSION_REQUEST_CODE = 0;
     EditText editText;
      Button button;
     TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        checkSelfPermission();


    }

    void initial() {
        setContentView(R.layout.activity_user);


        editText = findViewById(R.id.usernameEdit);
        button = findViewById(R.id.usernameButton);
        textView = findViewById(R.id.show_message);


        userDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                userModels.add(userModel);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editText.getText().toString().trim().isEmpty()) {

                    DataUtil.setUserId(editText.getText().toString());

                    if (!isUserExist(DataUtil.getUserId())) {
                        UserModel userModel = new UserModel();
                        if (textView.getVisibility() == View.VISIBLE) {
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translation_up);
                            textView.startAnimation(animation);
                            textView.setVisibility(View.INVISIBLE);
                        }
                        DataUtil.setUserId(editText.getText().toString());

                        Calendar calendar = Calendar.getInstance();
                        int a = calendar.get(Calendar.AM_PM);
                        String time;
                        if (a == Calendar.AM) {
                            time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + "AM";
                        } else {
                            time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + "PM";
                        }

                        userModel.setUserName(DataUtil.getUserId());
                        userModel.setCreateAt(time);

                        userDatabase.child(DataUtil.getUserId()).setValue(userModel);
                        Intent intent = new Intent(UserActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translation_down);
                        textView.startAnimation(animation);
                        textView.setVisibility(View.VISIBLE);
                        //userDatabase.child(editText.getText().toString().trim()).removeValue();
                    }

                } else {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translation_down);
                    textView.startAnimation(animation);
                    textView.setText("Oops ! You forget to Enter Username");
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private boolean isUserExist(String name) {
        for (int i = 0; i < userModels.size(); i++) {

            if (name.equals(userModels.get(i).getUserName())) {

                return true;
            }
        }
        return false;
    }


    private void checkSelfPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if( ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                //Normal flow
                initial();
            }
            else {
                int writeMemoryPerssion = ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int locationMemoryPerssion = ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                int readMemoryPerssion = ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                List<String> permssionList = new ArrayList<>();
                if (writeMemoryPerssion != PackageManager.PERMISSION_GRANTED) {
                    permssionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (locationMemoryPerssion != PackageManager.PERMISSION_GRANTED) {
                    permssionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (readMemoryPerssion != PackageManager.PERMISSION_GRANTED) {
                    permssionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                if (!permssionList.isEmpty()) {
                    ActivityCompat.requestPermissions(UserActivity.this, permssionList.toArray(new String[permssionList.size()]),
                            PERMISSION_REQUEST_CODE);
                }
            }
        }
        else {
            //No need to ask run time permssions
            initial();

            return;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE :

                HashMap<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i=0; i< permissions.length;i++)
                        perms.put(permissions[i],grantResults[i]);
                    if(perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //Normal flow
                       initial();
                    }

                    else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(UserActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(UserActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(UserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOk("This app need all Permssions would you try again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case DialogInterface.BUTTON_NEGATIVE:

                                            break;
                                        case DialogInterface.BUTTON_POSITIVE:
                                            checkSelfPermission();
                                            break;
                                    }
                                }
                            });

                        }

                    }
                }

                break;

        }
    }

    private void showDialogOk(String s,DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(UserActivity.this)
                .setMessage(s)
                .setPositiveButton("ok",onClickListener)
                .setNegativeButton("cancel",onClickListener)
                .create()
                .show();
    }
}
