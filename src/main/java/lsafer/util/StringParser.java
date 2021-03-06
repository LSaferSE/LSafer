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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract for string parsers. The purpose of string-parsers is to parse strings into objects. Or stringify objects into strings. To whether store
 * it. Or show it as a text.
 * <br>
 * As a string-parser that extends this class. You just have to navigate this class to where your parsing/stringing methods is. By using the following
 * annotations.
 *
 * <ul>
 * <li>{@link SwitchingMethod} for methods that tells this class what type a string should be parsed to</li>
 * <li>{@link ParsingMethod} for methods that parse strings into objects</li>
 * <li>{@link StringingMethod} for methods that stringify objects</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 2 release (28-Sep-19)
 * @since 28-Sep-19
 */
public abstract class StringParser {
	/**
	 * A map that stores previously solved parser-methods to improve performance.
	 */
	final protected Map<String, Method> parsers = new HashMap<>();
	/**
	 * A map that stores previously solved stringifier-methods to improve performance.
	 */
	final protected Map<String, Method> stringers = new HashMap<>();

	/**
	 * Parse the given string to an object that matches it.
	 *
	 * @param string to be parsed
	 * @return an object result from parsing the given string
	 */
	public Object parse(String string) {
		Class<?> klass = this.queryClass(string);

		if (klass != null) {
			Method parser = this.queryParsingMethod(klass);

			if (parser != null)
				try {
					return parser.invoke(this, string);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
		}

		return string;
	}

	/**
	 * Query what's the suitable class for the given string.
	 *
	 * @param string to query a suitable class for
	 * @return the suitable class for the given string
	 */
	public Class<?> queryClass(String string) {
		for (Method method : this.getClass().getMethods())
			if (method.isAnnotationPresent(SwitchingMethod.class))
				try {
					if ((boolean) method.invoke(this, string))
						return method.getAnnotation(SwitchingMethod.class).value();
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}

		return Object.class;
	}

	/**
	 * Query what's the method to parse the given type.
	 *
	 * @param type to query a method for
	 * @return the method to parse the given type. Or null if this class don't have one
	 */
	public Method queryParsingMethod(Class<?> type) {
		String key = type.getName();

		if (this.parsers.containsKey(key))
			return this.parsers.get(key);

		for (Method method : this.getClass().getMethods())
			if (method.isAnnotationPresent(ParsingMethod.class) &&
				method.getReturnType() == type) {
				this.parsers.put(key, method);
				return method;
			}

		return null;
	}

	/**
	 * Query what's the method to stringify the given type.
	 *
	 * @param type to query a method for
	 * @return the method to stringify the given type. Or null if this class don't have one
	 */
	public Method queryStringingMethod(Class<?> type) {
		if (type.isPrimitive())
			type = Classes.objective(type);

		String key = type.getName();

		if (this.stringers.containsKey(key))
			return this.stringers.get(key);

		for (Method method : this.getClass().getMethods())
			if (method.isAnnotationPresent(StringingMethod.class) &&
				method.getParameterTypes()[0].isAssignableFrom(type)) {
				this.stringers.put(key, method);
				return method;
			}

		return null;
	}

	/**
	 * Stringify the given object. Depending on the stringing methods in this parser.
	 *
	 * @param object to be stringed
	 * @return a string representation of the object.
	 */
	public String stringify(Object object) {
		return this.stringify(object, "");
	}

	/**
	 * Stringify the given object. Depending on the stringing methods in this parser.
	 *
	 * @param object to be stringed
	 * @param shift  the shift that the string should have
	 * @return a string representation of the object.
	 */
	public String stringify(Object object, String shift) {
		if (object == null)
			return "null";
		if (object.getClass().isArray() && object.getClass().getComponentType().isPrimitive())
			object = Arrays.objective(object);

		Method method = this.queryStringingMethod(object.getClass());

		if (method != null)
			try {
				switch (method.getParameters().length) {
					case 1:
						return (String) method.invoke(this, object);
					case 2:
						return (String) method.invoke(this, object, shift);
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}

		return String.valueOf(object);
	}

	/**
	 * Navigate the {@link StringParser} class that the annotated method is a parsing method.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	protected @interface ParsingMethod {
	}

	/**
	 * Navigate the {@link StringParser} class that the annotated method is a stringing method.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	protected @interface StringingMethod {
	}

	/**
	 * Navigate the {@link StringParser} class that the annotated method is a string-type-detecting method.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	protected @interface SwitchingMethod {
		/**
		 * Tells what class the annotated method is looking for.
		 *
		 * @return the class the annotated method is looking for
		 */
		Class<?> value();
	}
}
