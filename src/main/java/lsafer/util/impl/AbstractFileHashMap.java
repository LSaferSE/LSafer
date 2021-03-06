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
package lsafer.util.impl;

import lsafer.io.File;
import lsafer.io.FileMap;

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract to implement needed methods in the interfaces {@link FileMap} to {@link HashMap}.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 2 release (02-Nov-19)
 * @since 28-Sep-19
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractFileHashMap<K, V> extends HashMap<K, V> implements FileMap<K, V> {
	/**
	 * The targeted file.
	 */
	protected File file;

	/**
	 * Default constructor.
	 */
	public AbstractFileHashMap() {
	}

	/**
	 * Constructs an empty HashMap with the specified initial capacity and the default load factor (0.75).
	 *
	 * @param initialCapacity the initial capacity
	 * @throws IllegalArgumentException if the initial capacity is negative.
	 */
	public AbstractFileHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs an empty HashMap with the specified initial capacity and load factor.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor      the load factor
	 * @throws IllegalArgumentException if the initial capacity is negative or the load factor is nonpositive
	 */
	public AbstractFileHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructs a new HashMap with the same mappings as the specified Map. The HashMap is created with default load factor (0.75) and an initial
	 * capacity sufficient to hold the mappings in the specified Map.
	 *
	 * @param map the map whose mappings are to be placed in this map
	 * @throws NullPointerException if the specified map is null
	 */
	public AbstractFileHashMap(Map<? extends K, ? extends V> map) {
		super(map);
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public File setFile(File file) {
		File old = this.file;
		this.file = file;
		return old;
	}
}
