package world.camera;

import org.lwjgl.glfw.GLFW;

import main.input.Input;
import math.Vector2f;
import math.Vector3f;
import world.terrain.Terrain;

public class Camera {
	
	private Vector3f position;
	private float pitch;
	private float yaw;
	private float roll;
	
	private float sinX, sinY;
	private float normRoll, normPitch;
	
	private Vector2f last_mouse_pos;
	
	public Camera(int x, int y, int z){
		normRoll = 0.2f;
		normPitch = 0.01f;
		position = new Vector3f(x, y, z);
	}
	
	public void tick(Terrain t){
		if(Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			Vector2f pos = Input.getMousePos();
			if(last_mouse_pos != null) {
				if(pos.x>last_mouse_pos.x) {
					yaw+=(pos.x-last_mouse_pos.x)/2;
				} else if(pos.x<last_mouse_pos.x) {
					yaw-=(last_mouse_pos.x-pos.x)/2;
				}
				if(pos.y>last_mouse_pos.y) {
					pitch+=(pos.y-last_mouse_pos.y)/2;
				} else if(pos.y<last_mouse_pos.y) {
					pitch-=(last_mouse_pos.y-pos.y)/2;
				}
			}
			last_mouse_pos = pos;
		} else {
			last_mouse_pos = null;
		}
		
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_W)){
			position.z+=0.2f*Math.cos(Math.toRadians(getYaw()+180));
			position.x-=0.2f*Math.sin(Math.toRadians(getYaw()+180));
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_D)){
			position.z+=0.2f*Math.cos(Math.toRadians(getYaw()-90));
			position.x-=0.2f*Math.sin(Math.toRadians(getYaw()-90));
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_A)){
			position.z+=0.2f*Math.cos(Math.toRadians(getYaw()+90));
			position.x-=0.2f*Math.sin(Math.toRadians(getYaw()+90));
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_S)){
			position.z+=0.2f*Math.cos(Math.toRadians(getYaw()));
			position.x-=0.2f*Math.sin(Math.toRadians(getYaw()));
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_SPACE)){
			position.y+=0.2f;
		}
		
		
		roll=(float)(normRoll*Math.sin(sinX));
		sinX+=0.06;
		if(sinX>2f*Math.PI) {
			sinX=0;
		}
		
		pitch+=(float)(normPitch*Math.sin(sinY));
		sinY+=0.02;
		if(sinY>2f*Math.PI) {
			sinY=0;
		}
		
		float preferedY = t.heightAt(position.x, position.z)+4f;
		if(preferedY-position.y<0.2) {
			position.y-=0.2f;
			if(preferedY-position.y>0.2 && preferedY<position.y) {
				position.y=preferedY;
			}
		}
		if(preferedY-position.y>0.2) {
			position.y+=0.2f;
			/*if(preferedY-position.y<0.2 && preferedY>position.y) {
				position.y=preferedY;
			}*/
		}//position.y=20;
		//yaw+=0.1;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f v) {
		position = v;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	
}
