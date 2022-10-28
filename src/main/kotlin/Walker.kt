import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun files(path: Path): List<File> = path.toFile().walkTopDown().filter { it.extension == "php" && it.isFile }.toList()

fun projects(path: Path): List<Path> = Files.list(path).filter { Files.isDirectory(it) }.toList()
