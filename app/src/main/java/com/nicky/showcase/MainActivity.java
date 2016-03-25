package com.nicky.showcase;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicholas.myapplication.backend.postApi.model.Post;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NetworkUtil.OnNetworkUtilInteraction{

    private final int PERMISSION_RESULT = 2;
    private final String[] PERMISSIONS = {Manifest.permission.GET_ACCOUNTS};

    FloatingActionButton refreshFab;
    RecyclerView feedRecyclerView;
    LinearLayoutManager layoutManager;
    FeedAdapter feedAdapter;
    CoordinatorLayout mainFrame;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String userEmail;
    AuthorizationCheckTask authTask;
    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;
    GoogleAccountCredential credential;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    NetworkUtil networkUtility;
    TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initInstances();

        prefs = getSharedPreferences("USER_PREFS", 0);
        edit = prefs.edit();

        userEmail = prefs.getString("userEmail", "");
        System.out.println(userEmail);

        checkAccountPermission();
    }

    public void initInstances() {
        refreshFab = (FloatingActionButton) findViewById(R.id.refreshFab);
        feedRecyclerView = (RecyclerView) findViewById(R.id.feedRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        mainFrame = (CoordinatorLayout) findViewById(R.id.mainFrame);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);

        signIn = (TextView) findViewById(R.id.sign_in);

        setUpLayout();
    }

    public void setUpLayout() {
        setSupportActionBar(toolbar);
        refreshFab.setOnClickListener(this);
        collapsingToolbarLayout.setTitle("Showcase");
        drawerLayout.setDrawerListener(drawerToggle);
        signIn.setOnClickListener(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        feedRecyclerView.setLayoutManager(layoutManager);
    }

    private void checkAccountPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_RESULT);
        } else {
            initLogin();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_RESULT:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLogin();
                }
        }
    }

    public void initLogin() {
        if (Strings.isNullOrEmpty(userEmail)) {
            onClickSignIn();
        }
        else {
            credential = GoogleAccountCredential.usingAudience(this, AppConstants.AUDIENCE);
            credential.setSelectedAccountName(userEmail);
            networkUtility = new NetworkUtil(credential, this);
            initFeed();
        }
    }

    public void initFeed() {
        networkUtility.getPostsList();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.refreshFab:
                Intent i = new Intent();
                i.setClass(MainActivity.this, AddPostActivity.class);
                startActivity(i);
                break;
            case R.id.sign_in:
                initLogin();
                break;
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void blobAndStoreGettingResponse(Post result) {
        //NEVER USED HERE
    }

    @Override
    public void deletePostGettingResponse(Boolean result) {
        //NEVER USED HERE
    }

    @Override
    public void fetchingListGettingResponse(ArrayList<Post> result) {
        if (result != null) {
            feedAdapter = new FeedAdapter(result, this);
            feedRecyclerView.setAdapter(feedAdapter);
        } else {
            Snackbar.make(drawerLayout, "No Posts", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void onClickSignIn() {
        int googleAccounts = AppConstants.countGoogleAccounts(this);
        if (googleAccounts == 0) {
            Toast.makeText(MainActivity.this, "No Account On Device", Toast.LENGTH_SHORT).show();
        } else if (googleAccounts == 1) {
            AccountManager am = AccountManager.get(this);
            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            if (accounts != null && accounts.length >0) {
                userEmail = accounts[0].name;
                performAuthCheck(accounts[0].name);
            }
            //Toast.makeText(MainActivity.this, "One Account, Using it", Toast.LENGTH_SHORT).show();
        } else {
            Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                    "Select the account to use with Showcase", null, null, null);
            startActivityForResult(accountSelector, ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        userEmail = accountName;
        performAuthCheck(accountName);
    }

    public void performAuthCheck(String userEmail) {
        if (authTask != null) {
            try {
                authTask.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new AuthorizationCheckTask().execute(userEmail);
    }

    class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... emails) {
            if (!AppConstants.checkGooglePlayServicesAvailable(MainActivity.this)) {
                return false;
            }
            String emailAccount = emails[0];
            authTask = this;

            if (Strings.isNullOrEmpty(emailAccount)) {
                //Toast.makeText(MainActivity.this, "Null Email", Toast.LENGTH_SHORT).show();
                return false;
            }

            try {
                GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                        MainActivity.this, AppConstants.AUDIENCE);
                credential.setSelectedAccountName(emailAccount);
                String accessToken = credential.getToken();
                userEmail = emailAccount;
                return true;
            } catch (GoogleAuthException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Snackbar.make(drawerLayout, "Logged in as " + userEmail, Snackbar.LENGTH_LONG).show();
                commitEmailToPrefs(userEmail);
                initLogin();
            } else {
                Snackbar.make(drawerLayout, "Account Error", Snackbar.LENGTH_LONG).show();
            }

            authTask = null;
        }

    }

    private void commitEmailToPrefs(String userEmail) {
        edit.putString("userEmail", userEmail);
        edit.commit();
    }
}
