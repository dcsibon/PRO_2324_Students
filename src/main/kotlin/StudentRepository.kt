import java.sql.SQLException

class StudentRepository {

    fun getAllStudents(): List<String> {
        val students = mutableListOf<String>()
        Database.getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery("SELECT name FROM students").use { rs ->
                    while (rs.next()) {
                        students.add(rs.getString("name"))
                    }
                }
            }
        }
        return students
    }

    fun updateStudents(students: List<String>) {
        val connection = Database.getConnection()
        try {
            connection.autoCommit = false  // Start transaction
            connection.createStatement().use { stmt ->
                stmt.execute("DELETE FROM students")  // Clear the existing data

                val ps = connection.prepareStatement("INSERT INTO students (name) VALUES (?)")
                for (student in students) {
                    ps.setString(1, student)
                    ps.executeUpdate()
                }
                ps.close()
            }
            connection.commit()  // Commit transaction
        } catch (e: Exception) {
            connection.rollback()  // Rollback transaction on error
            e.printStackTrace()
        } finally {
            try {
                connection.autoCommit = true
                connection.close()
            } catch (se: SQLException) {
                se.printStackTrace()
            }
        }
    }

    /*fun addStudent(name: String) {
        val connection = Database.getConnection()
        try {
            val preparedStatement = connection.prepareStatement("INSERT INTO students (name) VALUES (?)")
            preparedStatement.setString(1, name)
            preparedStatement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()  // Log the exception
        } finally {
            try {
                connection.close()
            } catch (se: SQLException) {
                se.printStackTrace()  // Log closing exception
            }
        }
    }

    fun getAllStudents(): List<String> {
        val students = mutableListOf<String>()
        val connection = Database.getConnection()
        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT name FROM students")
            while (resultSet.next()) {
                students.add(resultSet.getString("name"))
            }
        } catch (e: Exception) {
            e.printStackTrace()  // Log the exception
        } finally {
            try {
                connection.close()
            } catch (se: SQLException) {
                se.printStackTrace()  // Log closing exception
            }
        }
        return students
    }*/
}
