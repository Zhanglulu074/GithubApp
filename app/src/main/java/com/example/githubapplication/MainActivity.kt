package com.example.githubapplication

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubapplication.adapter.RootAdapter
import com.example.githubapplication.adapter.UserListAdapter
import com.example.githubapplication.databinding.ActivityMainBinding
import com.example.githubapplication.databinding.ListLoadingViewBinding
import com.example.githubapplication.network.ApiUtil
import com.example.githubapplication.network.GithubSearchService
import com.example.githubapplication.network.ServiceFactory
import com.example.githubapplication.network.model.LoadingStatus
import com.example.githubapplication.network.model.UserSearchResponse
import com.example.githubapplication.viewmodel.MainActivityViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    var currentPage: Int = 1
    var curCount: Long = 0
    var hasNextPage: Boolean = true
    lateinit var searchSubject: Subject<Int>

    private lateinit var rxPermission: RxPermissions
    private var co: CompositeDisposable = CompositeDisposable()
    private lateinit var viewBind: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rxPermission = RxPermissions(this)
        requestAllPermissionToDo {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SearchFragment()).commit()
        }
    }

    private fun requestAllPermissionToDo(action: () -> Unit) {
        rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            action()
        }.addTo(co)
    }
}

fun Disposable.addTo(co: CompositeDisposable) {
    co.add(this)
}

fun <T> Single<T>.doInBackground(): Single<T> {
    return this.subscribeOn(Schedulers.io())
}