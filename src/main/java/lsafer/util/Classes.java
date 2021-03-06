/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * -You can edit this file (except the header).
 * -If you have change anything in this file. You
 *  shall mention that this file has been edited.
 *  By adding a new header (at the bottom of this header)
 *  with the word "Editor" on top of it.
 */
package lsafer.util;

import java.lang.reflect.Array;

/**
 * Useful utils for {@link Class classes}.
 *
 * @author LSaferSE
 * @version 3 release (28-Sep-19)
 * @since 25-Sep-19
 */
final public class Classes {
	/**
	 * This is a util class. and shall not be instanced as an object.
	 */
	private Classes() {
	}

	/**
	 * Get an array class of the given class.
	 *
	 * @param klass to get an array class of
	 * @param <C>   the targeted class
	 * @return an array class of the given class
	 */
	public static <C> Class<C[]> array(Class<C> klass) {
		return (Class<C[]>) Array.newInstance(klass, 0).getClass();
	}

	/**
	 * Get the class that extends {@link Object} that represent the given class.
	 *
	 * @param klass to get the object class of
	 * @return the class that extends Object class and represent the given class
	 */
	public static Class<?> objective(Class<?> klass) {
		if (klass.isArray()) {
			Class<?> component = klass.getComponentType();
			return component.isPrimitive() ? Classes.array(Classes.objective(component)) : klass;
		} else if (klass.isPrimitive()) {
			if (klass == char.class)
				return Character.class;
			if (klass == int.class)
				return Integer.class;
			if (klass == boolean.class)
				return Boolean.class;
			if (klass == byte.class)
				return Byte.class;
			if (klass == double.class)
				return Double.class;
			if (klass == float.class)
				return Float.class;
			if (klass == long.class)
				return Long.class;
			if (klass == short.class)
				return Short.class;
		}

		return klass;
	}

	/**
	 * Get the class that don't extends {@link Object} from the given class.
	 *
	 * @param klass to get the non-object class of
	 * @return the non-object class of the given class
	 */
	public static Class<?> primitive(Class<?> klass) {
		if (klass == Character.class)
			return char.class;
		if (klass == Integer.class)
			return int.class;
		if (klass == Boolean.class)
			return boolean.class;
		if (klass == Byte.class)
			return byte.class;
		if (klass == Double.class)
			return double.class;
		if (klass == Float.class)
			return float.class;
		if (klass == Long.class)
			return long.class;
		if (klass == Short.class)
			return short.class;

		return null;
	}
}
