package com.example.smsgroups.ui.sendmessage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsgroups.GroupModel;
import com.example.smsgroups.MessageModel;
import com.example.smsgroups.R;
import com.example.smsgroups.ui.addtogroup.GroupAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class SendMessageFragment extends Fragment {

    RecyclerView groupsRecyclerView, messagesRecyclerView;
    TextView selectedGroupTextView, selectedMessageTextView;
    Button sendButton;

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    ArrayList<GroupModel> groupModelList;
    ArrayList<MessageModel> messageModelList;

    GroupModel selectedGroupModel;
    MessageModel selectedMessageModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_message, container, false);

        groupsRecyclerView = view.findViewById(R.id.rycMsgSelector);
        messagesRecyclerView = view.findViewById(R.id.rycGroupSelector_msg);

        selectedGroupTextView = view.findViewById(R.id.addingGroupSelected);
        selectedMessageTextView = view.findViewById(R.id.addingMessageSelected);

        sendButton = view.findViewById(R.id.btnMsgSend);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        groupModelList = new ArrayList<>();
        messageModelList = new ArrayList<>();

        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGrant -> {
            if (isGrant) {
                SendSms();
            } else {
                Toast.makeText(getContext(), "Toplu sms g??ndermek i??in izin gereklidir", Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(Manifest.permission.SEND_SMS);
            } else {
                SendSms();
            }
        });
        FetchGroups();
        FetchMessages();
        return view;
    }

    private void FetchGroups(){
        String uid = mAuth.getCurrentUser().getUid();

        mStore.collection("/userdata/" + uid + "/groups").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                groupModelList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    GroupModel groupModel = new GroupModel(document.getString("name"), document.getString("description"), document.getString("image"), (List<String>)document.get("numbers"),document.getId());
                    groupModelList.add(groupModel);
                }

                groupsRecyclerView.setAdapter(new GroupAdapter(groupModelList, position -> {
                    selectedGroupModel = groupModelList.get(position);
                    selectedGroupTextView.setText("Se??ili Grup: " + selectedGroupModel.getName());
                }));

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                groupsRecyclerView.setLayoutManager(linearLayoutManager);
            }
        });
    }

    private void FetchMessages(){
        String uid = mAuth.getCurrentUser().getUid();

        mStore.collection("/userdata/" + uid + "/messages").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                messageModelList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    MessageModel messageModel = new MessageModel(document.getString("name"), document.getString("description"), document.getId());
                    messageModelList.add(messageModel);
                }

                messagesRecyclerView.setAdapter(new MessageAdapter(messageModelList, position -> {
                    selectedMessageModel = messageModelList.get(position);
                    selectedMessageTextView.setText("Se??ili Mesaj: " + selectedMessageModel.getName());
                }));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                messagesRecyclerView.setLayoutManager(linearLayoutManager);
            }
        });
    }

    private void SendSms(){
        if(selectedGroupModel == null || selectedMessageModel == null){
            Toast.makeText(getContext(), "L??tfen bir grup ve mesaj se??in", Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedGroupModel.getNumbers() != null && selectedGroupModel.getNumbers().size() > 0){
            SmsManager smsManager = SmsManager.getDefault();
            for (String number : selectedGroupModel.getNumbers()) {
                smsManager.sendTextMessage(number, null, selectedMessageModel.getDescripton(), null, null);
            }

            Toast.makeText(getContext(), "Mesajlar g??nderildi", Toast.LENGTH_SHORT).show();
        }
    }
}