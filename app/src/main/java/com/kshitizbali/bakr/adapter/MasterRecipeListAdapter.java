package com.kshitizbali.bakr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kshitizbali.bakr.R;
import com.kshitizbali.bakr.database.RecipeEntry;
import com.kshitizbali.bakr.utilities.ConstantUtilities;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MasterRecipeListAdapter extends RecyclerView.Adapter<MasterRecipeListAdapter.MasterRecipeListViewHolder> {

    final private ItemClickListener itemClickListener;
    final private FavRecipeClickListener favRecipeClickListener;
    private Context context;
    private List<RecipeEntry> mRecipeEntriesList;
    private String[] recipeImageArray;


    public MasterRecipeListAdapter(Context context, ItemClickListener itemClickListener, FavRecipeClickListener favRecipeClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.favRecipeClickListener = favRecipeClickListener;
        recipeImageArray = context.getResources().getStringArray(R.array.recipe_images_array);
    }

    @NonNull
    @Override
    public MasterRecipeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_master_recipe_list, parent, false);
        return new MasterRecipeListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MasterRecipeListViewHolder holder, int position) {

        RecipeEntry recipeEntry = mRecipeEntriesList.get(position);

        holder.tvName.setText(recipeEntry.getName());
        holder.tvServings.setText(context.getResources().getString(R.string.servings, recipeEntry.getServings()));
        /*Picasso.with(context).load(recipeEntry.getImage()).placeholder(R.drawable.placeholder_400_300).into(holder.imageView);*/
        if (recipeEntry.getImage() != null && recipeEntry.getImage().trim().length() != 0) {
            Picasso.with(context).load(recipeEntry.getImage()).placeholder(R.drawable.placeholder_400_300).into(holder.imageView);
        } else {
            Picasso.with(context).load(recipeImageArray[position]).into(holder.imageView);
        }

        if (!recipeEntry.getFavourite().isEmpty() && recipeEntry.getFavourite().equals(ConstantUtilities.FAV_YES)) {
            Picasso.with(context).load(R.drawable.ic_action_fav).into(holder.ivFav);
        } else if (!recipeEntry.getFavourite().isEmpty() && recipeEntry.getFavourite().equals(ConstantUtilities.FAV_NO)) {
            Picasso.with(context).load(R.drawable.ic_action_no_fav).into(holder.ivFav);
        }


    }

    @Override
    public int getItemCount() {
        if (mRecipeEntriesList == null) {
            return 0;
        }
        return mRecipeEntriesList.size();
    }


    public interface ItemClickListener {
        void onItemClickListener(int itemId, String recipeName);
    }

    public interface FavRecipeClickListener {
        void isFavRecipe(int recipeId, String favOrNot);
    }

    public List<RecipeEntry> getRecipeList() {
        return mRecipeEntriesList;
    }

    public void setRecipeList(List<RecipeEntry> mRecipeEntriesList) {
        this.mRecipeEntriesList = mRecipeEntriesList;
        notifyDataSetChanged();
    }

    class MasterRecipeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView tvServings, tvName;
        ImageView imageView, ivFav;
        CardView card_recipe_item;

        public MasterRecipeListViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.tvName);
            tvServings = (TextView) view.findViewById(R.id.tvServings);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            card_recipe_item = (CardView) view.findViewById(R.id.card_recipe_item);
            ivFav = (ImageView) view.findViewById(R.id.ivFav);
            view.setOnClickListener(this);
            ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    favRecipeClickListener.isFavRecipe(mRecipeEntriesList.get(pos).getId(), mRecipeEntriesList.get(pos).getFavourite());
                    Bitmap bitmapisFav = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_action_fav, null)).getBitmap();
                    Bitmap bitmapisNotFav = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_action_no_fav, null)).getBitmap();
                    BitmapDrawable imageDrawable = (BitmapDrawable) ivFav.getDrawable();
                    Bitmap imageViewBitmap = imageDrawable.getBitmap();
                    if (imageViewBitmap == bitmapisFav) {
                        Picasso.with(context).load(R.drawable.ic_action_no_fav).into(ivFav);
                    } else if (imageViewBitmap == bitmapisNotFav) {
                        Picasso.with(context).load(R.drawable.ic_action_fav).into(ivFav);
                    }
                    /*if (mRecipeEntriesList.get(pos).getFavourite().equals(ConstantUtilities.FAV_YES)) {
                        Picasso.with(context).load(R.drawable.ic_action_fav).into(ivFav);
                    } else if (mRecipeEntriesList.get(pos).getFavourite().equals(ConstantUtilities.FAV_NO)){
                        Picasso.with(context).load(R.drawable.ic_action_no_fav).into(ivFav);
                    }*/

                }
            });
        }


        @Override
        public void onClick(View v) {
            int elementId = mRecipeEntriesList.get(getAdapterPosition()).getId();
            String recipeName = mRecipeEntriesList.get(getAdapterPosition()).getName();
            itemClickListener.onItemClickListener(elementId, recipeName);

        }
    }


    public void emptyList() {
        if (mRecipeEntriesList != null && mRecipeEntriesList.size() > 0) {
            mRecipeEntriesList.clear();
            notifyDataSetChanged();
        }
    }
}
