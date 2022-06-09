package cn.luern0313.lson.constructor;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import cn.luern0313.lson.TypeReference;
import cn.luern0313.lson.util.TypeUtil;
import javafx.util.Pair;

/**
 * 自定义实例化管理list
 *
 * <p>创建于 2022/5/4.
 */

public class CustomConstructorList {
    private final HashMap<Type, CustomConstructor<?>> customConstructorMap;

    public CustomConstructorList() {
        customConstructorMap = new HashMap<>();
    }

    public void add(CustomConstructor<?> customConstructor) {
        if (customConstructor != null) {
            Type type = ((ParameterizedType) customConstructor.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == InstanceResult.class)
                customConstructorMap.put(((ParameterizedType) type).getActualTypeArguments()[0], customConstructor);
            else
                customConstructorMap.put(type, customConstructor);
        }
    }

    public void addAll(CustomConstructorList customConstructorList) {
        if (customConstructorList != null) {
            customConstructorMap.putAll(customConstructorList.customConstructorMap);
        }
    }

    @SuppressWarnings("Java8ListSort")
    public CustomConstructor<?> get(TypeUtil typeUtil) {
        ArrayList<Pair<Byte, Type>> typeList = null;
        for (Type currentType : customConstructorMap.keySet()) {
            byte order = typeFilter(currentType, typeUtil.getAsType(), typeUtil.getTypeReference() != null ? typeUtil.getTypeReference().typeMap : null);
            if (order > 0) {
                if (typeList == null)
                    typeList = new ArrayList<>();
                typeList.add(new Pair<>(order, currentType));
            }
        }
        if (typeList != null && typeList.size() > 0) {
            Collections.sort(typeList, (o1, o2) -> o1.getKey() - o2.getKey());
            return customConstructorMap.get(typeList.get(0).getValue());
        }
        return null;
    }

    private static byte typeFilter(Type currentType, Type type, LinkedHashMap<String, TypeReference<?>> typeMap) {
        try {
            if (currentType instanceof Class) {
                if (type instanceof Class && type.equals(currentType))
                    return 4;
                else if (type instanceof ParameterizedType) {
                    Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
                    boolean flag = true;
                    for (Type typeSub : typeArguments) {
                        if (typeSub instanceof Class && typeSub != Object.class) {
                            flag = false;
                            break;
                        } else if (typeSub instanceof WildcardType) {
                            Type[] loweBounds = ((WildcardType) typeSub).getLowerBounds();
                            if (((WildcardType) typeSub).getUpperBounds()[0] != Object.class || (loweBounds != null && loweBounds.length != 0)) {
                                flag = false;
                                break;
                            }
                        } else if (typeSub instanceof TypeVariable<?> && typeMap != null && typeMap.get(((TypeVariable<?>) typeSub).getName()).type != Object.class) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag)
                        return 3;
                }
                return 0;
            } else if (currentType instanceof GenericArrayType) {
                if (type instanceof GenericArrayType)
                    return typeFilter(((GenericArrayType) currentType).getGenericComponentType(), ((GenericArrayType) type).getGenericComponentType(), typeMap);
            } else if (currentType instanceof ParameterizedType) {
                if (type instanceof Class) {
                    return (byte) (type == ((ParameterizedType) currentType).getRawType() ? 1 : 0);
                } else if (type instanceof ParameterizedType) {
                    if (((ParameterizedType) currentType).getRawType() == ((ParameterizedType) type).getRawType() &&
                            ((ParameterizedType) currentType).getActualTypeArguments().length == ((ParameterizedType) type).getActualTypeArguments().length) {
                        Type[] currentTypeArguments = ((ParameterizedType) currentType).getActualTypeArguments();
                        Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
                        boolean flag = true;
                        for (int i = 0; i < currentTypeArguments.length; i++) {
                            if (typeFilter(currentTypeArguments[i], typeArguments[i], null) == 0) {
                                flag = false;
                                break;
                            }
                        }
                        return (byte) (flag ? 3 : 0);
                    }
                    return 0;
                }
                return 0;
            } else if (currentType instanceof WildcardType) {
                if (type instanceof Class) {
                    return (byte) (judgeBounds(((WildcardType) currentType).getLowerBounds(), ((WildcardType) currentType).getUpperBounds(), (Class<?>) type) ? 2 : 0);
                } else if (type instanceof ParameterizedType) {
                    return typeFilter(((ParameterizedType) type).getRawType(), type, typeMap);
                }
            }
            return 0;
        } catch (RuntimeException ignored) {
        }
        return 0;
    }

    private static boolean judgeBounds(Type[] lowers, Type[] uppers, Class<?> clz) {
        if (lowers != null) {
            boolean flag = false;
            for (Type lower : lowers) {
                if (lower instanceof Class && clz.isAssignableFrom(((Class<?>) lower))) {
                    flag = true;
                    break;
                }
            }
            if (flag)
                return true;
        }

        if (uppers != null) {
            boolean flag = false;
            for (Type upper : uppers) {
                if (upper instanceof Class && ((Class<?>) upper).isAssignableFrom(clz)) {
                    flag = true;
                    break;
                }
            }
            return flag;
        }
        return true;
    }
}
