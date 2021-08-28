package main.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import main.OpenWorldEngine;
import main.render.shader.StaticShader;
import main.render.shader.TerrainShader;
import main.render.shader.WaterShader;
import main.render.shadow.ShadowMapMasterRenderer;
import math.Matrix4f;
import world.World;
import world.camera.Camera;
import world.entity.Entity;
import world.entity.Light;
import world.terrain.Terrain;
import world.water.WaterTile;

public class MasterRenderer {
	
	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 6000;
	
	private Matrix4f projectionMatrix;
	
	private ShadowMapMasterRenderer shadowMapRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private WaterRenderer waterRenderer;
	private WaterShader waterShader = new WaterShader();
	
	
	
	private Map<TexturedModel,List<Entity>> entities = new HashMap<TexturedModel,List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<WaterTile> waters = new ArrayList<WaterTile>();
	
	public MasterRenderer(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		createProjectionMatrix();
		//shadowMapRenderer = new ShadowMapMasterRenderer(OpenWorldEngine.camera);
		renderer = new EntityRenderer(shader,projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader,projectionMatrix);
		waterRenderer = new WaterRenderer(World.loader,waterShader,projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(World.loader,projectionMatrix);
	}
	
	public int getShadowMapTexture() {
		return shadowMapRenderer.getShadowMap();
	}
	
	public void renderShadowMap(Entity e, Light sun) {
		processEntity(e);
		//shadowMapRenderer.render(entities,  sun);
	}
	
	public void render(Light sun,Camera camera){
		prepare();
		shader.start();
		shader.loadSkyColour(OpenWorldEngine.SKY_COLOR[0], OpenWorldEngine.SKY_COLOR[1], OpenWorldEngine.SKY_COLOR[2]);
		shader.loadLight(sun);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		
		terrainShader.start();
		terrainShader.loadSkyColour(OpenWorldEngine.SKY_COLOR[0], OpenWorldEngine.SKY_COLOR[1], OpenWorldEngine.SKY_COLOR[2]);
		terrainShader.loadLight(sun);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains, null/*shadowMapRenderer.getToShadowMapSpaceMatrix()*/);
		terrainShader.stop();
		
		waterShader.start();
		waterRenderer.render(waters, camera);
		waterShader.stop();
		
		skyboxRenderer.render(camera);
		
		waters.clear();
		terrains.clear();
		entities.clear();
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);		
		}
	}
	
	public void processWater(WaterTile w) {
		waters.add(w);
	}
	
	public void cleanUp(){
		shader.cleanUp();
		terrainShader.cleanUp();
		waterShader.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(OpenWorldEngine.SKY_COLOR[0], OpenWorldEngine.SKY_COLOR[1], OpenWorldEngine.SKY_COLOR[2], 1);
		/*GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());*/
	}
	
	private void createProjectionMatrix() {
		float aspectRatio = (float) OpenWorldEngine.WIDTH / (float) OpenWorldEngine.HEIGHT;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
}
