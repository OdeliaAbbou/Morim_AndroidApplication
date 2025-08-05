package com.example.morim;

import static com.example.morim.database.remote.FirebaseUserManager.CURRENT_USER_TYPE_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.morim.R;
import com.example.morim.adapter.MessageAdapter;
import com.example.morim.database.OnDataCallback;
import com.example.morim.model.Chat;
import com.example.morim.model.Message;
import com.example.morim.model.SingleChatData;
import com.example.morim.model.User;
import com.example.morim.viewmodel.AuthViewModel;
import com.example.morim.viewmodel.ChatViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatActivity extends BaseActivity {
    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private ChatViewModel chatViewModel;
    private AuthViewModel authViewModel;
    private Chat currentChat;
    private boolean chatExists = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatViewModel = viewModel(ChatViewModel.class);
        authViewModel = viewModel(AuthViewModel.class);
        String teacherId = getIntent().getStringExtra("TEACHER_ID");
        String studentId = getIntent().getStringExtra("STUDENT_ID");

        Log.d("ChatActivity", "onCreate: " + teacherId + ", " + studentId);

        if (teacherId.equals(FirebaseAuth.getInstance().getUid())) {
            chatViewModel.getChat(studentId, FirebaseAuth.getInstance().getUid(), new OnDataCallback<Chat>() {
                @Override
                public void onData(Chat chat) {
                    if (chat != null) {
                        chatExists = true;
                        chatViewModel.listenChat(chat.getId(), teacherId, authViewModel.getCurrentUser());
                    } else {
                        chatExists = false;
                    }
                }
                @Override
                public void onException(Exception e) {
                    chatExists = false;
                }
            });
        } else {
            chatViewModel.tryToGetExistingChat(FirebaseAuth.getInstance().getUid(), teacherId, new OnDataCallback<Chat>() {
                @Override
                public void onData(Chat chat) {
                    if (chat != null) {
                        chatExists = true;
                        chatViewModel.listenChat(chat.getId(), teacherId, authViewModel.getCurrentUser());
                    } else {
                        chatExists = false;
                    }
                }
                @Override
                public void onException(Exception e) {
                    chatExists = false;
                }
            });
        }

        chatViewModel.getActiveChat().observe(this, new Observer<SingleChatData>() {
            @Override
            public void onChanged(SingleChatData singleChatData) {
                Chat currentChat = singleChatData.getMyChat();

                if (!singleChatData.allResourcesAvailable() || currentChat == null || currentChat.getMessages() == null)
                    return;

                ChatActivity.this.currentChat = currentChat;

                String currentUserId = FirebaseAuth.getInstance().getUid();
                boolean updated = false;

                for (Message m : currentChat.getMessages()) {
                    if (!m.isRead() && !m.getSenderId().equals(currentUserId)) {
                        m.setRead(true);
                        updated = true;
                    }
                }

                if (updated) {
                    chatViewModel.updateChat(currentChat);
                }

                Log.d("ChatActivity", "onChanged: " + currentChat.getMessages().size());
                System.out.println(currentChat.getStudentId() + ", " + currentChat.getTeacherId() + ", " + currentChat.getMessages().size());

                adapter.update(currentChat.getMessages());
                recyclerView.scrollToPosition(currentChat.getMessages().size() - 1);
            }
        });

        chatViewModel.getUsers(new OnDataCallback<List<User>>() {
            @Override
            public void onData(List<User> value) {
                Map<String, User> users = new HashMap<>();
                for (User user : value) {
                    users.put(user.getId(), user);
                }
                adapter.setUsers(users);
            }

            @Override
            public void onException(Exception e) {

            }
        });

        recyclerView = findViewById(R.id.rvMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        EditText messageInput = findViewById(R.id.etMessage);
        ImageButton sendButton = findViewById(R.id.btnSend);
        sendButton.setOnClickListener(v -> {
            String content = messageInput.getText().toString();
            if (!content.trim().isEmpty()) {
                if (chatExists && currentChat != null) {
                    chatViewModel.sendMessage(currentChat, FirebaseAuth.getInstance().getUid(), content);
                } else {
                    chatViewModel.createOrGetChat(FirebaseAuth.getInstance().getUid(), teacherId, new OnDataCallback<Chat>() {
                        @Override
                        public void onData(Chat chat) {
                            currentChat = chat;
                            chatExists = true;
                            chatViewModel.listenChat(chat.getId(), teacherId, authViewModel.getCurrentUser());
                            chatViewModel.sendMessage(chat, FirebaseAuth.getInstance().getUid(), content);
                        }
                        @Override
                        public void onException(Exception e) {
                        }
                    });
                }
            }
            messageInput.setText("");
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

    }


}
