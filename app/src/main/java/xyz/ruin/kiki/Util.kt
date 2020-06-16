package xyz.ruin.kiki

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object Util {
    fun generateRetrofitBuilder(): Retrofit {
        val authenticator = Authenticator { _, response ->
            val credential = Credentials.basic(Config.SZURUPULL_USERNAME, Config.SZURUPULL_PASSWORD)
            response.request().newBuilder().header("Authorization", credential).build()
        }
        val client = OkHttpClient.Builder()
            .authenticator(authenticator)
            .build()
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        return Retrofit.Builder()
            .baseUrl(Config.SZURUPULL_HOST)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}