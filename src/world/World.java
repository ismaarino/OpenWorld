package world;


import main.render.Loader;
import main.render.MasterRenderer;
import main.render.ModelTexture;
import main.render.OBJLoader;
import main.render.RawModel;
import main.render.TexturedModel;
import math.Vector3f;
import world.camera.Camera;
import world.entity.Entity;
import world.entity.Light;
import world.entity.set.EntitySet;
import world.terrain.Terrain;
import world.water.WaterSurface;

public class World {
	
	public static Camera camera;
	
	public static Loader loader;
	
	private Terrain terrain;
	
	private WaterSurface watersurface;
	
	private EntitySet entities;
	
	private Light light;
	
	private MasterRenderer renderer;
	
	public World(Camera c) {
		
		loader = new Loader();

		camera = c;
		
		light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));
		
		terrain = new Terrain(300,10, loader, new ModelTexture(loader.loadTexture("./res/terrain.png"),false));
		//terrain = new Terrain("D:\\Users\\Downloads\\map.png",20,100);
		//terrain = new Terrain(100,100);
		camera.setPosition(new Vector3f(0,30,0));
		
		watersurface = new WaterSurface(0,0,terrain.amplitude()/2,terrain.size());
		
		RawModel tree_model = OBJLoader.loadObjModel("./res/tree.obj", loader);
		TexturedModel tree_textured = new TexturedModel(tree_model,new ModelTexture(loader.loadTexture("./res/tree.png"),true));
		
		RawModel rock_model = OBJLoader.loadObjModel("./res/boulder.obj", loader);
		TexturedModel rock_textured = new TexturedModel(rock_model,new ModelTexture(loader.loadTexture("./res/boulder.png"),true));
		
		RawModel fern_model = OBJLoader.loadObjModel("./res/fern.obj", loader);
		TexturedModel fern_textured = new TexturedModel(fern_model,new ModelTexture(loader.loadTexture("./res/fern.png"),true));
		
		RawModel grass_model = OBJLoader.loadObjModel("./res/grass.obj", loader);
		TexturedModel grass_textured = new TexturedModel(grass_model,new ModelTexture(loader.loadTexture("./res/grass.png"),true));
		
		entities = new EntitySet();
		/*Entity e = new Entity(0, 100, 0, 10);
		entities.add(e);*/
		float x, z;
		for(int i=0;i<terrain.size();i++) {
			x = (float) (Math.random()*(float)terrain.size()-1);
			z = (float) (Math.random()*(float)terrain.size()-1);
			entities.add(new Entity(tree_textured, new Vector3f(x,terrain.heightAt(x, z),z),0,(float)Math.random()*360f,0,0.2f));
		}
		
		for(int i=0;i<terrain.size()*2;i++) {
			x = (float) (Math.random()*(float)(terrain.size()-1));
			z = (float) (Math.random()*(float)(terrain.size()-1));
			entities.add(new Entity(fern_textured, new Vector3f(x,terrain.heightAt(x, z),z),0,0,0,0.6f));
		}
		
		for(int i=0;i<terrain.size();i++) {
			x = (float) (Math.random()*(float)(terrain.size()-1));
			z = (float) (Math.random()*(float)(terrain.size()-1));
			entities.add(new Entity(rock_textured, new Vector3f(x,terrain.heightAt(x, z),z),(float)Math.random()*360f,(float)Math.random()*360f,(float)Math.random()*360f,(float)Math.random()*0.6f));
		}
		
		for(int i=0;i<terrain.size()*300;i++) {
			x = (float) (Math.random()*(float)(terrain.size()-1));
			z = (float) (Math.random()*(float)(terrain.size()-1));
			entities.add(new Entity(grass_textured, new Vector3f(x,terrain.heightAt(x, z),z),0,(float)Math.random()*360f,0,0.8f));
		}
		
		renderer = new MasterRenderer();
		
		
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public void tick() {
		terrain.tick();
		entities.tick();
		camera.tick(terrain);
	}
	
	public void render() {
		//entities.renderShadows(renderer, light);
		renderer.processTerrain(terrain);
		entities.render(renderer);
		watersurface.render(renderer);
		renderer.render(light, camera);
	}

}
