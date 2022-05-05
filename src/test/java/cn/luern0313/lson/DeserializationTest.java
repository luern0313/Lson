package cn.luern0313.lson;

import cn.luern0313.lson.constructor.CustomConstructor;
import cn.luern0313.lson.deserialization.BaseModel;
import cn.luern0313.lson.deserialization.BaseModel2;
import cn.luern0313.lson.deserialization.Json1;
import cn.luern0313.lson.deserialization.Json2;
import cn.luern0313.lson.deserialization.Json3;
import cn.luern0313.lson.deserialization.Json4;
import org.junit.Test;
import java.lang.reflect.Type;

/**
 * 被 luern 创建于 2022/4/24.
 */
public class DeserializationTest {
    @Test
    public void fromJsonTest() {
        Json1.INSTANCE.check(Lson.def().fromJson(Lson.def().parse(Json1.INSTANCE.json()), Json1.FeedItemModel.class));
        Json2.INSTANCE.check(Lson.def().fromJson(Lson.def().parse(Json2.INSTANCE.json()), new TypeReference<BaseModel<Json2.FeedModel>>(){}));
        Json3.INSTANCE.check(Lson.def().fromJson(Lson.def().parse(Json3.INSTANCE.json()), new TypeReference<BaseModel2<Json3.FeedModel>>(){}));

        Lson lson4 = new Lson.LsonBuilder().setCustomConstructor(type -> new Json4.FeedItemModel(123)).build();

        Json4.INSTANCE.check(lson4.fromJson(lson4.parse(Json4.INSTANCE.json()), Json4.FeedItemModel.class));
    }
}
