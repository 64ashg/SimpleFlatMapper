package org.simpleflatmapper.reflect.test.setter;

import org.junit.Test;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMatchingScore;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Issue450 {

    public static final Predicate<PropertyMeta<?, ?>> TRUE_PREDICATE = new Predicate<PropertyMeta<?, ?>>() {
        @Override
        public boolean test(PropertyMeta<?, ?> propertyMeta) {
            return true;
        }
    };
    private PropertyFinder.PropertyFinderProbe prob = new PropertyFinder.PropertyFinderProbe() {
        @Override
        public void found(PropertyMeta propertyMeta, PropertyMatchingScore score) {
            System.out.println("PropertyFinder for  - found " + score + " " + propertyMeta.getPath());

        }

        @Override
        public void select(PropertyMeta propertyMeta) {
            System.out.println("PropertyFinder for  - select " + propertyMeta.getPath());

        }
    };

    @Test
    public void testTupleIntegerFooOrderedSpeculative() {
        PropertyFinder<?> finder = getTuple2IntegerFooPropertyFinder();

        assertEquals("element0", getPathFor(finder, "f"));
        assertEquals("element1.f", getPathFor(finder, "a_f"));
    }

    @Test
    public void testTupleIntegerFooUnOrderedSpeculative() {
        PropertyFinder<?> finder = getTuple2IntegerFooPropertyFinder();

        assertEquals("element0", getPathFor(finder, "a_f"));
        assertEquals("element1.f", getPathFor(finder, "f"));
    }


    @Test
    public void testTupleIntegerFooExactMatch() {
        PropertyFinder<?> finder = getTuple2IntegerFooPropertyFinder();

        assertEquals("element0", getPathFor(finder, "element0"));
        assertEquals("element1.f", getPathFor(finder, "element1_f"));
    }

    @Test
    public void testTupleIntegerFooExactMatchReverse() {
        PropertyFinder<?> finder = getTuple2IntegerFooPropertyFinder();

        assertEquals("element1.f", getPathFor(finder, "element1_f"));
        assertEquals("element0", getPathFor(finder, "element0"));
    }




    @Test
    public void testTupleIntegerListFooOrderedSpeculative() {
        PropertyFinder<?> finder = getTuple2IntegerListFooPropertyFinder();

        assertEquals("element0", getPathFor(finder, "f"));
        assertEquals("element1[0].f", getPathFor(finder, "a_f"));
    }

    @Test
    public void testTupleIntegerListFooUnOrderedSpeculative() {
        PropertyFinder<?> finder = getTuple2IntegerListFooPropertyFinder();

        assertEquals("element0", getPathFor(finder, "a_f"));
        assertEquals("element1[0].f", getPathFor(finder, "f"));
    }


    @Test
    public void testTupleIntegerListFooExactMatch() {
        PropertyFinder<?> finder = getTuple2IntegerListFooPropertyFinder();

        assertEquals("element0", getPathFor(finder, "element0"));
        assertEquals("element1[0].f", getPathFor(finder, "element1_f"));
    }

    @Test
    public void testTupleIntegerListFooExactMatchReverse() {
        PropertyFinder<?> finder = getTuple2IntegerListFooPropertyFinder();

        assertEquals("element1[0].f", getPathFor(finder, "element1_f"));
        assertEquals("element0", getPathFor(finder, "element0"));
    }

    @Test
    public void testTupleIntegerListBar() {
        PropertyFinder<?> finder = getTuple2IntegerListBarPropertyFinder();

        assertEquals("element0", getPathFor(finder, "id"));
        assertEquals("element1[0].barId", getPathFor(finder, "barId"));
    }



    @Test
    public void testBar2() {
        PropertyFinder<?> finder = getPropertyFinder(new TypeReference<Bar2>() {
        });

        assertEquals("barId", getPathFor(finder, "barId"));
        assertEquals("fooId", getPathFor(finder, "fooId"));
    }
    

    @Test
    public void testTupleIntegerListBarReverse() {
        PropertyFinder<?> finder = getTuple2IntegerListBarPropertyFinder();

        assertEquals("element1[0].barId", getPathFor(finder, "element1.barId"));
        assertEquals("element0", getPathFor(finder, "id"));
    }


    private String getPathFor(PropertyFinder<?> finder, String prop) {
        return finder.findProperty(DefaultPropertyNameMatcher.of(prop), new Object[0], (TypeAffinity)null, prob, TRUE_PREDICATE).getPath();
    }

    private PropertyFinder<?> getTuple2IntegerFooPropertyFinder() {
        return getPropertyFinder(new TypeReference<Tuple2<Integer, Foo>>() {
        });
    }

    private PropertyFinder<?> getPropertyFinder(TypeReference<?> typeReference) {
        ClassMeta<Tuple2<Integer, Foo>> classMeta = ReflectionService.newInstance().withSelfScoreFullName(true).getClassMeta(typeReference.getType());

        return classMeta.newPropertyFinder();
    }

    private PropertyFinder<?> getTuple2IntegerListFooPropertyFinder() {
        return getPropertyFinder(new TypeReference<Tuple2<Integer, List<Foo>>>() {
        });
    }

    private PropertyFinder<?> getTuple2IntegerListBarPropertyFinder() {
        return getPropertyFinder(new TypeReference<Tuple2<Integer, List<Bar>>>() {
        });
    }


    public static class Foo {
        public int f;
    }

    public static class Bar {
        public int barId;
    }


    public static class Bar2 {
        public int barId;
        public int fooId;
    }

}
