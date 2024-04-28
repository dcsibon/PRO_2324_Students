import java.sql.Connection
import java.sql.SQLException

class StudentRepository {

    fun getAllStudents(): Result<List<String>> {
        return try {
            val connectionDb = Database.getConnection()
            val students = mutableListOf<String>()
            connectionDb.use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeQuery("SELECT name FROM students").use { rs ->
                        while (rs.next()) {
                            students.add(rs.getString("name"))
                        }
                    }
                }
            }
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateStudents(students: List<String>): Result<Unit> {
        var connectionDb : Connection? = null
        return try {
            connectionDb = Database.getConnection()
            connectionDb.autoCommit = false
            connectionDb.createStatement().use { stmt ->
                stmt.execute("DELETE FROM students")
            }
            connectionDb.prepareStatement("INSERT INTO students (name) VALUES (?)").use { ps ->
                for (student in students) {
                    ps.setString(1, student)
                    ps.executeUpdate()
                }
            }
            connectionDb.commit()
            Result.success(Unit)
        } catch (e: Exception) {
            connectionDb?.rollback()
            Result.failure(e)
        } finally {
            connectionDb?.autoCommit = true
            connectionDb?.close()
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
