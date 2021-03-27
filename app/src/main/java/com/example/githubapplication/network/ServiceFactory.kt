package com.example.githubapplication.network

import android.content.Context
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import kotlin.collections.HashMap

object ServiceFactory {

    private val DEFAULT_HEADER_ACCEPT =
        ("application/vnd.github.squirrel-girl-preview," // reactions API preview
                + "application/vnd.github.v3.full+json")

    private val RETROFIT_BUILDER = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())

    private var sCache: HashMap<String, Any?> = HashMap<String, Any?>()

    private lateinit var sApiHttpClient: OkHttpClient


    public fun initClient(context: Context) {
        sApiHttpClient = OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "api-http"), 20 * 1024 * 1024)).build()
    }

    public fun <T> createService(clazz: Class<T>): T {
        if (sCache.containsKey(clazz.simpleName)) {
            return sCache.get(clazz.simpleName) as T
        }
        val clientBuilder = sApiHttpClient.newBuilder().addInterceptor {chain ->
            val original = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .method(original.method(), original.body())
            requestBuilder.addHeader("Accept", DEFAULT_HEADER_ACCEPT)
            chain.proceed(requestBuilder.build())
        }
        val retrofit: Retrofit = RETROFIT_BUILDER
            .baseUrl("https://api.github.com")
            .client(clientBuilder.build())
            .build()
        val service = retrofit.create(clazz)
        sCache.put(clazz.simpleName, service)
        return retrofit.create(clazz)
    }

    private fun <T> makeServiceKey(clazz: Class<T>): String {
        return clazz.simpleName
    }
}