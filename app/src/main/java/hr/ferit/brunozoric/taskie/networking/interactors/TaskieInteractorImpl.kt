package hr.ferit.brunozoric.taskie.networking.interactors

import hr.ferit.brunozoric.taskie.model.BackendTask
import hr.ferit.brunozoric.taskie.model.request.*
import hr.ferit.brunozoric.taskie.model.response.*
import hr.ferit.brunozoric.taskie.networking.TaskieApiService
import retrofit2.Callback

class TaskieInteractorImpl(private val apiService: TaskieApiService) : TaskieInteractor {

    override fun getTasks(taskieResponseCallback: Callback<GetTasksResponse>) {
        apiService.getTasks().enqueue(taskieResponseCallback)
    }

    override fun register(request: UserDataRequest, registerCallback: Callback<RegisterResponse>) {
        apiService.register(request).enqueue(registerCallback)
    }

    override fun login(request: UserDataRequest, loginCallback: Callback<LoginResponse>) {
        apiService.login(request).enqueue(loginCallback)
    }

    override fun save(request: AddTaskRequest, saveCallback: Callback<BackendTask>) {
        apiService.save(request).enqueue(saveCallback)
    }

    override fun getNote(request: GetTaskRequest, getNoteCallback: Callback<BackendTask>) {
        apiService.getNote(request.id).enqueue(getNoteCallback)
    }

    override fun deleteNote(request: DeleteTaskRequest, deleteNoteCallback: Callback<DeleteTaskResponse>) {
        apiService.deleteNote(request.noteId).enqueue(deleteNoteCallback)
    }

    override fun editNote(request: EditTaskRequest, editNoteCallback: Callback<EditTaskResponse>) {
        apiService.editNote(request).enqueue(editNoteCallback)
    }
}