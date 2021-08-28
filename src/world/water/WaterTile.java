package world.water;

import math.Vector3f;

public class WaterTile {
	
	
	public static int SIZE = 100;
	private float height;
	private int x,z;
	
	public WaterTile(int centerX, int centerZ, float height){
		this.x = centerX;
		this.z = centerZ;
		this.height = height;
	}
	
	public Vector3f getPosition() {
		return new Vector3f(x,height,z);
	}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}



}
