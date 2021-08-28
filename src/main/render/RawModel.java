package main.render;

public class RawModel {
	
	private int vaoID, vertexCount;
	private float height;

	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	
	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public void setModelHeight(float h) {
		if(height==0) {
			height = h;
		}
	}
	
	public float modelHeight() {
		return height;
	}
}
