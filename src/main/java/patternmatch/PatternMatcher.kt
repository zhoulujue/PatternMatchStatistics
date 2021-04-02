package patternmatch

class PatternMatcher {
    private lateinit var mRegex: java.util.regex.Pattern

    companion object {
        /**
         * 将匹配规则转化为 matcher
         * @param pattern 匹配规则
         */
        fun create(pattern: IPMPattern): PatternMatcher {
            val matcher = PatternMatcher()
            matcher.mRegex = java.util.regex.Pattern.compile(pattern.getStringForPatternMatch())
            return matcher
        }

    }

    /**
     * 在指定范围内找到首个满足匹配规则的对象(PMObject)，返回结果用 PMRange 表述
     * @param pmObjects 待匹配的队列
     * @param findInThisObjectRange 指定的查找范围, 这里范围描述的是 PMObject，并不是 PMObject 的 hashString 的范围
     */
    fun rangeOfFirstMatchInPMObjects(pmObjects: List<IPMObject>, findInThisObjectRange: PMRange): PMRange {
        val source = PMObjectSource.create(pmObjects) ?: return PMRange(PMConstant.PM_NOT_FOUND, -1)
        val eventsString = source.stringForSource()
        val findInThisStringRange = source.stringRangeFromObjectRange(findInThisObjectRange)
        var javaRegexMatcher = mRegex.matcher(eventsString)

        try {
            javaRegexMatcher = javaRegexMatcher.region(findInThisStringRange.location, findInThisStringRange.location + findInThisStringRange.length)
        } catch (t: Throwable) {
            t.printStackTrace()
            return PMRange(PMConstant.PM_NOT_FOUND, 0)
        }

        var startLocation = PMConstant.PM_NOT_FOUND
        var endLocation = 0
        if (javaRegexMatcher.find()) {
            startLocation = javaRegexMatcher.start()
            endLocation = javaRegexMatcher.end()
        }

        val resultStrRange = PMRange(startLocation, endLocation - startLocation)

        return source.objectRangeFromStringRange(resultStrRange)
    }
}