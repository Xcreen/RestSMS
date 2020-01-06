package net.xcreen.restsms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.xcreen.restsms.fragments.AboutFragment;
import net.xcreen.restsms.fragments.HomeFragment;
import net.xcreen.restsms.fragments.LoggingFragment;
import net.xcreen.restsms.fragments.SettingsFragment;

import org.slf4j.impl.SimpleLogger;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    Menu optionsMenu;
    Activity instance;

    int OPTION_ITEM_LOGGING_DELETE_ALL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        //Set SLF4J Log-Level
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");

        //Init Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init Navigation
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Dont replace Fragment, on orientation-change
        if(savedInstanceState == null) {
            //Set Home Fragment
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            try {
                Fragment homeFragment = HomeFragment.class.newInstance();
                fragmentTransaction.replace(R.id.main_framelayout, homeFragment).commit();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu optionsMenu) {
        this.optionsMenu = optionsMenu;
        getMenuInflater().inflate(R.menu.options_menu, optionsMenu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Remove all Option-Menu-Items
        optionsMenu.clear();

        //Set new Fragment
        Fragment fragment;
        switch(item.getItemId()){
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                toolbar.setTitle(R.string.nav_item_settings);
                break;
            case R.id.nav_logging:
                fragment = new LoggingFragment();
                toolbar.setTitle(R.string.nav_item_logging);
                //Add Option-Items (if not exist)
                if(optionsMenu.findItem(OPTION_ITEM_LOGGING_DELETE_ALL) == null) {
                    MenuItem loggingDeleteAllItem = optionsMenu.add(Menu.NONE, OPTION_ITEM_LOGGING_DELETE_ALL, 1, R.string.logging_options_delete_all);
                    loggingDeleteAllItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
                    loggingDeleteAllItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Get all Logs
                                            String logPath = instance.getFilesDir().getAbsolutePath() + File.separator + "logs";
                                            File logDir = new File(logPath);
                                            File[] logFiles = logDir.listFiles();
                                            if(logFiles != null){
                                                //Delete all Logs
                                                for(File logFile : logFiles){
                                                    logFile.delete();
                                                }
                                            }
                                            //Refresh List
                                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                            fragmentTransaction.replace(R.id.main_framelayout, new LoggingFragment()).commit();
                                            //Show Success Message
                                            Toast.makeText(instance, R.string.logging_successful_deleted_all, Toast.LENGTH_LONG).show();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(instance);
                            builder.setMessage(getString(R.string.logging_options_delete_all_quest))
                                   .setTitle(getString(R.string.logging_options_delete_all))
                                   .setPositiveButton(getString(R.string.yes), dialogClickListener)
                                   .setNegativeButton(getString(R.string.no), dialogClickListener)
                                   .show();
                            return true;
                        }
                    });
                }
                break;
            case R.id.nav_about:
                fragment = new AboutFragment();
                toolbar.setTitle(R.string.nav_item_about);
                break;
            case R.id.nav_home:
            default:
                fragment = new HomeFragment();
                toolbar.setTitle(R.string.app_name);
                break;
        }
        //Replace Fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_framelayout, fragment).addToBackStack("fragBack").commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
