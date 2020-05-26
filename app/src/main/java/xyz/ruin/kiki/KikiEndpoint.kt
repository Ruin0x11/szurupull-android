package xyz.ruin.kiki

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import xyz.ruin.kiki.model.Upload

interface KikiEndpoint {
    @GET("posts.json")
    fun getPosts(
        @Query("tags") tags: String?,
        @Query("limit") limit: Int?
    ): Observable<List<Upload>>

}