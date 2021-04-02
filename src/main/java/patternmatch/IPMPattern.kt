package patternmatch

/**
 * 用于描述匹配规则的最小单元
 */
interface IPMPattern {
    /**
     * 匹配规则需要将规则转化为一个符合正则表达式的字符串
     * 这个字符串在匹配过程可能会在 PatternGroup 和 PatternQueue 中再次处理
     * @return 描述本匹配规则的字符串
     */
    fun getStringForPatternMatch(): String
}