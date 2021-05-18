package com.example.githubapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.example.githubapplication.databinding.ActivityUserBinding
import com.example.githubapplication.databinding.RepoItemBinding
import com.example.githubapplication.network.GithubUserService
import com.example.githubapplication.network.ServiceFactory
import com.example.githubapplication.network.model.RepoItem
import com.example.githubapplication.network.model.UserItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserActivity : AppCompatActivity() {

    private lateinit var userName: String
    private lateinit var viewBind: ActivityUserBinding
    private var co: CompositeDisposable = CompositeDisposable()
    private lateinit var user: UserItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = DataBindingUtil.setContentView<ActivityUserBinding>(this, R.layout.activity_user)
        viewBind.user = UserItem("", "null")
        intent?.let {
            userName = it.extras?.getString("username")!!
        }
        refreshUser()
        refreshAllRepos()
    }

    fun refreshUser() {
        val single = ServiceFactory.createService(GithubUserService::class.java)
        val disposable = single.getUserForName(userName).doInBackground()
            .observeOn(AndroidSchedulers.mainThread()).subscribe {it ->
                user = it.body() as UserItem
                viewBind.user = user
            }
        co.add(disposable)
    }

    fun refreshAllRepos() {
        val single = ServiceFactory.createService(GithubUserService::class.java)
        val disposable = single.getUserRepositories(userName, 1).doInBackground()
            .observeOn(AndroidSchedulers.mainThread()).subscribe { repos ->
                fillRepos(repos.body() as List<RepoItem>)
            }
        co.add(disposable)
    }

    public fun <T> Single<T>.doInBackground(): Single<T> {
        return this.subscribeOn(Schedulers.io())
    }

    fun handleLoadFailed(error: Throwable) {

    }

    fun fillRepos(repoList: List<RepoItem>) {
        val repoLayout: LinearLayout = viewBind.llAllRepos
        repoLayout.removeAllViews()
        for (repo in repoList) {
            val itemViewBind = DataBindingUtil.inflate<RepoItemBinding>(
                LayoutInflater.from(this),
                R.layout.repo_item,
                null,
                false
            )
            itemViewBind.repoItem = repo
            repoLayout.addView(itemViewBind.root)
        }
    }

}
