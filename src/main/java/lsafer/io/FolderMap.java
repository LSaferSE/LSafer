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
package lsafer.io;

import lsafer.java.SERB64;
import lsafer.json.JSON;
import lsafer.microsoft.INI;
import lsafer.util.Configurable;
import lsafer.util.impl.FolderHashMap;
import lsafer.util.impl.ParsedFileHashMap;

import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static lsafer.io.File.PROCESS_CANCELED;
import static lsafer.io.File.PROCESS_FAILED;
import static lsafer.io.FileException.NOT_DIRECTORY;
import static lsafer.io.FileException.NOT_EXIST;

/**
 * A {@link Map} that is linked to {@link File Folder} as it's IO-Container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 9 release (02-Nov-19)
 * @since 19-Jul-19
 */
@FolderMap.Configurations
public interface FolderMap<K, V> extends FileMap<K, V>, Configurable {
	@Override
	default void move(File.Synchronizer<?, ?> synchronizer, java.io.File output) {
		FileMap.super.move(synchronizer, output);
		this.setFiles();
	}

	@Override
	default void rename(File.Synchronizer<?, ?> synchronizer, String name) {
		FileMap.super.rename(synchronizer, name);
		this.setFiles();
	}

	@Override
	default void save(File.Synchronizer<?, ?> synchronizer) {
		//<editor-fold desc="synchronizer.bind()">
		this.getFile().setMaxProgress((long) this.size());
		this.getFile().setProgress(0L);
		synchronizer.bind();
		//</editor-fold>
		this.forEach((key, value) -> {
			if (value instanceof FileMap) {
				((FileMap<?, ?>) value).setFile(file -> file == null ? this.getFile().child(String.valueOf(key)) : file);
				((FileMap<?, ?>) value).save(synchronizer);
			}
			this.getFile().progressed();
			synchronizer.bind();
		});
	}

	@Override
	default Map<K, V> read(File.Synchronizer<?, ?> synchronizer) {
		if (synchronizer.handle(!this.getFile().exists(), NOT_EXIST, this.getFile()) <= PROCESS_FAILED ||
			synchronizer.handle(!this.getFile().isDirectory(), NOT_DIRECTORY, this.getFile()) <= PROCESS_FAILED)
			return null;

		Configurations configurations = this.configurations(Configurations.class, FolderMap.class);
		String[] children = this.getFile().list();
		Map<K, V> map = new HashMap<>();

		//<editor-fold desc="synchronizer.bind()">
		this.getFile().setMaxProgress((long) (children == null ? 0 : children.length));
		this.getFile().setProgress(0L);
		synchronizer.in(this.getFile());
		synchronizer.bind();
		//</editor-fold>

		if (children != null)
			for (String child : children) {
				V value = this.get(child);
				map.put((K) child, (value instanceof FileMap ? value : (V) this.newInstanceFor(new File(child))));
				//<editor-fold desc="synchronizer.bind()">
				this.getFile().progressed();
				synchronizer.bind();
				//</editor-fold>
			}

		return map;
	}

	@Override
	default void write(File.Synchronizer<?, ?> synchronizer, Map<K, V> map) {
	}

	/**
	 * Load this. Then load all {@link FileMap}s mapped on this as a value.
	 *
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 * @param <F>          this
	 * @return this
	 */
	default <F extends FolderMap<K, V>> F loadAll(File.Synchronizer<?, ?> synchronizer) {
		this.load(synchronizer);

		if (synchronizer.status <= PROCESS_CANCELED)
			return (F) this;

		//<editor-fold desc="synchronizer.bind()">
		//progress_max already have been defined
		this.getFile().setProgress(0L);
		synchronizer.in(this.getFile());
		synchronizer.bind();
		//</editor-fold>
		this.forEach((key, value) -> {
			if (value instanceof FileMap) {
				((FileMap) value).setFile(file -> file == null ? this.getFile().child(String.valueOf(key)) : file);

				if (value instanceof FolderMap)
					((FolderMap<?, ?>) value).loadAll(synchronizer);
				else ((FileMap<?, ?>) value).load(synchronizer);

				//<editor-fold desc="synchronizer.bind()">
				this.getFile().progressed();
				synchronizer.bind();
				//</editor-fold>
			}
		});

		return (F) this;
	}

	/**
	 * Get a new {@link FileMap} instance for the given file. This method like an instance creator switch. To get the perfect instance class. For that
	 * specific file given. The new instance will not be touched by this. This object will just create it (the new instance) then return it as a
	 * results.
	 *
	 * @param file to get a new instance for
	 * @return a new instance for the given file
	 */
	default FileMap newInstanceFor(File file) {
		try {
			Configurations configurations = this.configurations(Configurations.class, FolderMap.class);
			Function<File, File> FILE = f -> file;

			if (file.isDirectory())
				try {
					return configurations.folder().getConstructor(Class.class, Class.class)
							.newInstance(configurations.folder(), configurations.file()).setFile(FILE);
				} catch (NoSuchMethodException ignored) {
					return configurations.folder().getConstructor().newInstance().setFile(FILE);
				}
			if (configurations.file() == FileMap.class)
				switch (file.getExtension().toLowerCase()) {
					default:
					case "json":
						return new ParsedFileHashMap<>(JSON.global).setFile(FILE);
					case "ini":
						return new ParsedFileHashMap(INI.global).setFile(FILE);
					case "serb64":
						return new ParsedFileHashMap(SERB64.global).setFile(FILE);
				}
			return configurations.file().getConstructor().newInstance().setFile(FILE);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the file of this. To every {@link FileMap} contained in this.
	 *
	 * @param <F> this
	 * @return this
	 */
	default <F extends FolderMap<K, V>> F setFiles() {
		this.forEach((key, value) -> {
			if (value instanceof FileMap)
				((FileMap<?, ?>) value).setFile(this.getFile().child(String.valueOf(key)));
			if (value instanceof FolderMap)
				((FolderMap<?, ?>) value).setFiles();
		});
		return (F) this;
	}

	/**
	 * Set the default values for the targeted folder-map.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.TYPE_USE})
	@Inherited
	@interface Configurations {
		/**
		 * The default file-map to initialize. for files found but no matching fields for them.
		 *
		 * @return default file-map class
		 */
		Class<? extends FileMap> file() default FileMap.class;

		/**
		 * The default folder-map to initialize. for folders found but no matching fields for them.
		 *
		 * @return default folder-map class
		 */
		Class<? extends FolderMap> folder() default FolderHashMap.class;
	}
}
