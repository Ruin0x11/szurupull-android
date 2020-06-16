package xyz.ruin.kiki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.content_upload.*
import xyz.ruin.kiki.model.Link
import xyz.ruin.kiki.model.Upload


class UploadActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()
    private val szurupullEndpoint = Util.generateRetrofitBuilder().create(SzurupullEndpoint::class.java)
    private var uploadsObservable: Observable<List<Upload>>? = null
    private val uploadList = ArrayList<Upload>()
    private val uploadAdapter = UploadAdapter(uploadList)
    private lateinit var link: Link
    private lateinit var loadingPanel: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        setSupportActionBar(toolbar)

        loadingPanel = findViewById(R.id.loadingPanel)

        link = handleIntent(intent)

        fab.setOnClickListener { _ -> sendLink(link) }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = uploadAdapter

        refreshUploads()
    }

    private fun handleIntent(intent: Intent): Link =
        when (intent.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleTextUrl(intent) // Handle text being sent
                } else {
                    Link("")
                }
            }
            else -> Link("")
        }

    private fun handleTextUrl(intent: Intent): Link {
        val url = intent.getStringExtra(Intent.EXTRA_TEXT)
        return Link(url!!)
    }

    private fun sendLink(link: Link) {
        val networkIntent = Intent(this, UploadService::class.java)
        networkIntent.putExtra("action", UploadService.INTENT_POST)
        networkIntent.putExtra("link", link)
        startService(networkIntent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_upload, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.retryFetch -> {
                refreshUploads()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshUploads() {
        uploadList.clear()
        recyclerView.visibility = View.GONE
        emptyText.visibility = View.GONE
        loadingPanel.visibility = View.VISIBLE
        uploadsObservable = szurupullEndpoint.extract(link.url)
        subscribeObservableOfUpload()
    }


    private fun subscribeObservableOfUpload() {
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
                loadingPanel.visibility = View.GONE;
                showUploadsOnRecyclerView()
            }

            override fun onError(e: Throwable) {
                Log.e("createUploadObserver", "Upload error: $e")
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
}
