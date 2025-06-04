package net.saint.acclimatize.library.common;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RingBuffer<T> implements Iterable<T> {

	// Properties

	private final T[] buffer;
	private final int capacity;
	private int head;
	private int tail;
	private int size;

	// Init

	@SuppressWarnings("unchecked")
	public RingBuffer(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("Can not create ring buffer with negative or zero capacity.");
		}

		this.capacity = capacity;
		this.buffer = (T[]) new Object[capacity];
		this.head = 0;
		this.tail = 0;
		this.size = 0;
	}

	// Iterator

	@Override
	public Iterator<T> iterator() {
		return new RingBufferIterator();
	}

	private class RingBufferIterator implements Iterator<T> {
		private int current = head;
		private int count = 0;

		@Override
		public boolean hasNext() {
			return this.count < RingBuffer.this.size;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			var item = buffer[current];

			this.current = (current + 1) % capacity;
			this.count++;

			return item;
		}
	}

	// Access

	/**
	 * Enqueue an item into the ring buffer.
	 */
	public void enqueue(T item) {
		this.buffer[this.tail] = item;
		this.tail = (this.tail + 1) % this.capacity;

		if (this.size == this.capacity) {
			this.head = (this.head + 1) % this.capacity;
		} else {
			this.size++;
		}
	}

	public T dequeue() {
		if (isEmpty()) {
			// Can not dequeue from an empty buffer.
			return null;
		}

		var item = this.buffer[this.head];
		this.buffer[this.head] = null;

		this.head = (this.head + 1) % this.capacity;
		this.size--;

		return item;
	}

	public void fill(T item) {
		while (!isFull()) {
			enqueue(item);
		}
	}

	public T peek() {
		if (isEmpty()) {
			// Can not peek into an empty buffer.
			return null;
		}

		return this.buffer[this.head];
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public boolean isFull() {
		return this.size == this.capacity;
	}

	public int size() {
		return this.size;
	}

	public int capacity() {
		return this.capacity;
	}

	public void clear() {
		for (int i = 0; i < this.capacity; i++) {
			this.buffer[i] = null;
		}

		this.head = 0;
		this.tail = 0;
		this.size = 0;
	}
}
