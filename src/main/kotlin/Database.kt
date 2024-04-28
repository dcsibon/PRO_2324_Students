import java.sql.Connection
import java.sql.DriverManager

object Database {
    private const val url = "jdbc:mysql://localhost:3306/studentdb"
    private const val user = "studentuser"
    private const val password = "password"

    fun getConnection(): Connection {
        return DriverManager.getConnection(url, user, password)
    }
}