package com.example.smsgroups.ui.createmessage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smsgroups.MessageModel;
import com.example.smsgroups.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;


public class CreateMessageFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    EditText messageNameEditText, messageDescriptionEditText;
    Button createMessageButton;
    RecyclerView messagesRecyclerView;

    ArrayList<MessageModel> messageModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_message, container, false);

        messageNameEditText = view.findViewById(R.id.editTextMsgName);
        messageDescriptionEditText = view.findViewById(R.id.editTextMsgDesc);
        createMessageButton = view.findViewById(R.id.btnCreateMsg);
        messagesRecyclerView = view.findViewById(R.id.rycMsgs);

        messageModelList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        createMessageButton.setOnClickListener(v -> {
            String messageName = messageNameEditText.getText().toString();
            String messageDescription = messageDescriptionEditText.getText().toString();

            if (messageName.isEmpty() || messageDescription.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }
            CreateMessage(messageName, messageDescription);

        });

        FetchMessages();
        return  view;

    }
    private void CreateMessage(String messageName, String messageDescription) {
        String userId = mAuth.getCurrentUser().getUid();

        mStore.collection("/userdata/" + userId + "/messages").add(new HashMap<String, String>(){{
                    put("name", messageName);
                    put("description", messageDescription);
                }})
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Mesaj başarıyla oluşturuldu", Toast.LENGTH_SHORT).show();

                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        MessageModel messageModel = new MessageModel(messageName, messageDescription, documentSnapshot.getId());
                        messageModelList.add(messageModel);
                        messagesRecyclerView.getAdapter().notifyItemInserted(messageModelList.size() - 1);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Mesaj oluşturulurken bir hata oluştu", Toast.LENGTH_SHORT).show();
                });
    }

    private void FetchMessages(){
        String userId = mAuth.getCurrentUser().getUid();
        mStore.collection("/userdata/" + userId + "/messages").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    messageModelList.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        MessageModel messageModel = new MessageModel(documentSnapshot.getString("name"), documentSnapshot.getString("description"), documentSnapshot.getId());
                        messageModelList.add(messageModel);
                    }

                    messagesRecyclerView.setAdapter(new MessageAdapter(messageModelList));
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    messagesRecyclerView.setLayoutManager(linearLayoutManager);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Mesajlar alınırken bir hata oluştu", Toast.LENGTH_SHORT).show();
                });
    }
}