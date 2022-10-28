import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {
    val path = Paths.get(args[0])
    File("result.txt").printWriter().use { out ->
        projects(path).forEach {
            files(it).forEach { it1 ->
                out.println(getStatistic(it1.readLines().joinToString("\n"), it1.toString()))
            }
        }
    }
}
