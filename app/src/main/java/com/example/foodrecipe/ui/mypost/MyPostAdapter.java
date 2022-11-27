package com.example.foodrecipe.ui.mypost;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.FoodViewHolder> {

    Context context;
    ArrayList<RecipeData> myFoodList;
    ArrayList<RecipeData> myFoodListFull;



    public MyPostAdapter(Context context, ArrayList<RecipeData> myFoodList) {
        this.context = context;
//        this.myFoodListFull = myFoodList;
//        this.myFoodList = new ArrayList<>(myFoodListFull);
        this.myFoodList = myFoodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_row_receipe,parent,false);



        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {

        RecipeData recipeData = myFoodList.get(position);

        holder.name.setText(recipeData.name);
        holder.description.setText(recipeData.description);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + recipeData.recipe_id );

        try {
//            progressDialog.show();

            final File localFile = File.createTempFile( recipeData.recipe_id , "jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                        ImageView tvImageID;

                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                            holder.image.setImageBitmap(bitmap);
//                            progressDialog.dismiss();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(getActivity(), "Fail to load image.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return myFoodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder{

        TextView name, description;
        ImageView image;


        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvName);
            description = itemView.findViewById(R.id.tvDescription);
            image = itemView.findViewById(R.id.tvimageID);

        }
    }

}
