/**
 * DiscreteFunction.java
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

/* ***************************************************************** */

public class DiscreteFunction {
	protected DiscreteVariable variables[];
	protected double values[];

	/**
	 * Default constructor for a DiscreteFunction.
	 */
	public DiscreteFunction() {
	}

	/**
	 * Simple constructor for DiscreteFunction.
	 * 
	 * @param n_vb
	 *            Number of variables in the function.
	 * @param n_vl
	 *            Number of values in the function.
	 */
	public DiscreteFunction(int n_vb, int n_vl) {
		variables = new DiscreteVariable[n_vb];
		values = new double[n_vl];
	}

	/**
	 * Simple constructor for DiscreteFunction.
	 * 
	 * @param pvs
	 *            An array of ProbabilityVariable objects.
	 * @param v
	 *            An array of values for the function.
	 */
	public DiscreteFunction(DiscreteVariable pvs[], double v[]) {
		variables = pvs;
		values = v;
	}

	/**
	 * Check whether an index is present in the function.
	 */
	public boolean memberOf(int index) {
		for (int i = 0; i < variables.length; i++) {
			if (index == variables[i].index)
				return (true);
		}
		return (false);
	}

	/*
	 * Method that determines whether a DiscreteFunction contain some
	 * DiscreteVariable in common with the current DiscreteFunction.
	 * 
	 * @param df DiscreteFunction to be compared with the current
	 * DiscreteFunction.
	 */
	boolean same_variables(DiscreteFunction df) {
		if (variables.length != df.variables.length)
			return (false);
		for (int i = 0; i < variables.length; i++) {
			if (variables[i] != df.variables[i])
				return (false);
		}
		return (true);
	}

	/**
	 * Evaluate a function given a (possibly partial) instantiation of variables
	 * through the indexes. Indexes indicate which variables are present in the
	 * function to be evaluated, assuming an array of DiscreteVariable objects
	 * is present.
	 * 
	 * @param pvs
	 *            The array of DiscreteVariable objects that is used to compute
	 *            the position indicated by the markers.
	 * @param value_indexes
	 *            The markers.
	 */
	public double evaluate(DiscreteVariable pvs[], int value_indexes[]) {
		int position = get_position_from_indexes(pvs, value_indexes);
		return (values[position]);
	}

	/**
	 * Get position in a function from a (possibly partial) instantiation of
	 * variables through the indexes.
	 */
	public int get_position_from_indexes(DiscreteVariable pvs[],
			int variable_indexes[]) {
		int k, pos = 0, jump = 1;
		for (int i = (variables.length - 1); i >= 0; i--) {
			k = variables[i].index;
			pos += variable_indexes[k] * jump;
			jump *= pvs[k].values.length;
		}
		return (pos);
	}

