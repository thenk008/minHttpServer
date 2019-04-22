package org.shareData.acceptor.tools;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class SuperMap<K, V> {
	// 高并发SuperMap api 介绍
	// SuperMAP<Integer,Object>,目前主键只支持整型数据
	// get()//获取元素,非线程安全，执行获取没有并发控制 但会监控PUT是否有并发
	// put()//插入元素，线程安全返回布尔值，若布尔值为TURE 则代表着前面的get()有并发情况，禁止写入。
	private static final boolean RED = true;
	private static final boolean BLACK = false;
	private Node<V> root;
	private class Node<V> {
		int key;
		V val;
		Node<V> left, right;
		int N;
		boolean color;
		private ReentrantReadWriteLock rwl;
		private AtomicReference<Integer> ato;
		private int nownub;
		private boolean together = false;// 是否处于并发状态

		public boolean isTogether() {
			return together;
		}

		public void setTogether(boolean together) {
			this.together = together;
		}

		public boolean unlock() {
			nownub = ato.get();
			return ato.compareAndSet(nownub, nownub + 1);
		}

		Node(int key, V val, int N, boolean color) {
			this.key = key;
			this.val = val;
			this.N = N;
			this.color = color;
			this.rwl = new ReentrantReadWriteLock();
			ato = new AtomicReference<Integer>();
			ato.set(0);
		}

		public ReentrantReadWriteLock getRwl() {
			return rwl;
		}

	}

	public synchronized boolean put(int key, V val) {
		root = put(root, key, val);
		root.color = BLACK;
		boolean bm = root.isTogether();
		root.setTogether(false);
		return bm;
	}

	public V get(int key) {
		V val = null;
		Node<V> node;
		node = root;
		boolean bm = true;
		do {
			if (node != null) {
				ReadLock lock = node.getRwl().readLock();
				lock.lock();
				int old = node.key;
				if (old > key) {
					node = node.left;
					if (node != null && key == 3) {
						System.out.println("1key==" + node.key);
					}
				} else if (old < key) {
					node = node.right;
					if (node != null && key == 3) {
						System.out.println("2key==" + node.key);
					}
				} else {
					// 更新并发值
					do {
						if (node.unlock()) {
							val = node.val;
							break;
						}
					} while (true);
					val = node.val;
					bm = false;
				}
				lock.unlock();
			} else {
				bm = false;
			}
		} while (bm);

		return val;
	}


	private Node<V> delN(Node<V> h) {
		// WriteLock writelock = h.getRwl().writeLock();
		// writelock.lock();
		if (h.left != null) {
			if (h.left.left != null) {
				System.out.println("aaaaaaa=" + h.left.right.key);
				exchangeLeft(h);// 将左节点的值替换给自己
				h = delN(h.left);
			} else if (h.left.right != null) {
				System.out.println("bbbbbbbbb");
				exchangeRight(h);// 将左节点的值替换给自己
				h = delN(h.right);
			} else {
				System.out.println("eeeeeeeeee==" + h.left.val);
				exchangeLeft(h);// 将左节点的值替换给自己
				System.out.println("eeeeeeeeee2==" + h.right.key);
				h.left = null;
			}
		} else if (h.right != null) {
			if (h.right.left != null) {
				System.out.println("cccccc");
				exchangeLeft(h);// 将左节点的值替换给自己
				h = delN(h.right);
			} else if (h.right.right != null) {
				System.out.println("dddddddd");
				exchangeRight(h);// 将左节点的值替换给自己
				h = delN(h.right);
			} else {
				exchangeRight(h);// 将左节点的值替换给自己
				System.out.println("fffffffff");
				h.right = null;
			}
		} else {
			System.out.println("gggggggggggg");
			h = null;
		}

		// writelock.unlock();
		return h;
	}

	void exchangeLeft(Node<V> x) {// 跟他的左子树交换值
		x.key = x.left.key;
		x.val = x.left.val;
	}

	void exchangeRight(Node<V> x) {// 跟他的左子树交换值
		x.key = x.right.key;
		x.val = x.right.val;
	}
    
	private Node<V> put(Node<V> h, int key, V val) {
		if (h == null) {
			return new Node<V>(key, val, 1, RED);
		}
		int old = h.key;
		if (key > old) {
			h.right = put(h.right, key, val);
			if (h.right != null && h.right.isTogether()) {// 产生并发
				h.right.setTogether(false);
				h.setTogether(true);
			}
		} else if (key < old) {
			h.left = put(h.left, key, val);
			if (h.left != null && h.left.isTogether()) {// 产生并发
				h.left.setTogether(false);
				h.setTogether(true);
			}
		} else {
			if (h.unlock()) {
				h.setTogether(false);
				h.val = val;
			} else {// 有读产生并发
				h.setTogether(true);
				System.out.println("该房间有读产生并发");
			}
		}
		WriteLock lock = h.getRwl().writeLock();
		lock.lock();
		if (isRed(h.right) && !isRed(h.left)) {
			h = rotateLeft(h);
		}
		if (isRed(h.left) && isRed(h.left.left)) {
			h = rotateRight(h);
		}
		if (isRed(h.left) && isRed(h.right)) {
			flipColors(h);
		}
		lock.unlock();
		return h;
	}

	public void delete(int key) {
		if (!isRed(root.left) && !isRed(root.right)) {
			root.color = RED;

		}
	}

	
    public void deleteMin() {
    	if(!isRed(root.left)&&!isRed(root.right)) {
    		root.color =RED;
    		
    	}
    	
    }
   
	private Node<V> moveRedLeft(Node<V> h) {
		flipColors(h);
		if (isRed(h.right.left)) {
			h.right = rotateRight(h.right);
			h = rotateLeft(h);
		}
		return h;
	}

	private Node<V> moveRedRight(Node<V> h) {
		flipColors(h);
		if (!isRed(h.left.left)) {
			h = rotateRight(h);
		}
		return h;
	}

	private boolean isRed(Node<V> x) {
		if (x == null) {
			return false;
		}
		return x.color == RED;
	}

	void flipColors(Node<V> h) {
		h.color = RED;
		h.left.color = BLACK;
		h.right.color = BLACK;
	}

	Node<V> rotateLeft(Node<V> h) {//
		Node<V> x = h.right;
		h.right = x.left;
		x.left = h;
		x.color = h.color;
		h.color = RED;
		x.N = h.N;
		return x;
	}

	Node<V> rotateRight(Node<V> h) {
		Node<V> x = h.left;
		h.left = x.right;
		x.right = h;
		x.color = h.color;
		h.color = RED;
		x.N = h.N;
		return x;
	}
}
