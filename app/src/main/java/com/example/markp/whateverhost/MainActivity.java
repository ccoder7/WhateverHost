package com.example.markp.whateverhost;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.AsyncTask;
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
import com.dropbox.core.android.AuthActivity;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxRawClientV2;
import com.dropbox.core.v2.auth.DbxUserAuthRequests;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.example.markp.whateverhost.adapters.SectionsStatePagerAdapter;
import com.example.markp.whateverhost.files.DropboxFile;
import com.example.markp.whateverhost.files.GoogleDriveFile;
import com.example.markp.whateverhost.files.OneDriveFile;
import com.example.markp.whateverhost.fragments.DeviceListFragment;
import com.example.markp.whateverhost.fragments.DropboxListFragment;
import com.example.markp.whateverhost.fragments.GoogleDriveListFragment;
import com.example.markp.whateverhost.fragments.OneDriveListFragment;
import com.example.markp.whateverhost.tasks.DropboxConnectTask;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.Drive;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import com.onedrive.sdk.authentication.MSAAuthenticator;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.DefaultClientConfig;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.Item;
import com.onedrive.sdk.extensions.OneDriveClient;
import com.onedrive.sdk.extensions.Share;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity mainActivity;

    //region Check Connection Variables

    AlertDialog connectionLostAlertDialog;

    boolean isSignedInDropbox = false;

    boolean isSignedInGoogleDrive = false;

    boolean isSignedInOneDrive = false;


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

    public GoogleSignInAccount googleAccount;

    private static final int RC_SIGN_IN = 9001;

    public ArrayList<GoogleDriveFile> googleDriveFiles;

    //endregion

    //region ONEDRIVE

    final MSAAuthenticator msaAuthenticator = new MSAAuthenticator() {
        @Override
        public String getClientId() {
            return getString(R.string.ondrive_client_id);
        }

        @Override
        public String[] getScopes() {
            return new String[] {"onedrive.readwrite", "onedrive.appfolder", "wl.offline_access"};
        }
    };

    public AtomicReference<IOneDriveClient> mOneDriveClient = new AtomicReference<>();

    public ArrayList<OneDriveFile> oneDriveFiles;

    //endregion


    //endregion

    //region Fragment Variables


    public ViewPager myDeviceViewPager;
    public ViewPager dropboxViewPager;
    public ViewPager googleDriveViewPager;
    public ViewPager oneDriveViewPager;
    ConstraintLayout homepage;

    public SectionsStatePagerAdapter deviceListPagerAdapter, dropboxPagerAdapter, googleDrivePagerAdapter, oneDrivePagerAdapter;

    public static DeviceListFragment deviceListFragment;

    public static DropboxListFragment dropboxListFragment;

    public static GoogleDriveListFragment googleDriveListFragment;

    public static OneDriveListFragment oneDriveListFragment;

    //endregion

    //region Application Start

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //region Other

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

        //endregion

        //region Dropbox

        Button dropboxSignInButton = findViewById(R.id.dropboxSignInButton);

        dropboxSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSignedInDropbox)
                {
                    setDropboxListView();
                    navigationView.getMenu().getItem(2).setChecked(true);
                }
                else
                {
                    Auth.startOAuth2Authentication(MainActivity.this, DROPBOX_APP_KEY);
                }

            }
        });

        Button dropboxLogoutButton = findViewById(R.id.logoutDropboxButton);

        dropboxLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                signOutDropbox();
            }
        });

        //endregion

        //region GoogleDrive

        Button googleDriveSignInButton = findViewById(R.id.googleDriveSignInButton);

        googleDriveSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSignedInGoogleDrive)
                {
                    setGoogleDriveView();
                    navigationView.getMenu().getItem(3).setChecked(true);
                }
                else
                {
                    signInGoogleDrive();
                    //start authentication process
                }
            }
        });

        //endregion

        //region OneDrive

        Button oneDriveSignInButton = findViewById(R.id.oneDriveSignInButton);

        oneDriveSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSignedInOneDrive)
                {
                    setOneDriveView();
                    navigationView.getMenu().getItem(4).setChecked(true);
                }
                else
                {
                    signInOneDrive();
                }
            }
        });

        //endregion

        this.registerReceiver(myBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        checkSignedInAccounts();

        getPermissions();




    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
                if (connectionLostAlertDialog!=null)
                {
                    if (connectionLostAlertDialog.isShowing())
                    {
                        connectionLostAlertDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Connection recovered.",Toast.LENGTH_SHORT).show();

                    }
                }

            }else{
                connectionLostAlertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false).setTitle("No Internet connection")
                        .setMessage("'Whatever! - Host' requires internet connection to work. \n \n" +
                                "Please connect to the internet.")
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(0);
                            }
                        }).create();
                connectionLostAlertDialog.show();
            }
        }
    };

    private void checkSignedInAccounts()
    {
        //RETRIEVE ACCOUNTS


        //region Dropbox

        SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);

        DROPBOX_ACCESS_TOKEN = sp.getString("dropboxToken",null);

        if (DROPBOX_ACCESS_TOKEN!=null)
        {
            Log.d("TOKEN",DROPBOX_ACCESS_TOKEN);
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



            //UPDATE INFO

        }
        else
        {
            Button dropboxLogoutButton = findViewById(R.id.logoutDropboxButton);
            dropboxLogoutButton.setVisibility(View.GONE);
        }

        //endregion

        //region Google Drive

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account!=null)
        {
            signInGoogleDrive();

            googleAccount = account;

            Button googleDriveButton = findViewById(R.id.googleDriveSignInButton);
            googleDriveButton.setText(googleAccount.getDisplayName());

            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            this, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(googleAccount.getAccount());
            Drive googleDriveService =
                    new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName("Drive API Migration")
                            .build();

            // The DriveServiceHelper encapsulates all REST API and SAF functionality.
            // Its instantiation is required before handling any onClick actions.
            mDriveServiceHelper = new DriveServiceHelper(googleDriveService);

            isSignedInGoogleDrive=true;

            Button googleDriveLogoutButton = findViewById(R.id.logoutGoogleDriveButton);

            googleDriveLogoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOutGoogleDrive();
                }
            });
        }
        else
        {
            Button googleDriveLogoutButton = findViewById(R.id.logoutGoogleDriveButton);
            googleDriveLogoutButton.setVisibility(View.GONE);
        }

        //endregion

        //region OneDrive

        String hasSignedInOneDrive = sp.getString("onedriveToken",null);

        if (hasSignedInOneDrive!=null)
        {
            signInOneDrive();
        }
        else
        {
            Button oneDriveLogoutButton = findViewById(R.id.logoutOneDriveButton);
            oneDriveLogoutButton.setVisibility(View.GONE);
        }

        //endregion

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

                    dropboxFiles.add(new DropboxFile(metadata.getName(),"",(FileMetadata)metadata));

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

    private void signOutDropbox()
    {
        try
        {
            new AsyncTask<Void,Void,Void>()
            {
                @Override
                protected Void doInBackground(Void... voids)
                {
                    try
                    {
                        client.auth().tokenRevoke();
                    }catch (DbxException e)
                    {

                    }
                    return null;
                }
            }.execute().get();



            SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sp.edit();

            editor.remove("dropboxToken").commit();

            client = null;

            AuthActivity.result = null;

            isSignedInDropbox = false;

            Button logoutDropboxButton = findViewById(R.id.logoutDropboxButton);

            logoutDropboxButton.setVisibility(View.GONE);

            Button signInDropboxButton = findViewById(R.id.dropboxSignInButton);

            signInDropboxButton.setText("Sign in to Dropbox");

            Toast.makeText(getApplicationContext(),"Logged out of Dropbox",Toast.LENGTH_SHORT).show();


        }
        catch (Exception e)
        {

        }
    }

    //endregion

    //region GoogleDrive


    public String TAG = "MainActivity";
    private void handleSignInResult(Intent result)
    {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Drive API Migration")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);


                    Button googleDriveButton = findViewById(R.id.googleDriveSignInButton);
                    googleDriveButton.setText(googleAccount.getDisplayName());
                    //successfully signed in
                    isSignedInGoogleDrive=true;
                    Toast.makeText(getApplicationContext(),"Signed into GoogleDrive.",Toast.LENGTH_SHORT).show();

                    Button googleDriveLogoutButton = findViewById(R.id.logoutGoogleDriveButton);

                    googleDriveLogoutButton.setVisibility(View.VISIBLE);

                    googleDriveLogoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signOutGoogleDrive();
                        }
                    });

                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to sign in.", exception));
    }


    public DriveServiceHelper mDriveServiceHelper;


    private void signInGoogleDrive()
    {
        //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent,RC_SIGN_IN);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_FILE))
                .requestIdToken(getString(R.string.server_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        startActivityForResult(mGoogleSignInClient.getSignInIntent(),RC_SIGN_IN);


    }

    private void signOutGoogleDrive()
    {

        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //sign out

                Button signInGoogleButton = findViewById(R.id.googleDriveSignInButton);
                signInGoogleButton.setText("Sign in to GoogleDrive");
                isSignedInGoogleDrive =false;
                Button logoutGoogleButton = findViewById(R.id.logoutGoogleDriveButton);
                logoutGoogleButton.setVisibility(View.GONE);
                //Update UI
                Toast.makeText(getApplicationContext(),"Signed out of GoogleDrive.",Toast.LENGTH_SHORT).show();
            }
        });
    }


    //endregion

    //region One Drive

    public IClientConfig oneDriveConfig;

    final DefaultCallback<IOneDriveClient> oneDriveCallback = new DefaultCallback<IOneDriveClient>(this) {
        @Override
        public void success(final IOneDriveClient result) {
            // OneDrive client created successfully.


            mOneDriveClient = new AtomicReference<>(result);
            Toast.makeText(getApplicationContext(),"Signed into OneDrive.",Toast.LENGTH_SHORT).show();

            Button oneDriveButton = findViewById(R.id.oneDriveSignInButton);

            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sp.edit();

            editor.putString("onedriveToken","true");

            editor.commit();

            isSignedInOneDrive=true;

            mOneDriveClient.get()
                    .getDrive()
                    .getRoot()
                    .buildRequest().get(new ICallback<Item>() {
                @Override
                public void success(Item item) {
                    oneDriveButton.setText("root : " +item.id);
                }

                @Override
                public void failure(ClientException ex) {
                    oneDriveButton.setText("Signed In");
                }
            });

            Button logoutOneDriveButton = findViewById(R.id.logoutOneDriveButton);

            logoutOneDriveButton.setVisibility(View.VISIBLE);

            logoutOneDriveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOutOneDrive();
                }
            });
        }

        @Override
        public void failure(final ClientException error) {
            // Exception happened during creation.

            Toast.makeText(getApplicationContext(),"Failed signing into OneDrive.",Toast.LENGTH_SHORT).show();
        }
    };

    private void signInOneDrive()
    {
        oneDriveConfig = DefaultClientConfig.createWithAuthenticator(msaAuthenticator);
        new OneDriveClient.Builder().fromConfig(oneDriveConfig).loginAndBuildClient(this,oneDriveCallback);
    }

    private void signOutOneDrive()
    {
        mOneDriveClient.get().getAuthenticator().logout();

        Button oneDriveLogoutButton = findViewById(R.id.logoutOneDriveButton);

        oneDriveLogoutButton.setVisibility(View.GONE);

        Button oneDriveSignInButton = findViewById(R.id.oneDriveSignInButton);

        oneDriveSignInButton.setText("Sign in to OneDrive");

        isSignedInOneDrive=false;

        Toast.makeText(getApplicationContext(),"Logged out of OneDrive.",Toast.LENGTH_SHORT).show();
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

        oneDriveViewPager = findViewById(R.id.oneDriveViewPager);
        oneDriveViewPager.setVisibility(View.GONE);

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

    public int REQUEST_CODE_OPEN_DOCUMENT = 2;
    public void setGoogleDriveView()
    {
        hideAllContainers();

        googleDriveListFragment = new GoogleDriveListFragment();

        googleDriveViewPager = findViewById(R.id.googleDriveViewPager);

        googleDriveViewPager.setVisibility(View.VISIBLE);

        googleDrivePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        googleDrivePagerAdapter.addFragment(googleDriveListFragment,"GoogleDriveFragment");

        googleDriveViewPager.setAdapter(googleDrivePagerAdapter);



    }

    public void setOneDriveView()
    {
        hideAllContainers();

        oneDriveListFragment = new OneDriveListFragment();

        oneDriveViewPager = findViewById(R.id.oneDriveViewPager);

        oneDriveViewPager.setVisibility(View.VISIBLE);

        oneDrivePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        oneDrivePagerAdapter.addFragment(oneDriveListFragment,"OneDriveFragment");

        oneDriveViewPager.setAdapter(oneDrivePagerAdapter);
    }

    public void setHomepage()
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
        } else if (homepage.getVisibility()==View.VISIBLE)
        {
            System.exit(0);
        }else if (dropboxViewPager.getVisibility()==View.VISIBLE)
        {
            if (dropboxListFragment.currentPath.equals(""))
            {
                setHomepage();
                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(0).setChecked(true);
            }else
            {
                dropboxListFragment.updateAdapterPath(dropboxListFragment.getParentFolder());
            }

        }
        else
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

        if (id == R.id.nav_google_drive)
        {
            if (isSignedInGoogleDrive)
            {
                setGoogleDriveView();
            }
            else
            {
                signInGoogleDrive();
                //start authentication process
            }

        } else if (id == R.id.nav_dropbox)
        {
            if (isSignedInDropbox)
            {
                setDropboxListView();
            }
            else
            {
                Auth.startOAuth2Authentication(MainActivity.this, DROPBOX_APP_KEY);
            }


        }else if (id ==R.id.nav_onedrive)
        {
            if (isSignedInOneDrive)
            {
                setOneDriveView();
            }
            else
            {
                signInOneDrive();
            }
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

            Log.d("TOKEN",DROPBOX_ACCESS_TOKEN);

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

                    Button dropboxLogoutButton = findViewById(R.id.logoutDropboxButton);
                    dropboxLogoutButton.setVisibility(View.VISIBLE);
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
        Log.d("RequestCode",Integer.toString(requestCode));
        Log.d("RCSignIn",Integer.toString(RC_SIGN_IN));

        if (requestCode == RC_SIGN_IN)
        {
            /*
            Log.d("ActivityResult","Running Handle SignIn Result");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            */

            Log.d("ResultCode",Integer.toString(resultCode));

            Log.d("ResultOK",Integer.toString(Activity.RESULT_OK));

            //handleSignInResult(data);

            if (resultCode == Activity.RESULT_OK && data != null) {
                handleSignInResult(data);
            }
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    //endregion

}
