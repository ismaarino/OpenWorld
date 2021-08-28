package world.entity.set;


import main.OpenWorldEngine;
import main.render.MasterRenderer;
import world.entity.Entity;
import world.entity.Light;

public class EntitySet {
	
	private Node set;
	
	public EntitySet() {
		set = null;
	}
	
	public void add(Entity e) {
		set = new Node(e, set);
	}
	
	public void delete(int id) {
		Node last = null, n = set;
		boolean del = false;
		while(!del && n!=null) {
			if(n.getEntity().getId()==id) {
				last.setNext(n.next());
				del = true;
			}
			last = n;
			n = n.next();
		}
	}
	
	public void tick() {
		Node n = set;
		while(n!=null) {
			n.getEntity().tick();
			n = n.next();
		}
	}
	
	public void renderShadows(MasterRenderer renderer, Light light) {
		Node n = set;
		float distOffset;
		while(n!=null) {
			distOffset = n.getEntity().getWorldModelHeight()*4<OpenWorldEngine.renderDistance/2 ? n.getEntity().getWorldModelHeight()*4 : OpenWorldEngine.renderDistance/2;
			if(n.getEntity().distanceOfCamera()<OpenWorldEngine.renderDistance-(OpenWorldEngine.renderDistance/2-distOffset)) {
				renderer.renderShadowMap(n.getEntity(), light);
			}
			n = n.next();
		}
	}
	
	public void render(MasterRenderer renderer) {
		Node n = set;
		float distOffset;
		while(n!=null) {
			distOffset = n.getEntity().getWorldModelHeight()*4<OpenWorldEngine.renderDistance/2 ? n.getEntity().getWorldModelHeight()*4 : OpenWorldEngine.renderDistance/2;
			if(n.getEntity().distanceOfCamera()<OpenWorldEngine.renderDistance-(OpenWorldEngine.renderDistance/2-distOffset)) {
				renderer.processEntity(n.getEntity());
			}
			n = n.next();
		}
	}

}
