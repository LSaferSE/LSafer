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

import lsafer.io.IOMap;
import lsafer.util.JSObject;

import java.io.Serializable;

/**
 * An abstract for structures with the needed methods for the interfaces {@link JSObject}, {@link Serializable} and {@link IOMap}.
 *
 * @param <R> type of the remote of the third IO-port container.
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 1 release (28-Sep-19)
 * @since 28-Sep-19
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractIOJSObject<R, K, V> extends AbstractJSObject<K, V> implements IOMap<R, K, V> {
	/**
	 * The 3rd IO-container's remote.
	 */
	protected R remote;

	@Override
	public R remote() {
		return this.remote;
	}

	@Override
	public R remote(R remote) {
		R old = this.remote();
		this.remote = remote;
		return old;
	}
}
