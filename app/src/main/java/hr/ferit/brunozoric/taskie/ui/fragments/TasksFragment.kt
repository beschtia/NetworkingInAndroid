package hr.ferit.brunozoric.taskie.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.ferit.brunozoric.taskie.R
import hr.ferit.brunozoric.taskie.common.*
import hr.ferit.brunozoric.taskie.model.BackendTask
import hr.ferit.brunozoric.taskie.model.request.DeleteTaskRequest
import hr.ferit.brunozoric.taskie.model.response.DeleteTaskResponse
import hr.ferit.brunozoric.taskie.model.response.GetTasksResponse
import hr.ferit.brunozoric.taskie.networking.BackendFactory
import hr.ferit.brunozoric.taskie.ui.SwipeToDeleteCallback
import hr.ferit.brunozoric.taskie.ui.activities.ContainerActivity
import hr.ferit.brunozoric.taskie.ui.adapters.TaskAdapter
import hr.ferit.brunozoric.taskie.ui.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_tasks.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TasksFragment : BaseFragment(), AddTaskFragmentDialog.TaskAddedListener {

    private val adapter by lazy { TaskAdapter { onItemSelected(it) } }
    private val interactor = BackendFactory.getTaskieInteractor()

    companion object {
        fun newInstance(): Fragment {
            return TasksFragment()
        }
    }

    override fun getLayoutResourceId() = R.layout.fragment_tasks

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initListeners()
    }

    override fun onResume() {
        super.onResume()
        getAllTasks()
    }

    private fun initUi() {
        progress.visible()
        noData.visible()
        tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        tasksRecyclerView.adapter = adapter
        val swipeHandler = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                showDeleteTaskAlertDialog(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)
        getAllTasks()
    }

    private fun showDeleteTaskAlertDialog(position: Int){
        AlertDialog.Builder(activity as Context)
            .setTitle("Delete Task")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("yes") { _, _ ->
                interactor.deleteNote(
                    DeleteTaskRequest(noteId = adapter.getTaskID(position)),
                    deleteNoteCallback()
                )
                adapter.removeAt(position) }
            .setNegativeButton("no") { _, _ -> getAllTasks()}
            .show()
    }

    private fun deleteNoteCallback(): Callback<DeleteTaskResponse> = object : Callback<DeleteTaskResponse>{
        override fun onFailure(call: Call<DeleteTaskResponse>, t: Throwable) {
            //TODO : handle default error 400 , 404, 500
        }

        override fun onResponse(call: Call<DeleteTaskResponse>, response: Response<DeleteTaskResponse>) {
            if (response.isSuccessful) {
                when (response.code()) {
                    RESPONSE_OK -> handleDeletOkResponse(response.body())
                    else -> handleSomethingWentWrong()
                }
            }
        }

    }

    private fun handleDeletOkResponse(deleteTaskResponse: DeleteTaskResponse?) {
        deleteTaskResponse?.message?.let { this.activity?.displayToast(it) }
        getAllTasks()
    }

    private fun initListeners() {
        addTask.setOnClickListener { addTask() }
        swipeContainer.setOnRefreshListener { refresh() }
    }

    private fun refresh() {
        getAllTasks()
    }

    private fun onItemSelected(task: BackendTask) {
        val detailsIntent = Intent(context, ContainerActivity::class.java).apply {
            putExtra(EXTRA_SCREEN_TYPE, ContainerActivity.SCREEN_TASK_DETAILS)
            putExtra(EXTRA_TASK_ID, task.id)
        }
        startActivity(detailsIntent)
    }

    override fun onTaskAdded(task: BackendTask) {
        adapter.addData(task)
    }

    private fun addTask() {
        val dialog = AddTaskFragmentDialog.newInstance()
        dialog.setTaskAddedListener(this)
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun getAllTasks() {
        progress.visible()
        interactor.getTasks(getTaskieCallback())
    }

    private fun getTaskieCallback(): Callback<GetTasksResponse> = object : Callback<GetTasksResponse> {
        override fun onFailure(call: Call<GetTasksResponse>?, t: Throwable?) {
            progress.gone()
            swipeContainer.isRefreshing = false
            //TODO : handle default error
        }

        override fun onResponse(call: Call<GetTasksResponse>?, response: Response<GetTasksResponse>) {
            progress.gone()
            noData.gone()
            if (response.isSuccessful) {
                when (response.code()) {
                    RESPONSE_OK -> handleOkResponse(response)
                    else -> handleSomethingWentWrong()
                }
            }
        }
    }

    private fun handleOkResponse(response: Response<GetTasksResponse>) {
        response.body()?.notes?.run {
            checkList(this)
            adapter.setData(this)
            swipeContainer.isRefreshing = false
        }
    }

    private fun handleSomethingWentWrong() = this.activity?.displayToast("Something went wrong!")

    private fun checkList(notes: MutableList<BackendTask>) {
        if (notes.isEmpty()) {
            noData.visible()
        } else {
            noData.gone()
        }
    }

    private fun onTaskiesReceived(taskies: MutableList<BackendTask>) = adapter.setData(taskies)

}