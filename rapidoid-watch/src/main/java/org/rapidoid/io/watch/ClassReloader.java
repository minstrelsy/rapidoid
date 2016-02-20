package org.rapidoid.io.watch;

/*
 * #%L
 * rapidoid-watch
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class ClassReloader extends ClassLoader {

	private final List<String> names;
	private final Collection<String> classpath;
	private final ClassLoader parent;

	public ClassReloader(Collection<String> classpath, ClassLoader parent, List<String> names) {
		super(parent);
		this.classpath = classpath;
		this.parent = parent;
		this.names = names;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {

		String filename = getClassFilename(name);

		if (filename != null) {
			Log.debug("Hot swap", "file", filename);
			return reload(name, filename);
		} else {
			return super.findClass(name);
		}
	}

	private Class<?> reload(String name, String filename) throws ClassNotFoundException {
		for (int i = 0; i < 100; i++) {
			try {
				byte[] classData = IO.loadBytes(filename);
				return defineClass(name, classData, 0, classData.length);
			} catch (ClassFormatError e) {
				// wait some time and retry again...
				U.sleep(50);
			}
		}

		throw new ClassNotFoundException("Couldn't find class: " + name);
	}

	private String getClassFilename(String name) {
		String filename = findOnClasspath(name);
		if (filename != null) return filename;

		URL res = parent.getResource(getClassRelativePath(name));
		return res != null ? getFilename(res) : null;
	}

	private String findOnClasspath(String name) {
		for (String dir : classpath) {
			File ff = new File(dir, getClassRelativePath(name));

			if (ff.exists()) {
				return ff.getAbsolutePath();
			}
		}
		return null;
	}

	private String getFilename(URL res) {
		try {
			return res.toURI().getPath();
		} catch (URISyntaxException e) {
			throw U.rte(e);
		}
	}

	public Class<?> loadClass(String classname) throws ClassNotFoundException {
		Log.debug("Loading class", "name", classname);

		if (names.contains(classname) || (!classname.startsWith("org.rapidoid.") && !Cls.isJREClass(classname)
				&& findOnClasspath(classname) != null)) {

			try {
				return findClass(classname);
			} catch (ClassNotFoundException e) {
				Class<?> fallbackClass = super.loadClass(classname);
				Log.debug("Couldn't reload class, fallback load", "name", classname);
				return fallbackClass;
			}
		} else {
			return super.loadClass(classname);
		}
	}

	private static String getClassRelativePath(String classname) {
		return classname.replace('.', File.separatorChar) + ".class";
	}

	public void add(List<String> classnames) {
		names.addAll(classnames);
	}

}
