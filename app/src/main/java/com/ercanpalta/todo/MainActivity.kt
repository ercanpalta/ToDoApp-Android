package com.ercanpalta.todo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.ercanpalta.todo.databinding.ActivityMainBinding
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.receiver.ReminderReceiver
import com.ercanpalta.todo.view.HomeFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        binding.appBarMain.toolbar.setTitleTextColor(getColor(R.color.title))


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // to get task info from receiver
        var uid = -1
        val intent = intent
        intent?.extras?.apply {
            uid = this.getInt("uid",-1)
        }

        if (uid != -1){
            val action = HomeFragmentDirections.actionNavHomeToReminderFragment(uid)
            navController.navigate(action)
        }

    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }*/


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setReminder(task:ToDo) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this@MainActivity, ReminderReceiver::class.java)
        intent.putExtra("request_code",task.requestCode)

        val pendingIntent = PendingIntent.getBroadcast(this@MainActivity, task.requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        when (task.repeat) {
            "Does not repeat" -> {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,task.remindTimeInMillis,pendingIntent)
            }
            "Daily" -> {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, task.remindTimeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
            }
            "Weekly" -> {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, task.remindTimeInMillis, 1000 * 60 * 60 * 24 * 7, pendingIntent)
            }
        }

        Toast.makeText(this, "Reminder is set", Toast.LENGTH_SHORT).show()
    }

    fun cancelReminder(requestCode:Int){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this@MainActivity, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this@MainActivity, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
    }
}