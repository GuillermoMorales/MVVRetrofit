package com.petrlr14.mvvm.database.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.petrlr14.mvvm.database.daos.GitHubDAO
import com.petrlr14.mvvm.database.entities.GitHubRepo
import com.petrlr14.mvvm.services.retrofit.GithubService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Response

class GitHubRepoRepository (private val repoDao:GitHubDAO){

    //fun retrieveReposAsync(user:String):Deferred<Response<List<GitHubRepo>>>=GithubService.getGithubServices().getAllReposPerUser(user)

    fun retrieveRepos(user:String) = GlobalScope.launch(Dispatchers.IO) {
        this@GitHubRepoRepository.nuke()
        val response = GithubService.getGithubServices().getAllReposPerUser().await()

        if(response.isSuccesful) with(response){
            this.body()?.forEach{
                this@GitHubRepoRepository.insert(it)
            }
    }


    @WorkerThread
    suspend fun insert(repo:GitHubRepo){
        repoDao.insert(repo)
    }

    fun getAll():LiveData<List<GitHubRepo>>{
        return repoDao.getAllRepos()
    }

    @WorkerThread
    suspend fun nuke(){
        return repoDao.nukeTable()
    }

}