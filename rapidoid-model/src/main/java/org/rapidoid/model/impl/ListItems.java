package org.rapidoid.model.impl;

/*
 * #%L
 * rapidoid-model
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Property;
import org.rapidoid.util.U;

public class ListItems implements Items {

	private final List<Item> list = new ArrayList<Item>();

	@Override
	public void insert(int index, Item item) {
		list.add(index, ifFitsIn(item));
	}

	@Override
	public void add(Item item) {
		list.add(ifFitsIn(item));
	}

	@Override
	public void addAll(Items items) {
		for (int i = 0; i < items.size(); i++) {
			list.add(ifFitsIn(items.get(i)));
		}
	}

	@Override
	public void addAll(List<Item> items) {
		for (Item item : items) {
			list.add(ifFitsIn(item));
		}
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public Item get(int index) {
		return list.get(index);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public void remove(int index) {
		list.remove(index);
	}

	@Override
	public void set(int index, Item item) {
		list.set(index, ifFitsIn(item));
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Items range(int fromIndex, int toIndex) {
		ListItems subitems = new ListItems();
		subitems.addAll(list.subList(fromIndex, toIndex));
		return subitems;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> properties() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean fitsIn(Item item) {
		return item.value() != null;
	}

	protected final Item ifFitsIn(Item item) {
		U.must(fitsIn(item), "This item doesn't fit in the items: %s", item);
		return item;
	}

	@Override
	public Iterator<Item> iterator() {
		return list.iterator();
	}

}
