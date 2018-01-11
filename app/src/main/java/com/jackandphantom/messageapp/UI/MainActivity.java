package com.jackandphantom.messageapp.UI;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jackandphantom.messageapp.R;
import com.jackandphantom.messageapp.Utils.DataUtil;
import com.jackandphantom.messageapp.adapter.DataAdapter;
import com.jackandphantom.messageapp.model.MessageModel;
import com.jackandphantom.messageapp.model.UserModel;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance() ;
   private DatabaseReference userDatabase = firebaseDatabase.getReferenceFromUrl("https://message-app-368c2.firebaseio.com/userDB");
    private DatabaseReference messageDatabase = firebaseDatabase.getReferenceFromUrl("https://message-app-368c2.firebaseio.com/messageDB");

   private EditText text;
   private Button bt;
   private RecyclerView recyclerView ;
   private DataAdapter dataAdapter;
   private TextView onlineStatus;

   private boolean firstTime=true;

   private Button uploadImage;
   private List<MessageModel> messageModels = new ArrayList<>();
   private String messageId="1";
   private static final int RESULT_LOAD_IMAGE = 1;
   private Uri filePath;
   private Toolbar mTopToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.message_layout);



        bt = findViewById(R.id.button_chatbox_send);
        text = findViewById(R.id.edittext_chatbox);
        recyclerView = findViewById(R.id.layout_chatbox);
        onlineStatus = findViewById(R.id.online_status);
        uploadImage = findViewById(R.id.uploadImage);
        dataAdapter = new DataAdapter(MainActivity.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");

                        startActivityForResult(Intent.createChooser(i, "select Profile"), RESULT_LOAD_IMAGE);
                    }
        });

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if (charSequence.length() > 0 && !charSequence.toString().trim().isEmpty()) {
                    bt.setVisibility(View.VISIBLE);
                } else {
                    bt.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MessageModel messageModel = new MessageModel();

                Calendar calendar = Calendar.getInstance();
                int a = calendar.get(Calendar.AM_PM);
                String time;
                if(a == Calendar.AM) {
                    time = calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+"AM";
                }
                else {
                    time = calendar.get(Calendar.HOUR)+ ":"+calendar.get(Calendar.MINUTE)+"PM";
                }

                messageModel.setTiming(time);
                messageId = messageDatabase.push().getKey();
                messageModel.setMessage(text.getText().toString());
                messageModel.setSender_name(DataUtil.getUserId());
                messageModel.setId(messageId);
                messageModel.setUserType(true);
                messageModels.add(messageModel);

                messageDatabase.child(messageId).setValue(messageModel);
                addDataToAdapter();
                text.setText("");

            }
        });

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String number = "Ther are "+dataSnapshot.getChildrenCount()+" People Connected";
                onlineStatus.setText(number);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        messageDatabase.limitToLast(1);
        messageDatabase.limitToFirst(1);


        messageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MessageModel model = dataSnapshot.getValue(MessageModel.class);
//                Log.e("MY TAG", "Model is added = "+model.getMessage());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        messageDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageModel model = dataSnapshot.getValue(MessageModel.class);

                if (!messageId.equals(model.getId()) && !messageModels.contains(model)) {
                    model.setUserType(false);
                    messageModels.add(model);
                    addDataToAdapter();
                }


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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void addDataToAdapter() {
        dataAdapter.setAddData(messageModels);
        if (firstTime) {
            recyclerView.setAdapter(dataAdapter);
            firstTime = false;
        }

        recyclerView.scrollToPosition(messageModels.size()-1);


    }

    @Override
    protected void onStop() {

        super.onStop();

    }

    @Override
    protected void onDestroy() {
        userDatabase.child(DataUtil.getUserId()).removeValue();
        DataUtil.setUserId("");
        Toast.makeText(MainActivity.this, "This is on stop", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
