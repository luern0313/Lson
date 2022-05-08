package cn.luern0313.lson.deserialization

import cn.luern0313.lson.annotation.field.LsonPath
import kotlin.test.assertEquals

/**
 * 测试Constructor
 * 2022/4/25
 */
object Json4: DeserializationJsonChecker<Json4.FeedItemModel> {
    private val json = """
        {
            "content": "《唐顿庄园2》定档5月20日，华美精致的英伦风尚即将与你大银幕邂逅！当古老庄园里拍起了电影，当老伯爵夫人的过往秘密被揭开，一个全新的时代即将到来。",
            "time": 1650293516,
            "image": "https://i0.hdslb.com/bfs/album/b26706e3fe65c681c6265797324ec6201f1ec4f6.jpg",
            "topic": "春始万物生",
            "watching": 22678,
            "watching_users_profile_photo": ["", "", ""],
            "like": 13596,
            "reply": 1164,
            "share": 431,
            "user_like": true,
            "user": {
              "user_id": "100002",
              "user_name": "陪你聊电影",
              "user_profile_photo": "https://i2.hdslb.com/bfs/face/440be69dce22ca3d55382e8051d252062284adfc.jpg"
            }
        }
    """.trimIndent()

    override fun json(): String {
        return json
    }

    override fun check(model: FeedItemModel) {
        assertEquals(model.constructor, 123)
        assertEquals(model.user?.constructor, 1234)
        assertEquals(model.content, "《唐顿庄园2》定档5月20日，华美精致的英伦风尚即将与你大银幕邂逅！当古老庄园里拍起了电影，当老伯爵夫人的过往秘密被揭开，一个全新的时代即将到来。")
        assertEquals(model.topic, "春始万物生")
        assertEquals(model.user?.userName, "陪你聊电影")
        assertEquals(model.user?.userId, "100002")
    }

    class FeedItemModel(val constructor: Int /* 123 */) {
        @LsonPath
        var user: FeedUserModel? = null

        @LsonPath
        var content: String? = null

        @LsonPath
        var topic: String? = null

        class FeedUserModel(val constructor: Int /* 1234 */) {
            @LsonPath
            var userId: String? = null

            @LsonPath
            var userName: String? = null
        }
    }
}
