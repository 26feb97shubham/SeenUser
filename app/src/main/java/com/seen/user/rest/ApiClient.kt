package com.seen.user.rest

import android.text.TextUtils
import android.util.Log
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object {
        private var retrofit: Retrofit? = null
        private const val  CONNECTION_TIMEOUT = (1000 * 30).toLong()
        private const val RETROFIT_LOGGER = "Result "
        private val baseUrl: String = "https://seen-uae.com/BuyerApi/"
//       private val baseUrl: String = "https://seen-e-commerce.devtechnosys.tech/BuyerApi/"
        fun getClient(): Retrofit? {
            if (retrofit == null) {
//                val okHttpClient = OkHttpClient().newBuilder().connectTimeout(80, TimeUnit.SECONDS)
//                        .readTimeout(80, TimeUnit.SECONDS).writeTimeout(80, TimeUnit.SECONDS)
//                        .addInterceptor(okHttpClient).build()

                retrofit = Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofit
        }

        fun createBuilder(paramsName: Array<String>, paramsValue: Array<String>): FormBody.Builder {
            val builder = FormBody.Builder()
            for (i in paramsName.indices) {
                Log.e("create_builder:", paramsName[i] + ":" + paramsValue[i])
                if (!TextUtils.isEmpty(paramsValue[i])) {
                    builder.add(paramsName[i], paramsValue[i])
                } else {
                    builder.add(paramsName[i], "")
                }
            }
            return builder
        }
        fun createMultipartBodyBuilder(paramsName: Array<String>, paramsValue: Array<String>): MultipartBody.Builder? {
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            for (i in paramsName.indices) {
                Log.e("multipart_builder:", paramsName[i] + ":" + paramsValue[i])
                if (!TextUtils.isEmpty(paramsValue[i])) {
                    builder.addFormDataPart(paramsName[i], paramsValue[i])
                } else {
                    builder.addFormDataPart(paramsName[i], "")
                }
            }
            return builder
        }

        private val okHttpClient: OkHttpClient
            get() {
                val okClientBuilder = OkHttpClient.Builder().protocols(listOf(Protocol.HTTP_1_1))
                val httpLoggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger{
                    override fun log(message: String) {
                        Log.e(
                            RETROFIT_LOGGER,
                            message
                        )
                    }
                })
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                okClientBuilder.addInterceptor(httpLoggingInterceptor)
                okClientBuilder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                okClientBuilder.readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                okClientBuilder.writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                return okClientBuilder.build()
            }

       /* class LoginInterceptor : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val t1 = System.nanoTime()
                Log.e("OkHttp", String.format("--> Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()))
                try {
                    val requestBuffer = Buffer()
                    Log.e("OkHttp", requestBuffer.readUtf8().replace("=", ":").replace("&", "\n"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val response = chain.proceed(request)
                val t2 = System.nanoTime()
                Log.e("OkHttp", String.format("<-- Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6, response.headers()))
                val contentType = response.body()!!.contentType()
                val content = response.body()!!.string()
                Log.e("OkHttp", content)
                val wrappedBody = ResponseBody.create(contentType, content)
                return response.newBuilder().body(wrappedBody).build()
            }
        }*/
    }
}