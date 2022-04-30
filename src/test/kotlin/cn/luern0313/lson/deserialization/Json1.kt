package cn.luern0313.lson.deserialization

import cn.luern0313.lson.annotation.field.LsonPath
import kotlin.test.assertEquals

/**
 * 测试单一类
 * 2022/4/25.
 */

object Json1: DeserializationJsonChecker<Json1.FeedItemModel> {
    private val json = """
        {
            "content": "《唐顿庄园2》定档5月20日，华美精致的英伦风尚即将与你大银幕邂逅！当古老庄园里拍起了电影，当老伯爵夫人的过往秘密被揭开，一个全新的时代即将到来。",
            "topic": "春始万物生",
            "user": {
                "user_id": "100002",
                "user_name": "陪你聊电影"
            }
        }
    """.trimIndent()

    override fun json(): String {
        return json
    }

    override fun check(model: FeedItemModel) {
        assertEquals(model.content, "《唐顿庄园2》定档5月20日，华美精致的英伦风尚即将与你大银幕邂逅！当古老庄园里拍起了电影，当老伯爵夫人的过往秘密被揭开，一个全新的时代即将到来。")
        assertEquals(model.topic, "春始万物生")
        assertEquals(model.user?.userName, "陪你聊电影")
        assertEquals(model.user?.userId, "100002")
    }

    class FeedItemModel {
        @LsonPath
        var user: FeedUserModel? = null

        @LsonPath
        var content: String? = null

        @LsonPath
        var topic: String? = null

        class FeedUserModel {
            @LsonPath
            var userId: String? = null

            @LsonPath
            var userName: String? = null

        }
    }
}
