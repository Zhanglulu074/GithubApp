package com.example.githubapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.IntDef
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubapplication.adapter.RepoListAdapter
import com.example.githubapplication.adapter.RootAdapter
import com.example.githubapplication.adapter.SearchAdapter
import com.example.githubapplication.databinding.FragmentSearchBinding
import com.example.githubapplication.databinding.ListLoadingViewBinding
import com.example.githubapplication.network.ApiUtil
import com.example.githubapplication.network.GithubSearchService
import com.example.githubapplication.network.ServiceFactory
import com.example.githubapplication.network.model.LoadingStatus
import com.example.githubapplication.network.model.RepoSearchResponse
import com.example.githubapplication.network.model.UserSearchResponse
import com.example.githubapplication.viewmodel.MainActivityViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import okhttp3.OkHttpClient
import retrofit2.Response

class SearchFragment : Fragment() {

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
    private var searchTYpe: Int = TYPE_USER

    private var searchAdapter: SearchAdapter? = null

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
        searchAdapter = SearchAdapter(context!!)
        val loadingBind = DataBindingUtil.inflate<ListLoadingViewBinding>(
            LayoutInflater.from(context),
            R.layout.list_loading_view,
            viewBind.searchItemsList,
            false
        )
        searchAdapter?.searchMode = searchTYpe
        loadingBind.loadingStatus = viewModel.loadingStatus
        viewModel.loadingStatus.setLoadingStatus(LoadingStatus.STATUS_INITIAL)
        searchAdapter?.footView = loadingBind.root
        searchAdapter?.footerListener = object : RootAdapter.ScrollToFooterListener {
            override fun onScrollToFooter() {
                if (hasNextPage) {
                    searchSubject.onNext(currentPage)
                }
            }
        }
        viewBind.searchView.setIconifiedByDefault(true)
        viewBind.searchView.requestFocus()
        viewBind.searchView.onActionViewExpanded()
        viewBind.searchView.isIconified = false
        viewBind.searchItemsList.adapter = searchAdapter
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
        ArrayAdapter.createFromResource(
            context!!,
            R.array.search_mode_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            viewBind.searchModeSpinner.adapter = it
            viewBind.searchModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    searchTYpe = position
                    searchAdapter?.searchMode = searchTYpe
                    viewBind.searchItemsList.recycledViewPool.clear()
                    viewBind.searchItemsList.adapter = null
                    viewBind.searchItemsList.adapter = searchAdapter
                    searchAdapter?.clear()
                    searchSubject = BehaviorSubject.createDefault(1)
                    viewBind.searchItemsList.visibility = View.GONE
                }
            }
        }
    }

    private fun makeUserSearchSingle(
        page: Long,
        pageSize: Long
    ): Single<Response<UserSearchResponse>> {
        val service = ServiceFactory.createService(GithubSearchService::class.java)
        return service.searchUsers(queryString, null, null, page, pageSize)
    }

    private fun makeRepoSearchSingle(
        page: Long,
        pageSize: Long
    ): Single<Response<RepoSearchResponse>> {
        val service = ServiceFactory.createService(GithubSearchService::class.java)
        return service.searchRepositories(queryString, null, null, page, pageSize)
    }


    private fun resetSearch() {
        viewModel.userItemList.clear()
        currentPage = 1
        curCount = 0
        hasNextPage = true
        searchAdapter?.clear()
        searchSubject = BehaviorSubject.createDefault(1)
    }

    private fun refreshQueryData() {
        resetSearch()
        if (queryString == "") {
            return
        }
        viewBind.searchItemsList.visibility = View.VISIBLE
        viewModel.loadingStatus.setLoadingStatus(LoadingStatus.STATUS_LOADING)
        searchSubject.flatMap {
            if (searchTYpe == TYPE_USER) {
                makeUserSearchSingle(
                    it.toLong(),
                    UserSearchResponse.PAGE_SIZE.toLong()
                ).doInBackground().toObservable()
            } else {
                makeRepoSearchSingle(
                    it.toLong(),
                    UserSearchResponse.PAGE_SIZE.toLong()
                ).doInBackground().toObservable()
            }

        }.subscribeOn(Schedulers.io())
            .map {
                ApiUtil.throwOnFailure(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ items ->
                if (searchTYpe == TYPE_USER) {
                    val objects = items as UserSearchResponse
                    currentPage++
                    curCount += objects.users.size
                    searchAdapter?.addAll(objects.users)
                    if (curCount == objects.totalCount) {
                        hasNextPage = false
                        viewModel.loadingStatus.setLoadingStatus(
                            if (objects.totalCount > 0) LoadingStatus.STATUS_SUCCESS_FULL
                            else LoadingStatus.STATUS_SUCCESS_EMPTY
                        )
                    }
                } else {
                    val objects = items as RepoSearchResponse
                    currentPage++
                    curCount += objects.repos.size
                    searchAdapter?.addAll(objects.repos)
                    if (curCount == objects.totalCount) {
                        hasNextPage = false
                        viewModel.loadingStatus.setLoadingStatus(
                            if (objects.totalCount > 0) LoadingStatus.STATUS_SUCCESS_FULL
                            else LoadingStatus.STATUS_SUCCESS_EMPTY
                        )
                    }
                }

            }, {
                viewModel.loadingStatus.setLoadingStatus(
                    LoadingStatus.STATUS_FAILED,
                    it.localizedMessage
                )
            }).addTo(co)
    }
}