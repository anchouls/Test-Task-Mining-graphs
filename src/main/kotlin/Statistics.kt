import java.lang.Integer.min

const val S = """\s*"""

fun getStatistic(code: String, name: String): String =
    getMethods(code).joinToString("\n") {
        """$name:${it.name}
        Access: ${it.access}
        Arguments: ${it.arguments}
        Return type: ${it.returnType.substring(min(1, it.returnType.length)).trim()}
        Vars: ${getVars(it.code)}
        If number: ${getIfNumber(it.code)}
        Switch number: ${getSwitchNumber(it.code)}
        For number: ${getForNumber(it.code)}
        Foreach number: ${getForeachNumber(it.code)}
        While number: ${getWhileNumber(it.code)}
        Method length: ${getLength(it.code)}
        Number of lines: ${getNumberLines(it.code)}
        Comment: ${it.comment}
    """
    }

fun getMethods(code: String): List<Function> {
    val accessModifiers = "public|private|protected"
    val args = """[\w,$\n\r\s]*"""
    val pattern = """($accessModifiers)?${S}function$S(\w+)$S\(($args)\)\s*(:\s*[\\\w]+)?[\n\s]*\{""".toRegex()
    return pattern.findAll(code).map {
        Function(
            name = it.groups[2]!!.value,
            access = it.groups[1]?.value ?: "",
            arguments = it.groups[3]?.value ?: "",
            returnType = it.groups[4]?.value ?: "",
            comment = getComment(code, it.range.first) ?: "",
            code = getBody(code, it.range.last + 1)
        )
    }.toList()
}

private fun getBody(code: String, startIndex: Int): String {
    var counter = 1
    var end = startIndex
    for (c in code.substring(startIndex)) {
        when (c) {
            '{' -> counter++
            '}' -> counter--
        }
        if (counter == 0) break
        end++
    }
    return code.substring(startIndex, end)
}

private fun getComment(code: String, endIndex: Int): String? {
    val pattern = """[\s\n]*/\*[\s\n]*(([^\n*]*\s*\n?)|([^\n*]*\*\s*\n?)*)\*\*/\s*\n""".toRegex()
    val reversedPrefix = code.substring(0, endIndex).reversed()
    return pattern.find(reversedPrefix)?.groups?.get(1)?.value?.reversed()
}

private fun getVars(code: String): Set<String> {
    val pattern = """[$](\w+)""".toRegex()
    return pattern.findAll(code).map { it.groupValues[1] }.toSet()
}

private fun getLength(code: String): Int = code.length

private fun getNumberLines(code: String): Int {
    val pattern = """\n""".toRegex()
    return pattern.findAll(code).count()
}

private fun getCallableNumber(main_pattern: String, code: String): Int {
    val pattern = """[\s;{]${main_pattern}\s*\(""".toRegex()
    return pattern.findAll(code).count()
}

private fun getIfNumber(code: String): Int = getCallableNumber("if", code)

private fun getForNumber(code: String): Int = getCallableNumber("for", code)

private fun getForeachNumber(code: String): Int = getCallableNumber("foreach", code)

private fun getWhileNumber(code: String): Int = getCallableNumber("while", code)

private fun getSwitchNumber(code: String): Int = getCallableNumber("switch", code)