	/**
	 * Sum out some variables in the function.
	 * 
	 * @param markers
	 *            A boolean vector indicating which variables are to be summed
	 *            out. If markers[i] is true, then the ith variable is to be
	 *            summed out; if markers[i] is false, the ith variable is not to
	 *            be summed out.
	 */
	public DiscreteFunction sum_out(DiscreteVariable dvs[], boolean markers[]) {
		int i, j, k, current;
		double t, v;

		// Initialize the indexes and the maximum length for all
		// ProbabilityVariable objects.
		// This is used to circle through all the values in the new_df.
		int indexes[] = new int[dvs.length];
		int value_lengths[] = new int[dvs.length];
		for (i = 0; i < dvs.length; i++) {
			indexes[i] = 0;
			value_lengths[i] = dvs[i].number_values();
		}

		// Collect some information used to construct the new_df.
		int number_of_variables_to_sum_out = 0;
		int number_of_variables_to_stay = 0;
		int number_of_values_new_df = 1;
		int number_of_values_to_sum_out = 1;
		for (i = 0; i < variables.length; i++) {
			if (markers[variables[i].get_index()] == true) {
				number_of_variables_to_sum_out++;
				number_of_values_to_sum_out *= variables[i].number_values();
			} else {
				number_of_variables_to_stay++;
				number_of_values_new_df *= variables[i].number_values();
			}
		}

		// If there is no variable that must stay, then return null.
		if (number_of_variables_to_stay == 0)
			return (null);

		// If there is no variable to sum out, then return copy.
		if (number_of_variables_to_sum_out == 0)
			return (new DiscreteFunction(variables, values));

		// Initialize a vector with the indexes of variables to sum out.
		int index_for_variables_to_sum_out[] = new int[number_of_variables_to_sum_out];

		// Build the new_df and the indexes of variables to sum out.
		DiscreteFunction new_df = new DiscreteFunction(
				number_of_variables_to_stay, number_of_values_new_df);

		for (i = 0, j = 0, k = 0; i < variables.length; i++) {
			if (markers[variables[i].get_index()] == true) { // Fill in the
																// index of
																// variables to
																// sum out.
				index_for_variables_to_sum_out[k] = variables[i].get_index();
				k++;
			} else { // Fill in the variables in the new_df.
				new_df.set_variable(j, variables[i]);
				j++;
			}
		}

		// Store the last valid indexes (efficiency purposes only).
		int last_new_df = new_df.number_variables() - 1;
		int last_index_for_variables_to_sum_out = index_for_variables_to_sum_out.length - 1;

		// Now circle through all the values, doing the summation.
		for (i = 0; i < new_df.number_values(); i++) { // Go through all values
														// of the new_df.
			v = 0.0;
			// Reset the indexes for the values to sum out.
			for (j = 0; j < index_for_variables_to_sum_out.length; j++)
				indexes[index_for_variables_to_sum_out[j]] = 0;
			// Do the summation for a value.
			for (j = 0; j < number_of_values_to_sum_out; j++) { // Go through
																// all values to
																// be summed
																// out.
				v += evaluate(dvs, indexes); // Do the summation for each value
												// of the new_df.

				// Increment the last index to be summed out.
				indexes[index_for_variables_to_sum_out[last_index_for_variables_to_sum_out]]++;
				for (k = last_index_for_variables_to_sum_out; k > 0; k--) { // Now
																			// do
																			// the
																			// updating
																			// of
																			// all
																			// indexes.
					current = index_for_variables_to_sum_out[k];
					if (indexes[current] >= value_lengths[current]) { // If
																		// overflow
																		// in an
																		// index,
						indexes[current] = 0;
						indexes[index_for_variables_to_sum_out[k - 1]]++; // then
																			// update
																			// the
																			// next
																			// index.
					} else
						break;
				}
			}
			// Insert the summation for the value of new_df into new_df.
			new_df.set_value(i, v);

			// Update the indexes.
			indexes[new_df.get_index(last_new_df)]++; // Increment the last
														// index.
			for (j = last_new_df; j > 0; j--) { // Now do the updating of all
												// indexes.
				current = new_df.get_index(j);
				if (indexes[current] >= value_lengths[current]) { // If overflow
																	// in an
																	// index,
					indexes[current] = 0;
					indexes[new_df.get_index(j - 1)]++; // then update the next
														// index.
				} else
					break;
			}
		}
		return (new_df);
	}

	/**
	 * Multiply two DiscreteFunction objects.
	 */
	public DiscreteFunction multiply(DiscreteVariable dvs[],
			DiscreteFunction mult) {
		int i, j, k, n, v, current, joined_indexes[];
		double t;
		boolean variable_markers[] = new boolean[dvs.length];

		// This is used to circle through all the values in the new_df.
		int indexes[] = new int[dvs.length];
		int value_lengths[] = new int[dvs.length];
		for (i = 0; i < dvs.length; i++) {
			indexes[i] = 0;
			value_lengths[i] = dvs[i].number_values();
			variable_markers[i] = false;
		}

		// Join the indexes of this and mult.
		n = 0;
		for (j = 0; j < this.number_variables(); j++) {
			k = this.get_index(j);
			if (variable_markers[k] == false) {
				variable_markers[k] = true;
				n++;
			}
		}
		for (j = 0; j < mult.number_variables(); j++) {
			k = mult.get_index(j);
			if (variable_markers[k] == false) {
				variable_markers[k] = true;
				n++;
			}
		}
		// Calculate necessary quantities
		joined_indexes = new int[n];
		j = 0;
		v = 1;
		for (i = 0; i < variable_markers.length; i++) {
			if (variable_markers[i] == true) {
				joined_indexes[j] = i;
				j++;
				v *= dvs[i].number_values();
			}
		}

		// Create new function to be filled with joined variables
		DiscreteFunction new_df = new DiscreteFunction(n, v);
		for (i = 0; i < n; i++) {
			new_df.set_variable(i, dvs[joined_indexes[i]]);
		}

		// Store the last valid indexes (efficiency purposes only).
		int last_new_df = new_df.number_variables() - 1;

		// Now circle through all the values, doing the multiplication.
		for (i = 0; i < new_df.number_values(); i++) { // Go through all values
														// of the new_df.
			t = this.evaluate(dvs, indexes) * mult.evaluate(dvs, indexes);
			// Insert the summation for the value of new_df into new_df.
			new_df.set_value(i, t);

			// Update the indexes.
			indexes[new_df.get_index(last_new_df)]++; // Increment the last
														// index.
			for (j = last_new_df; j > 0; j--) { // Now do the updating of all
												// indexes.
				current = new_df.get_index(j);
				if (indexes[current] >= value_lengths[current]) { // If overflow
																	// in an
																	// index,
					indexes[current] = 0;
					indexes[new_df.get_index(j - 1)]++; // then update the next
														// index.
				} else
					break;
			}
		}

		return (new_df);
	}

