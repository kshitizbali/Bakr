package com.kshitizbali.bakr;

import android.app.Fragment;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kshitizbali.bakr.adapter.RecipeIngredientsAdapter;
import com.kshitizbali.bakr.adapter.RecipeStepsAdapter;
import com.kshitizbali.bakr.database.AppDatabase;
import com.kshitizbali.bakr.database.RecipeEntry;
import com.kshitizbali.bakr.database.RecipeIngredientsEntry;
import com.kshitizbali.bakr.database.RecipeStepsEntry;
import com.kshitizbali.bakr.utilities.ConstantUtilities;
import com.kshitizbali.bakr.utilities.SavePreferences;
import com.kshitizbali.bakr.viewmodel.RecipeStepsAndIngredientsViewModel;
import com.kshitizbali.bakr.viewmodel.RecipeStepsAndIngredientsViewModelFactory;
import com.kshitizbali.bakr.widget.BakrStarRecipeWidgetProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeDetailFragment extends android.support.v4.app.Fragment implements RecipeIngredientsAdapter.onIngreItemClickListener, RecipeStepsAdapter.onStepsItemClickListener/*, RecipeStepDetailFragment.onFabRecipeStepChangeListener*/ {


    private RecyclerView rvRecipeSteps, rvRecipeIngre;
    private AppDatabase mDb;
    public static final String INSTANCE_RECIPE_ID = "instanceRecipeId";
    public static final String INSTANCE_RECIPE_STEP_ID = "instanceRecipeStepId";
    public static final String INSTANCE_RECIPE_STEP_POSITION = "instanceRecipeStepPosition";
    public static final String INSTANCE_RECIPE_NAME = "instanceRecipeName";

    private int recipeId, recipeStepId, recipeStepPosition = 0, noOfSteps;
    private String recipeName;
    private RecipeIngredientsAdapter recipeIngredientsAdapter;
    private RecipeStepsAdapter recipeStepsAdapter;
    private static final int DEFAULT_RECIPE_ID = -1;
    private static final String DEFAULT_RECIPE_NAME = "Recipe";
    private static final int DEFAULT_RECIPE_STEP_ID = 1;
    private static final int DEFAULT_RECIPE_STEP_POSITION = 0;
    private TextView tvIngredients;
    private boolean mTwoPane;

    public static final String INSTANCE_RV_POS = "rv_pos";

    OnRecipeStepClickListener mRecipeStepClickListener;


    public RecipeDetailFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public interface OnRecipeStepClickListener {
        void onRecipeStepSelected(int recipeId, String videoLink, String desc, String shortDesc, int mainRecipeId, int noOfSteps, List<RecipeStepsEntry> recipeStepsEntryList, int position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        instantiateViews(rootView, savedInstanceState);


        return rootView;
    }

    private void instantiateViews(View view, Bundle savedInstanceState) {


        if (view.findViewById(R.id.ll_recipe_detail_two_pane_tab) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }


        if (savedInstanceState != null) {
            setRecipeId(savedInstanceState.getInt(INSTANCE_RECIPE_ID, DEFAULT_RECIPE_ID));
            setRecipeStepId(savedInstanceState.getInt(INSTANCE_RECIPE_STEP_ID, DEFAULT_RECIPE_STEP_ID));
            setRecipeStepPosition(savedInstanceState.getInt(INSTANCE_RECIPE_STEP_POSITION, DEFAULT_RECIPE_STEP_POSITION));
            /*recipeId = savedInstanceState.getInt(INSTANCE_RECIPE_ID, DEFAULT_RECIPE_ID);*/
            recipeName = savedInstanceState.getString(INSTANCE_RECIPE_NAME, DEFAULT_RECIPE_NAME);

        } else {
            if (this.getArguments() != null && this.getArguments().getInt(INSTANCE_RECIPE_ID) != 0) {

                setRecipeId(this.getArguments().getInt(INSTANCE_RECIPE_ID));

            }
            if (this.getArguments() != null && !this.getArguments().getString(INSTANCE_RECIPE_NAME).isEmpty()) {

                recipeName = this.getArguments().getString(INSTANCE_RECIPE_NAME);

            }

        }

        tvIngredients = (TextView) view.findViewById(R.id.tvIngredients);
        rvRecipeSteps = (RecyclerView) view.findViewById(R.id.rvRecipeSteps);
        if (view.findViewById(R.id.cl_Land) != null) {
            rvRecipeSteps.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            rvRecipeSteps.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        /*rvRecipeSteps.setHasFixedSize(true);*/
        recipeStepsAdapter = new RecipeStepsAdapter(getActivity(), this, mTwoPane, getRecipeStepPosition());
        rvRecipeSteps.setAdapter(recipeStepsAdapter);

        if (savedInstanceState != null) {
            // scroll to existing position which exist before rotation.
            recipeStepsAdapter.setInitialSelectedStepPos(savedInstanceState.getInt(INSTANCE_RV_POS));
        }

        rvRecipeIngre = (RecyclerView) view.findViewById(R.id.rvRecipeIngre);
        rvRecipeIngre.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*rvRecipeIngre.setHasFixedSize(true);*/
        recipeIngredientsAdapter = new RecipeIngredientsAdapter(getActivity(), this);
        rvRecipeIngre.setAdapter(recipeIngredientsAdapter);
        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        tvIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rvRecipeIngre.getVisibility() == View.GONE) {
                    rvRecipeIngre.setVisibility(View.VISIBLE);
                    tvIngredients.setText(getResources().getString(R.string.hide_recipe_ingredients));

                } else {
                    rvRecipeIngre.setVisibility(View.GONE);
                    tvIngredients.setText(getResources().getString(R.string.show_recipe_ingredients));
                }
            }
        });


        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(recipeName);
        }

        getSelectedRecipe(recipeId, 0);
    }


    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mRecipeStepClickListener = (OnRecipeStepClickListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeClickListener");
        }
    }

   /* @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getSelectedRecipe(recipeId);
    }*/

    protected void getSelectedRecipe(int recipeId, int recipeStepId) {

        //We get the position of the recipe selected from the master fragment and then show the recipe detail here
        try {
            RecipeStepsAndIngredientsViewModelFactory factory = new RecipeStepsAndIngredientsViewModelFactory(mDb, recipeId, recipeStepId);
            final RecipeStepsAndIngredientsViewModel viewModel =
                    ViewModelProviders.of(this, factory).get(RecipeStepsAndIngredientsViewModel.class);
            /*if (recipeStepId == 0) {*/


            viewModel.getmRecipeIngre().removeObservers(this);

            viewModel.getmRecipeIngre().observe(this, new Observer<List<RecipeIngredientsEntry>>() {
                @Override
                public void onChanged(@Nullable List<RecipeIngredientsEntry> recipeIngredientsEntries) {
                    //Populate Ingredients

                    popultarRecipeIngreUI(recipeIngredientsEntries);
                }
            });

            viewModel.getmRecipeSteps().removeObservers(this);

            viewModel.getmRecipeSteps().observe(this, new Observer<List<RecipeStepsEntry>>() {
                @Override
                public void onChanged(@Nullable List<RecipeStepsEntry> recipeStepsEntries) {
                    //Populate Recipe Steps
                    populateRecipeStepsUI(recipeStepsEntries);
                }
            });
            /*} else {


                if (viewModel.getSelectedRecipeSteps().getValue() != null) {
                    RecipeStepsEntry recipeStepsEntry = viewModel.getSelectedRecipeSteps().getValue();
                    mRecipeStepClickListener.onRecipeStepSelected(recipeStepsEntry.getId(), recipeStepsEntry.getVideoURL(), recipeStepsEntry.getDescription(), recipeStepsEntry.getShortDescription(), recipeStepsEntry.getRecipeId(), getNoOfSteps());
                }

            }*/


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void populateRecipeStepsUI(List<RecipeStepsEntry> recipeStepsEntry) {

        if (recipeStepsEntry == null || recipeStepsEntry.size() == 0) {
            return;
        }
        //set data in views

        recipeStepsAdapter.setmRecipeStepsList(recipeStepsEntry);

    }

    private void popultarRecipeIngreUI(List<RecipeIngredientsEntry> recipeIngredientsEntry) {
        if (recipeIngredientsEntry == null || recipeIngredientsEntry.size() == 0) {
            return;
        }


        recipeIngredientsAdapter.setmRecipeIngreEntriesList(recipeIngredientsEntry);
        //set data in views


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i("Detail RecipeId onSave", "" + recipeId);
        outState.putInt(INSTANCE_RECIPE_ID, recipeId);
        outState.putInt(INSTANCE_RECIPE_STEP_ID, getRecipeStepId());
        outState.putString(INSTANCE_RECIPE_NAME, recipeName);
        outState.putInt(INSTANCE_RECIPE_STEP_POSITION, recipeStepPosition);
        outState.putInt(INSTANCE_RV_POS, recipeStepsAdapter.getInitialSelectedStepPos()); // get current recycle view position here.
        super.onSaveInstanceState(outState);
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getRecipeStepId() {
        return recipeStepId;
    }

    public void setRecipeStepId(int recipeStepId) {
        this.recipeStepId = recipeStepId;
    }


    public int getRecipeStepPosition() {
        return recipeStepPosition;
    }

    public void setRecipeStepPosition(int recipeStepPosition) {
        this.recipeStepPosition = recipeStepPosition;
    }

    public int getNoOfSteps() {
        return noOfSteps;
    }

    public void setNoOfSteps(int noOfSteps) {
        this.noOfSteps = noOfSteps;
    }

    @Override
    public void onIngreItemClickListener(int itemId, String itemName) {

        //Nothing to do.
        //maybe google search the item for its info.
        try {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, itemName); // query contains search string
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRecipeStepsItemClickListener(int itemId, String videoLink, String desc, String shortDesc, int mainRecipeId, int position, int noOfSteps) {


        setRecipeId(mainRecipeId);
        setRecipeStepId(itemId);
        setRecipeStepPosition(position);
        setNoOfSteps(noOfSteps);
        if (mTwoPane) {
            setRecipeStepFragment(itemId, videoLink, desc, shortDesc, mainRecipeId, position);
        } else {
            mRecipeStepClickListener.onRecipeStepSelected(itemId, videoLink, desc, shortDesc, mainRecipeId, noOfSteps, recipeStepsAdapter.getmRecipeStepsList(), position);
        }


    }


   /* @Override
    public void onFabRecipeStepClicked(int stepId, int recipeId) {

        getSelectedRecipe(recipeId, stepId);
    }*/


    private void setRecipeStepFragment(int recipeStepId, String videoLink, String desc, String shortDesc, int mainRecipeId, int position) {

        RecipeStepDetailFragment newFragment = new RecipeStepDetailFragment();
        Bundle args = new Bundle();
        args.putInt(RecipeStepDetailFragment.RECIPE_STEP_ID, recipeStepId);
        args.putString(RecipeStepDetailFragment.RECIPE_STEP_VIDEO_LINK, videoLink);
        args.putString(RecipeStepDetailFragment.RECIPE_STEP_DESC, desc);
        args.putString(RecipeStepDetailFragment.RECIPE_STEP_SHORT_DESC, shortDesc);
        args.putInt(RecipeStepDetailFragment.RECIPE_STEP_POSITION, position);
        args.putInt(RecipeStepDetailFragment.RECIPE_ID, mainRecipeId);
        newFragment.setArguments(args);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.recipe_step_detail_frame_tab, newFragment).commit();
        }

        /*getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_content, newFragment).commit();*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_favourite_recipes, menu);
        if (SavePreferences.fetchString(Objects.requireNonNull(getActivity()), getResources().getString(R.string.star_recipe_pref), ConstantUtilities.DEF_RECIPE_STAR).equals(String.valueOf(recipeId))) {
            menu.getItem(0).setIcon(ResourcesCompat.getDrawable(getResources(), android.R.drawable.star_big_on, null));
        } else {
            menu.getItem(0).setIcon(ResourcesCompat.getDrawable(getResources(), android.R.drawable.star_big_off, null));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_star:
                Bitmap bitmap = ((BitmapDrawable) item.getIcon()).getBitmap();
                Bitmap bitmapStarOff = ((BitmapDrawable) getResources().getDrawable(android.R.drawable.star_big_off, null)).getBitmap();
                Bitmap bitmapStarOn = ((BitmapDrawable) getResources().getDrawable(android.R.drawable.star_big_on, null)).getBitmap();

                if (bitmap == bitmapStarOff) {
                    item.setIcon(ResourcesCompat.getDrawable(getResources(), android.R.drawable.star_big_on, null));
                    SavePreferences.saveString(Objects.requireNonNull(getActivity()), getResources().getString(R.string.star_recipe_pref), String.valueOf(recipeId));
                    BakrStarRecipeWidgetProvider.sendRefreshBroadcast(getActivity());
                } else if (bitmap == bitmapStarOn) {
                    item.setIcon(ResourcesCompat.getDrawable(getResources(), android.R.drawable.star_big_off, null));
                    SavePreferences.saveString(Objects.requireNonNull(getActivity()), getResources().getString(R.string.star_recipe_pref), ConstantUtilities.DEF_RECIPE_STAR);
                    BakrStarRecipeWidgetProvider.sendRefreshBroadcast(getActivity());
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
