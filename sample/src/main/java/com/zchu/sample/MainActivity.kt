package com.zchu.sample

import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.zchu.log.Logger
import com.zchu.rxcache.RxCache
import com.zchu.rxcache.data.CacheResult
import com.zchu.rxcache.data.ResultFrom
import com.zchu.rxcache.diskconverter.SerializableDiskConverter
import com.zchu.rxcache.kotlin.load
import com.zchu.rxcache.kotlin.rxCache
import com.zchu.rxcache.stategy.CacheStrategy
import com.zchu.rxcache.stategy.IStrategy
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var serverAPI: ServerAPI? = null
    private var tvData: TextView? = null
    private var mSubscription: Disposable? = null
    private var swIsAsync: Switch? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvData = findViewById(R.id.tv_data)
        swIsAsync = findViewById(R.id.sw_is_async)
        bindOnClickLister(
                R.id.btn_first_remote,
                R.id.btn_first_cache,
                R.id.btn_first_cache_timeout,
                R.id.btn_only_remote,
                R.id.btn_only_cache,
                R.id.btn_cache_and_remote,
                R.id.btn_none,
                R.id.btn_clean_cache,
                R.id.btn_load_cache,
                R.id.btn_save_cache
        )
        serverAPI = Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(OkHttpClient.Builder().build())
                .build()
                .create(ServerAPI::class.java)
        RxCache.initializeDefault(RxCache.Builder()
                .appVersion(2)
                .diskDir(File(cacheDir.path + File.separator + "data-cache"))
                .diskConverter(SerializableDiskConverter())
                .diskMax((20 * 1024 * 1024).toLong())
                .memoryMax(0)
                .setDebug(true)
                .build())
        Logger.init("RxCache")
    }

    fun bindOnClickLister(@IdRes vararg ids: Int) {
        for (id in ids) {
            val view = findViewById<View>(id)
            view?.setOnClickListener(this)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_first_remote ->
                if (swIsAsync!!.isChecked) {
                    loadData(CacheStrategy.firstRemote())
                } else {
                    loadData(CacheStrategy.firstRemoteSync())
                }
            R.id.btn_first_cache ->
                if (swIsAsync!!.isChecked) {
                    loadData(CacheStrategy.firstCache())
                } else {
                    loadData(CacheStrategy.firstCacheSync())
                }
            R.id.btn_first_cache_timeout ->
                if (swIsAsync!!.isChecked) {
                    loadData(CacheStrategy.firstCacheTimeout(5000))
                } else {
                    loadData(CacheStrategy.firstCacheTimeoutSync(5000))
                }
            R.id.btn_only_remote ->
                if (swIsAsync!!.isChecked) {
                    loadData(CacheStrategy.onlyRemote())
                } else {
                    loadData(CacheStrategy.onlyRemoteSync())
                }
            R.id.btn_only_cache ->
                loadData(CacheStrategy.onlyCache())
            R.id.btn_cache_and_remote ->
                if (swIsAsync!!.isChecked) {
                    loadData(CacheStrategy.cacheAndRemote())
                } else {
                    loadData(CacheStrategy.cacheAndRemoteSync())
                }
            R.id.btn_none ->
                loadData(CacheStrategy.none())
            R.id.btn_load_cache ->
                RxCache.getDefault()
                        .load<String>("custom_key")
                        .subscribe(object : io.reactivex.rxjava3.core.Observer<CacheResult<String>> {
                            override fun onSubscribe(disposable: Disposable) {
                                mSubscription = disposable
                            }

                            override fun onNext(t: CacheResult<String>) {
                                Logger.d(t)

                                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                        .format(Date(t.timestamp))
                                tvData!!.text = "来自缓存" + ResultFrom.ifFromCache(t.from) + "  写入时间：" + format + "\n " + t.data

                            }

                            override fun onError(throwable: Throwable) {
                                tvData!!.text = throwable.message
                            }

                            override fun onComplete() {

                            }
                        })

            R.id.btn_save_cache -> {
                RxCache.getDefault().save("custom_key", "测试数据，测试数据测试数据测试数据测试数据").subscribe()

                Toast.makeText(this, "已写入测试数据", Toast.LENGTH_SHORT).show()
            }
            R.id.btn_clean_cache -> {
                RxCache.getDefault().clear().subscribe()
                tvData!!.text = "数据"
            }
        }


    }

    private fun loadData(strategy: IStrategy) {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
        tvData!!.text = "加载中..."
        val startTime = System.currentTimeMillis()
        serverAPI!!.fetchUsers()
                //泛型这样使用
                .rxCache("custom_key", strategy)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.rxjava3.core.Observer<CacheResult<String>> {
                    override fun onSubscribe(disposable: Disposable) {
                        mSubscription = disposable
                    }

                    override fun onError(throwable: Throwable) {
                        tvData!!.text = throwable.message
                    }

                    override fun onComplete() {

                    }

                    override fun onNext(t: CacheResult<String>) {
                        Logger.d(t)
                        if (ResultFrom.ifFromCache(t.from)) {
                            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    .format(Date(t.timestamp))
                            tvData!!.text = "来自缓存  写入时间：" + format + "\n " + t.data
                        } else {
                            tvData!!.text = "来自网络：\n " + t.data + "\n 响应时间：" + (System.currentTimeMillis() - startTime) + "毫秒"
                        }
                    }
                })
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
