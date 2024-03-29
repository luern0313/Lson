package cn.luern0313.lson.json;

/**
 * 被 luern0313 创建于 2020/8/22.
 */

enum Status {
    STATUS_EXPECT_OBJECT_BEGIN,
    STATUS_EXPECT_OBJECT_END,
    STATUS_EXPECT_OBJECT_KEY,
    STATUS_EXPECT_OBJECT_VALUE,
    STATUS_EXPECT_ARRAY_BEGIN,
    STATUS_EXPECT_ARRAY_END,
    STATUS_EXPECT_ARRAY_ELEMENT,
    STATUS_EXPECT_COLON,
    STATUS_EXPECT_COMMA,
    STATUS_EXPECT_SINGLE_ELEMENT,
    STATUS_EXPECT_END_DOCUMENT;

    int index;

    Status() {
        this.index = 1 << ordinal();
    }
}
