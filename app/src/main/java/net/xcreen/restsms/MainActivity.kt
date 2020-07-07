package net.xcreen.restsms

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import net.xcreen.restsms.fragments.AboutFragment
import net.xcreen.restsms.fragments.HomeFragment
import net.xcreen.restsms.fragments.LoggingFragment
import net.xcreen.restsms.fragments.SettingsFragment
import org.slf4j.impl.SimpleLogger
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var toolbar: Toolbar? = null
    var drawerToggle: ActionBarDrawerToggle? = null
    var optionsMenu: Menu? = null
    var instance: Activity? = null
    var OPTION_ITEM_LOGGING_DELETE_ALL = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instance = this

        //Set SLF4J Log-Level
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO")

        //Init Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Init Navigation
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(drawerToggle!!)
        drawerToggle!!.syncState()
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        //Dont replace Fragment, on orientation-change
        if (savedInstanceState == null) {
            //Set Home Fragment
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            try {
                val homeFragment: Fragment = HomeFragment::class.java.newInstance()
                fragmentTransaction.replace(R.id.main_framelayout, homeFragment).commit()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(optionsMenu: Menu): Boolean {
        this.optionsMenu = optionsMenu
        menuInflater.inflate(R.menu.options_menu, optionsMenu)
        return true
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        //Remove all Option-Menu-Items
        optionsMenu!!.clear()

        //Set new Fragment
        val fragment: Fragment
        when (item.itemId) {
            R.id.nav_settings -> {
                fragment = SettingsFragment()
                toolbar!!.setTitle(R.string.nav_item_settings)
            }
            R.id.nav_logging -> {
                fragment = LoggingFragment()
                toolbar!!.setTitle(R.string.nav_item_logging)
                //Add Option-Items (if not exist)
                if (optionsMenu!!.findItem(OPTION_ITEM_LOGGING_DELETE_ALL) == null) {
                    val loggingDeleteAllItem = optionsMenu!!.add(Menu.NONE, OPTION_ITEM_LOGGING_DELETE_ALL, 1, R.string.logging_options_delete_all)
                    loggingDeleteAllItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                    loggingDeleteAllItem.setOnMenuItemClickListener {
                        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    //Get all Logs
                                    val logPath = instance!!.filesDir.absolutePath + File.separator + "logs"
                                    val logDir = File(logPath)
                                    val logFiles = logDir.listFiles()
                                    if (logFiles != null) {
                                        //Delete all Logs
                                        for (logFile in logFiles) {
                                            logFile.delete()
                                        }
                                    }
                                    //Refresh List
                                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                                    fragmentTransaction.replace(R.id.main_framelayout, LoggingFragment()).commit()
                                    //Show Success Message
                                    Toast.makeText(instance, R.string.logging_successful_deleted_all, Toast.LENGTH_LONG).show()
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {
                                }
                            }
                        }
                        val builder = AlertDialog.Builder(instance)
                        builder.setMessage(getString(R.string.logging_options_delete_all_quest))
                                .setTitle(getString(R.string.logging_options_delete_all))
                                .setPositiveButton(getString(R.string.yes), dialogClickListener)
                                .setNegativeButton(getString(R.string.no), dialogClickListener)
                                .show()
                        true
                    }
                }
            }
            R.id.nav_about -> {
                fragment = AboutFragment()
                toolbar!!.setTitle(R.string.nav_item_about)
            }
            R.id.nav_home -> {
                fragment = HomeFragment()
                toolbar!!.setTitle(R.string.app_name)
            }
            else -> {
                fragment = HomeFragment()
                toolbar!!.setTitle(R.string.app_name)
            }
        }
        //Replace Fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_framelayout, fragment).addToBackStack("fragBack").commit()
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}