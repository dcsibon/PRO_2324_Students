import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import java.awt.Toolkit
import java.io.File

fun main() = application {
/*
    val url = "jdbc:mysql://localhost:3306/studentdb"
    val usuario = "studentuser"
    val passw = "password"

    var texto = ""

    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
        val conexion = DriverManager.getConnection(url, usuario, passw)
        texto = "Conexión exitosa"
        conexion.close()
    } catch (e: SQLException) {
        texto = "Error en la conexión: ${e.message}"
    } catch (e: ClassNotFoundException) {
        texto = "No se encontró el driver JDBC: ${e.message}"
    }
*/
    val icon = painterResource("sample.png")
    val windowState = GetWindowState(
        windowWidth = 800.dp,
        windowHeight = 800.dp
    )

    MainWindowStudents(
        title = "My Students",
        icon = icon,
        windowState = windowState,
        resizable = false,
        onCloseMainWindow = { exitApplication() }
    )
}

@Composable
fun GetWindowState(
    windowWidth: Dp,
    windowHeight: Dp,
): WindowState {
    // Obtener las dimensiones de la pantalla
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val screenWidth = screenSize.width
    val screenHeight = screenSize.height

    // Calcular la posición para centrar la ventana
    val positionX = (screenWidth / 2 - windowWidth.value.toInt() / 2)
    val positionY = (screenHeight / 2 - windowHeight.value.toInt() / 2)

    return rememberWindowState(
        size = DpSize(windowWidth, windowHeight),
        position = WindowPosition(positionX.dp, positionY.dp)
    )
}

@Composable
fun MainWindowStudents(
    title: String,
    icon: Painter,
    windowState: WindowState,
    resizable: Boolean,
    onCloseMainWindow: () -> Unit,
) {
    Window(
        onCloseRequest = onCloseMainWindow,
        title = title,
        icon = icon,
        resizable = resizable,
        state = windowState
    ) {

        val fileManagement = FileManagement()
        val studentsFile = File("studentList.txt")
        val studentViewModelFile = StudentViewModelFile(fileManagement, studentsFile)

        val studentRepository = StudentRepository()
        val studentViewModelDb = StudentViewModelDb(studentRepository)

        MaterialTheme {
            Surface(
                color = colorWindowBackground,
                modifier = Modifier.fillMaxSize()
            ) {
                //StudentScreen(studentsViewModelFile)
                StudentScreen(studentViewModelDb)
            }
        }
    }
}