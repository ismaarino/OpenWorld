package main;


import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import main.input.Input;
import world.World;
import world.camera.Camera;

public class OpenWorldEngine extends Thread{
	
	private static final String title = "Open World Project";
	public static final int WIDTH = 900;
	public static final int HEIGHT = (int)((float)WIDTH*0.7f);
	
	public static final int FPS = 60;
	public static final int TICKTIME = 1000/FPS;
	
	public static final float[] SKY_COLOR = {0.7f, 0.7f, 0.8f};
	
	
	public static int renderDistance = 220;
	
	public static Camera camera;
	
	
	private World world;
	
	private Input input;
	
	private double ltime, lttime;
	private long window;

	public void run() {
		init();
		loop();
		destroy();
	}

	public static void main(String[] args) {
		new OpenWorldEngine().start();
	}
	
	private void init() {
		
		if(!GLFW.glfwInit()) {
			return;
		}
		
		
		input = new Input();
		
		
		window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, title, 0, 0);
		if(window==0) {
			return;
		}
		
		GLFWVidMode videomode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(window, videomode.width()/2, (videomode.height()-HEIGHT)/2);
		GLFW.glfwSetKeyCallback(window, input.getKeyboardCallback());
		GLFW.glfwSetCursorPosCallback(window, input.getMouseMoveCallback());
		GLFW.glfwSetMouseButtonCallback(window, input.getMouseButtonsCallback());
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		
		GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8);
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		camera = new Camera(50,10,50);
		world = new World(camera);
		
		
		GLFW.glfwShowWindow(window);
		
	}

	private void loop() {
		while(!GLFW.glfwWindowShouldClose(window)) {
			ltime = System.currentTimeMillis();
			GLFW.glfwPollEvents();
			GLFW.glfwSwapBuffers(window);
			world.tick();
			world.render();
			while(System.currentTimeMillis()-ltime<TICKTIME) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					return;
				}
			}
			tickWindowTitle(ltime);
		}
	}
	
	private void destroy() {
		input.destroy();
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}
	
	private void tickWindowTitle(double t) {
		if(System.currentTimeMillis()-lttime>1000 || lttime==0) {
			GLFW.glfwSetWindowTitle(window, title+" | "+(int)(1000/(System.currentTimeMillis()-t))+" fps");
			lttime=System.currentTimeMillis();
		}
	}
	

}
