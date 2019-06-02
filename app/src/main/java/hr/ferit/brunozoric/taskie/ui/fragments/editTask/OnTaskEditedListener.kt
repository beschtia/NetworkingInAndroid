package hr.ferit.brunozoric.taskie.ui.fragments.editTask

import hr.ferit.brunozoric.taskie.model.BackendTask

interface OnTaskEditedListener {
    fun onTaskEdited(task: BackendTask)
}