package cn.luern0313.lson;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * 被 luern 创建于 2021/12/8.
 */
public class TypeReferenceTest {
    public A<B<C>, D> testA;
    public B<C> testB;

    private Type testTypeA;
    private Type testTypeB;

    @Before
    public void before() throws Exception {
        testTypeA = TypeReferenceTest.class.getField("testA").getGenericType();
        testTypeB = TypeReferenceTest.class.getField("testB").getGenericType();
    }

    @Test
    public void testTypeReferenceConstructor() {
        TypeReference<?> emptyTypeReference = new TypeReference(){};
        assertNull(emptyTypeReference.type);
        assertNull(emptyTypeReference.rawType);
        assertNull(emptyTypeReference.typeMap);

        TypeReference<?> typeReference = new TypeReference<A<B<C>, D>>(){};
        assertEquals(typeReference.type, testTypeA);
        assertEquals(typeReference.rawType, A.class);

        TypeReference<?> typeReferenceA = typeReference.typeMap.get("A");
        assertEquals(typeReferenceA.type, testTypeB);
        assertEquals(typeReferenceA.rawType, B.class);

        TypeReference<?> typeReferenceAA = typeReference.typeMap.get("A").typeMap.get("A");
        assertEquals(typeReferenceAA.type, C.class);
        assertEquals(typeReferenceAA.rawType, C.class);

        TypeReference<?> typeReferenceB = typeReference.typeMap.get("B");
        assertEquals(typeReferenceB.type, D.class);
        assertEquals(typeReferenceB.rawType, D.class);
    }

    @Test
    public void testTypeReferenceMethod() {
        TypeReference<?> emptyTypeReference = new TypeReference<>(null);
        assertNull(emptyTypeReference.type);
        assertNull(emptyTypeReference.rawType);
        assertNull(emptyTypeReference.typeMap);

        TypeReference<?> typeReference = new TypeReference<>(testTypeA);
        assertEquals(typeReference.type, testTypeA);
        assertEquals(typeReference.rawType, A.class);

        TypeReference<?> typeReferenceA = typeReference.typeMap.get("A");
        assertEquals(typeReferenceA.type, testTypeB);
        assertEquals(typeReferenceA.rawType, B.class);

        TypeReference<?> typeReferenceAA = typeReference.typeMap.get("A").typeMap.get("A");
        assertEquals(typeReferenceAA.type, C.class);
        assertEquals(typeReferenceAA.rawType, C.class);

        TypeReference<?> typeReferenceB = typeReference.typeMap.get("B");
        assertEquals(typeReferenceB.type, D.class);
        assertEquals(typeReferenceB.rawType, D.class);

    }

    private static class A<A, B> {
        A a;
        B b;
    }

    private static class B<A> {
        A a;
    }

    private static class C {
    }

    private static class D {
    }
}