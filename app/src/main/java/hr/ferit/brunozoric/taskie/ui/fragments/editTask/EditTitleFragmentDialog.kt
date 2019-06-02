package hr.ferit.brunozoric.taskie.ui.fragments.editTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import hr.ferit.brunozoric.taskie.R
import hr.ferit.brunozoric.taskie.common.RESPONSE_OK
import hr.ferit.brunozoric.taskie.common.displayToast
import hr.ferit.brunozoric.taskie.model.BackendTask
import hr.ferit.brunozoric.taskie.model.request.EditTaskRequest
import hr.ferit.brunozoric.taskie.model.response.EditTaskResponse
import hr.ferit.brunozoric.taskie.networking.BackendFactory
import kotlinx.android.synthetic.main.fragment_edit_title.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditTitleFragmentDialog: DialogFragment() {

    private val interactor = BackendFactory.getTaskieInteractor()
    private var onTaskEditedListener: OnTaskEditedListener? = null
    lateinit var task: BackendTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FragmentDialogTheme)
    }

    fun setOnTaskEditedListener(listener: OnTaskEditedListener){
      onTaskEditedListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_title, container)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initUi()
    }

    private fun initListeners() {
        saveEditAction.setOnClickListener {
            interactor.editNote(
                EditTaskRequest(
                    id = task.id,
                    title = editTaskInput.text.toString(),
                    content = task.content,
                    taskPriority = task.taskPriority),
                editNoteCallback()
            )
        }
    }

    private fun editNoteCallback(): Callback<EditTaskResponse> = object : Callback<EditTaskResponse> {
        override fun onFailure(call: Call<EditTaskResponse>, t: Throwable) {
            //TODO : handle default error 400 , 404, 500
        }

        override fun onResponse(call: Call<EditTaskResponse>, response: Response<EditTaskResponse>) {
            if (response.isSuccessful) {
                when (response.code()) {
                    RESPONSE_OK -> handleOkResponse(response.body())
                    else -> handleSomethingWentWrong()
                }
            }
        }
    }

    private fun handleSomethingWentWrong() {
        this.activity?.displayToast("Something went wrong!")
    }

    private fun handleOkResponse(response: EditTaskResponse?) {
        response?.message?.let { this.activity?.displayToast(it) }
        onTaskEditedListener?.onTaskEdited(task)
        dismiss()
    }

    private fun initUi() {
        editTaskHeading.text = getString(R.string.edit_title)
        editTaskInput.setText(task.title)
    }

    companion object{
        fun newInstance(): EditTitleFragmentDialog {
            return EditTitleFragmentDialog()
        }
    }
}