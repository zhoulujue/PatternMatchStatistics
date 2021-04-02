package patternmatch

import java.lang.StringBuilder

/**
 * 用正则表达式实现的 PatternMatch 的一个 source
 */
class PMObjectSource {
    private lateinit var mPMObjects: List<IPMObject>
    private val mPMObjectRanges: List<PMRange> by lazy {
        val ranges = ArrayList<PMRange>(mPMObjects.size)
        var location = 0

        for (pmObject in mPMObjects) {
            val length = pmObject.hashStringForPatternMatch().length
            val range = PMRange(location, length)
            ranges.add(range)
            location += length
        }
        ranges
    }

    companion object {
        fun create(objs: List<IPMObject>): PMObjectSource? {
            if (objs.isEmpty()) {
                return null
            }
            val objectSource = PMObjectSource()
            objectSource.mPMObjects = objs
            return objectSource
        }
    }

    /**
     * 返回所有的 PMObjects 代表的匹配正则字符串
     */
    fun stringForSource(): String {
        val sb = StringBuilder()
        for (obj in mPMObjects) {
            sb.append(obj.hashStringForPatternMatch())
        }
        return sb.toString()
    }

    /**
     * 把正则表达式的匹配结果，转化为 PMObject 的匹配结果
     * @param strRange 字符串正则匹配的结果，即匹配到的位置
     */
    fun objectRangeFromStringRange(strRange: PMRange): PMRange {
        if (strRange.location == PMConstant.PM_NOT_FOUND) {
            return PMRange(PMConstant.PM_NOT_FOUND, 0)
        }
        var objLocation = PMConstant.PM_NOT_FOUND
        var objLength = 0

        for (r in mPMObjectRanges) {
            // 找到开头，记录 objLocation
            if (objLocation == PMConstant.PM_NOT_FOUND && strRange.location <= r.location) {
                objLocation = mPMObjectRanges.indexOf(r)
            }
            // 开头找到了，再继续往后寻找
            if(objLocation != PMConstant.PM_NOT_FOUND) {
                if ((strRange.location + strRange.length) >= (r.location + r.length)) {
                    objLength += 1
                } else {
                    break
                }
            }
        }
        return PMRange(objLocation, objLength)
    }

    fun stringRangeFromObjectRange(range: PMRange): PMRange {
        if (range.location == PMConstant.PM_NOT_FOUND) {
            return PMRange(PMConstant.PM_NOT_FOUND, 0)
        }
        // 找到头和尾，union 一下得到结果
        val headRange = mPMObjectRanges[range.location]
        if (range.length == 0) {
            return PMRange(headRange.location, 0)
        }
        val tailRange = mPMObjectRanges[range.location + range.length - 1]
        return PMRange.unionRange(headRange, tailRange)
    }
}
