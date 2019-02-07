package com.example.markp.whateverhost;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.example.markp.whateverhost.adapters.SectionsStatePagerAdapter;
import com.example.markp.whateverhost.files.DropboxFile;
import com.example.markp.whateverhost.fragments.DeviceListFragment;
import com.example.markp.whateverhost.fragments.DropboxListFragment;
import com.example.markp.whateverhost.fragments.GoogleDriveListFragment;
import com.example.markp.whateverhost.tasks.DropboxConnectTask;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity mainActivity;

    //region Check Connection Variables

    boolean isSignedInDropbox = false;

    boolean isSignedInGoogleDrive = false;


    //endregion

    //region Permission Variables


    private static final int MY_PERMISSIONS_REQUEST_CODE = 1234;


    //region DROPBOX

    private static final String DROPBOX_APP_KEY = "c2lmx0loqgnggdb";

    private static String DROPBOX_ACCESS_TOKEN = null;

    public DbxRequestConfig config;
    public DbxClientV2 client;
    public FullAccount account;

    public ArrayList<DropboxFile> dropboxFiles;

    //endregion


    //region GOOGLE DRIVE

    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInAccount googleAccount;

    private static final int RC_SIGN_IN = 9001;

    //endregion

    //endregion

    //region Fragment Variables


    public ViewPager myDeviceViewPager;
    public ViewPager dropboxViewPager;
    public ViewPager googleDriveViewPager;
    ConstraintLayout homepage;

    public SectionsStatePagerAdapter deviceListPagerAdapter, dropboxPagerAdapter, googleDrivePagerAdapter;

    public static DeviceListFragment deviceListFragment;

    public static DropboxListFragment dropboxListFragment;

    public static GoogleDriveListFragment googleDriveListFragment;

    //endregion

    //region Application Start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button dropboxSignInButton = findViewById(R.id.dropboxSignInButton);

        dropboxSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSignedInDropbox)
                {
                    setDropboxListView();
                }
                else
                {
                    Auth.startOAuth2Authentication(MainActivity.this, DROPBOX_APP_KEY);
                }

            }
        });

        Button googleDriveSignInButton = findViewById(R.id.googleDriveSignInButton);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(getString(R.string.server_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        googleDriveSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSignedInGoogleDrive)
                {
                    setGoogleDriveView();
                }
                else
                {
                    signInGoogleDrive();
                    //start authentication process
                }
            }
        });

        checkSignedInAccounts();

        getPermissions();

    }

    private void checkSignedInAccounts()
    {
        //RETRIEVE ACCOUNTS

        SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);

        DROPBOX_ACCESS_TOKEN = sp.getString("dropboxToken",null);

        if (DROPBOX_ACCESS_TOKEN!=null)
        {
            try
            {
                if (new DropboxConnectTask().execute(this).get())
                {
                    //SIGNED IN

                    Button dropboxSignInButton = findViewById(R.id.dropboxSignInButton);

                    dropboxSignInButton.setText(account.getEmail());

                    isSignedInDropbox = true;
                }
            }
            catch (Exception e)
            {

            }

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            //UPDATE INFO

        }


        //GOOGLE DRIVE

        if (true)
        {
            //Already signed in
        }
    }


    private void getPermissions()
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED)
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this, Manifest.permission.INTERNET)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this, Manifest.permission.INTERNET)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)

            )
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Read/Write External Storage and Internet Access Permissions needed.");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[] {
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.INTERNET,
                                        Manifest.permission.ACCESS_NETWORK_STATE
                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });

                builder.setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else
            {
                //PERMISSIONS ALREADY GRANTED

                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[] {
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );

                startApplication();
            }

        }
        else
        {
            //PERMISSIONS ALREADY GRANTED
            startApplication();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_CODE:
            {
                if ((grantResults.length>0) && (grantResults[0]
                        + grantResults[1]
                        + grantResults[2]
                        + grantResults[3]
                        == PackageManager.PERMISSION_GRANTED))
                {
                    Toast.makeText(getApplicationContext(),"Permissions granted.",Toast.LENGTH_SHORT).show();
                    startApplication();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Permissions denied.",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    private void startApplication()
    {
        mainActivity=this;
    }


    //endregion

    //region Access file hosting services


    //region Dropbox

    public boolean accessDropbox()
    {
        if (DROPBOX_ACCESS_TOKEN!=null)
        {
            config = DbxRequestConfig.newBuilder("dropbox/Whatever-Host").build();

            client = new DbxClientV2(config, DROPBOX_ACCESS_TOKEN);

            try
            {
                account = client.users().getCurrentAccount();

                return true;
            }catch (DbxException e)
            {
                Log.d("1st","login exception");
                Log.d("Error",e.getRequestId());
                return false;
            }
            catch (NetworkOnMainThreadException e)
            {
                Log.d("1st - 2","login exception");
                Log.d("Error",e.getMessage());
                return false;
            }
        }
        else
        {
            return false;
        }



    }

    public ArrayList<DropboxFile> getDropboxList(String path)
    {
            try
            {
                ListFolderResult result = client.files().listFolder(path);
                dropboxFiles = new ArrayList<>();

                for (Metadata metadata : result.getEntries())
                {

                    dropboxFiles.add(new DropboxFile(metadata.getName(),""));

                }

                return dropboxFiles;


            }
            catch (DbxException e)
            {
                Log.d("2nd","retrieve files exception");
                Log.d("Error 2",e.getRequestId());
                return null;
            }
    }

    //endregion

    //region GoogleDrive

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            googleAccount = completedTask.getResult(ApiException.class);

            Button googleDriveButton = findViewById(R.id.googleDriveSignInButton);
            googleDriveButton.setText(googleAccount.getDisplayName());
            //successfully signed in
            isSignedInGoogleDrive=true;
            Toast.makeText(getApplicationContext(),"Signed into GoogleDrive.",Toast.LENGTH_SHORT).show();
        }
        catch (ApiException e)
        {
            Log.d("GoogleDrive","signInResult:failed code=" + e.getStatusCode());

            //not signed in
        }
    }

    private void signInGoogleDrive()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);

    }

    private void signOutGoogleDrive()
    {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //sign out

                //Update UI
                Toast.makeText(getApplicationContext(),"Signed out of GoogleDrive.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean accessGoogleDrive()
    {
        return false;
    }

    //endregion

    //endregion

    //region Containers

    private void hideAllContainers()
    {
        homepage = findViewById(R.id.welcomeLayout);
        homepage.setVisibility(View.GONE);

        myDeviceViewPager = findViewById(R.id.myDeviceViewPager);
        myDeviceViewPager.setVisibility(View.GONE);

        dropboxViewPager = findViewById(R.id.dropboxViewpager);
        dropboxViewPager.setVisibility(View.GONE);

        googleDriveViewPager = findViewById(R.id.googleDriveViewPager);
        googleDriveViewPager.setVisibility(View.GONE);

    }

    private void setDeviceListView()
    {
        hideAllContainers();

        myDeviceViewPager = findViewById(R.id.myDeviceViewPager);

        myDeviceViewPager.setVisibility(View.VISIBLE);

        deviceListPagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        deviceListFragment = new DeviceListFragment();

        deviceListPagerAdapter.addFragment(deviceListFragment,"DeviceList");

        myDeviceViewPager.setAdapter(deviceListPagerAdapter);

    }

    public void setDropboxListView()
    {

        dropboxListFragment = new DropboxListFragment();

        hideAllContainers();

        dropboxViewPager = findViewById(R.id.dropboxViewpager);

        dropboxViewPager.setVisibility(View.VISIBLE);

        dropboxPagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        dropboxPagerAdapter.addFragment(dropboxListFragment,"DropboxFragment");

        dropboxViewPager.setAdapter(dropboxPagerAdapter);

    }

    public void setGoogleDriveView()
    {
        googleDriveListFragment = new GoogleDriveListFragment();

        hideAllContainers();

        googleDriveViewPager = findViewById(R.id.googleDriveViewPager);

        googleDriveViewPager.setVisibility(View.VISIBLE);

        googleDrivePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        googleDrivePagerAdapter.addFragment(googleDriveListFragment,"GoogleDriveFragment");

        googleDriveViewPager.setAdapter(googleDrivePagerAdapter);
    }

    private void setHomepage()
    {
        hideAllContainers();

        homepage=findViewById(R.id.welcomeLayout);

        homepage.setVisibility(View.VISIBLE);
    }

    //endregion

    //region Button clicks and UI


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (myDeviceViewPager.getVisibility()==View.VISIBLE)
        {
            deviceListFragment.setList(deviceListFragment.currentFolder.getParentFile());
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_google_drive) {
            setGoogleDriveView();
        } else if (id == R.id.nav_dropbox)
        {
            setDropboxListView();

        } else if (id == R.id.nav_device)
        {
            //Show list of device files
            setDeviceListView();


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.homePage)
        {
            setHomepage();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Auth.getOAuth2Token()!=null)
        {
            Toast.makeText(getApplicationContext(),"Signed into Dropbox.",Toast.LENGTH_SHORT).show();

            DROPBOX_ACCESS_TOKEN = Auth.getOAuth2Token();

            SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sp.edit();

            editor.putString("dropboxToken",DROPBOX_ACCESS_TOKEN);

            editor.commit();

            try
            {
                boolean access = new DropboxConnectTask().execute(this).get();

                isSignedInDropbox = true;

                if (isSignedInDropbox)
                {
                    Button dropboxSignInButton = findViewById(R.id.dropboxSignInButton);
                    dropboxSignInButton.setText(account.getEmail());
                }
            }
            catch (Exception e)
            {

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("ActivityResult","Inside On Activity Result");
        //Return from launching signInIntent
        if (requestCode == RC_SIGN_IN)
        {
            Log.d("ActivityResult","Running Handle SignIn Result");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    //endregion

}
