package com.example.foodrecipe.ui.addrecipe;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrecipe.MainActivity;
import com.example.foodrecipe.R;
import com.example.foodrecipe.databinding.ActivityMainBinding;
import com.example.foodrecipe.databinding.FragmentAddRecipeBinding;
import com.example.foodrecipe.ui.home.HomeFragment;
import com.example.foodrecipe.ui.mypost.MyPostFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddRecipeFragment extends Fragment {

    private FragmentAddRecipeBinding binding;
    private AddRecipeViewModel mViewModel;
    private Spinner mSpRecipeCategory;
    private ImageView mIvUploadRecipeImage;
    private ImageButton mIbStepInfo, mIbAddStep;
    private LinearLayout mllStepsWrapper;
    private EditText mEtRecipeStepDesc1;
    private Button mBtnUploadRecipeImage, mBtnAddRecipe, mBtnUpdateRecipe;
    private EditText mEtRecipeName;
    private EditText mEtRecipeDesc;
    private EditText mEtRecipeTime;
    private EditText mEtRecipeIngredients;
    private Uri imageUri;
    private String recipe_id;
    private ProgressDialog progressdialog;

    FirebaseFirestore db;
    FirebaseStorage storage;

    int CHOOSE_IMAGE = 200;

    public static AddRecipeFragment newInstance() {
        return new AddRecipeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    imageUri = result;
                    mIvUploadRecipeImage.setImageURI(imageUri);
                }
            });

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddRecipeViewModel.class);
        mSpRecipeCategory = getView().findViewById(R.id.spRecipeCategory);
        mIvUploadRecipeImage = getView().findViewById(R.id.ivUploadRecipeImage);
        mBtnUploadRecipeImage = getView().findViewById(R.id.btnUploadRecipeImage);
        mIbStepInfo = getView().findViewById(R.id.ibStepInfo);
        mIbAddStep = getView().findViewById(R.id.ibAddStep);
        mllStepsWrapper = getView().findViewById(R.id.llStepsWrapper);
        mEtRecipeStepDesc1 = getView().findViewWithTag("etRecipeStepDesc1");
        mEtRecipeTime = getView().findViewWithTag("etRecipeStepTime1");
        mBtnAddRecipe = getView().findViewById(R.id.btnAddRecipe);
        mBtnUpdateRecipe = getView().findViewById(R.id.btnUpdateRecipe);
        mEtRecipeName = getView().findViewById(R.id.etRecipeName);
        mEtRecipeDesc = getView().findViewById(R.id.etRecipeDesc);
        mEtRecipeIngredients = getView().findViewById(R.id.etRecipeIngredients);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressdialog = new ProgressDialog(getActivity());

        // recipe category
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity().getBaseContext(),
                R.array.recipe_category,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpRecipeCategory.setAdapter(adapter);

        // choose image from gallery
        mBtnUploadRecipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        mIbStepInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupWindow popup = new PopupWindow(getActivity());
                LinearLayout layout = new LinearLayout(getActivity());
                TextView tv = new TextView(getActivity());
                Button btn = new Button(getActivity());

                // set btn of into
                btn.setText("OK");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popup.dismiss();
                    }
                });

                // set layout param of layout in popup
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setBackgroundColor(getResources().getColor(R.color.white));
                layout.setPadding(dpToPx(45),dpToPx(45),dpToPx(45),dpToPx(45));
                layout.setLayoutParams(lp);

                // set text of info
                tv.setText("Click on the plus icon below the steps to add a new step. \n\nFill in the step description and time in minutes.");
                tv.setTextColor(getResources().getColor(R.color.black));
                tv.setPadding(0,0,0,dpToPx(40));

                // add tv and btn, set layout in popup
                layout.addView(tv);
                layout.addView(btn);
                popup.setBackgroundDrawable(null);
                popup.setContentView(layout);
                popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
            }
        });

        // add input field for step
        mIbAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStep();
            }
        });

        if (getArguments() != null) {
            fillRecipeDetails();

            if (getArguments().getString("action").equals("edit")) {
                mBtnAddRecipe.setVisibility(View.GONE);
                mBtnUpdateRecipe.setVisibility(View.VISIBLE);
                recipe_id = getArguments().getString("recipe_id");
                Toast.makeText(getActivity(), recipe_id, Toast.LENGTH_SHORT).show();
            }
        }

        mBtnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecipe();
            }
        });

        mBtnUpdateRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecipe();
            }
        });

        getView().findViewById(R.id.btnMyPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Fragment fragment = new MyPostFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.navigation_addrecipe, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();

                Bundle bundle = new Bundle();
                bundle.putString("recipe_id", "123");
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_addrecipe_to_mypost, bundle);
            }
        });

        getView().findViewById(R.id.btnJowynRecipeDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("recipe_id", "7IvhCQRQzSpBTVPC6sro");
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_addrecipe_to_recipedetails, bundle);
            }
        });

        getView().findViewById(R.id.btnWjRecipeDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("recipe_id", "NISPcUJNrSFlYity0s5y");
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_addrecipe_to_recipedetails, bundle);
            }
        });
    }

    public void fillRecipeDetails() {
        String recipe_id = getArguments().getString("recipe_id");

        // retrieve from storage and show recipe image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + recipe_id);

        try {
            progressdialog.show();

            final File localFile = File.createTempFile(recipe_id, "jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            mIvUploadRecipeImage.setImageBitmap(bitmap);

                            // retrieve from firestore and show recipe details
                            db.collection("recipe").document(recipe_id).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                mEtRecipeName.setText(documentSnapshot.getString("name"));
                                                String[] stringArray = getResources().getStringArray(R.array.recipe_category);
                                                mSpRecipeCategory.setSelection(Arrays.asList(stringArray).indexOf(documentSnapshot.getString("category")));
                                                mEtRecipeDesc.setText(documentSnapshot.getString("description"));
                                                mEtRecipeIngredients.setText(documentSnapshot.getString("ingredients"));

                                                ArrayList<HashMap<String, String>> steps = (ArrayList<HashMap<String, String>>) documentSnapshot.get("step");
                                                EditText et_desc_temp = new EditText(getActivity());
                                                EditText et_time_temp = new EditText(getActivity());

                                                for (int i = 0; i < steps.size(); i++) {
                                                    addStep();
                                                    et_desc_temp = getView().findViewWithTag("etRecipeStepDesc" + (i+1));
                                                    et_time_temp = getView().findViewWithTag("etRecipeStepTime" + (i+1));
                                                    et_desc_temp.setText(steps.get(i).get("step_description"));
                                                    et_time_temp.setText(steps.get(i).get("step_time"));
                                                }
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

    public void updateRecipe() {
        if (!validateField())
            return;

        Map<String, Object> recipe = new HashMap<>();
        recipe.put("user_id", "FRwgYsXhJNPx6rL6ZzKnaq3APbI3"); // jowyn id
//        recipe.put("user_id", "bs4k9NoVopfGlKwWNhuSvztNOEI2"); // wj id
        recipe.put("category", mSpRecipeCategory.getSelectedItem().toString());
        recipe.put("description", mEtRecipeDesc.getText().toString());
        recipe.put("ingredients", mEtRecipeIngredients.getText().toString());
        recipe.put("name", mEtRecipeName.getText().toString());

        int stepsCount = mllStepsWrapper.getChildCount();


//        Map<String, String>[] stepsArr = new HashMap[stepsCount];
        ArrayList<Map<String, String>> stepsList = new ArrayList<>();
        EditText et_desc_temp = new EditText(getActivity());
        EditText et_time_temp = new EditText(getActivity());

        for (int i = 1; i <= stepsCount; i++) {
            et_desc_temp = getView().findViewWithTag("etRecipeStepDesc" + i);
            et_time_temp = getView().findViewWithTag("etRecipeStepTime" + i);

            if (!et_desc_temp.getText().toString().isEmpty() && !et_time_temp.getText().toString().isEmpty()) {
                Map<String, String> step = new HashMap<>();
                step.put("step_description", et_desc_temp.getText().toString());
                step.put("step_time", et_time_temp.getText().toString());
//                stepsArr[i-1] = new HashMap<>(step);
                stepsList.add(step);
            }
        }

        Map<String, String>[] stepsArr = stepsList.toArray(new HashMap[stepsList.size()]);

        recipe.put("step", FieldValue.arrayUnion(stepsArr));

        // upload image to firebase storage only when user upload new image
        if (imageUri != null && !imageUri.equals(Uri.EMPTY)) {
            progressdialog.setMessage("Uploading image ...");
            progressdialog.show();

            StorageReference reference = storage.getReference().child("images/" + recipe_id);
            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Fail to upload image.", Toast.LENGTH_LONG).show();
                    }
                }
            });
            progressdialog.dismiss();
        }

        progressdialog.setMessage("Updating recipe ...");
        progressdialog.show();

        // insert recipe in firestore
        db.collection("recipe").document(recipe_id)
                .set(recipe)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Recipe has been successfully updated.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Fail to update recipe.", Toast.LENGTH_LONG).show();
                    }
                });

        progressdialog.dismiss();
    }

    public void addRecipe() {
        if (!validateField())
            return;

        Map<String, Object> recipe = new HashMap<>();
        recipe.put("user_id", "FRwgYsXhJNPx6rL6ZzKnaq3APbI3"); // jowyn id
//        recipe.put("user_id", "bs4k9NoVopfGlKwWNhuSvztNOEI2"); // wj id
        recipe.put("category", mSpRecipeCategory.getSelectedItem().toString());
        recipe.put("description", mEtRecipeDesc.getText().toString());
        recipe.put("ingredients", mEtRecipeIngredients.getText().toString());
        recipe.put("name", mEtRecipeName.getText().toString());
        recipe.put("users_saved", FieldValue.arrayUnion());

        int stepsCount = mllStepsWrapper.getChildCount();
        ArrayList<Map<String, String>> stepsList = new ArrayList<>();
        EditText et_desc_temp;
        EditText et_time_temp;

        for (int i = 1; i <= stepsCount; i++) {
            et_desc_temp = getView().findViewWithTag("etRecipeStepDesc" + i);
            et_time_temp = getView().findViewWithTag("etRecipeStepTime" + i);

            if (!et_desc_temp.getText().toString().isEmpty() && !et_time_temp.getText().toString().isEmpty()) {
                Map<String, String> step = new HashMap<>();
                step.put("step_description", et_desc_temp.getText().toString());
                step.put("step_time", et_time_temp.getText().toString());
                stepsList.add(step);
            }
        }

        Map<String, String>[] stepsArr = stepsList.toArray(new HashMap[stepsList.size()]);

        recipe.put("step", FieldValue.arrayUnion(stepsArr));

        String document_id = db.collection("recipe").document().getId();

        progressdialog.setMessage("Uploading image ...");
        progressdialog.show();

        // upload image to firebase storage
        StorageReference reference = storage.getReference().child("images/" + document_id);
        reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    progressdialog.dismiss();
                    progressdialog.setMessage("Publishing recipe ...");
                    progressdialog.show();

                    // insert recipe in firestore
                    db.collection("recipe").document(document_id)
                            .set(recipe)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressdialog.dismiss();
                                    Toast.makeText(getActivity(), "Recipe has been successfully published.", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressdialog.dismiss();
                                    Toast.makeText(getActivity(), "Fail to publish recipe.", Toast.LENGTH_LONG).show();
                                }
                            });
                }
                else {
                    progressdialog.dismiss();
                    Toast.makeText(getActivity(), "Fail to upload image.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public boolean validateField() {

        if (imageUri == null || imageUri.equals(Uri.EMPTY)) {
            Toast.makeText(getActivity(), "Recipe image is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mEtRecipeName.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Recipe name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mEtRecipeDesc.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Description is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mEtRecipeIngredients.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Ingredients is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mEtRecipeName.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Recipe name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        int stepsCount = mllStepsWrapper.getChildCount();
        EditText et_desc_temp;
        EditText et_time_temp;
        boolean allStepEmpty = true;

        for (int i = 1; i <= stepsCount; i++) {
            et_desc_temp = getView().findViewWithTag("etRecipeStepDesc" + i);
            et_time_temp = getView().findViewWithTag("etRecipeStepTime" + i);

            if (!(et_desc_temp.getText().toString().isEmpty() && et_time_temp.getText().toString().isEmpty())) {
                if (et_desc_temp.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Description is required in step " + i, Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (et_time_temp.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Time is required in step " + i, Toast.LENGTH_SHORT).show();
                    return false;
                }
                else if (!isNumeric(et_time_temp.getText().toString())) {
                    Toast.makeText(getActivity(), "Only digit is allowed in time field of step " + i, Toast.LENGTH_SHORT).show();
                    return false;
                }
                allStepEmpty = false;
            }
        }

        if (allStepEmpty) {
            Toast.makeText(getActivity(), "Steps is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void addStep() {
        // get total steps count
        int stepsCount = mllStepsWrapper.getChildCount();


        // create new linear layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        // define layout param
        LinearLayout.LayoutParams r_lp = new LinearLayout.LayoutParams(
                dpToPx(350),
                dpToPx(45)
        );
        r_lp.setMargins(0, dpToPx(15), 0, 0);
        linearLayout.setLayoutParams(r_lp);


        // create new textview for step num
        TextView tv = new TextView(getActivity());
        // define textview layout param
        LinearLayout.LayoutParams tv_lp = new LinearLayout.LayoutParams(
                dpToPx(30),
                dpToPx(45));
        tv.setLayoutParams(tv_lp);
        tv.setText(Integer.toString(stepsCount + 1));
        tv.setPadding(dpToPx(20), 0, 0, 0);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);


        // create new edittext for step desc
        EditText et_desc = new EditText(getActivity());
        LinearLayout.LayoutParams et_desc_lp = new LinearLayout.LayoutParams(
                dpToPx(230),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        et_desc_lp.setMargins(dpToPx(10), 0, 0, 0);
        et_desc.setLayoutParams(et_desc_lp);
        et_desc.setTag("etRecipeStepDesc" + (stepsCount + 1));
        et_desc.setMinHeight(dpToPx(45));
        et_desc.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.et_style));
        et_desc.setHint(R.string.label_recipe_step_description);
        et_desc.setTextColor(getResources().getColor(R.color.black));
        et_desc.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        et_desc.getBackground().setTint(getResources().getColor(R.color.light_grey));
        et_desc.setPadding(mEtRecipeStepDesc1.getPaddingLeft(),
                mEtRecipeStepDesc1.getPaddingTop(),
                mEtRecipeStepDesc1.getPaddingRight(),
                mEtRecipeStepDesc1.getPaddingBottom());
        et_desc.setEms(10);
        et_desc.setGravity(Gravity.START | Gravity.TOP);
        et_desc.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        // create new edittext for time
        EditText et_time = new EditText(getActivity());
        LinearLayout.LayoutParams et_time_lp = new LinearLayout.LayoutParams(
                dpToPx(70),
                dpToPx(45)
        );
        et_time_lp.setMargins(dpToPx(8), 0, 0, 0);
        et_time.setLayoutParams(et_time_lp);
        et_time.setTag("etRecipeStepTime" + (stepsCount + 1));
        et_time.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.et_style));
        et_time.setHint(R.string.label_recipe_time);
        et_time.setTextColor(getResources().getColor(R.color.black));
        et_time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        et_time.getBackground().setTint(getResources().getColor(R.color.light_grey));
        et_time.setPadding(mEtRecipeTime.getPaddingLeft(),
                mEtRecipeTime.getPaddingTop(),
                mEtRecipeTime.getPaddingRight(),
                mEtRecipeTime.getPaddingBottom());

        // add views in linear layout
        linearLayout.addView(tv);
        linearLayout.addView(et_desc);
        linearLayout.addView(et_time);
        mllStepsWrapper.addView(linearLayout);
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public int dpToPx(int dp) {
        float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return (int) value;
    }

}