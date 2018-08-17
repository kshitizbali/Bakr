package com.kshitizbali.bakr.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kshitizbali.bakr.R;
import com.kshitizbali.bakr.database.RecipeIngredientsEntry;

import java.util.List;

public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.RecipeIngredientsAdapterViewHolder> {

    final private onIngreItemClickListener onIngreItemClickListener;
    private Context context;
    private List<RecipeIngredientsEntry> mRecipeIngreEntriesList;
    private int ingreNum;


    public RecipeIngredientsAdapter(Context context, onIngreItemClickListener itemClickListener) {
        this.context = context;
        this.onIngreItemClickListener = itemClickListener;
        this.ingreNum = 1;
    }

    @NonNull
    @Override
    public RecipeIngredientsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.item_recipe_ingre, parent, false);

        return new RecipeIngredientsAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeIngredientsAdapterViewHolder holder, int position) {

        RecipeIngredientsEntry recipeIngredientsEntry = mRecipeIngreEntriesList.get(position);

        holder.tvIngre_Name.setText(recipeIngredientsEntry.getIngredient());
        holder.tvMeasure.setText(recipeIngredientsEntry.getMeasure());
        context.getResources();
        holder.tvNo.setText(context.getResources().getString(R.string.recipe_ingre, ingreNum++));
        holder.tvQuantity.setText(String.valueOf(recipeIngredientsEntry.getQuantity()));

    }

    @Override
    public int getItemCount() {
        if (mRecipeIngreEntriesList == null || mRecipeIngreEntriesList.size() == 0) {
            return 0;
        } else {
            return mRecipeIngreEntriesList.size();
        }
    }


    public List<RecipeIngredientsEntry> getmRecipeIngreEntriesList() {
        return mRecipeIngreEntriesList;
    }

    public void setmRecipeIngreEntriesList(List<RecipeIngredientsEntry> mRecipeIngreEntriesList) {
        this.mRecipeIngreEntriesList = mRecipeIngreEntriesList;
        notifyDataSetChanged();
    }

    public interface onIngreItemClickListener {
        void onIngreItemClickListener(int itemId, String itemName);
    }


    class RecipeIngredientsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvQuantity, tvMeasure, tvNo;
        AppCompatTextView tvIngre_Name;

        public RecipeIngredientsAdapterViewHolder(View itemView) {
            super(itemView);
            tvIngre_Name = (AppCompatTextView) itemView.findViewById(R.id.tvIngre_Name);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            tvMeasure = (TextView) itemView.findViewById(R.id.tvMeasure);
            tvNo = (TextView) itemView.findViewById(R.id.tvNo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int elementId = mRecipeIngreEntriesList.get(getAdapterPosition()).getId();
            onIngreItemClickListener.onIngreItemClickListener(elementId, mRecipeIngreEntriesList.get(getAdapterPosition()).getIngredient());
        }
    }
}
