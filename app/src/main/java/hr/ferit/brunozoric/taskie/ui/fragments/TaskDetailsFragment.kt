package hr.ferit.brunozoric.taskie.ui.fragments

import android.os.Bundle
import android.view.View
import hr.ferit.brunozoric.taskie.R
import hr.ferit.brunozoric.taskie.common.EXTRA_TASK_ID
import hr.ferit.brunozoric.taskie.common.RESPONSE_OK
import hr.ferit.brunozoric.taskie.common.displayToast
import hr.ferit.brunozoric.taskie.model.BackendTask
import hr.ferit.brunozoric.taskie.model.PriorityColor
import hr.ferit.brunozoric.taskie.model.request.GetTaskRequest
import hr.ferit.brunozoric.taskie.networking.BackendFactory
import hr.ferit.brunozoric.taskie.ui.fragments.base.BaseFragment
import hr.ferit.brunozoric.taskie.ui.fragments.editTask.EditContentFragmentDialog
import hr.ferit.brunozoric.taskie.ui.fragments.editTask.EditPriorityFragmentDialog
import hr.ferit.brunozoric.taskie.ui.fragments.editTask.EditTitleFragmentDialog
import hr.ferit.brunozoric.taskie.ui.fragments.editTask.OnTaskEditedListener
import kotlinx.android.synthetic.main.fragment_task_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskDetailsFragment : BaseFragment(), OnTaskEditedListener {

    private val interactor = BackendFactory.getTaskieInteractor()
    private var taskID = NO_TASK
    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_task_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(EXTRA_TASK_ID)?.let { taskID = it }
       tryDisplayTask(taskID)

    }

    override fun onTaskEdited(task: BackendTask) {
        tryDisplayTask(task.id)
    }

    private fun tryDisplayTask(id: String) {
        try {
            interactor.getNote(
                GetTaskRequest(id = id),
                getNoteCallback()
            )

        } catch (e: NoSuchElementException) {
            context?.displayToast(getString(R.string.noTaskFound))
        }
    }

    private fun getNoteCallback(): Callback<BackendTask> = object : Callback<BackendTask>{
        override fun onFailure(call: Call<BackendTask>, t: Throwable) {
            //TODO : handle default error 400 , 404, 500
        }

        override fun onResponse(call: Call<BackendTask>, response: Response<BackendTask>) {
            if (response.isSuccessful) {
                when (response.code()) {
                    RESPONSE_OK -> handleOkResponse(response.body())
                    else -> handleSomethingWentWrong()
                }
            }
        }

    }

    private fun handleOkResponse(body: BackendTask?) {
        body?.run { displayTask(this) }

    }

    private fun handleSomethingWentWrong() = this.activity?.displayToast("Something went wrong!")

    private fun displayTask(task: BackendTask) {
        detailsTaskTitle.text = task.title
        detailsTaskTitle.setOnClickListener{onTitleClick(task)}

        detailsTaskDescription.text = task.content
        detailsTaskDescription.setOnClickListener { onContentClick(task) }

        val priorityColor = when (task.taskPriority) {
            1 -> PriorityColor.LOW
            2 -> PriorityColor.MEDIUM
            else -> PriorityColor.HIGH
        }
        detailsPriorityView.setBackgroundResource(priorityColor.getColor())
        detailsPriorityView.setOnClickListener { onPriorityClick(task) }
    }

    private fun onTitleClick(task: BackendTask) {
        val dialog = EditTitleFragmentDialog.newInstance()
        dialog.task = task
        dialog.setOnTaskEditedListener(this)
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun onContentClick(task: BackendTask) {
        val dialog = EditContentFragmentDialog.newInstance()
        dialog.task = task
        dialog.setOnTaskEditedListener(this)
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun onPriorityClick(task: BackendTask) {
        val dialog = EditPriorityFragmentDialog.newInstance()
        dialog.task = task
        dialog.setOnTaskEditedListener(this)
        dialog.show(childFragmentManager, dialog.tag)
    }

    companion object {
        const val NO_TASK = ""

        fun newInstance(taskId: String): TaskDetailsFragment {
            val bundle = Bundle().apply { putString(EXTRA_TASK_ID, taskId) }
            return TaskDetailsFragment().apply { arguments = bundle }
        }
    }
}
