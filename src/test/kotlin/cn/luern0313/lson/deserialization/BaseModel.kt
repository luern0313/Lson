package cn.luern0313.lson.deserialization

import cn.luern0313.lson.annotation.field.LsonPath

/**
 * 被 luern 创建于 2022/4/25.
 */
class BaseModel<Model> {
    @LsonPath
    val code = -1

    @LsonPath("message", "msg")
    val message: String? = null

    @LsonPath("data", "result")
    val data: Model? = null
}