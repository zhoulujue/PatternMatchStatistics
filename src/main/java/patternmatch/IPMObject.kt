package patternmatch

/**
 * 用来匹配的元素
 * 匹配的规则使用 pattern 来描述，规则可以组合，组合方式分为两种：
 * PatternGroup (Pattern的或组合)
 * PatternQueue (Pattern的且组合并要求出现顺序)
 */
interface IPMObject {
    /**
     * 用于匹配的元素必须返回一个字符串来唯一标识自己
     * 如果有两个或多个元素他们的这个方法返回的值是相等的，那么它们在匹配的时候，将会被看作是相同的
     * @return 唯一的标识元素的字符串
     */
    fun hashStringForPatternMatch(): String
}