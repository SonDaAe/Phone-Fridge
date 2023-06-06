package kr.ac.kumoh.s20190610.first

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface APIs {
    @POST("upload")
    fun uploadImage(@Body requestBody: RequestBody): Call<ResponseBody>

    companion object {
        private const val BASE_URL = "http://iriya.iptime.org:5000/"

        fun create(): APIs {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIs::class.java)

        }
    }
}