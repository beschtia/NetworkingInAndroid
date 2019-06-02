package hr.ferit.brunozoric.taskie.networking

import hr.ferit.brunozoric.taskie.model.BackendTask
import hr.ferit.brunozoric.taskie.model.request.*
import hr.ferit.brunozoric.taskie.model.response.*
import retrofit2.Call
import retrofit2.http.*


interface TaskieApiService {

    @POST("/api/register")
    fun register(@Body userData: UserDataRequest): Call<RegisterResponse>

    @POST("/api/login")
    fun login(@Body userData: UserDataRequest): Call<LoginResponse>

    @GET("/api/note")
    fun getTasks(): Call<GetTasksResponse>

    @POST("/api/note")
    fun save(@Body taskData: AddTaskRequest): Call<BackendTask>

    @GET("/api/note/{id}")
    fun getNote(@Path("id") id:String): Call<BackendTask>

    @POST("api/note/delete")
    fun deleteNote(@Query("id") id:String): Call<DeleteTaskResponse>

    @POST("/api/note/edit")
    fun editNote(@Body taskData: EditTaskRequest): Call<EditTaskResponse>
}