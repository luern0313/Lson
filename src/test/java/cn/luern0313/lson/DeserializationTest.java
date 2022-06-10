package cn.luern0313.lson;

import cn.luern0313.lson.adapter.TypeAdapter;
import cn.luern0313.lson.constructor.CustomConstructor;
import cn.luern0313.lson.constructor.InstanceResult;
import cn.luern0313.lson.deserialization.BaseModel;
import cn.luern0313.lson.deserialization.BaseModel2;
import cn.luern0313.lson.deserialization.JSON1;
import cn.luern0313.lson.deserialization.JSON2;
import cn.luern0313.lson.deserialization.JSON3;
import cn.luern0313.lson.deserialization.JSON4;
import cn.luern0313.lson.deserialization.JSON5;
import cn.luern0313.lson.deserialization.JSON6;
import cn.luern0313.lson.deserialization.JSON7;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonPrimitive;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 被 luern 创建于 2022/4/24.
 */
public class DeserializationTest {
    @Test
    public void fromJsonTest1() {
        JSON1.INSTANCE.check(Lson.def().fromJson(Lson.def().parse(JSON1.INSTANCE.json()), JSON1.FeedItemModel.class));
    }

    @Test
    public void fromJsonTest2() {
        JSON2.INSTANCE.check(Lson.def().fromJson(Lson.def().parse(JSON2.INSTANCE.json()), new TypeReference<BaseModel<JSON2.FeedModel>>(){}));
    }

    @Test
    public void fromJsonTest3() {
        JSON3.INSTANCE.check(Lson.def().fromJson(Lson.def().parse(JSON3.INSTANCE.json()), new TypeReference<BaseModel2<JSON3.FeedModel>>(){}));
    }

    @Test
    public void fromJsonTest4() {
        Lson lson4 = new Lson.LsonBuilder().setCustomConstructor(new CustomConstructor<JSON4.FeedItemModel>() {
            @Override
            public JSON4.FeedItemModel create(Type type, Object... parameter) {
                return new JSON4.FeedItemModel(123);
            }
        }).setCustomConstructor(new CustomConstructor<JSON4.FeedItemModel.FeedUserModel>() {
            @Override
            public JSON4.FeedItemModel.FeedUserModel create(Type type, Object... parameter) {
                return new JSON4.FeedItemModel.FeedUserModel(1234);
            }
        }).build();
        JSON4.INSTANCE.check(lson4.fromJson(lson4.parse(JSON4.INSTANCE.json()), JSON4.FeedItemModel.class));
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
        JSON5.INSTANCE.check(lson5.fromJson(lson5.parse(JSON5.INSTANCE.json()), new TypeReference<JSON5.ColorModel>(){}));
    }

    @Test
    public void fromJsonTest6() {
        Lson lson6 = new Lson.LsonBuilder().setCustomConstructor(new CustomConstructor<JSON6.ItemModel.N1>() {
            @Override
            public JSON6.ItemModel.N1 create(Type type, Object... parameter) {
                return new JSON6.ItemModel.N1(1);
            }
        }).setCustomConstructor(new CustomConstructor<InstanceResult<? extends JSON6.ItemModel.N23>>() {
            @Override
            public InstanceResult<? extends JSON6.ItemModel.N23> create(Type type, Object... parameter) {
                if (type == JSON6.ItemModel.N2.class) {
                    return new InstanceResult<>(new JSON6.ItemModel.N2(2));
                } else if (type == JSON6.ItemModel.N3.class) {
                    return new InstanceResult<>(new JSON6.ItemModel.N3(3));
                }
                return null;
            }
        }).setCustomConstructor(new CustomConstructor<JSON6.ItemModel.N4<Integer>>() {
            @Override
            public JSON6.ItemModel.N4<Integer> create(Type type, Object... parameter) {
                return new JSON6.ItemModel.N4<>(4);
            }
        }).setCustomConstructor(new CustomConstructor<JSON6.ItemModel.N4<String>>() {
            @Override
            public JSON6.ItemModel.N4<String> create(Type type, Object... parameter) {
                return new JSON6.ItemModel.N4<>("4");
            }
        }).setCustomConstructor(new CustomConstructor<InstanceResult<JSON6.ItemModel.N5>>() {
            @Override
            public InstanceResult<JSON6.ItemModel.N5> create(Type type, Object... parameter) {
                return new InstanceResult<>(new JSON6.ItemModel.N5(5));
            }
        }).setCustomConstructor(new CustomConstructor<JSON6.ItemModel.N6<Integer>>() {
            @Override
            public JSON6.ItemModel.N6<Integer> create(Type type, Object... parameter) {
                return new JSON6.ItemModel.N6<>(6);
            }
        }).setCustomConstructor(new CustomConstructor<JSON6.ItemModel.N6<String>>() {
            @Override
            public JSON6.ItemModel.N6<String> create(Type type, Object... parameter) {
                return new JSON6.ItemModel.N6<>("6");
            }
        }).build();
        JSON6.INSTANCE.check(lson6.fromJson(JSON6.INSTANCE.json(), JSON6.ItemModel.class));
    }

    @Test
    public void fromJsonTest7() {
        JSON7.INSTANCE.check(Lson.def().fromJson(JSON7.INSTANCE.json(), new TypeReference<ArrayList<JSON7.FeedItemModel>>(){}));
    }
}
