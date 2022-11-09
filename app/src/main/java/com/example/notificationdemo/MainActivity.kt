package com.example.notificationdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import com.example.notificationdemo.databinding.ActivityMainBinding
import com.example.notificationdemo.util.DataStoreManager
import com.example.notificationdemo.util.PreferenceKeys.NOTIFICATION_TONE_URI_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("UnspecifiedImmutableFlag")
class MainActivity : AppCompatActivity() {

    lateinit var alarmManager: AlarmManager
    lateinit var binding: ActivityMainBinding
    private var currentToneUri: String? = ""

    lateinit var dataStoreManager: DataStoreManager

    private val alarmPendingIntent by lazy {
        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PendingIntent.getBroadcast(applicationContext, 0, intent, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
        } else{
            PendingIntent.getBroadcast(applicationContext, 0, intent,0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        dataStoreManager = DataStoreManager(applicationContext)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
      dataStoreManager.readStringFromDataStore(NOTIFICATION_TONE_URI_KEY).asLiveData().observe(this) {
          currentToneUri = it
      }
        binding.btn.setOnClickListener {
            Toast.makeText(this,"Pressed", Toast.LENGTH_SHORT).show()
            val calendar = Calendar.getInstance()
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                6000*1,
                alarmPendingIntent
            )
        }


        binding.btnRingTone.setOnClickListener{

            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                "Select ringtone for notifications:"
            )
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentToneUri)
            startActivityForResult(intent, 999)

//            playErrorTone(this, applicationContext)
        }
    }


    fun playErrorTone(activity: Activity, context: Context, notificationName: String = "Thallium") {

        val notifications = getNotificationSounds(activity)

        Log.d("NOT_LOGGG",notifications.toString())

        try {
            val tone = notifications.getValue(notificationName)
            val errorTone = RingtoneManager.getRingtone(context, Uri.parse(tone))
            errorTone.play()
        } catch (e: NoSuchElementException) {
            try {
                // If sound not found, default to first one in list
                val errorTone = RingtoneManager.getRingtone(context, Uri.parse(notifications.values.first()))
                errorTone.play()
            } catch (e: NoSuchElementException) {
                Log.d("NOT_LOGGG","NO NOTIFICATION SOUNDS FOUND")

            }
        }
    }

    private fun getNotificationSounds(activity: Activity): HashMap<String, String> {
        val manager = RingtoneManager(activity)
        manager.setType(RingtoneManager.TYPE_NOTIFICATION)
        val cursor = manager.cursor

        val list = HashMap<String, String>()
        while (cursor.moveToNext()) {
            val id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
            val uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)

            list.set(title, "$uri/$id")
        }

        return list
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 999) {

            val uri = data!!.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            currentToneUri = uri.toString()
//            sharedPref.edit().putString(AppUtils.NOTIFICATION_TONE_URI_KEY, currentToneUri).apply()
            CoroutineScope(Dispatchers.IO).launch {
                dataStoreManager.saveToneUriStringToDataStore(NOTIFICATION_TONE_URI_KEY,currentToneUri!!)
            }

            val ringtone = RingtoneManager.getRingtone(applicationContext, uri)
            binding.btnRingTone.setText(ringtone.getTitle(applicationContext))

        }
    }

}