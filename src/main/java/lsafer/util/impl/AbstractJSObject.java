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

import lsafer.util.JSObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An abstract to implement needed methods in the interfaces {@link JSObject} and {@link Serializable}.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSafer
 * @version 14 release (02-Nov-2019)
 * @since 11 Jun 2019
 **/
@SuppressWarnings({"unused"})
public abstract class AbstractJSObject<K, V> implements JSObject<K, V>, Serializable {
	/**
	 * The secondary container.
	 */
	protected transient Map<K, JSObject.Entry<K, V>> entries = new HashMap<>();

	@Override
	public Map<K, JSObject.Entry<K, V>> entries() {
		return this.entries;
	}

	@Override
	public String toString() {
		Iterator entries = this.entrySet().iterator();

		if (!entries.hasNext()) {
			return "{}";
		} else {
			StringBuilder builder = new StringBuilder("{");

			while (true) {
				Map.Entry entry = (Map.Entry) entries.next();
				Object key = entry.getKey();
				Object value = entry.getValue();

				builder.append(key == this ? "(this Map)" : key);
				builder.append('=');
				builder.append(value == this ? "(this Map)" : value);

				if (!entries.hasNext()) {
					return builder.append('}').toString();
				}

				builder.append(',').append(' ');
			}
		}
	}

	/**
	 * Backdoor initializing method, or custom deserialization method.
	 *
	 * @param stream to initialize this using
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 * @throws IOException            if an I/O error occurs.
	 */
	private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
		this.entries = this.entries == null ? new HashMap<>() : this.entries;
		int size = stream.readInt();

		for (int i = 0; i < size; i++) {
			K key = (K) stream.readObject();
			V value = (V) stream.readObject();
			this.put(key, value);
		}
	}

	/**
	 * Custom JSObject serialization method.
	 *
	 * @param stream to use to serialize this
	 * @throws IOException if an I/O error occurs
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		Set<Map.Entry<K, V>> entries = this.entrySet();
		stream.writeInt(entries.size());

		for (Map.Entry<K, V> entry : entries) {
			stream.writeObject(entry.getKey());
			stream.writeObject(entry.getValue());
		}
	}
}
