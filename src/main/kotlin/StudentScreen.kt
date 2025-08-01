import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun StudentScreen(
    viewModel : IStudentViewModel
) {
    val newStudent by viewModel.newStudent
    val students = viewModel.students

    val newStudentFocusRequester = remember { FocusRequester() }
    val studentListFocusRequester = remember { FocusRequester() }

    val infoMessage by viewModel.infoMessage
    val showInfoMessage by viewModel.showInfoMessage

    val showScrollStudentListImage = remember {
        derivedStateOf { viewModel.shouldShowScrollStudentListImage() }
    }

    val selectedIndex by viewModel.selectedIndex

    /*
    LaunchedEffect(key1 = true) {  // key1 = true asegura que esto se ejecute solo una vez
        viewModel.loadStudents()
    }
    */

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxSize().weight(3f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AddNewStudent(
                newStudent = newStudent,
                focusRequester = newStudentFocusRequester,
                onNewStudentChange = { name -> viewModel.newStudentChange(name) },
                onButtonAddNewStudentClick = {
                    viewModel.addStudent()
                    newStudentFocusRequester.requestFocus()
                }
            )
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                StudentList(
                    studentsState = students,
                    selectedIndex = selectedIndex,
                    focusRequester = studentListFocusRequester,
                    onStudentSelected = { index -> viewModel.studentSelected(index) },
                    onIconDeleteStudentClick = { index -> viewModel.removeStudent(index) },
                    onButtonClearStudentsClick = { viewModel.clearStudents() }
                )
                ImageUpDownScroll(
                    showImgScrollStudentList = showScrollStudentListImage.value,
                )
            }
        }
        SaveChangesButton(
            modifier = Modifier.fillMaxSize().weight(1f),
            onButtonSaveChangesClick = { viewModel.saveStudents() }
        )
    }

    if (showInfoMessage) {
        InfoMessage(
            message = infoMessage,
            onDismiss = { viewModel.showInfoMessage(false) }
        )
    }

    // Solicitar el foco solo cuando cambia el tamaño de la lista
    LaunchedEffect(students.size) {
        newStudentFocusRequester.requestFocus()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddNewStudent(
    newStudent: String,
    focusRequester: FocusRequester,
    onNewStudentChange: (String) -> Unit,
    onButtonAddNewStudentClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(end = 20.dp)
            .onKeyEvent { event: KeyEvent ->
                if (event.key == Key.Enter) {
                    onButtonAddNewStudentClick()
                    true // Consumimos el evento
                } else {
                    false // No consumimos el evento
                }
            }
    ) {
        StudentTextField(
            newStudent = newStudent,
            focusRequester = focusRequester,
            onNewStudentChange = onNewStudentChange
        )
        AddStudentButton(
            onButtonAddNewStudentClick = onButtonAddNewStudentClick
        )
    }
}

@Composable
fun StudentTextField(
    newStudent: String,
    focusRequester: FocusRequester,
    onNewStudentChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .focusRequester(focusRequester),
        value = newStudent,
        onValueChange = onNewStudentChange,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically){
                Text(text = "New student name ")
                Text(
                    text = "(10 chars max.)",
                    style = TextStyle(fontStyle = FontStyle.Italic)
                )
            }
        },
        maxLines = 1,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = colorFocusComponentsBackground
        )
    )
}

@Composable
fun AddStudentButton(
    onButtonAddNewStudentClick: () -> Unit,
) {
    Button(
        modifier = Modifier.padding(15.dp),
        onClick = onButtonAddNewStudentClick,
    ) {
        Text(text = "Add new student")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StudentList(
    studentsState: List<String>,
    selectedIndex: Int,
    focusRequester: FocusRequester,
    onStudentSelected: (Int) -> Unit,
    onIconDeleteStudentClick: (Int) -> Unit,
    onButtonClearStudentsClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Students: ${studentsState.size}",
            modifier = Modifier.padding(bottom = 5.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight(0.78f)
                .width(240.dp)
                .background(colorFocusComponentsBackground)
                .border(2.dp, colorBorder)
                .padding(10.dp)
                .focusRequester(focusRequester)
                .focusable()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && selectedIndex >= 0) {
                        onStudentSelected(selectedIndex)
                    }
                }
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyUp) {
                        when (event.key) {
                            Key.DirectionUp -> {
                                if (selectedIndex > 0) {
                                    onStudentSelected(selectedIndex - 1)
                                    true
                                } else false
                            }

                            Key.DirectionDown -> {
                                if (selectedIndex < studentsState.size - 1) {
                                    onStudentSelected(selectedIndex + 1)
                                    true
                                } else false
                            }

                            else -> false
                        }
                    } else {
                        false//Solo manejar cuando la tecla se haya levantado de la presión
                    }
                }
        ) {
            items(studentsState.size) { index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onStudentSelected(index) }
                        .background(if (index == selectedIndex) colorSelected else colorUnselected)
                        .padding(horizontal = 5.dp)
                ) {
                    StudentText(
                        name = studentsState[index],
                        Modifier.weight(0.8f)
                    )
                    IconButton(
                        modifier = Modifier.weight(0.2f),
                        onClick = { onIconDeleteStudentClick(index) }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete student")
                    }
                }
            }
        }
        Button(
            onClick = onButtonClearStudentsClick
        ) {
            Text("Clear all")
        }
    }
}

@Composable
fun StudentText(name: String, modifier: Modifier) {
    Text(
        text = name,
        style = MaterialTheme.typography.h5,
        modifier = modifier
    )
}

@Composable
fun ImageUpDownScroll(
    showImgScrollStudentList: Boolean,
) {
    if (showImgScrollStudentList) {
        ImageWithTooltip(
            tooltipText = "Use scroll down-up",
            imagePath = "up_down_arrows.png",
            contentDesc = "Use scroll down-up",
            modifierImg = Modifier
                .padding(start = 5.dp, bottom = 50.dp)
                .width(20.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .padding(start = 5.dp, bottom = 50.dp)
                .size(20.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageWithTooltip(tooltipText: String, imagePath: String, contentDesc: String, modifierImg: Modifier) {
    TooltipArea(
        tooltip = {
            Box(
                modifier = Modifier
                    .background(colorTooltipBackground)
                    .border(1.dp, colorBorder)
            ) {
                Text(
                    text = tooltipText,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    ) {
        Image(
            painter = painterResource(imagePath),
            contentDescription = contentDesc,
            modifier = modifierImg
        )
    }
}

@Composable
fun SaveChangesButton(
    modifier: Modifier,
    onButtonSaveChangesClick: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onButtonSaveChangesClick
        ) {
            Text(text = "Save changes")
        }
    }
}

@Composable
fun InfoMessage(message: String, onDismiss: () -> Unit) {
    Dialog(
        icon = painterResource("info_icon.png"),
        title = "Atención",
        resizable = false,
        onCloseRequest = onDismiss
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(message)
        }
    }
}