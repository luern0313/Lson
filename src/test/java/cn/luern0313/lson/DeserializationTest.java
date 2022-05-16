package cn.luern0313.lson;

import cn.luern0313.lson.adapter.TypeAdapter;
import cn.luern0313.lson.constructor.CustomConstructor;
import cn.luern0313.lson.deserialization.BaseModel;
import cn.luern0313.lson.deserialization.BaseModel2;
import cn.luern0313.lson.deserialization.Json1;
import cn.luern0313.lson.deserialization.Json2;
import cn.luern0313.lson.deserialization.Json3;
import cn.luern0313.lson.deserialization.Json4;
import cn.luern0313.lson.deserialization.Json5;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonPrimitive;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.awt.Color;
import java.lang.reflect.Type;

/**
 * 被 luern 创建于 2022/4/24.
 */
public class DeserializationTest {
    @Test
    public void fromJsonTest1() {
        Json1.INSTANCE.check(Lson.def().fromJson(Lson.def().parse(Json1.INSTANCE.json()), Json1.FeedItemModel.class));
    }

    @Test
    public void fromJsonTest2() {
        Json2.INSTANCE.check(Lson.def().fromJson(Lson.def().parse(Json2.INSTANCE.json()), new TypeReference<BaseModel<Json2.FeedModel>>(){}));
    }

    @Test
    public void fromJsonTest3() {
        Json3.INSTANCE.check(Lson.def().fromJson(Lson.def().parse(Json3.INSTANCE.json()), new TypeReference<BaseModel2<Json3.FeedModel>>(){}));
    }

    @Test
    public void fromJsonTest4() {
        Lson lson4 = new Lson.LsonBuilder().setCustomConstructor(new CustomConstructor<Json4.FeedItemModel>() {
            @Override
            public Json4.FeedItemModel create(@Nullable Type type) {
                return new Json4.FeedItemModel(123);
            }
        }).setCustomConstructor(new CustomConstructor<Json4.FeedItemModel.FeedUserModel>() {
            @Override
            public Json4.FeedItemModel.FeedUserModel create(@Nullable Type type) {
                return new Json4.FeedItemModel.FeedUserModel(1234);
            }
        }).build();
        Json4.INSTANCE.check(lson4.fromJson(lson4.parse(Json4.INSTANCE.json()), Json4.FeedItemModel.class));
    }

    @Test
    public void fromJsonTest5() {
        Lson lson5 = new Lson.LsonBuilder().setTypeAdapter(new TypeAdapter<Color>() {
            @Override
            public Color deserialization(@NotNull LsonElement value) {
                if (value.isLsonPrimitive())
                    return Color.decode(((LsonPrimitive) value).getAsString());
                else if (value.isLsonObject())
                    return des(value.getAsLsonObject().get("r"), value.getAsLsonObject().get("g"), value.getAsLsonObject().get("b"));
                else if (value.isLsonArray())
                    return des(value.getAsLsonArray().get(0), value.getAsLsonArray().get(1), value.getAsLsonArray().get(2));
                return null;
            }

            private Color des(LsonElement r, LsonElement g, LsonElement b) {
                if (r.isLsonPrimitive() && g.isLsonPrimitive() && b.isLsonPrimitive()) {
                    LsonPrimitive rp = r.getAsLsonPrimitive();
                    LsonPrimitive gp = g.getAsLsonPrimitive();
                    LsonPrimitive bp = b.getAsLsonPrimitive();

                    if (rp.isFloat() || rp.isDouble())
                        return new Color(rp.getAsFloat(), gp.getAsFloat(), bp.getAsFloat());
                    else
                        return new Color(rp.getAsInt(), gp.getAsInt(), bp.getAsInt());
                }
                return null;
            }

            @Override
            public LsonElement serialization(Color obj) {
                return null;
            }
        }).build();
        Json5.INSTANCE.check(lson5.fromJson(lson5.parse(Json5.INSTANCE.json()), new TypeReference<Json5.ColorModel>(){}));
    }
}
