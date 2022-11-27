package com.example.foodrecipe.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.MainActivity;
import com.example.foodrecipe.R;
import com.example.foodrecipe.ui.RecipeDetailsFragment;
import com.example.foodrecipe.ui.login.Login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.FoodViewHolder>  {

    private static ClickListener clickListener;

    Context context;
    ArrayList<RecipeData> myFoodList;
    ArrayList<RecipeData> myFoodListFull;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            name = (TextView) itemView.findViewById(R.id.tvName);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        HomeAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public HomeAdapter(Context context, ArrayList<RecipeData> myFoodList) {
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


    public static class FoodViewHolder extends RecyclerView.ViewHolder {

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