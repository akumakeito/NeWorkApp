package ru.netology.neworkapp.apiservice

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.neworkapp.auth.AuthState
import ru.netology.neworkapp.dto.*

interface ApiService {
    //posts
    @GET("posts/latest")
    suspend fun getLatestPosts(@Query("count") count: Int): Response<List<Post>>

    @GET("posts/{id}/before")
    suspend fun getBeforePosts(
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getAfterPosts(
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("posts")
    suspend fun getPosts() : Response<List<Post>>

    @POST("posts")
    suspend fun savePost(@Body post: Post) : Response<Post>

    @GET("posts/{post_id}")
    suspend fun getPostById(@Path("post_id") id : Int) : Response<Post>

    @DELETE("posts/{post_id}")
    suspend fun removePostById(@Path("post_id") postId : Int) : Response<Unit>

    @POST("posts/{post_id}/likes")
    suspend fun likeById(@Path("post_id") id : Int) : Response<Post>

    @DELETE("posts/{post_id}/likes")
    suspend fun unlikeById(@Path("post_id") id : Int) : Response<Post>



    //users
    @GET("users")
    suspend fun getUsers() : Response<List<User>>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun authenticateUser(@Field("login") login : String, @Field("password") password : String) : Response<AuthState>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(@Field("login") login : String, @Field("password") password : String, @Field("name") name : String) : Response<AuthState>

    @GET("users/{user_id}")
    suspend fun getUserById(@Path("user_id") userId : Int) : Response<User>

    @Multipart
    @POST("users/registration")
    suspend fun registerWithPhoto(
        @Part("login") login: RequestBody,
        @Part("password") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part file: MultipartBody.Part?,
    ): Response<AuthState>


    //jobs
    @GET("my/jobs")
    suspend fun getMyJobs() : Response<List<Job>>

    @POST("my/jobs")
    suspend fun saveJob(@Body job : Job) : Response<Job>

    @DELETE("my/jobs/{job_id}")
    suspend fun removeJobById(@Path("job_id") jobId : Int) : Response<Unit>

    @GET("{user_id}/jobs")
    suspend fun getJobsByUserId(@Path("user_id") userId : Int) : Response<List<Job>>



    //events
    @GET("events/latest")
    suspend fun getLatestEvents(@Query("count") count: Int): Response<List<Event>>

    @GET("events/{id}/before")
    suspend fun getBeforeEvents(
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Event>>

    @GET("events/{id}/after")
    suspend fun getAfterEvents(
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Event>>

    @DELETE("events/{id}")
    suspend fun removeEventById(@Path("id") id: Int): Response<Unit>

    @POST("events/{id}/likes")
    suspend fun likeEventById(@Path("id") id: Int): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun dislikeEventById(@Path("id") id: Int): Response<Event>

    @POST("events")
    suspend fun saveEvent(@Body event: Event): Response<Event>

    @POST("events")
    suspend fun addEvent(@Body post: EventRequest): Response<Event>

    @GET("events")
    suspend fun getEvents() : Response<List<Event>>

    @GET("events/{event_id}")
    suspend fun getEvent(@Path("event_id") id: Int): Response<Event>


    //media
    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part media : MultipartBody.Part) : Response<Media>
}