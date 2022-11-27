package com.example.foodrecipe.ui.profile;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrecipe.R;
import com.example.foodrecipe.databinding.FragmentHomeBinding;
import com.example.foodrecipe.databinding.FragmentProfileBinding;
import com.example.foodrecipe.ui.addrecipe.AddRecipeFragment;
import com.example.foodrecipe.ui.home.HomeViewModel;
import com.example.foodrecipe.ui.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    EditText email_et, pw_et,user_et;
    Button update_btn,logout_btn,delete_btn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        email_et = getView().findViewById(R.id.email_et);
        pw_et = getView().findViewById(R.id.pw_et);
        user_et = getView().findViewById(R.id.user_et);

        logout_btn = getView().findViewById(R.id.logout_btn);
        delete_btn = getView().findViewById(R.id.delete_btn);
        update_btn = getView().findViewById(R.id.update_btn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

//        userId = fAuth.getCurrentUser().getUid();
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "");

        DocumentReference documentReference = fStore.collection("user").document(userId);

        getData();

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(userId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                Toast.makeText(getActivity(), "Account have been deleted successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), Login.class);
                                    startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clear user id in shared preferences when logout
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE);
                sharedPreferences.edit().remove("user_id").commit();
                Toast.makeText(getActivity(), "Logout successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> user = new HashMap<>();
                user.put("email", email_et.getText().toString());
                user.put("password", pw_et.getText().toString());
                user.put("username",user_et.getText().toString());

                db.collection("users").document(userId)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Update successful", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error to update", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        });

    }


    private void getData() {
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String current = user.getUid();//getting unique user id

        fStore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            user_et.setText((CharSequence) document.get("username"));
                            email_et.setText((CharSequence) document.get("email"));
                            pw_et.setText((CharSequence) document.get("password"));
                        }
                    }

                });
    }//get data

}//end fragment

