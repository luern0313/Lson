package cn.luern0313.lson.json;

/**
 * 被 luern0313 创建于 2020/8/22.
 */

enum TokenType
{
    /**
     * object开始: {
     */
    OBJECT_BEGIN,

    /**
     * object结束: }
     */
    OBJECT_END,

    /**
     * array开始: [
     */
    ARRAY_BEGIN,

    /**
     * array结束: ]
     */
    ARRAY_END,

    /**
     * 字符串: "xxx"
     */
    STRING,

    /**
     * 数字: 1
     */
    NUMBER,

    /**
     * 布尔值: true/false
     */
    BOOLEAN,

    /**
     * null
     */
    NULL,

    /**
     * :
     */
    SPLIT_COLON,

    /**
     * ,
     */
    SPLIT_COMMA,

    /**
     * EOF
     */
    END_DOCUMENT
}
