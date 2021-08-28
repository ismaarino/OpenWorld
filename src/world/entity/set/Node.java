package world.entity.set;

import world.entity.Entity;

public class Node {
	
	private Entity ths;
	private Node next;
	
	public Node(Entity e) {
		ths = e;
	}
	
	public Node(Entity e, Node n) {
		ths = e;
		next = n;
	}
	
	public Entity getEntity() {
		return ths;
	}
	
	public void setNext(Node n) {
		next = n;
	}
	
	public Node next() {
		return next;
	}
}
