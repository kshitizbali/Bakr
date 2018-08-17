package com.kshitizbali.bakr;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kshitizbali.bakr.adapter.MasterRecipeListAdapter;
import com.kshitizbali.bakr.database.RecipeEntry;
import com.kshitizbali.bakr.utilities.ConstantUtilities;
import com.kshitizbali.bakr.viewmodel.MainRecipeListViewModel;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MasterRecipeListFragment extends android.support.v4.app.Fragment implements MasterRecipeListAdapter.ItemClickListener, MasterRecipeListAdapter.FavRecipeClickListener {

    private TextView tvError;
    private RecyclerView rvRecipes;
    private ProgressBar progressBar;
    private MasterRecipeListAdapter masterRecipeListAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();
    /*private AppDatabase appDatabase;*/
    OnRecipeClickListener mCallBackRecipeClick;
    onFavSelectedClickListener onFavSelectedClickListener;
    private boolean isFavShowAllowed;

    @Override
    public void onItemClickListener(int itemId, String recipeName) {
        mCallBackRecipeClick.onRecipeSelected(itemId, recipeName);

    }

    @Override
    public void isFavRecipe(int recipeId, String favOrNot) {
        onFavSelectedClickListener.onRecipeFavOrNot(recipeId, favOrNot);
    }

    public interface OnRecipeClickListener {
        void onRecipeSelected(int recipeId, String recipeName);
    }

    public interface onFavSelectedClickListener {
        void onRecipeFavOrNot(int recipeId, String recipeFavOrNot);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_master_recipe_list, container, false);
        setRetainInstance(true);


        if (this.getArguments() != null) {

            isFavShowAllowed = this.getArguments().getBoolean(ConstantUtilities.SHOW_WIDGET_DATA);

        }

        instantiateViews(rootView);
        //setupViewModel();

        return rootView;
    }

    public MasterRecipeListFragment() {
        //Required Empty Constructor
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallBackRecipeClick = (OnRecipeClickListener) context;
            onFavSelectedClickListener = (onFavSelectedClickListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeClickListener");
        }
    }


    private void instantiateViews(View view) {
        tvError = (TextView) view.findViewById(R.id.tvError);
        rvRecipes = (RecyclerView) view.findViewById(R.id.rv_Recipes);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        if (view.findViewById(R.id.frameLayout_tab) != null) {
            rvRecipes.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            rvRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));
        }


        masterRecipeListAdapter = new MasterRecipeListAdapter(getActivity(), this, this);
        rvRecipes.setAdapter(masterRecipeListAdapter);
        /*rvRecipes.setHasFixedSize(true);*/
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), VERTICAL);
        rvRecipes.addItemDecoration(decoration);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
            /*((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //setupViewModel();
        if (isFavShowAllowed) {
            setUpFavViewModel();
        } else {
            setupViewModel();
        }

    }

    /*public class FetchRecipesAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (NetworkUtilities.isOnlineB()) {
                URL recipeRequest = NetworkUtilities.buildLaunchUrl();


                try {
                    return NetworkUtilities.getResponseFromHttpUrl(recipeRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                rvRecipes.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);
            if (s != null && !s.isEmpty()) {

                NetworkUtilities.saveRecipeEntryIngreSteps(s, getActivity().getApplicationContext());
            }
        }
    }*/
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_favourite_recipes, menu);


    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_fav:
                Bitmap bitmap = ((BitmapDrawable) item.getIcon()).getBitmap();
                Bitmap bitmap1 = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_fav, null)).getBitmap();
                Bitmap bitmap2 = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_no_fav, null)).getBitmap();
                *//*fav.getIcon().equals(R.drawable.ic_action_fav);*//*

                if (masterRecipeListAdapter.getItemCount() != 0) {
                    masterRecipeListAdapter.emptyList();
                }
                if (bitmap == bitmap1) {
                    item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_fav, null));
                    setUpFavViewModel();
                } else if (bitmap == bitmap2) {
                    item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_no_fav    , null));
                    setupViewModel();
                }


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }*/

    private void setupViewModel() {
        MainRecipeListViewModel viewModel = ViewModelProviders.of(this).get(MainRecipeListViewModel.class);
        viewModel.getRecipes().removeObservers(this);
        viewModel.getRecipes().observe(this, new Observer<List<RecipeEntry>>() {
            @Override
            public void onChanged(@Nullable List<RecipeEntry> recipeEntries) {
                //Log.d(TAG, "Updating list of tasks from LiveData in ViewModel " + recipeEntries.get(0).getName());
                masterRecipeListAdapter.setRecipeList(recipeEntries);

            }
        });
    }

    private void setUpFavViewModel() {
        MainRecipeListViewModel viewModel = ViewModelProviders.of(this).get(MainRecipeListViewModel.class);
        viewModel.getRecipes().removeObservers(this);
        viewModel.getFavRecipes().observe(this, new Observer<List<RecipeEntry>>() {
            @Override
            public void onChanged(@Nullable List<RecipeEntry> recipeEntries) {
                masterRecipeListAdapter.setRecipeList(recipeEntries);
            }
        });
    }

    /*private void setUpFavViewModel() {
        MainRecipeListViewModel viewModel = ViewModelProviders.of(this).get(MainRecipeListViewModel.class);
        if (viewModel.getRecipes().hasActiveObservers()) {
            viewModel.getRecipes().removeObservers(this);
        }
        viewModel.getFavRecipes().removeObservers(this);
        viewModel.getFavRecipes().observe(this, new Observer<List<RecipeEntry>>() {
            @Override
            public void onChanged(@Nullable List<RecipeEntry> recipeEntries) {
                if (recipeEntries != null && recipeEntries.size() == 0) {
                    rvRecipes.setVisibility(View.GONE);
                    tvError.setText(R.string.no_fav_yum_yums);
                    tvError.setVisibility(View.VISIBLE);
                } else {
                    masterRecipeListAdapter.setRecipeList(recipeEntries);
                }

            }
        });
    }*/
}
