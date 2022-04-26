package cn.luern0313.lson.deserialization

import cn.luern0313.lson.annotation.field.LsonPath

/**
 * 被 luern 创建于 2022/4/25.
 */
class Json2: DeserializationJsonChecker<BaseModel<Json2.FeedModel>> {
    private val json = """
        {
          "code": 0,
          "message": "",
          "data": {
            "feed": [
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
              },
              {
                "content": "雨生百谷，万物更新\n今天是春季的最后一个节气谷雨，因此时节降水增多，利于谷物生长而得名。\n在古代，人们在谷雨这天喝谷雨茶、赏牡丹。",
                "time": 1650416892,
                "image": "https://i0.hdslb.com/bfs/album/37ab6d28d8d2bb3655d08f66c340952fbf99603e.jpg",
                "topic": "为高考加油",
                "watching": 846,
                "watching_users_profile_photo": ["", "", ""],
                "like": 348,
                "reply": 54,
                "share": 32,
                "user_like": true,
                "user": {
                  "user_id": "100001",
                  "user_name": "国风阁小书童",
                  "user_profile_photo": "https://i2.hdslb.com/bfs/face/5d412fd724086b64d93f2660aa2a442cb36f9fd2.jpg"
                }
              }
            ]
          }
        }
    """.trimIndent()
    
    override fun json(): String {
        return json
    }

    override fun check(model: BaseModel<FeedModel>) {
        
    }

    class FeedModel {
        @LsonPath
        val feed: ArrayList<FeedItemModel>? = null
    }

    class FeedItemModel {
        @LsonPath
        var user: FeedUserModel? = null

        @LsonPath
        var content: String? = null

        @LsonPath
        var topic: String? = null


        @LsonPath
        var userLike: Boolean? = null

        class FeedUserModel {
            @LsonPath
            var userId: String? = null

            @LsonPath
            var userName: String? = null

        }
    }
}