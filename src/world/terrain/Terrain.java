package world.terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import main.render.Loader;
import main.render.ModelTexture;
import main.render.RawModel;
import math.Maths;
import math.Vector2f;
import math.Vector3f;

public class Terrain {
	
	private float[][] mesh;
	
	private int x, y, z, seed, w, h,  a, max, min;
	private float ppp;
	
	private RawModel model;
	private ModelTexture texture;
	private static int[] dx = {0,-1,0,1,1,1,0,-1,-1};
	private static int[] dz = {0,-1,-1,-1,0,1,1,1,0};
	
	private Random random;
	
	public Terrain(int width, int height, Loader loader) {
		x = y = z = 0;
		w = width;
		h = height;
		ppp = 100;
		max=0;
		min=0;
		mesh = new float[w][h];
		for(int z=0;z<h;z++) {
			for(int x=0;x<w;x++) {
				mesh[x][z]=0;
			}
		}
		model = generateModel(loader);
	}
	
	public Terrain(String path, int amplitude, int units_per_vertex, Loader loader) {
		try {
			BufferedImage image = ImageIO.read(new File(path));
			x = y = z = 0;
			w = image.getWidth();
			h = image.getHeight();
			ppp = units_per_vertex;
			a = amplitude;
			mesh = new float[w][h];
			int c;
			float rgbsum;
			max=-a/2;
			min=a/2;
			for(int z=0;z<h;z++) {
				for(int x=0;x<w;x++) {
					c = image.getRGB(x, z);
					rgbsum = ((c>>16)&0xFF) + ((c>>8)&0xFF) + (c&0xFF);
					if(rgbsum<0) {
						rgbsum=0;
					} else if(rgbsum>255*3) {
						rgbsum=255*3;
					}
					mesh[x][z]=(int)((float)a*(rgbsum/(255f*3f)))-a/2;
					if(mesh[x][z]>max) {
						max=(int)mesh[x][z];
					} else if(mesh[x][z]<min) {
						min=(int)mesh[x][z];
					}
				}
			}
			model = generateModel(loader);
		} catch (IOException e) {
			//
		}
		
	}
	
	public Terrain(int size, int maxh, Loader loader, ModelTexture texture) {
		x = y = z = 0;
		seed = (int)(Math.random()*1000000f);
		random = new Random();
		h = w = (int) (ppp = size);
		a = maxh;
		max=-a/2;
		min=a/2;
		mesh = new float[w][h];
		for(int z=0;z<h;z++) {
			for(int x=0;x<w;x++) {
				mesh[x][z]=calcYfor(x,z);
				if(mesh[x][z]>max) {
					max=(int)mesh[x][z];
				} else if(mesh[x][z]<min) {
					min=(int)mesh[x][z];
				}
			}
		}
		this.texture = texture;
		model = generateModel(loader);
	}
	
	private Vector3f calcNorm(int x, int z) {
		float heightL = calcYfor(x-1,z);
		float heightR = calcYfor(x+1,z);
		float heightD = calcYfor(x,z-1);
		float heightU = calcYfor(x,z+1);
		Vector3f n = new Vector3f(heightL-heightR, 2f, heightD-heightU);
		n.normalize();
		return n;
	}
	
	private RawModel generateModel(Loader loader) {
		int count = w * h;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(w-1)*(h-1)];
		int vertexPointer = 0;
		Vector3f normal;
		for(int i=0;i<h;i++){
			for(int j=0;j<w;j++){
				vertices[vertexPointer*3] = (float)j/((float)w - 1) * ppp;
				vertices[vertexPointer*3+1] = mesh[j][i];
				vertices[vertexPointer*3+2] = (float)i/((float)h - 1) * ppp;
				normal = calcNorm(j, i);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)w - 1)* ppp;
				textureCoords[vertexPointer*2+1] = (float)i/((float)h - 1)* ppp;
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<h-1;gz++){
			for(int gx=0;gx<w-1;gx++){
				int topLeft = (gz*w)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*h)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	private float calcYfor(int x, int z) {
		float total = getInterpolatedNoise((float)x/20f, (float)z/20f)*(float)a;
		total += getInterpolatedNoise((float)x/10f, (float)z/10f)*(float)a/3f;
		total += getInterpolatedNoise((float)x/5f, (float)z/5f)*(float)a/9f;
		total += getInterpolatedNoise((float)x/2.5f, (float)z/2.5f)*(float)a/18f;
		return total;
	}
	
	private float smoothNoiseOf(int x, int z) {
		
		int sumq=0;
		float sum=0;
		for(int i=0;i<dx.length;i++) {
			sum+=noiseOf(x+dx[i], z+dz[i]);
			sumq++;
		}
		return sum/(float)sumq;
	}
	
	private float noiseOf(int x, int z) {
		random.setSeed(x*50000 + z*400000 + seed);
		return random.nextFloat()*2f - 1f;
	}
	
	private float interpolate(float a, float b, float blend) {
		double theta = blend*Math.PI;
		float f = (float)(1f-Math.cos(theta))*0.5f;
		return a*(1f-f)+b*f;
	}
	
	private float getInterpolatedNoise(float x, float z) {
		int intX = (int)x;
		int intZ = (int)z;
		float fracX = x-intX;
		float fracZ = z-intZ;
		float v1 = smoothNoiseOf(intX, intZ);
		float v2 = smoothNoiseOf(intX+1, intZ);
		float v3 = smoothNoiseOf(intX, intZ+1);
		float v4 = smoothNoiseOf(intX+1, intZ+1);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
	}
	
	public int pixelsPerUnit() {
		return (int)ppp;
	}
	
	public int size() {
		return w;
	}
	
	public float heightAt(float x, float z) {
		int intX = (int)x;
		int intZ = (int)z;
		float r=0;
		float gsq = (float)size()/(float)(mesh.length-1);
		float xcord = (x%gsq)/gsq;
		float zcord = (z%gsq)/gsq;
		if(x<w-1 && z<h-1 && x>=0 && z>=0) {
			if(xcord<=1f-zcord) {
				r = Maths.barryCentric(new Vector3f(0, mesh[intX][intZ], 0),
									new Vector3f(1, mesh[intX + 1][intZ], 0),
									new Vector3f(0, mesh[intX][intZ + 1], 1),
									new Vector2f(xcord, zcord));
			} else {
				r = Maths.barryCentric(new Vector3f(1, mesh[intX+1][intZ], 0),
						new Vector3f(1, mesh[intX + 1][intZ+1], 1),
						new Vector3f(0, mesh[intX][intZ + 1], 1),
						new Vector2f(xcord, zcord));
			}
		}
		//return mesh[(int)x][(int)z];//r;
		return r;
	}
	
	public void tick() {
		
	}
	
	public int amplitude() {
		return a;
	}
	

	public RawModel getModel() {
		return model;
	}

	public ModelTexture getTexture() {
		return texture;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

}
