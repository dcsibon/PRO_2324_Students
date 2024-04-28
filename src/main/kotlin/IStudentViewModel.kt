import androidx.compose.runtime.*

interface IStudentViewModel {
    val newStudent: State<String>
    val students: List<String>

    val infoMessage: State<String>
    val showInfoMessage: State<Boolean>
    val selectedIndex: State<Int>

    fun addStudent()
    fun removeStudent(index: Int)
    fun loadStudents()
    fun saveStudents()
    fun clearStudents()
    fun shouldShowScrollStudentListImage(): Boolean
    fun newStudentChange(name: String)
    fun studentSelected(index: Int)
    fun updateInfoMessage(message: String)
    fun showInfoMessage(show: Boolean)
}