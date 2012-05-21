/**
 * VertexSet.java
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

package javabayes.CredalSets;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.DiscreteVariable;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;

public class VertexSet extends FinitelyGeneratedSet {

	// Variable that indicates which extreme point is active
	ProbabilityVariable auxiliary_variable;

	// The set of extreme points; the first coordinate indexes
	// a set of double[] arrays (each array contains the values
	// for an extreme point).
	double extreme_points[][];

	/**
	 * Default constructor for a VertexQBProbabilityFunction.
	 */
	public VertexSet(BayesNet b_n, ProbabilityVariable pvs[], double ep[][],
			Vector prop) {
		// Call the super constructor with ep[0].
		super(b_n, pvs, ep[0], prop);

		// Now replace ep[0] with a new array to avoid wrong
		// cross-references among arrays.
		double[] vals = new double[ep[0].length];
		values = vals;

		// Update the extreme_points and the values.
		extreme_points = ep;
		compose_values();
	}

	/**
	 * Constructor for a VertexQBProbabilityFunction.
	 */
	public VertexSet(BayesNet b_n, ProbabilityVariable pvs[], double v[],
			Vector prop, double ep[][]) {
		super(b_n, pvs, v, (double[]) null, (double[]) null, prop);
		extreme_points = ep;
	}

	/**
	 * Constructor for a VertexQBProbabilityFunction.
	 */
	public VertexSet(ProbabilityFunction pf) {
		super(pf, pf.get_values());
		if (pf instanceof VertexSet)
			extreme_points = ((VertexSet) pf).extreme_points;
		else {
			extreme_points = new double[1][];
			extreme_points[0] = pf.get_values();
		}
	}

	/**
	 * Constructor for a VertexQBProbabilityFunction from a ProbabilityFunction
	 * object and new values.
	 */
	public VertexSet(ProbabilityFunction pf, double new_values[]) {
		super(pf, new_values);
		if (pf instanceof VertexSet) {
			extreme_points = ((VertexSet) pf).extreme_points;
			auxiliary_variable = ((VertexSet) pf).auxiliary_variable;
		} else {
			extreme_points = new double[1][];
			extreme_points[0] = pf.get_values();
		}
	}

	/**
	 * Put together all the values for the possible vertices of credal set and
	 * create an auxiliary variable to indicate which vertex to consider There
	 * are three things to do: 1) Create an auxiliary_variable with correct
	 * values. 2) Combine the values into a new array. 3) Insert the
	 * auxiliary_variable into the variables array.
	 */
	public VertexSet prepare_auxiliary_variable(BayesNet transformed_bn) {
		int i;
		double new_values[];

		// Assume that values and auxiliary_variable are correct if
		// auxiliary_variable is non null (cannot happen in current version)
		if (auxiliary_variable != null)
			return (this);

		// Create the auxiliary variable for this credal set
		ProbabilityVariable auxv = create_auxiliary_variable(transformed_bn);

		// Create the new values for the credal set with auxiliary_variable
		new_values = create_new_values(transformed_bn);

		// Now insert the auxiliary_variable in the variables array
		DiscreteVariable new_variables[] = new DiscreteVariable[variables.length + 1];
		for (i = 0; i < variables.length; i++)
			new_variables[i] = variables[i];
		new_variables[i] = auxv;

		// Use the new_values array to create a new
		// VertexQBProbabilityFunction that incorporates the auxiliary_variable
		VertexSet new_qbpf = new VertexSet(this, new_values);
		new_qbpf.bn = transformed_bn;
		new_qbpf.auxiliary_variable = auxv;
		new_qbpf.variables = new_variables;

		return (new_qbpf);
	}

	/**
	 * Create a new array of values that combines extreme points.
	 */
	private double[] create_new_values(BayesNet transformed_bn) {
		int i, j;
		// Combine vertices and the auxiliary_variable and create new values
		double new_values[] = new double[extreme_points.length * values.length];
		for (i = 0; i < values.length; i++) {
			for (j = 0; j < extreme_points.length; j++) {
				new_values[j + i * extreme_points.length] = extreme_points[j][i];
			}
		}
		return (new_values);
	}

	/**
	 * Create an auxiliar variable to indicate the vertices.
	 */
	private ProbabilityVariable create_auxiliary_variable(
			BayesNet transformed_bn) {
		int i, j;

		// Compose the name of the auxiliary variable
		StringBuffer buffer_auxiliary_variable_name = new StringBuffer(
				"<Transparent:");
		buffer_auxiliary_variable_name.append(variables[0].get_name());
		buffer_auxiliary_variable_name.append(">");
		String auxiliary_variable_name = new String(
				buffer_auxiliary_variable_name);

		// Compose the values of the auxiliary variable
		String auxiliary_variable_values[] = new String[extreme_points.length];
		for (i = 0; i < auxiliary_variable_values.length; i++)
			auxiliary_variable_values[i] = String.valueOf(i);

		// Create the auxiliary variable
		ProbabilityVariable auxv = new ProbabilityVariable(transformed_bn,
				auxiliary_variable_name, BayesNet.INVALID_INDEX,
				auxiliary_variable_values, ((Vector) null));
		// Mark the auxiliary variable as auxiliary
		auxv.set_type(ProbabilityVariable.TRANSPARENT);

		// Return the created auxiliary variable
		return (auxv);
	}

	/**
	 * Evaluate a function given a list of pairs (Variable Value) which
	 * specifies a value of the function, and the index of the extreme
	 * distribution to consider.
	 */
	public double evaluate(String variable_value_pairs[][],
			int index_extreme_point) {
		int index;
		ProbabilityVariable pv;

		// Initialize with zeros an array of markers.
		int value_indexes[] = new int[bn.number_variables()];

		// Fill the array of markers.
		for (int i = 0; i < variable_value_pairs.length; i++) {
			index = bn.index_of_variable(variable_value_pairs[i][0]);
			pv = bn.get_probability_variable(index);
			value_indexes[index] = pv
					.index_of_value(variable_value_pairs[i][1]);
		}

		// Now evaluate
		int position = get_position_from_indexes(
				bn.get_probability_variables(), value_indexes);
		return (extreme_points[index_extreme_point][position]);
	}

	/**
	 * Set a single value of the probability function.
	 */
	public void set_value(String variable_value_pairs[][], double val,
			int index_extreme_point) {
		int index;
		ProbabilityVariable pv;

		// Initialize with zeros an array of markers.
		int value_indexes[] = new int[bn.number_variables()];

		// Fill the array of markers.
		for (int i = 0; i < variable_value_pairs.length; i++) {
			index = bn.index_of_variable(variable_value_pairs[i][0]);
			pv = bn.get_probability_variable(index);
			value_indexes[index] = pv
					.index_of_value(variable_value_pairs[i][1]);
		}

		// Get the position of the value in the array of values
		int pos = get_position_from_indexes(bn.get_probability_variables(),
				value_indexes);
		// Set the value.
		extreme_points[index_extreme_point][pos] = val;
		compose_values();
	}

	/**
	 * Print method.
	 */
	@Override
	public void print(PrintStream out) {
		int i, j;
		String property;

		if (variables != null) {
			out.print("probability ( ");
			for (j = 0; j < variables.length; j++) {
				out.print(" \"" + variables[j].get_name() + "\" ");
			}
			out.print(") {");
			out.println(" //" + variables.length + " variable(s) and "
					+ values.length + " values");
			if (extreme_points != null) {
				for (i = 0; i < extreme_points.length; i++) {
					out.print("\ttable ");
					for (j = 0; j < extreme_points[i].length; j++)
						out.print(extreme_points[i][j] + " ");
					out.println(";");
				}
				out.print(" // Values: ");
			}
			out.print("\ttable ");
			for (j = 0; j < values.length; j++)
				out.print(values[j] + " ");
			out.print(";");
		}
		out.println();
		if ((properties != null) && (properties.size() > 0)) {
			for (Enumeration e = properties.elements(); e.hasMoreElements();) {
				property = (String) (e.nextElement());
				out.println("\tproperty \"" + property + "\" ;");
			}
		}
		out.println("}");
	}

	/**
	 * Produce the centroid of all extreme distributions and insert it into the
	 * values of the distribution.
	 */
	public void compose_values() {
		double aux, n;

		if (extreme_points == null)
			return;

		n = (extreme_points.length);

		for (int i = 0; i < values.length; i++) {
			aux = 0.0;
			for (int j = 0; j < extreme_points.length; j++)
				aux += extreme_points[j][i];
			values[i] = aux / n;
		}
	}

	/**
	 * Set the number of extreme distributions in the credal set.
	 */
	public void set_local_credal_set(int number_extreme_points) {
		int i, j, k;
		int number_current_extreme_points;
		double new_extreme_points[][];

		// Update the values in case some extreme distributions
		// have changed.
		compose_values();

		if (extreme_points == null)
			number_current_extreme_points = 0;
		else {
			number_current_extreme_points = extreme_points.length;

			// If the new size is equal to current size, return.
			if (number_extreme_points == number_current_extreme_points)
				return;
		}

		// Allocate the new extreme distributions.
		new_extreme_points = new double[number_extreme_points][values.length];

		// If the new size is larger than the current size.
		if (number_extreme_points > number_current_extreme_points) {
			// First copy what is already there.
			for (i = 0; i < number_current_extreme_points; i++) {
				for (j = 0; j < extreme_points[i].length; j++) {
					new_extreme_points[i][j] = extreme_points[i][j];
				}
			}
			// Then fill with copies of values.
			for (k = i; k < new_extreme_points.length; k++) {
				for (j = 0; j < values.length; j++) {
					new_extreme_points[k][j] = values[j];
				}
			}
		} else {
			// If the new size is smaller than the current size.
			for (i = 0; i < new_extreme_points.length; i++) {
				for (j = 0; j < values.length; j++) {
					new_extreme_points[i][j] = extreme_points[i][j];
				}
			}
		}

		extreme_points = new_extreme_points;
	}

	/**
	 * Set an extreme point of the credal set.
	 */
	public void set_extreme_point(int index, double ep[]) {
		extreme_points[index] = ep;
	}

	/**
	 * Methods that allow basic manipulation of non-public variables.
	 */

	public ProbabilityVariable get_auxiliary_variable() {
		return (auxiliary_variable);
	}

	public double[][] get_extreme_points() {
		return (extreme_points);
	}
}
