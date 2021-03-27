package com.example.githubapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubapplication.adapter.RepoListAdapter
import com.example.githubapplication.adapter.RootAdapter
import com.example.githubapplication.adapter.UserListAdapter
import com.example.githubapplication.databinding.FragmentSearchBinding
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
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import retrofit2.Response

class SearchFragment: Fragment() {

    var currentPage: Int = 1
    var curCount: Long = 0
    var hasNextPage: Boolean = true
    lateinit var searchSubject: Subject<Int>

    private lateinit var rxPermission: RxPermissions
    private var co: CompositeDisposable = CompositeDisposable()
    private lateinit var viewBind: FragmentSearchBinding
    private lateinit var viewModel: MainActivityViewModel
    private var queryString: String? = ""
    @SearchType
    private  var searchTYpe: Int = TYPE_REPO

    private var userAdapter: UserListAdapter? = null
    private var repoAdapter: RepoListAdapter? = null

    companion object {
        const val TYPE_REPO = 0
        const val TYPE_USER = 1
    }
    @IntDef(TYPE_REPO, TYPE_USER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class SearchType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        viewBind = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        rxPermission = RxPermissions(activity!!)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        initView()
        return viewBind.root
    }

    private fun initView() {
        viewBind.searchItemsList.layoutManager = LinearLayoutManager(context)
        userAdapter = UserListAdapter(context!!, viewModel.userItemList)
        val loadingBind = DataBindingUtil.inflate<ListLoadingViewBinding>(
            LayoutInflater.from(context), R.layout.list_loading_view, viewBind.searchItemsList, false
        )
        loadingBind.loadingStatus = viewModel.loadingStatus
        viewModel.loadingStatus.setLoadingStatus(LoadingStatus.STATUS_INITIAL)
        userAdapter?.footView = loadingBind.root
        userAdapter?.footerListener = object : RootAdapter.ScrollToFooterListener {
            override fun onScrollToFooter() {
                searchSubject.onNext(currentPage)
            }
        }
        viewBind.searchView.setIconifiedByDefault(true)
        viewBind.searchView.requestFocus()
        viewBind.searchView.onActionViewExpanded()
        viewBind.searchView.isIconified = false
        viewBind.searchItemsList.adapter = userAdapter
        viewBind.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @SuppressLint("CheckResult")
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryString = query
                refreshQueryData()
                viewBind.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        searchSubject = BehaviorSubject.createDefault(1)
    }

    private fun makeUserSearchSingle(
        page: Long,
        pageSize: Long
    ): Single<Response<UserSearchResponse>> {
        val service = ServiceFactory.createService(GithubSearchService::class.java)
        return service.searchUsers(queryString, null, null, page, pageSize)
    }


    private fun resetSearch() {
        viewModel.userItemList.clear()
        currentPage = 1
        curCount = 0
        hasNextPage = true
        searchSubject = BehaviorSubject.createDefault(1)
    }

    private fun refreshQueryData() {
        resetSearch()
        if (queryString == "") {
            return
        }
        viewModel.loadingStatus.setLoadingStatus(LoadingStatus.STATUS_LOADING)
        searchSubject.flatMap {
            makeUserSearchSingle(
                it.toLong(),
                UserSearchResponse.PAGE_SIZE.toLong()
            ).doInBackground().toObservable()
        }.subscribeOn(Schedulers.io())
            .map {
                ApiUtil.throwOnFailure(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                currentPage++
                curCount += it.users.size
                viewModel.userItemList.addAll(it.users)
                if (curCount == it.totalCount) {
                    hasNextPage = false
                    viewModel.loadingStatus.setLoadingStatus(
                        if (it.totalCount > 0) LoadingStatus.STATUS_SUCCESS_FULL
                        else LoadingStatus.STATUS_SUCCESS_EMPTY
                    )
                }
            }, {
                Log.d(MainActivity.TAG, "initView: ${it.localizedMessage}")
                viewModel.loadingStatus.setLoadingStatus(
                    LoadingStatus.STATUS_FAILED,
                    it.localizedMessage
                )
            }).addTo(co)
    }
}