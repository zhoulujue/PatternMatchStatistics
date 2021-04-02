package pattern

import org.json.JSONArray
import org.json.JSONObject

import java.util.ArrayList

/**
 * 从 JSON 解析出 PatternQueue 和 PatternGroup
 * 我们约定：
 * "patterns": [ pattern1, patter2, pattern3... ] 表示 PatternGroup
 * {[ pattern1, pattern2, pattern3... ]} 表示 PatternQueue
 *
 * PatternQueue 可以包含 PatternGroup
 * PatternGroup 可以包含 PatternQueue
 *
 *
 */
object PatternFactory {
    fun create(jsonArray: JSONArray): IPattern? {
        if (jsonArray.length() == 0) {
            return null
        }
        val patterns = ArrayList<IPattern>()
        for (i in 0 until jsonArray.length()) {
            val pjo = jsonArray.optJSONObject(i)
            var p = create(pjo)
            if (p != null) {
                patterns.add(p)
                continue
            }
            // 如果是 array 则递归
            val pja = jsonArray.optJSONArray(i)
            p = create(pja)
            if (p != null) {
                patterns.add(p)
            }
        }
        return PatternQueue.create(patterns)
    }

    fun create(json: JSONObject): IPattern? {
        val pattern: IPattern?
        val patternsJA = json.optJSONArray("patterns")
        if (patternsJA == null) {
            // 我们约定 pattern 的最外层默认是一个 PatternGroup，在 tracker 的 JSON 结构里表示为 { "pattern" : { "patterns" : [] } }
            // 如果不是 patterns 为 key 的 JSON，说明是一个 Pattern 的 JSON 对象，不是 PatternQueue 也不是 PatternGroup
            pattern = Pattern.create(json)
        } else {
            val patterns = ArrayList<IPattern>()
            for (i in 0 until patternsJA.length()) {
                val pjo = patternsJA.optJSONObject(i)
                // 每一个 JSONObject 先递归到具体的单个 pattern
                var p = create(pjo)
                if (p != null) {
                    patterns.add(p)
                    continue
                }
                val pja = patternsJA.optJSONArray(i)
                p = create(pja)
                if (p != null) {
                    patterns.add(p)
                }
            }
            val quantifier = json.optString("match_quantifier")
            pattern = PatternGroup.create(quantifier, patterns)
        }
        return pattern
    }
}