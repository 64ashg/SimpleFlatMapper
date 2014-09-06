package org.sfm.reflect;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;
import org.sfm.beans.Bar;
import org.sfm.beans.DbPrimitiveObject;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.beans.Foo;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.FieldSetter;
import org.sfm.reflect.impl.MethodSetter;
import org.sfm.reflect.primitive.BooleanFieldSetter;
import org.sfm.reflect.primitive.BooleanMethodSetter;
import org.sfm.reflect.primitive.ByteFieldSetter;
import org.sfm.reflect.primitive.ByteMethodSetter;
import org.sfm.reflect.primitive.CharacterFieldSetter;
import org.sfm.reflect.primitive.CharacterMethodSetter;
import org.sfm.reflect.primitive.DoubleFieldSetter;
import org.sfm.reflect.primitive.DoubleMethodSetter;
import org.sfm.reflect.primitive.FloatFieldSetter;
import org.sfm.reflect.primitive.FloatMethodSetter;
import org.sfm.reflect.primitive.IntFieldSetter;
import org.sfm.reflect.primitive.IntMethodSetter;
import org.sfm.reflect.primitive.LongFieldSetter;
import org.sfm.reflect.primitive.LongMethodSetter;
import org.sfm.reflect.primitive.ShortFieldSetter;
import org.sfm.reflect.primitive.ShortMethodSetter;

public class SetterFactoryTest {
	
	SetterFactory nonAsmfactory = new SetterFactory(null);
	SetterFactory asmfactory = new SetterFactory(new AsmFactory());

