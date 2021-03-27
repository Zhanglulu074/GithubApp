package com.example.githubapplication

import android.app.Application
import com.example.githubapplication.network.ServiceFactory

class GithubApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceFactory.initClient(applicationContext)
    }
}