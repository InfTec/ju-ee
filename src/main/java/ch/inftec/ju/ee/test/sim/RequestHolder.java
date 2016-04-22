package ch.inftec.ju.ee.test.sim;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class RequestHolder {
	private Map<Class<?>, Deque<?>> reqs = new HashMap<>();

	private <T> Deque<T> getDeque(Class<T> clazz) {
		if (!this.reqs.containsKey(clazz)) {
			this.reqs.put(clazz, new ArrayDeque<>());
		}
		@SuppressWarnings("unchecked")
		Deque<T> deq = (Deque<T>) this.reqs.get(clazz);
		return deq;
	}

	/**
	 * Puts a request to the holder (at the end of the queue).
	 * 
	 * @param clazz
	 *            Type of the request
	 * @param req
	 *            Request
	 */
	public <T> void putRequest(Class<T> clazz, T req) {
		this.getDeque(clazz).add(req);
	}

	/**
	 * Polls a request from the holder (from the start at the queue, i.e. FIFO).
	 * <p>
	 * Removes the request from the queue.
	 * 
	 * @param clazz
	 *            Type of the request
	 * @return Request or null if there is none
	 */
	public <T> T pollRequest(Class<T> clazz) {
		return this.getDeque(clazz).poll();
	}

	/**
	 * Gets a request from the holder (from the start at the queue, i.e. FIFO) without removing it from the queue.
	 * 
	 * @param clazz
	 *            Type of the request
	 * @return Request or null if there is none
	 */
	public <T> T peekRequest(Class<T> clazz) {
		return this.getDeque(clazz).peek();
	}

	/**
	 * Gets the last request on the queue, i.e. the most recent request.
	 * <p>
	 * Removes all other requests.
	 * 
	 * @param clazz
	 * @return Request or null if there is none
	 */
	public <T> T pollLastRequest(Class<T> clazz) {
		T lastRequest = this.getDeque(clazz).pollLast();
		this.getDeque(clazz).clear();
		return lastRequest;
	}

	/**
	 * Gets a request from the holder (from the end of the queu, i.e. LIFO) without removing it from the queue.
	 * 
	 * @param clazz
	 *            Type of the request
	 * @return Last request or null if there is none
	 */
	public <T> T peekLastRequest(Class<T> clazz) {
		T lastRequest = this.getDeque(clazz).peekLast();
		return lastRequest;
	}

	/**
	 * Gets a request from the holder (from the end of the queu, i.e. LIFO) without removing it from the queue.
	 * 
	 * @param clazz
	 *            Type of the request
	 * @return Last request or null if there is none
	 */
	public <T> T peekFromEndWithOffset(Class<T> clazz, int offset) {
		if (offset >= this.getRequestCount(clazz)) return null;

		@SuppressWarnings("unchecked")
		T req = (T) this.getDeque(clazz).toArray()[this.getRequestCount(clazz) - 1 - offset];
		return req;
	}

	/**
	 * Gets the count of requests for the specified type.
	 * 
	 * @param clazz
	 * @return Count of requests of the specified type
	 */
	public <T> int getRequestCount(Class<T> clazz) {
		return this.getDeque(clazz).size();
	}
}