	@Test
	public void testFailFallBackToMethod() throws Exception {
		Setter<Foo, String> setter = new SetterFactory(new AsmFactory(){
			@Override
			public <T, P> Setter<T, P> createSetter(Method m) throws Exception {
				throw new UnsupportedOperationException();
			}
		}).getSetter(Foo.class, "foo");
		assertTrue(setter instanceof MethodSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	
	@Test
	public void testMethodToAsm() throws Exception {
		Setter<Foo, String> setter = asmfactory.getSetter(Foo.class, "foo");
		assertFalse(setter instanceof MethodSetter);
		assertFalse(setter instanceof FieldSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	
	@Test
	public void testDefaultToMethod() throws Exception {
		Setter<Foo, String> setter = nonAsmfactory.getSetter(Foo.class, "foo");
		assertTrue(setter instanceof MethodSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	
	@Test
	public void testMatchFullMethodName() throws Exception {
		Setter<Foo, String> setter = nonAsmfactory.getSetter(Foo.class, "setFoo");
		assertFalse(setter instanceof FieldSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	
	@Test
	public void testFallBackToField() throws Exception {
		Setter<Bar, String> setter = nonAsmfactory.getSetter(Foo.class, "bar");
		assertTrue(setter instanceof FieldSetter);
		SetterTestHelper.validateBarSetter(setter);
	}
	
	@Test
	public void testReturnNullIfNotFound() throws Exception {
		Setter<Foo, String> setter = nonAsmfactory.getSetter(Foo.class, "xxbar");
		assertNull(setter);
	}
	
	@Test
	public void testToBooleanSetter() throws Exception {
		assertTrue(nonAsmfactory.toBooleanSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pBoolean")) instanceof BooleanFieldSetter);
		assertTrue(nonAsmfactory.toBooleanSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean")) instanceof BooleanMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean");
		assertSame(setter, asmfactory.toBooleanSetter(setter));
		
		try {
			nonAsmfactory.toBooleanSetter(new Setter<DbPrimitiveObject, Boolean>() {
				@Override
				public void set(DbPrimitiveObject target, Boolean value) throws Exception {
				}
				@Override
				public Class<? extends Boolean> getPropertyType() {
					return null;
				}
			});
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToByteSetter() throws Exception {
		assertTrue(nonAsmfactory.toByteSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pByte")) instanceof ByteFieldSetter);
		assertTrue(nonAsmfactory.toByteSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte")) instanceof ByteMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte");
		assertSame(setter, asmfactory.toByteSetter(setter));
		
		try {
			nonAsmfactory.toByteSetter(new Setter<DbPrimitiveObject, Byte>() {
				@Override
				public void set(DbPrimitiveObject target, Byte value) throws Exception {
				}
				@Override
				public Class<? extends Byte> getPropertyType() {
					return null;
				}
			});
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToCharacterSetter() throws Exception {
		assertTrue(nonAsmfactory.toCharacterSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pCharacter")) instanceof CharacterFieldSetter);
		assertTrue(nonAsmfactory.toCharacterSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pCharacter")) instanceof CharacterMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pCharacter");
		assertSame(setter, asmfactory.toCharacterSetter(setter));
		
		try {
			nonAsmfactory.toCharacterSetter(new Setter<DbPrimitiveObject, Character>() {
				@Override
				public void set(DbPrimitiveObject target, Character value) throws Exception {
				}
				@Override
				public Class<? extends Character> getPropertyType() {
					return null;
				}
			});
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToShortSetter() throws Exception {
		assertTrue(nonAsmfactory.toShortSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pShort")) instanceof ShortFieldSetter);
		assertTrue(nonAsmfactory.toShortSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort")) instanceof ShortMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort");
		assertSame(setter, asmfactory.toShortSetter(setter));
		
		try {
			nonAsmfactory.toShortSetter(new Setter<DbPrimitiveObject, Short>() {
				@Override
				public void set(DbPrimitiveObject target, Short value) throws Exception {
				}
				@Override
				public Class<? extends Short> getPropertyType() {
					return null;
				}
			});
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToIntSetter() throws Exception {
		assertTrue(nonAsmfactory.toIntSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pInt")) instanceof IntFieldSetter);
		assertTrue(nonAsmfactory.toIntSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt")) instanceof IntMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt");
		assertSame(setter, asmfactory.toIntSetter(setter));
		
		try {
			nonAsmfactory.toIntSetter(new Setter<DbPrimitiveObject, Integer>() {
				@Override
				public void set(DbPrimitiveObject target, Integer value) throws Exception {
				}
				@Override
				public Class<? extends Integer> getPropertyType() {
					return null;
				}
			});
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToLongSetter() throws Exception {
		assertTrue(nonAsmfactory.toLongSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pLong")) instanceof LongFieldSetter);
		assertTrue(nonAsmfactory.toLongSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong")) instanceof LongMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong");
		assertSame(setter, asmfactory.toLongSetter(setter));
		
		try {
			nonAsmfactory.toLongSetter(new Setter<DbPrimitiveObject, Long>() {
				@Override
				public void set(DbPrimitiveObject target, Long value) throws Exception {
				}
				@Override
				public Class<? extends Long> getPropertyType() {
					return null;
				}
			});
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToFloatSetter() throws Exception {
		assertTrue(nonAsmfactory.toFloatSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pFloat")) instanceof FloatFieldSetter);
		assertTrue(nonAsmfactory.toFloatSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat")) instanceof FloatMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat");
		assertSame(setter, asmfactory.toFloatSetter(setter));
		
		try {
			nonAsmfactory.toFloatSetter(new Setter<DbPrimitiveObject, Float>() {
				@Override
				public void set(DbPrimitiveObject target, Float value) throws Exception {
				}
				@Override
				public Class<? extends Float> getPropertyType() {
					return null;
				}
			});
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToDoubleSetter() throws Exception {
		assertTrue(nonAsmfactory.toDoubleSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pDouble")) instanceof DoubleFieldSetter);
		assertTrue(nonAsmfactory.toDoubleSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble")) instanceof DoubleMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble");
		assertSame(setter, asmfactory.toDoubleSetter(setter));
		
		try {
			nonAsmfactory.toDoubleSetter(new Setter<DbPrimitiveObject, Double>() {
				@Override
				public void set(DbPrimitiveObject target, Double value) throws Exception {
				}
				@Override
				public Class<? extends Double> getPropertyType() {
					return null;
				}
			});
			fail("Should fail");
		} catch (Exception e) {
		}
	}
}
