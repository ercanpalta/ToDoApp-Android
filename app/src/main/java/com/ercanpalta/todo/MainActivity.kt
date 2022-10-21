package com.ercanpalta.todo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ercanpalta.todo.databinding.ActivityMainBinding
import com.ercanpalta.todo.enums.Repeat
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.receiver.ReminderReceiver
import com.ercanpalta.todo.view.HomeFragmentDirections
import com.google.android.material.navigation.NavigationView
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = this.getSharedPreferences("com.ercanpalta.todo",
            MODE_PRIVATE
        )
        val locale = sharedPreferences.getString("locale","gb")
        setLocale(locale)

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
            Repeat.NOT -> {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,task.remindTimeInMillis,pendingIntent)
            }
            Repeat.DAILY -> {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, task.remindTimeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
            }
            Repeat.WEEKLY -> {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, task.remindTimeInMillis, 1000 * 60 * 60 * 24 * 7, pendingIntent)
            }
        }

        Toast.makeText(this, R.string.reminder_is_set, Toast.LENGTH_SHORT).show()
    }

    fun cancelReminder(requestCode:Int){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this@MainActivity, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this@MainActivity, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
    }

    fun setLocale(languageCode: String?) {
        val locale = Locale(languageCode!!)
        Locale.setDefault(locale)
        val resources: Resources = this.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun refresh(){
        val refresh = Intent(this, MainActivity::class.java)
        finish()
        startActivity(refresh)
    }

    fun changeToolbarColor(isSearchViewOpened:Boolean){
        if (isSearchViewOpened){
            binding.appBarMain.toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.searchview_background))
        }else{
            binding.appBarMain.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.toolbar_background))
        }

    }

    fun getFormattedDate(millis:Long):String{
        val formattedDate: String
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        val month = resources.getStringArray(R.array.months)[calendar.get(Calendar.MONTH)]
        val dayOfWeek = resources.getStringArray(R.array.days_of_week)[calendar.get(Calendar.DAY_OF_WEEK)-1]
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
        var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        var minute = calendar.get(Calendar.MINUTE).toString()

        if (calendar.get(Calendar.HOUR_OF_DAY) < 10){
            hour = "0$hour"
        }
        if (calendar.get(Calendar.MINUTE) < 10){
            minute = "0$minute"
        }

        val time = getString(R.string.time_format,hour,minute)

        formattedDate = "$dayOfMonth $month $dayOfWeek $time"
        return formattedDate
    }
}