package world.water;

import main.OpenWorldEngine;
import main.render.MasterRenderer;
import math.Maths;

public class WaterSurface {
	
	private WaterTile[] surface;
	private int size;
	
	public WaterSurface(int cx, int cz, float h, int size) {
		//surface = new WaterTile(cx, cz, h, size);
		this.size = size;
		surface = new WaterTile[(int)(Math.pow(size, 2)/Math.pow(WaterTile.SIZE, 2))];
		int x=cx-size/2, z=cz-size/2;
		for(int i=0;i<surface.length;i++) {
			surface[i]=new WaterTile(x, z, h);
			if(x<cx+size/2) {
				x++;
			} else {
				x=0;
				z++;
			}
		}
	}
	
	public float height() {
		return surface[0].getHeight();
	}
	
	public void render(MasterRenderer renderer) {
		for(int i=0;i<surface.length;i++) {
			if(Maths.distance(OpenWorldEngine.camera.getPosition(), surface[i].getPosition())<OpenWorldEngine.renderDistance) {
				renderer.processWater(surface[i]);
			}
		}
	}

}
