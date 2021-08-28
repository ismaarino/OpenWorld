package math;

import java.io.Serializable;
import java.nio.FloatBuffer;

public abstract class Matrix implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor for Matrix.
	 */
	protected Matrix() {
		super();
	}

	/**
	 * Set this matrix to be the identity matrix.
	 * @return this
	 */
	public abstract Matrix setIdentity();


	/**
	 * Invert this matrix
	 * @return this
	 */
	public abstract Matrix invert();


	/**
	 * Load from a float buffer. The buffer stores the matrix in column major
	 * (OpenGL) order.
	 *
	 * @param buf A float buffer to read from
	 * @return this
	 */
	public abstract Matrix load(FloatBuffer buf);


	/**
	 * Load from a float buffer. The buffer stores the matrix in row major
	 * (mathematical) order.
	 *
	 * @param buf A float buffer to read from
	 * @return this
	 */
	public abstract Matrix loadTranspose(FloatBuffer buf);


	/**
	 * Negate this matrix
	 * @return this
	 */
	public abstract Matrix negate();


	/**
	 * Store this matrix in a float buffer. The matrix is stored in column
	 * major (openGL) order.
	 * @param buf The buffer to store this matrix in
	 * @return this
	 */
	public abstract Matrix store(FloatBuffer buf);


	/**
	 * Store this matrix in a float buffer. The matrix is stored in row
	 * major (maths) order.
	 * @param buf The buffer to store this matrix in
	 * @return this
	 */
	public abstract Matrix storeTranspose(FloatBuffer buf);


	/**
	 * Transpose this matrix
	 * @return this
	 */
	public abstract Matrix transpose();


	/**
	 * Set this matrix to 0.
	 * @return this
	 */
	public abstract Matrix setZero();


	/**
	 * @return the determinant of the matrix
	 */
	public abstract float determinant();


}