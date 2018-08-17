package com.kshitizbali.bakr.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kshitizbali.bakr.R;
import com.kshitizbali.bakr.RecipeDetailFragment;
import com.kshitizbali.bakr.database.RecipeStepsEntry;

import java.util.ArrayList;
import java.util.List;

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.RecipeStepsAdapterViewHolder> {

    final private onStepsItemClickListener onStepsItemClickListener;
    private Context context;
    private List<RecipeStepsEntry> mRecipeStepsList;
    private boolean tabletMode;
    List<CardView> cardViewList = new ArrayList<>();
    private int stepPosition;
    private int stepNumber;


    private int initialSelectedStepPos = 0;


    public RecipeStepsAdapter(Context context, onStepsItemClickListener onStepsItemClickListener, boolean isTabletMode, int position) {
        this.context = context;
        this.onStepsItemClickListener = onStepsItemClickListener;
        this.tabletMode = isTabletMode;
        this.stepPosition = position;
        this.stepNumber = 1;
    }

    @NonNull
    @Override
    public RecipeStepsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_recipe_steps, parent, false);
        return new RecipeStepsAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeStepsAdapterViewHolder holder, int position) {

        RecipeStepsEntry recipeStepsEntry = mRecipeStepsList.get(position);


        holder.tvStepNo.setText(String.valueOf(stepNumber++));
        if (tabletMode) {
            cardViewList.add(holder.cv_recipeStepItem);
        }
        if (tabletMode && position == getInitialSelectedStepPos()) {
            cardViewList.get(getInitialSelectedStepPos()).setCardBackgroundColor(context.getResources().getColor(R.color.darkGrey));
        }


        holder.tvShortDesc.setText(recipeStepsEntry.getShortDescription());

    }

    @Override
    public int getItemCount() {
        if (mRecipeStepsList == null || mRecipeStepsList.size() == 0) {
            return 0;
        } else {
            return mRecipeStepsList.size();
        }
    }


    public interface onStepsItemClickListener {
        void onRecipeStepsItemClickListener(int itemId, String videoLink, String desc, String shortDesc, int manRecipeId, int position, int noOfSteps);
    }

    public List<RecipeStepsEntry> getmRecipeStepsList() {
        return mRecipeStepsList;
    }

    public void setmRecipeStepsList(List<RecipeStepsEntry> mRecipeStepsList) {
        this.mRecipeStepsList = mRecipeStepsList;
        notifyDataSetChanged();
    }

    class RecipeStepsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvShortDesc, tvStepNo;
        CardView cv_recipeStepItem;

        public RecipeStepsAdapterViewHolder(View itemView) {
            super(itemView);
            tvShortDesc = (TextView) itemView.findViewById(R.id.tvShortDesc);
            cv_recipeStepItem = (CardView) itemView.findViewById(R.id.cv_recipeStepItem);
            tvStepNo = (TextView) itemView.findViewById(R.id.tvStepNo);
            if (tabletMode) {
                getRecipeSteps(mRecipeStepsList, stepPosition);

              /*  for (CardView cardView : cardViewList) {
                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                }
                //The selected card is set to colorSelected
                cv_recipeStepItem.setCardBackgroundColor(context.getResources().getColor(R.color.darkGrey));*/

            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*int elementId = mRecipeStepsList.get(getAdapterPosition()).getId();
            int mainRecipeId = mRecipeStepsList.get(getAdapterPosition()).getRecipeId();
            String videoUrl = mRecipeStepsList.get(getAdapterPosition()).getVideoURL();
            String desc = mRecipeStepsList.get(getAdapterPosition()).getDescription();
            String shortDesc = mRecipeStepsList.get(getAdapterPosition()).getShortDescription();
            onStepsItemClickListener.onRecipeStepsItemClickListener(elementId, videoUrl, desc, shortDesc, mainRecipeId);*/
            getRecipeSteps(mRecipeStepsList, getAdapterPosition());

            if (tabletMode) {
                for (CardView cardView : cardViewList) {
                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                }
                //The selected card is set to colorSelected
                cv_recipeStepItem.setCardBackgroundColor(context.getResources().getColor(R.color.darkGrey));
                setInitialSelectedStepPos(getAdapterPosition());

            }

        }
    }

    public void getRecipeSteps(List<RecipeStepsEntry> mRecipeStepsList, int position) {
        if (mRecipeStepsList != null && mRecipeStepsList.size() != 0) {
            int elementId = mRecipeStepsList.get(position).getId();
            int mainRecipeId = mRecipeStepsList.get(position).getRecipeId();
            String videoUrl = mRecipeStepsList.get(position).getVideoURL();
            String desc = mRecipeStepsList.get(position).getDescription();
            String shortDesc = mRecipeStepsList.get(position).getShortDescription();
            onStepsItemClickListener.onRecipeStepsItemClickListener(elementId, videoUrl, desc, shortDesc, mainRecipeId, position, mRecipeStepsList.size());
        }
    }

    public int getInitialSelectedStepPos() {
        return initialSelectedStepPos;
    }

    public void setInitialSelectedStepPos(int initialSelectedStepPos) {
        this.initialSelectedStepPos = initialSelectedStepPos;
    }


}
