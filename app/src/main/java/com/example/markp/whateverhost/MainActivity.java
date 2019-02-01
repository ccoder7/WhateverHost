package com.example.markp.whateverhost;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity mainActivity;

    //region Permission Variables

    private static final String DROPBOX_ACCESS_TOKEN = "";

    private static final int MY_PERMISSIONS_REQUEST_CODE = 1234;


    //DROPBOX

    public DbxRequestConfig config;
    public DbxClientV2 client;
    public FullAccount account;

    //endregion

    //region FRAGMENTS VARIABLES
    public ViewPager myDeviceViewPager;
    public ViewPager dropboxViewPager;
    ConstraintLayout homepage;

    public SectionsStatePagerAdapter deviceListPagerAdapter, dropboxPagerAdapter;

    public static DeviceListFragment deviceListFragment;

    public static DropboxListFragment dropboxListFragment;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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



        getPermissions();

        new DropboxConnectTask().execute(this);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            // Handle the camera action
        } else if (id == R.id.nav_dropbox)
        {
            //new DropboxConnectTask().execute(this);

            //setDropboxListView();

            new DropboxRetrieveTask().execute(this);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void accessDropbox()
    {
        config = DbxRequestConfig.newBuilder("dropbox/Whatever-Host").build();
        client = new DbxClientV2(config, DROPBOX_ACCESS_TOKEN);
        try
        {
            account = client.users().getCurrentAccount();
        }catch (DbxException e)
        {
            Log.d("1st","login exception");
            Log.d("Error",e.getRequestId());
        }
        catch (NetworkOnMainThreadException e)
        {
            Log.d("1st - 2","login exception");
            Log.d("Error",e.getMessage());
        }

    }

    private void hideAllContainers()
    {
        homepage = findViewById(R.id.welcomeLayout);
        homepage.setVisibility(View.GONE);

        myDeviceViewPager = findViewById(R.id.myDeviceViewPager);
        myDeviceViewPager.setVisibility(View.GONE);

        dropboxViewPager = findViewById(R.id.dropboxViewpager);
        dropboxViewPager.setVisibility(View.GONE);

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

        dropboxViewPager = findViewById(R.id.dropboxViewpager);


        dropboxPagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        dropboxListFragment = new DropboxListFragment();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideAllContainers();
                dropboxViewPager.setVisibility(View.VISIBLE);

                dropboxPagerAdapter.addFragment(dropboxListFragment,"DropboxFragment");

                dropboxViewPager.setAdapter(dropboxPagerAdapter);
            }
        });

    }

    private void setHomepage()
    {
        hideAllContainers();

        homepage=findViewById(R.id.welcomeLayout);

        homepage.setVisibility(View.VISIBLE);
    }

    public void openFolder(File folder)
    {
        //add Fragment

        DeviceListFragment list = new DeviceListFragment();

        list.setList(folder);

        deviceListPagerAdapter.addFragment(list,"Another page");

        ViewPager myDeviceViewPager = findViewById(R.id.myDeviceViewPager);
        myDeviceViewPager.setVisibility(View.VISIBLE);

        int curr = myDeviceViewPager.getCurrentItem();
        myDeviceViewPager.setCurrentItem(curr + 1);



    }
}
