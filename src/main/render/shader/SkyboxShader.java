package main.render.shader;

import math.Maths;
import math.Matrix4f;
import world.camera.Camera;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "src/main/render/shader/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/main/render/shader/skyboxFragmentShader.txt";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera){
		Matrix4f m = Maths.createViewMatrix(camera);
		m.m30 = m.m31 = m.m32 = 0;
		super.loadMatrix(location_viewMatrix, m);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}