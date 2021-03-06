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

import java.util.function.BiFunction;

/**
 * A factory that creates an Unused IDs (instance wise).
 *
 * @param <I> id type
 * @param <F> flavor type
 * @author LSaferSE
 * @version 3 release (04-Nov-19)
 * @since 07-Sep-19
 */
public class IDFactory<I, F> {
	/**
	 * The function to use to create new IDs.
	 */
	protected BiFunction<I, F[], I> creator;

	/**
	 * The last unused ID.
	 *
	 * <ul>
	 * <li>note: this field should be always refer to unused value</li>
	 * </ul>
	 */
	protected volatile I last;

	/**
	 * Initialize this.
	 *
	 * @param init   ID to start after
	 * @param creator the function to create new IDs; (old -> new)
	 */
	public IDFactory(I init, BiFunction<I, F[], I> creator) {
		this.last = init;
		this.creator = creator;
	}

	/**
	 * Get an unused ID by any user of this factory.
	 *
	 * @param flavors flavor type
	 * @return a new unused ID
	 */
	@SuppressWarnings("unchecked")
	public synchronized I next(F... flavors) {
		return this.last = this.creator.apply(this.last, flavors);
	}
}
