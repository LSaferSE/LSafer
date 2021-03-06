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

import java.util.function.Function;

/**
 * Controllable loop. The concept is to do a block. Check if shall continue or not. Then do the next block and so on.
 *
 * @param <I> the type of the items'll be passed while looping
 * @author LSaferSE
 * @version 5 release (28-Sep-2019)
 * @since 18 May 2019
 */
public abstract class Loop<I> {
	/**
	 * A position for loops. Tells that the loop shall be paused
	 */
	final public static String PAUSE = "pause";
	/**
	 * A position for loops. Tells that the loop shall be resumed
	 */
	final public static String RESUME = "resume";
	/**
	 * A position for loops. Tells that the loop shall be stopped.
	 */
	final public static String STOP = "stop";

	/**
	 * The code to loop.
	 */
	protected Function<I, Boolean> block;
	/**
	 * Linking var.
	 */
	protected volatile boolean check = true;
	/**
	 * The position of this loop.
	 */
	protected volatile String position = RESUME;

	/**
	 * Initialize this.
	 *
	 * @param block the code to loop
	 */
	public Loop(Function<I, Boolean> block) {
		this.block = block;
	}

	/**
	 * Update the status of loop.
	 *
	 * @param position new status
	 */
	public synchronized void setPosition(String position) {
		this.check = true;
		this.position = position;
	}

	/**
	 * Made for loop original class. To tell the loop what it should do. If the position of the loop is "pause". Then it'll enter a loop until any new
	 * commands.
	 *
	 * @return if true the the loop shall continue else shall break
	 */
	protected synchronized boolean check() {
		if (!this.check) return true; //no updates
		this.check = false; //done reading it :)

		switch (this.position) {
			case Loop.RESUME:
				return true;
			case Loop.PAUSE:
				//noinspection ALL do nothing until next command
				while (!this.check) ;
				return this.check();  //to read the next command
			case Loop.STOP:
				return false; //break
			default:
				return false;
		}
	}

	/**
	 * Continue the next step of the loop.
	 *
	 * @param item to pass it to the next step
	 * @return whether allowed to continue the loop or not
	 */
	protected boolean next(I item) {
		return this.check() && this.block.apply(item);
	}

	/**
	 * The looping cod. call {@link #next(Object)} inside the loop to do the loop. Break the loop if it returned false.
	 *
	 * @see Foreach#start() foreach
	 * @see Limited#start() limited
	 * @see Forever#start() forever
	 */
	public abstract void start();

	/**
	 * Loop for each item of a list.
	 *
	 * @param <I> items Type
	 */
	public static class Foreach<I> extends Loop<I> {
		/**
		 * List of items to loop.
		 */
		protected Iterable<I> iterable;

		/**
		 * Initialize this.
		 *
		 * @param iterable of items to be looped foreach
		 * @param block    code to loop
		 */
		public Foreach(Iterable<I> iterable, Function<I, Boolean> block) {
			super(block);
			this.iterable = iterable;
		}

		@Override
		public void start() {
			for (I t : this.iterable)
				if (!this.next(t))
					break;
		}
	}

	/**
	 * Looping until get broken manually.
	 */
	public static class Forever extends Loop<Integer> {
		/**
		 * Initialize this.
		 *
		 * @param block the code to loop
		 */
		public Forever(Function<Integer, Boolean> block) {
			super(block);
		}

		@Override
		public void start() {
			for (int i = 0; ; i++)
				if (!this.next(i))
					break;
		}
	}

	/**
	 * Loop from a specific int to another int.
	 */
	public static class Limited extends Loop<Integer> {
		/**
		 * The int to stop before.
		 */
		protected int before;
		/**
		 * The int to start from.
		 */
		protected int from;

		/**
		 * Initialize this.
		 * <br>
		 * example for lists: from last to first ( size-1 , -1 , ...) from first to last ( 0 , size , ...)
		 *
		 * @param from   number to start from
		 * @param before number to stop before
		 * @param block  code to loop
		 */
		public Limited(int from, int before, Function<Integer, Boolean> block) {
			super(block);
			this.from = from;
			this.before = before;
		}

		@Override
		public void start() {
			if (this.from > this.before) {
				for (int i = this.from; i > this.before; i--) //reversed
					if (!this.next(i))
						break;
			} else {
				for (int i = this.from; i < this.before; i++) //strait
					if (!this.next(i))
						break;
			}
		}
	}
}