	/**
	 * Normalize a function (in-place).
	 */
	public void normalize() {
		int i;
		double total = 0.0;

		for (i = 0; i < values.length; i++)
			total += values[i];
		if (total > 0.0) {
			for (i = 0; i < values.length; i++)
				values[i] = values[i] / total;
		}
	}

	/**
	 * Normalize a function (in-place) assuming that it is a conditional
	 * distribution for the first variable
	 */
	public void normalize_first() {
		int i, j;
		int jump = 1;
		double n;

		for (i = 1; i < variables.length; i++)
			jump *= variables[i].values.length;

		for (i = 0; i < jump; i++) {
			n = 0.0;
			for (j = 0; j < variables[0].values.length; j++)
				n += values[i + j * jump];
			if (n > 0.0) {
				for (j = 0; j < variables[0].values.length; j++)
					values[i + j * jump] = values[i + j * jump] / n;
			}
		}
	}

	/**
	 * Print method for DiscreteFunction.
	 */
	public void print() {
		print(System.out);
	}

	/**
	 * Print method for DiscreteFunction into a PrintStream.
	 */
	public void print(PrintStream out) {
		int j;

		if (variables != null) {
			out.print("discrete function ( ");
			for (j = 0; j < variables.length; j++)
				out.print(" \"" + variables[j].name + "\" ");
			out.print(") {");
			out.println(" //" + variables.length + " variable(s) and "
					+ values.length + " values");
			out.print("\ttable ");
			for (j = 0; j < values.length; j++)
				out.print(values[j] + " ");
			out.print(";");
		}
		out.println();
		out.println("}");
	}

	/* *************************************************************** */
	/* Methods that allow basic manipulation of non-public variables. */
	/* *************************************************************** */

	/**
	 * Return the number of DiscreteVariable objects in the current
	 * DiscreteFunction.
	 */
	public int number_variables() {
		return (variables.length);
	}

	/**
	 * Return the number of values in the current DiscreteFunction.
	 */
	public int number_values() {
		return (values.length);
	}

	/**
	 * Get the variables in the current DiscreteFunction.
	 */
	public DiscreteVariable[] get_variables() {
		return (variables);
	}

	/**
	 * Get a variable in the current DiscreteFunction.
	 * 
	 * @param index
	 *            Position of the variable to be returned in the array of
	 *            DiscreteVariable objects.
	 */
	public DiscreteVariable get_variable(int index) {
		return (variables[index]);
	}

	/**
	 * Get an array with all the indexes of the DiscreteVariable objects in the
	 * current DiscreteFunction.
	 */
	public int[] get_indexes() {
		int ind[] = new int[variables.length];
		for (int i = 0; i < ind.length; i++)
			ind[i] = variables[i].index;
		return (ind);
	}

	/**
	 * Get a DiscreteVariable object with a particular index.
	 * 
	 * @param ind
	 *            Index of the desired DiscreteVariable.
	 */
	public int get_index(int ind) {
		return (variables[ind].index);
	}

	/**
	 * Get all values of the current DiscreteFunction.
	 */
	public double[] get_values() {
		return (values);
	}

	/**
	 * Get a value of the current DiscreteFunction given the position of the
	 * value in the array of values.
	 */
	public double get_value(int index) {
		return (values[index]);
	}

	/**
	 * Set a value in the current DiscreteFunction given its position in the
	 * array of values.
	 * 
	 * @param index
	 *            The position of the value.
	 * @param v
	 *            The new value.
	 */
	public void set_value(int index, double v) {
		values[index] = v;
	}

	/**
	 * Set the values in the DiscreteFunction.
	 */
	public void set_values(double vs[]) {
		values = vs;
	}

	/**
	 * Set a DiscreteVariable in the current DiscreteFunction given its position
	 * in the array of values.
	 * 
	 * @param index
	 *            The position of the value.
	 * @param pv
	 *            The new DiscreteVariable.
	 */
	public void set_variable(int index, DiscreteVariable dv) {
		variables[index] = dv;
	}
}
