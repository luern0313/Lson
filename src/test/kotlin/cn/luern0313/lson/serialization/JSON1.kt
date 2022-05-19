package cn.luern0313.lson.serialization

import cn.luern0313.lson.annotation.field.LsonPath
import cn.luern0313.lson.element.LsonElement
import cn.luern0313.lson.element.LsonObject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 被 luern 创建于 2022/5/17.
 */
class JSON1: SerializationJSONChecker<JSON1.FeedItemModel> {
    override fun model(): FeedItemModel {
        return FeedItemModel().apply {
            content = "《唐顿庄园2》定档5月20日，华美精致的英伦风尚即将与你大银幕邂逅！当古老庄园里拍起了电影，当老伯爵夫人的过往秘密被揭开，一个全新的时代即将到来。"
            topic = "春始万物生"
            user = FeedItemModel.FeedUserModel().apply {
                userId = "100002"
                userName = "陪你聊电影"
            }
        }
    }

    override fun check(lsonElement: LsonElement) {
        assertTrue(lsonElement is LsonObject)
        assertEquals(lsonElement["content"].asLsonPrimitive.asString, "《唐顿庄园2》定档5月20日，华美精致的英伦风尚即将与你大银幕邂逅！当古老庄园里拍起了电影，当老伯爵夫人的过往秘密被揭开，一个全新的时代即将到来。")
        assertEquals(lsonElement["topic"].asLsonPrimitive.asString, "春始万物生")
        assertEquals(lsonElement["user"].asLsonObject["userId"].asLsonPrimitive.asString, "100002")
        assertEquals(lsonElement["user"].asLsonObject["userName"].asLsonPrimitive.asString, "陪你聊电影")
        assertEquals(lsonElement["user"].asLsonObject.keys.size, 2)
        assertEquals(lsonElement.keys.size, 3)
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
