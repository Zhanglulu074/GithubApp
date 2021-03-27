package com.example.githubapplication.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.githubapplication.network.model.LoadingStatus
import com.example.githubapplication.network.model.RepoItem
import com.example.githubapplication.network.model.UserItem
import java.util.*

class MainActivityViewModel: ViewModel() {
    var userItemList = ObservableArrayList<UserItem>()
    var repoItemList = ObservableArrayList<RepoItem>()
    var codeItemList = ObservableArrayList<UserItem>()

    var loadingStatus = LoadingStatus()
}