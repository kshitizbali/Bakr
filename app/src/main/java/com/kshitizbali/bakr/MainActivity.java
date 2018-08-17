package com.kshitizbali.bakr;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kshitizbali.bakr.adapter.MasterRecipeListAdapter;
import com.kshitizbali.bakr.database.AppDatabase;
import com.kshitizbali.bakr.database.RecipeStepsEntry;
import com.kshitizbali.bakr.utilities.ConstantUtilities;
import com.kshitizbali.bakr.utilities.NetworkUtilities;
import com.kshitizbali.bakr.widget.BakrFavouriteService;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MasterRecipeListFragment.OnRecipeClickListener, RecipeDetailFragment.OnRecipeStepClickListener, RecipeStepDetailFragment.onFabRecipeStepChangeListener, MasterRecipeListFragment.onFavSelectedClickListener {

    FrameLayout fragment_content;
    private TextView tvError;
    private ProgressBar progressBar;
    private AppDatabase database;
    private List<RecipeStepsEntry> mRecipeStepsList;

    boolean widgetFavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NetworkUtilities.initializeStetho(getApplicationContext());
        instantiateViews();
        if (NetworkUtilities.isInternetAvailable(MainActivity.this)) {
            onStartFragmentLoad();
        } else {
            fragment_content.setVisibility(View.GONE);
            tvError.setVisibility(View.VISIBLE);
        }



        /*if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        try {
            //List<RecipeStepsEntry> recipeStepsEntryList = (List<RecipeStepsEntry>) getLastCustomNonConfigurationInstance();
            setmRecipeStepsList((List<RecipeStepsEntry>) getLastCustomNonConfigurationInstance());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRecipeSelected(final int recipeId, String recipeName) {
        //We will get the position of the selected recipe and will show its detail in the second fragment.
        // Create fragment and give it an argument for the selected article
        if (recipeId > 0) {
            RecipeDetailFragment newFragment = new RecipeDetailFragment();
            Bundle args = new Bundle();
            args.putInt(RecipeDetailFragment.INSTANCE_RECIPE_ID, recipeId);
            args.putString(RecipeDetailFragment.INSTANCE_RECIPE_NAME, recipeName);
            newFragment.setArguments(args);
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            getSupportFragmentManager().beginTransaction().addToBackStack(ConstantUtilities.RECIPE_DETAIL).replace(R.id.fragment_content, newFragment, ConstantUtilities.RECIPE_DETAIL).commit();
        }

    }

    private void instantiateViews() {
        fragment_content = (FrameLayout) findViewById(R.id.fragment_content);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvError = (TextView) findViewById(R.id.tvError);
        database = AppDatabase.getInstance(getApplication());

        if (getIntent().getExtras() != null) {
            widgetFavView = getIntent().getExtras().getBoolean(ConstantUtilities.SHOW_FAV);
        }

    }


    private void onStartFragmentLoad() {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (database.recipeDao().getNumberOfRows() == 0) {
                    new FetchRecipesAsyncTask().execute();
                    /*Log.i("NO of Fav", "" + database.recipeDao().getFavRecipeCount());*/
                }


            }
        });
        if ((getSupportFragmentManager().getBackStackEntryCount() == 0)) {

            MasterRecipeListFragment newFragment = new MasterRecipeListFragment();
           /* Bundle args = new Bundle();
            args.putBoolean(ConstantUtilities.SHOW_WIDGET_DATA, widgetFavView);
            newFragment.setArguments(args);*/
            // Replace the old head fragment with a new one
            getSupportFragmentManager().beginTransaction().addToBackStack(ConstantUtilities.MASTER_FRAGMENT)
                    .replace(R.id.fragment_content, newFragment, ConstantUtilities.MASTER_FRAGMENT)
                    .commit();


        }


    }

    @Override
    public void onRecipeStepSelected(int recipeId, String videoLink, String desc, String shortDesc, int mainRecipeId, int noOfSteps, List<RecipeStepsEntry> recipeStepsEntryList, int position) {
        setmRecipeStepsList(recipeStepsEntryList);
        setRecipeStepFragment(recipeId, videoLink, desc, shortDesc, mainRecipeId, noOfSteps, true, position);

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return getmRecipeStepsList();
    }

    @Override
    public void onFabRecipeStepClicked(int nextOrPrev) {

        try {

            if (getmRecipeStepsList().size() > nextOrPrev) {
                setRecipeStepFragment(getmRecipeStepsList().get(nextOrPrev).getId(), getmRecipeStepsList().get(nextOrPrev).getVideoURL(),
                        getmRecipeStepsList().get(nextOrPrev).getDescription(), getmRecipeStepsList().get(nextOrPrev).getShortDescription(),
                        getmRecipeStepsList().get(nextOrPrev).getRecipeId(), getmRecipeStepsList().size(), false, nextOrPrev);
            }



            /*Log.i("RecipeStepId before ID", "" + stepId);
            RecipeStepsAndIngredientsViewModelFactory factory = new RecipeStepsAndIngredientsViewModelFactory(database, recipeId, stepId);
            final RecipeStepsAndIngredientsViewModel viewModel = ViewModelProviders.of(MainActivity.this, factory).get(RecipeStepsAndIngredientsViewModel.class);
            viewModel.getSelectedRecipeSteps().removeObservers(MainActivity.this);
            viewModel.getSelectedRecipeSteps().observe(MainActivity.this, new Observer<RecipeStepsEntry>() {
                @Override
                public void onChanged(@Nullable RecipeStepsEntry recipeStepsEntry) {

                    if (recipeStepsEntry == null) {
                        return;
                    }
                    Log.i("RecipeStepId ID", "" + stepId);
                    setRecipeStepFragment(stepId, recipeStepsEntry.getVideoURL(),
                            recipeStepsEntry.getDescription(), recipeStepsEntry.getShortDescription()
                            , recipeStepsEntry.getRecipeId(), noOfSteps, false);
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<RecipeStepsEntry> getmRecipeStepsList() {
        return mRecipeStepsList;
    }

    public void setmRecipeStepsList(List<RecipeStepsEntry> mRecipeStepsList) {
        this.mRecipeStepsList = mRecipeStepsList;
    }

    @Override
    public void onRecipeFavOrNot(final int recipeId, String recipeFavOrNot) {
        final String recipeFavStatus;
        if (recipeFavOrNot.equals(ConstantUtilities.FAV_NO)) {
            recipeFavStatus = ConstantUtilities.FAV_YES;
        } else {
            recipeFavStatus = ConstantUtilities.FAV_NO;
        }
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.recipeDao().updateFavRecipe(recipeFavStatus, recipeId);

                BakrFavouriteService.startActionShowFav(MainActivity.this);
            }
        });
    }

  /*  @Override
    public void onFabRecipeStepClicked(int stepId, final int recipeId) {

        try {
            RecipeStepsAndIngredientsViewModelFactory factory = new RecipeStepsAndIngredientsViewModelFactory(database, recipeId, stepId);
            final RecipeStepsAndIngredientsViewModel viewModel = ViewModelProviders.of(MainActivity.this, factory).get(RecipeStepsAndIngredientsViewModel.class);
            viewModel.getSelectedRecipeSteps().observe(MainActivity.this, new Observer<RecipeStepsEntry>() {
                @Override
                public void onChanged(@Nullable RecipeStepsEntry recipeStepsEntry) {

                    if (recipeStepsEntry == null) {
                        return;
                    }
                    setRecipeStepFragment(recipeStepsEntry.getId(), recipeStepsEntry.getVideoURL(),
                            recipeStepsEntry.getDescription(), recipeStepsEntry.getShortDescription()
                            , recipeStepsEntry.getRecipeId());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/


    public class FetchRecipesAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            //if (NetworkUtilities.isOnlineB()) {
            URL recipeRequest = NetworkUtilities.buildLaunchUrl();


            try {
                return NetworkUtilities.getResponseFromHttpUrl(recipeRequest);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            //}
            /*else {
                setVisibilityOnError();
            }
            return null;*/
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setVisibilityOnSuccess();
            if (s != null && !s.isEmpty()) {

                NetworkUtilities.saveRecipeEntryIngreSteps(s, getApplicationContext());
            }
        }
    }

    private void setVisibilityOnError() {
        progressBar.setVisibility(View.INVISIBLE);
        fragment_content.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.VISIBLE);
    }

    private void setVisibilityOnSuccess() {
        progressBar.setVisibility(View.INVISIBLE);
        fragment_content.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.INVISIBLE);
    }


    private void setRecipeStepFragment(int recipeId, String videoLink, String desc, String shortDesc, int mainRecipeId, int noOfSteps, boolean addbackStack, int position) {

        if (addbackStack) {
            RecipeStepDetailFragment newFragment = new RecipeStepDetailFragment();
            Bundle args = new Bundle();
            args.putInt(RecipeStepDetailFragment.RECIPE_STEP_ID, recipeId);
            args.putString(RecipeStepDetailFragment.RECIPE_STEP_VIDEO_LINK, videoLink);
            args.putString(RecipeStepDetailFragment.RECIPE_STEP_DESC, desc);
            args.putString(RecipeStepDetailFragment.RECIPE_STEP_SHORT_DESC, shortDesc);
            args.putInt(RecipeStepDetailFragment.RECIPE_ID, mainRecipeId);
            args.putInt(RecipeStepDetailFragment.RECIPE_STEPS_COUNT, noOfSteps);
            args.putInt(RecipeStepDetailFragment.RECIPE_STEP_POSITION, position);
            newFragment.setArguments(args);
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            //replaceFragment(newFragment);
            getSupportFragmentManager().beginTransaction().addToBackStack(ConstantUtilities.RECIPE_STEP_DETAIL).replace(R.id.fragment_content, newFragment, ConstantUtilities.RECIPE_STEP_DETAIL).commit();
        } else {
            RecipeStepDetailFragment newFragment = new RecipeStepDetailFragment();
            Bundle args = new Bundle();
            args.putInt(RecipeStepDetailFragment.RECIPE_STEP_ID, recipeId);
            args.putString(RecipeStepDetailFragment.RECIPE_STEP_VIDEO_LINK, videoLink);
            args.putString(RecipeStepDetailFragment.RECIPE_STEP_DESC, desc);
            args.putString(RecipeStepDetailFragment.RECIPE_STEP_SHORT_DESC, shortDesc);
            args.putInt(RecipeStepDetailFragment.RECIPE_ID, mainRecipeId);
            args.putInt(RecipeStepDetailFragment.RECIPE_STEPS_COUNT, noOfSteps);
            args.putInt(RecipeStepDetailFragment.RECIPE_STEP_POSITION, position);
            newFragment.setArguments(args);
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            /*removeOldFragment("Recipe_Step_Detail");*/
            //replaceFragment(newFragment);
            //onBackPressFragmentsMethod();
            getSupportFragmentManager().beginTransaction()./*addToBackStack("Recipe_Step_Detail").*/replace(R.id.fragment_content, newFragment, ConstantUtilities.RECIPE_STEP_DETAIL).commit();
        }


    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        onBackPressFragmentsMethod();
        /*onBackPressFragmentsMethod();*/

        /*int fragments = getSupportFragmentManager().getBackStackEntryCount();
        Log.i("BackStackCount Back", "" + fragments);*/

       /* if (!getSupportFragmentManager().getBackStackEntryAt(0).getName().equalsIgnoreCase("Recipe_Detail")) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("Recipe_Detail");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
        }*/

      /*  if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack("Recipe_Detail", android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }*/

        //Log.i("Fragment", "" + getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()));

     /*   android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag("Recipe_Step_Detail");
        if (fragment != null && fragment instanceof RecipeStepDetailFragment) {
            Log.i("Frag Present", "true");
            *//*getSupportFragmentManager().beginTransaction().remove(fragment).commit();*//*

        } else {
            Log.i("Frag Present", "false");
            super.onBackPressed();
        }*/
    }


    public void removeOldFragment(String tag) {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_content);


        if (currentFrag != null && currentFrag.getClass().equals(fragment.getClass())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).addToBackStack(null).commit();
        }

        if (fragment != null && fragment instanceof RecipeStepDetailFragment) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }
    }

    private void onBackPressFragmentsMethod() {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(ConstantUtilities.RECIPE_DETAIL);
        android.support.v4.app.Fragment fragmentMaster = getSupportFragmentManager().findFragmentByTag(ConstantUtilities.MASTER_FRAGMENT);
        /*android.support.v4.app.Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_content);*/

        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        if (tag != null && tag.equals(ConstantUtilities.RECIPE_STEP_DETAIL)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment, ConstantUtilities.RECIPE_DETAIL).addToBackStack(ConstantUtilities.RECIPE_DETAIL).commit();
        } else if (tag != null && tag.equals(ConstantUtilities.RECIPE_DETAIL)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragmentMaster, ConstantUtilities.MASTER_FRAGMENT).addToBackStack(ConstantUtilities.MASTER_FRAGMENT).commit();
        } else {
            finish();
            /*getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            super.onBackPressed();*/
        }




       /* if (currentFrag != null && currentFrag instanceof RecipeStepDetailFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
        } else {
            super.onBackPressed();
        }*/

    }

    /*public void replaceFragment(Fragment frag) {


        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            FragmentTransaction t = manager.beginTransaction();
            Fragment currentFrag = manager.findFragmentById(R.id.fragment_content);

            //Check if the new Fragment is the same
            //If it is, don't add to the back stack
            if (currentFrag != null && currentFrag.getClass().equals(frag.getClass())) {
                t.replace(R.id.fragment_content, frag).commit();
            } else {
                t.replace(R.id.fragment_content, frag).addToBackStack(ConstantUtilities.RECIPE_STEP_DETAIL).commit();
            }
        }
    }*/
}
