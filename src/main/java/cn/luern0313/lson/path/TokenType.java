package cn.luern0313.lson.path;

/**
 * 被 luern0313 创建于 2020/8/9.
 */

enum TokenType {
    /**
     * 根路径: $
     */
    JSON_ROOT,

    /**
     * 当前路径: @
     */
    JSON_CURRENT,

    /**
     * 分隔符: '.'
     */
    SPLIT_POINT,

    /**
     * 分隔符 ':'
     */
    SPLIT_COLON,

    /**
     * 分隔符 ','
     */
    SPLIT_COMMA,

    /**
     * 表达式或切片开始符: [
     */
    EXPRESSION_START,

    /**
     * 表达式或切片结束符: ]
     */
    EXPRESSION_END,

    /**
     * 过滤器开始符: ?(
     */
    FILTER_START,

    /**
     * 过滤器结束符: )
     */
    FILTER_END,

    /**
     * 比较符
     */
    FILTER_COMPARISON,

    /**
     * 星号: *
     */
    SYNTAX_ASTERISK,

    /**
     * 字符串: "xxx"
     */
    STRING,

    /**
     * 数字: 1
     */
    NUMBER,

    /**
     * path结束
     */
    END_DOCUMENT
}
