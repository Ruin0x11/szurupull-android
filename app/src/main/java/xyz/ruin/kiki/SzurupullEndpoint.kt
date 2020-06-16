package xyz.ruin.kiki

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import xyz.ruin.kiki.model.Link
import xyz.ruin.kiki.model.Upload

interface SzurupullEndpoint {
    @GET("api/uploads/extract")
    fun extract(
        @Query("url") url: String
    ): Observable<List<Upload>>

    @POST("api/uploads")
    fun createUpload(@Body link: Link) : Observable<Upload>
}