package patternmatch

class PMRange(
    var location: Int = 0,
    var length: Int = 0,
): Cloneable {
    constructor(rangeStr: String) : this() {
        // 匹配一下类似 "{3, 5}" 这样的 range 表述方式
        // 举例：{3, 5} => location=3, length=3
        val pattern: java.util.regex.Pattern = java.util.regex.Pattern.compile("\\{(\\d+),\\s*(\\d+)}")
        val matcher: java.util.regex.Matcher = pattern.matcher(rangeStr)
        this.location = matcher.group(0)?.toInt() ?: -1
        this.length = matcher.group(1)?.toInt() ?: -1
    }

    companion object {
        fun unionRange(range1: PMRange, range2: PMRange) = range1.rangeByMergingRange(range2)
    }

    override fun clone(): Any {
        val cloned = super.clone() as PMRange

        cloned.location = this.location
        cloned.length = this.length

        return cloned
    }

    override fun hashCode(): Int = (122 * length) - location


    override fun equals(other: Any?): Boolean {
        if (other !is PMRange) {
            return false
        }
        return (location == other.location) && (length == other.length)
    }

    /**
     * location 是否落入了给定的 range
     */
    fun locationInRange(loc: Int): Boolean {
        return (loc >= location) && (loc < (location + length))
    }

    fun maxRange():Int = location + length

    /**
     * 判断两个 range 是否有交集
     */
    fun intersectsRange(range: PMRange): Boolean {
        return (location + length) >= range.location && location <= (range.location + range.length)
    }

    /**
     * 判断两个 range 是否是包含关系
     */
    fun isSubrangeOfRange(range: PMRange): Boolean {
        return location >= range.location && (location + length) < (range.location + range.length)
    }

    /**
     * 返回两个range的交集
     */
    fun rangeByIntersectingRange(range: PMRange): PMRange {
        return (this.clone() as PMRange).also{ intersectRange(range) }
    }

    fun rangeByMergingRange(range: PMRange): PMRange {
        val output = this.clone() as PMRange
        output.unionRange(range)
        return output
    }

    fun intersectRange(range: PMRange) {
        val newLocation = if (this.location > range.location) this.location else range.location

        val maxRange1 = this.location + this.length
        val maxRange2 = range.location + range.length

        val newLength = if (maxRange1 < maxRange2) {
            maxRange1 - this.location
        } else {
            maxRange2 - range.location
        }

        this.location = newLocation
        this.length = newLength
    }


    /**
     * 仿造 Google 的 Guava Range 写的简单的并集算法，他的这种搞法是一种不会出现空集的并集
     * 参考自 http://grepcode.com/file/repo1.maven.org/maven2/com.google.guava/guava/18.0/com/google/common/collect/Range.java#Range
     * Returns the minimal range that encloses both this range and other. For example, the span of [1..3] and (5..7) is [1..7).
     * If the input ranges are connected, the returned range can also be called their union. If they are not, note that the span might contain values that are not contained in either input range.
     * Like intersection, this operation is commutative, associative and idempotent. Unlike it, it is always well-defined for any two input ranges.
     * @param range
     */
    fun unionRange(range: PMRange) {
        val newLocation = if (location < range.location) location else range.location

        val maxRange1 = this.location + this.length
        val maxRange2 = range.location + range.length

        val newLength = if (maxRange1 > maxRange2) {
            maxRange1 - this.location
        } else {
            maxRange2 - this.location
        }

        this.location = newLocation
        this.length = newLength
    }

    fun isEmpty(): Boolean {
        return length == 0
    }

    override fun toString(): String {
        return "{location=${location}; length=${length}}"
    }

}