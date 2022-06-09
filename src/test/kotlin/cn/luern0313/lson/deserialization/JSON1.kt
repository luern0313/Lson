package cn.luern0313.lson.deserialization

import cn.luern0313.lson.annotation.field.LsonPath
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * 测试单一类以及类继承等
 * 2022/4/25.
 */

object JSON1: DeserializationJSONChecker<JSON1.FeedItemModel> {
    private val json = """
        {
            "content": "《唐顿庄园2》定档5月20日，华美精致的英伦风尚即将与你大银幕邂逅！当古老庄园里拍起了电影，当老伯爵夫人的过往秘密被揭开，一个全新的时代即将到来。",
            "topic": "春始万物生",
            "user": {
                "user_id": "100002",
                "user_name": "陪你聊电影",
                "user_lv": {
                    "lv": 6,
                    "is_hardcode": true
                }
            }
        }
    """.trimIndent()

    override fun json(): String {
        return json
    }

    override fun check(model: FeedItemModel) {
        assertEquals(model.content, "《唐顿庄园2》定档5月20日，华美精致的英伦风尚即将与你大银幕邂逅！当古老庄园里拍起了电影，当老伯爵夫人的过往秘密被揭开，一个全新的时代即将到来。")
        assertEquals(model.topic, "春始万物生")
        assertEquals(model.userLv?.lv, 6)
        assertEquals(model.userLv?.isHardcode, true)
        assertEquals(model.userId?.userId, "100002")
        assertEquals(model.userId?.userLv?.lv, 6)
        assertEquals(model.userId?.userLv?.isHardcode, true)
        assertEquals(model.innerUserId?.userId, "100002")
        assertEquals(model.innerUserId?.userLv?.lv, 6)
        assertEquals(model.innerUserId?.userLv?.isHardcode, true)
        assertEquals(model.userName?.userName, "陪你聊电影")
    }

    open class FeedBaseModel {
        @LsonPath
        var content: String? = null

        @LsonPath
        var topic: String? = null

        @LsonPath("user")
        var userId: FeedItemModel.FeedUserIdModel? = null

        @LsonPath("user.user_lv")
        var userLv: FeedItemModel.FeedUserIdModel.FeedUserLvModel? = null
    }

    class FeedItemModel : FeedBaseModel() {
        @LsonPath("user")
        var innerUserId: FeedUserIdModel? = null

        @LsonPath("user")
        var userName: FeedUserNameModel? = null

        inner class FeedUserIdModel {
            @LsonPath
            var userId: String? = null

            @LsonPath
            var userLv: FeedUserLvModel? = null

            inner class FeedUserLvModel {
                @LsonPath
                var lv: Int? = null

                @LsonPath
                var isHardcode: Boolean? = null
            }
        }

        class FeedUserNameModel {
            @LsonPath
            var userName: String? = null
        }


    }
}
