package xyz.ruin.kiki

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.content_upload.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import xyz.ruin.kiki.model.Upload


class UploadActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()
    private val kikiEndpoint = generateRetrofitBuilder().create(KikiEndpoint::class.java)
    private var uploadsObservable: Observable<List<Upload>>? = null
    private val uploadList = ArrayList<Upload>()
    private val uploadAdapter = UploadAdapter(uploadList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = uploadAdapter

        refreshUploads()
    }

    private fun generateRetrofitBuilder(): Retrofit {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        return Retrofit.Builder()
            .baseUrl(Config.ENDPOINT_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_upload, menu)
        return true
    }

    private fun refreshUploads() {
        uploadsObservable = kikiEndpoint.getPosts("hibike!_euphonium", 10)
        subscribeObservableOfPost()
    }


    private fun subscribeObservableOfPost() {
        uploadList.clear()
        compositeDisposable.add(
            uploadsObservable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.fromIterable(it)
                }
                .subscribeWith(createUploadObserver())
        )
    }

    private fun createUploadObserver(): DisposableObserver<Upload> {
        return object : DisposableObserver<Upload>() {
            override fun onNext(upload: Upload) {
                if (!uploadList.contains(upload)) {
                    uploadList.add(upload)
                }
            }

            override fun onComplete() {
                showUploadsOnRecyclerView()
            }

            override fun onError(e: Throwable) {
                Log.e("createUploadObserver", "Upload error: ${e.message}")
            }
        }
    }

    private fun showUploadsOnRecyclerView() {
        if (uploadList.size > 0) {
            recyclerView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
            uploadAdapter.setUploads(uploadList)
        } else {
            recyclerView.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"
    }
}
