package cn.luern0313.lson.deserialization

import cn.luern0313.lson.annotation.field.LsonPath

/**
 * 被 luern 创建于 2022/4/25.
 */
data class BaseModel2<Model>(
    @LsonPath
    val code: Int = -1,

    @LsonPath("message", "msg")
    val message: String,

    @LsonPath("data", "result")
    val data: Model
)