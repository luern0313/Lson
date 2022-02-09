package cn.luern0313.lson.json;

/**
 * 被 luern0313 创建于 2020/8/22.
 */

enum TokenType
{
    /**
     * object开始: {
     */
    OBJECT_BEGIN("{"),

    /**
     * object结束: }
     */
    OBJECT_END("}"),

    /**
     * array开始: [
     */
    ARRAY_BEGIN("["),

    /**
     * array结束: ]
     */
    ARRAY_END("]"),

    /**
     * 字符串: "xxx" / 'xxx'
     */
    STRING,

    /**
     * 字符串: xxx
     *
     * 注：只允许在object的key中出现
     */
    STRING_WITHOUT_QUOTATION,

    /**
     * 数字: 1
     */
    NUMBER,

    /**
     * 布尔值: true
     */
    BOOLEAN_TRUE("true"),

    /**
     * 布尔值: false
     */
    BOOLEAN_FALSE("false"),

    /**
     * null
     */
    NULL("null"),

    /**
     * :
     */
    SPLIT_COLON(":"),

    /**
     * ,
     */
    SPLIT_COMMA(","),

    /**
     * 单行注释：//
     */
    COMMENT_SINGLE("//"),

    /**
     * 多行注释开始符：/*
     */
    COMMENT_MULTIPLE_START("/*"),

    /**
     * EOF
     */
    END_DOCUMENT;

    private String symbol;

    TokenType() {
    }

    TokenType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
