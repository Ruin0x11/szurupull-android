package xyz.ruin.kiki

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import xyz.ruin.kiki.model.Link
import xyz.ruin.kiki.model.Upload

class UploadService : IntentService("UploadService") {
    private var mContext: Context = this
    private var mToastHandler = Handler(Looper.getMainLooper())
    private val szurupullEndpoint = Util.generateRetrofitBuilder().create(SzurupullEndpoint::class.java)
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onHandleIntent(intent: Intent?) {
        when (val action = intent!!.getIntExtra("action", -1)) {
            INTENT_POST -> {
                val link: Link = intent.getSerializableExtra("link") as Link
                postLink(link)
                stopSelf()
            }
            else ->
                Log.e("NETWORK_ERROR", "Unknown intent action received: $action")
        }
    }

    private inner class DisplayToast(private val mText: String) : Runnable {
        override fun run() {
            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show()
        }
    }

    private fun postLink(link: Link) {
        val observable = szurupullEndpoint.createUpload(link)
        compositeDisposable.add(
            observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(createUploadObserver(link))
        )
    }

    private fun createUploadObserver(link: Link): DisposableObserver<Upload> {
        return object : DisposableObserver<Upload>() {
            override fun onNext(upload: Upload) {

            }

            override fun onComplete() {
                mToastHandler.post(DisplayToast(getString(R.string.add_success)))
                Log.i("SUCCESS", "Success after uploading link")
            }

            override fun onError(e: Throwable) {
                Log.e("ERROR", "Upload error: $e")
                sendNotificationShareError(link)
            }
        }
    }

    private fun sendNotificationShareError(link: Link) {
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Failed to upload " + link.url)
            .setContentText("Press to try again")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        // Creates an explicit intent To relaunch this service
        val resultIntent = Intent(this, UploadService::class.java)
        resultIntent.putExtra("action", INTENT_POST)
        resultIntent.putExtra("link", link)

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = PendingIntent.getService(this, 0, resultIntent, 0)
        mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // mId allows you to update the notification later on.
        mNotificationManager.notify(link.url.hashCode(), mBuilder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.notification_channel_error_name)
            val description = getString(R.string.notification_channel_error_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val INTENT_POST = 200

        // Notification channels
        const val CHANNEL_ID = "error_channel"
    }
}
