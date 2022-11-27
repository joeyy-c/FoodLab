package com.example.foodrecipe.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrecipe.MainActivity;
import com.example.foodrecipe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDetailsFragment extends Fragment {

    private RecipeDetailsViewModel mViewModel;
    private ImageView mIvRecipeImage;
    private TextView mTvRecipeName, mTvRecipeUser, mTvRecipeCategory, mTvRecipeDescription, mTvRecipeIngredients, mTvRecipeSteps;
    private ImageButton mIbDuplicateRecipe, mIbEditRecipe, mIbDeleteRecipe, mIbSaveRecipe, mIbShareRecipe;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String recipe_id, user_id, recipe_user_id;
    private ProgressDialog progressdialog;

    public static RecipeDetailsFragment newInstance() {
        return new RecipeDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mIvRecipeImage = getView().findViewById(R.id.ivRecipeImage);
        mTvRecipeName = getView().findViewById(R.id.tvRecipeName);
        mTvRecipeUser = getView().findViewById(R.id.tvRecipeUser);
        mTvRecipeCategory = getView().findViewById(R.id.tvRecipeCategory);
        mTvRecipeDescription = getView().findViewById(R.id.tvRecipeDescription);
        mTvRecipeIngredients = getView().findViewById(R.id.tvRecipeIngredients);
        mTvRecipeSteps = getView().findViewById(R.id.tvRecipeSteps);
        mIbDuplicateRecipe = getView().findViewById(R.id.ibDuplicateRecipe);
        mIbEditRecipe = getView().findViewById(R.id.ibEditRecipe);
        mIbDeleteRecipe = getView().findViewById(R.id.ibDeleteRecipe);
        mIbSaveRecipe = getView().findViewById(R.id.ibSaveRecipe);
        mIbShareRecipe = getView().findViewById(R.id.ibShareRecipe);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressdialog = new ProgressDialog(getActivity());

        recipe_id = getArguments().getString("recipe_id");
        user_id = "FRwgYsXhJNPx6rL6ZzKnaq3APbI3"; // jowyn id

        showRecipe();

        mIbDuplicateRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                BottomNavigationView bottomNavigationView= getView().findViewById(R.id.nav_view);
////                bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
////                View vieww = bottomNavigationView.findViewById(R.id.navigation_profile);
////                vieww.performClick();
//                bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
//                        return false;
//                    }
//                });

                Bundle bundle = new Bundle();
                bundle.putString("recipe_id", recipe_id);
                bundle.putString("action", "duplicate");
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_recipedetails_to_addrecipe, bundle);

            }
        });

        mIbEditRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("recipe_id", recipe_id);
                bundle.putString("action", "edit");
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_recipedetails_to_addrecipe, bundle);
            }
        });

        mIbDeleteRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Recipe")
                        .setMessage("Are you sure you want to delete this recipe?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.dialog_dlt_recipe, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteRecipe();
                                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                                navController.navigate(R.id.action_recipedetails_to_addrecipe);
                            }})
                        .setNegativeButton(R.string.dialog_cancel, null).show();
            }
        });

        mIbSaveRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIbSaveRecipe.getColorFilter() == null) {
                    bookmarkRecipe(true);
                    mIbSaveRecipe.setColorFilter(getResources().getColor(R.color.yellow));
                }
                else {
                    bookmarkRecipe(false);
                    mIbSaveRecipe.setColorFilter(null);
                }
            }
        });

        mIbShareRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String share_content =
                        "Recipe of " + mTvRecipeName.getText().toString() + "\n\n" +
                        "About Recipe" + "\n" + mTvRecipeDescription.getText().toString() + "\n\n" +
                        "Ingredients" + "\n" + mTvRecipeIngredients.getText().toString() + "\n\n" +
                        mTvRecipeUser.getText().toString() + "\n\n" +
                        "Check out full recipe step by step in our apps! Download FoodLab now!";

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
//                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_TEXT, share_content);
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });

    }

    public void deleteRecipe() {
        // delete recipe
        db.collection("recipe").document(recipe_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Recipe has been successfully deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Fail to delete recipe", Toast.LENGTH_SHORT).show();
                    }
                });

        // delete image in firebase storage
        StorageReference storageReference = storage.getReference().child("images/" + recipe_id);
        storageReference
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Image deleted successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), "Something wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void bookmarkRecipe(boolean save_recipe) {
        Map<String, Object> bookmark_user = new HashMap<>();

        if (save_recipe) {
            // add user to array
            bookmark_user.put("users_saved", FieldValue.arrayUnion(user_id));
        }
        else {
            // remove user from array
            bookmark_user.put("users_saved", FieldValue.arrayRemove(user_id));
        }

        db.collection("recipe").document(recipe_id)
                .update(bookmark_user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (save_recipe)
                            Toast.makeText(getActivity(), "Recipe saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Something wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showRecipe() {

        // retrieve from storage and show recipe image
        StorageReference storageReference = storage.getReference().child("images/" + recipe_id);

        try {
            progressdialog.show();

            final File localFile = File.createTempFile(recipe_id, "jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            mIvRecipeImage.setImageBitmap(bitmap);

                            // retrieve from firestore and show recipe details
                            db.collection("recipe").document(recipe_id).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                mTvRecipeName.setText(documentSnapshot.getString("name"));
                                                recipe_user_id = documentSnapshot.getString("user_id");
                                                showRecipeUser();
                                                mTvRecipeCategory.setText(documentSnapshot.getString("category"));
                                                mTvRecipeDescription.setText(documentSnapshot.getString("description"));
                                                mTvRecipeIngredients.setText(documentSnapshot.getString("ingredients"));
                                                List<String> users_saved = (List<String>) documentSnapshot.get("users_saved");

                                                if (users_saved.contains(user_id))
                                                    mIbSaveRecipe.setColorFilter(getResources().getColor(R.color.yellow));

                                                if (user_id.equals(recipe_user_id)) {
                                                    mIbDuplicateRecipe.setVisibility(View.GONE);
                                                    mIbEditRecipe.setVisibility(View.VISIBLE);
                                                    mIbDeleteRecipe.setVisibility(View.VISIBLE);
                                                }

                                                ArrayList<HashMap<String, String>> steps = (ArrayList<HashMap<String, String>>) documentSnapshot.get("step");

                                                String stepStr = "";
                                                int step_counter = 0;

                                                for (int i = 0; i < steps.size(); i++) {
                                                    step_counter = i+1;
                                                    stepStr += (i + 1) + ". " + steps.get(i).get("step_description") + " [ " + steps.get(i).get("step_time") + " mins ]\n\n";
                                                }

                                                mTvRecipeSteps.setText(stepStr);
                                                progressdialog.dismiss();
                                            }
                                            else {
                                                progressdialog.dismiss();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressdialog.dismiss();
                                            Toast.makeText(getActivity(), "Fail to load recipe data.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressdialog.dismiss();
                            Toast.makeText(getActivity(), "Fail to load image.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showRecipeUser() {
        db.collection("users").document(recipe_user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                mTvRecipeUser.setText("by " + documentSnapshot.getString("username"));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Fail to load recipe data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}