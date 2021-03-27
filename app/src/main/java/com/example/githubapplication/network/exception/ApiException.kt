package com.example.githubapplication.network.exception

import retrofit2.Response
import java.lang.RuntimeException

class ApiException(var response: Response<*>): RuntimeException() {

    override fun getLocalizedMessage(): String? {
        return response.raw().message()
    }
}