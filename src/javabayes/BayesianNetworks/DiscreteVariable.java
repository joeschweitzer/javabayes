/**
 * DiscreteVariable.java
 * @author Fabio G. Cozman 
 * Copyright 1996 - 1999, Fabio G. Cozman,
 *          Carnergie Mellon University, Universidade de Sao Paulo
 * fgcozman@usp.br, http://www.cs.cmu.edu/~fgcozman/home.html
 *
 * The JavaBayes distribution is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation (either
 * version 2 of the License or, at your option, any later version), 
 * provided that this notice and the name of the author appear in all 
 * copies. Upon request to the author, some of the packages in the 
 * JavaBayes distribution can be licensed under the GNU Lesser General
 * Public License as published by the Free Software Foundation (either
 * version 2 of the License, or (at your option) any later version).
 * If you're using the software, please notify fgcozman@usp.br so
 * that you can receive updates and patches. JavaBayes is distributed
 * "as is", in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with the JavaBayes distribution. If not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package javabayes.BayesianNetworks;

import java.io.PrintStream;

/*******************************************************************/

public class DiscreteVariable {
	protected String name; // Name of the variable
	protected int index; // Index of the variable in a collection of variables
	protected String values[]; // Values of the variable

	/**
	 * Default constructor for a DiscreteVariable.
	 */
	public DiscreteVariable() {
		name = null;
		index = BayesNet.INVALID_INDEX;
		values = null;
	}

	/**
	 * Simple constructor for DiscreteVariable.
	 * 
	 * @param n_vb
	 *            Name of the variable.
	 */
	public DiscreteVariable(String n_vb) {
		name = n_vb;
		index = BayesNet.INVALID_INDEX;
		values = null;
	}

	/**
	 * Simple constructor for DiscreteVariable.
	 * 
	 * @param vb
	 *            Name of the variable.
	 * @param vi
	 *            Index of the variable.
	 * @param vl
	 *            Values of the variable.
	 */
	public DiscreteVariable(String vb, int vi, String vl[]) {
		name = vb;
		index = vi;
		values = vl;
	}

	/**
	 * Simple constructor for DiscreteVariable.
	 * 
	 * @param dv
	 *            DiscreteVariable that is copied into current DiscreteVariable.
	 */
	public DiscreteVariable(DiscreteVariable dv) {
		name = dv.name;
		index = dv.index;
		values = dv.values;
	}

	/**
	 * Determine the index of a value given its name; returns INVALID_INDEX if
	 * there is no index.
	 */
	public int index_of_value(String value) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value))
				return (i);
		}
		return (BayesNet.INVALID_INDEX);
	}

	/**
	 * Produce an array of numeric values for the values of a variable. The
	 * values are direct translation of the string values into doubles; if the
	 * translation fails for a particular value, that value is replaced by its
	 * index.
	 */
	public DiscreteFunction get_numeric_values() {
		Double daux;
		DiscreteVariable dvs[] = new ProbabilityVariable[1];
		dvs[0] = this;
		double numeric_values[] = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			try {
				daux = Double.valueOf(values[i]);
				numeric_values[i] = daux.doubleValue();
			} catch (NumberFormatException e) {
				numeric_values[i] = i;
			}
		}
		return (new DiscreteFunction(dvs, numeric_values));
	}

	/**
	 * Print method for DiscreteVariable.
	 */
	public void print() {
		print(System.out);
	}

	/**
	 * Print method for DiscreteVariable.
	 */
	public void print(PrintStream out) {
		if (this == null)
			return;
		out.print("variable ");
		if (name != null)
			out.print(" \"" + name + "\" ");
		out.print("{");
		if (values != null) {
			out.println("//" + values.length + " values");
			out.print("\ttype discrete[" + values.length + "] { ");
			for (int i = 0; i < values.length; i++)
				out.print(" \"" + values[i] + "\" ");
			out.println("};");
		}
		out.println("}");
	}

	/* *************************************************************** */
	/* Methods that allow basic manipulation of non-public variables */
	/* *************************************************************** */

	/**
	 * Get the name of the current DiscreteVariable.
	 */
	public String get_name() {
		return (name);
	}

	/**
	 * Set the name of the current DiscreteVariable.
	 */
	public void set_name(String n) {
		name = n;
	}

	/**
	 * Get the index of the current DiscreteVariable.
	 */
	public int get_index() {
		return (index);
	}

	/**
	 * Return the number of values in the current DiscreteVariable.
	 */
	public int number_values() {
		return (values.length);
	}

	/**
	 * Get the values of the current DiscreteVariable.
	 */
	public String[] get_values() {
		return (values);
	}

	/**
	 * Set the values of the current DiscreteVariable.
	 */
	public void set_values(String vals[]) {
		values = vals;
	}

	/**
	 * Get a value of the current DiscreteVariable.
	 * 
	 * @param i
	 *            Position of the value in the array of values.
	 */
	public String get_value(int i) {
		return (values[i]);
	}
}
