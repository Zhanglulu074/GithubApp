package com.example.githubapplication.network

import com.example.githubapplication.network.exception.ApiException
import retrofit2.Response
import java.net.HttpURLConnection

class ApiUtil {
    companion object {
        public fun <T> throwOnFailure(response: Response<T>): T{
            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            }
            if (!response.isSuccessful) {
                throw ApiException(response)
            }
            return response.body()!!
        }
    }
}