/*
 * Bucket.java
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

package javabayes.BayesianInferences;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.DiscreteFunction;
import javabayes.BayesianNetworks.DiscreteVariable;
import javabayes.BayesianNetworks.ProbabilityVariable;

/*******************************************************************/

class Bucket {
	BucketTree bucket_tree; // BucketTree that holds the Bucket.

	ProbabilityVariable variable; // The Bucket variable.
	Vector discrete_functions; // The functions in the Bucket.

	DiscreteFunction backward_pointers; // The pointers used for maximization.

	DiscreteFunction separator; // The function that is sent from a Bucket to
								// another.
	boolean do_produce_clusters; // Whether or not to compute distributions for
									// all variables in the Bucket.
	DiscreteFunction cluster; // The distribution for all variables involved in
								// the Bucket.

	Vector non_conditioning_variables; // Variables that are not conditioning
										// variables.

	Vector parents; // The parents of the Bucket in the BucketTree.
	Bucket child; // The child of the Bucket in the BucketTree.

	int bucket_status = EMPTY;

	private DiscreteFunction ordered_dfs[];
	private boolean is_ordered_dfs_ready;

	static final int EMPTY = 0;
	static final int REDUCED = 1;
	static final int DISTRIBUTED = 2;

	/*
	 * Simple constructor for Bucket. Default behavior is not to build the
	 * distributions for clusters of variables.
	 * 
	 * @param bs The BucketTree that holds the bucket.
	 * 
	 * @param pv The bucket variable for the Bucket.
	 */
	Bucket(BucketTree bs, ProbabilityVariable pv) {
		this(bs, pv, false);
	}

	/*
	 * Basic constructor for Bucket.
	 * 
	 * @param bs The BucketTree that holds the bucket.
	 * 
	 * @param pv The bucket variable for the Bucket.
	 * 
	 * @param produce_clusters Flag that indicates whether distributions for
	 * clusters of variables are to be computed or not.
	 */
	Bucket(BucketTree bs, ProbabilityVariable pv, boolean dpc) {
		bucket_tree = bs;
		variable = pv;
		discrete_functions = new Vector();
		do_produce_clusters = dpc;
		non_conditioning_variables = new Vector();
		parents = new Vector();
	}

	/*
	 * Print method for Bucket.
	 */
	void print() {
		print(System.out);
	}

	/*
	 * Print method for Bucket.
	 */
	void print(PrintStream out) {
		Enumeration e;
		boolean is_explanation_flag = false;
		DiscreteFunction d_f;

		if (is_explanation())
			is_explanation_flag = true;

		if (is_explanation_flag)
			out.print("MAP");
		out.println("Bucket; variable " + variable.get_name() + " with "
				+ discrete_functions.size() + " function(s).");
		switch (bucket_status) {
		case EMPTY:
			out.println("Bucket is empty.");
			break;
		case REDUCED:
			out.println("Bucket has been reduced.");
			break;
		case DISTRIBUTED:
			out.println("Bucket has been distributed.");
			break;
		}
		for (e = discrete_functions.elements(); e.hasMoreElements();) {
			d_f = (DiscreteFunction) (e.nextElement());
			d_f.print(out);
		}
		if (is_explanation_flag && (backward_pointers != null)) {
			out.println("Backward pointers:");
			backward_pointers.print(out);
		}
		if (cluster != null) {
			out.println("Cluster:");
			cluster.print(out);
		}
		if (separator != null) {
			out.println("Separator:");
			separator.print(out);
		}
		if (parents.size() > 0) {
			out.println("\tParents:");
			for (e = parents.elements(); e.hasMoreElements();)
				out.println("\t"
						+ ((Bucket) (e.nextElement())).variable.get_name());
		}
		if (child != null) {
			out.println("\tChild:");
			out.println("\t" + child.variable.get_name());
		}
	}

	/*
	 * Reduce the Bucket, either by summation or maximization. The final result
	 * is in the Bucket's separator. Notice that if all functions in a bucket
	 * have a single variable, then the separator is null.
	 */
	void reduce() {
		// Order all the probability functions in the bucket
		order_dfs();
		// If the bucket is empty, return null
		if (ordered_dfs.length == 0) {
			separator = null;
			return;
		}
		// Create a ProbabilityFunction with the relevant variables
		DiscreteFunction new_df = build_new_function(false);
		// If new_df is null, then the only remaining variable
		// in the Bucket is the bucket variable. In this case, combine the
		// functions.
		if (new_df == null) {
			combine();
			separator = null;
			return;
		}
		// Either sum out or maximize out the bucket variable.
		if (is_explanation())
			max_out(new_df);
		else
			sum_out(new_df);

		// Mark the Bucket as REDUCED;
		bucket_status = REDUCED;
		// Set the separator.
		separator = new_df;
	}

	/*
	 * Combine a number of functions in the bucket into a single function.
	 */
	DiscreteFunction combine() {
		int i, j, k, m, p, current;
		int indexes[] = new int[bucket_tree.bn.number_variables()];
		int value_lengths[] = new int[bucket_tree.bn.number_variables()];
		double t, v;

		// Order all the probability functions in the bucket
		order_dfs();
		// If the bucket is empty, return null
		if (ordered_dfs.length == 0)
			return (null);

		// Create the combined DiscreteFunction object
		DiscreteFunction new_df = build_new_function(true);

		// Initialize some necessary values
		for (i = 0; i < bucket_tree.bn.number_variables(); i++) {
			indexes[i] = 0;
			value_lengths[i] = bucket_tree.bn.get_probability_variable(i)
					.number_values();
		}

		// Build all values for the combined ProbabilityFunction object
		for (i = 0; i < new_df.number_values(); i++) {
			// Calculate the combined value
			v = 1.0;
			for (m = 0; m < ordered_dfs.length; m++)
				v *= ordered_dfs[m].evaluate(
						bucket_tree.bn.get_probability_variables(), indexes);
			p = new_df.get_position_from_indexes(
					bucket_tree.bn.get_probability_variables(), indexes);
			new_df.set_value(p, v);

			// Update the indexes
			indexes[new_df.get_index(new_df.number_variables() - 1)]++;
			for (j = (new_df.number_variables() - 1); j > 0; j--) {
				current = new_df.get_index(j);
				if (indexes[current] >= value_lengths[current]) {
					indexes[current] = 0;
					indexes[new_df.get_index(j - 1)]++;
				} else
					break;
			}
		}

		// Maximize if necessary. If the combined function
		// has conditioning variables, only the first
		// combination of conditioning variables is analyzed.
		if (is_explanation()) {
			int jump = 1;
			for (i = 1; i < new_df.number_variables(); i++) {
				jump *= new_df.get_variable(i).number_values();
			}
			j = 0;
			t = 0.0;
			backward_pointers = new DiscreteFunction(1, 1);
			backward_pointers.set_variable(0, variable);
			for (i = 0; i < variable.number_values(); i++) {
				if (new_df.get_value(i) > t) {
					t = new_df.get_value(i * jump);
					j = i;
				}
			}
			backward_pointers.set_value(0, j);
		}

		if (do_produce_clusters)
			cluster = new_df;
		return (new_df);
	}

	/*
	 * Sum out all variables in the cluster, except the bucket variable, and put
	 * the summation in the bucket_tree.result.
	 */
	void reduce_cluster() {
		// Check whether the cluster is null.
		if (cluster == null) {
			bucket_tree.unnormalized_result = null;
			return;
		}
		// Construct the markers.
		boolean markers[] = new boolean[bucket_tree.bn.number_variables()];
		for (int i = 0; i < markers.length; i++)
			markers[i] = true;
		markers[variable.get_index()] = false;
		// Fill in the bucket_tree.result.
		bucket_tree.unnormalized_result = cluster.sum_out(
				bucket_tree.bn.get_probability_variables(), markers);
	}

	/*
	 * Detect whether the bucket variable is an explanatory variable.
	 */
	boolean is_explanation() {
		if (bucket_tree.explanation_status == Inference.IGNORE_EXPLANATION)
			return (false);
		if (bucket_tree.explanation_status == Inference.FULL_EXPLANATION)
			return (true);
		return (variable.is_explanation());
	}

	/*
	 * Order the probability functions in the Bucket.
	 */
	private void order_dfs() {
		if (is_ordered_dfs_ready == true)
			return;
		is_ordered_dfs_ready = true;
		ordered_dfs = new DiscreteFunction[discrete_functions.size()];
		for (int i = 0; i < ordered_dfs.length; i++)
			ordered_dfs[i] = (DiscreteFunction) (discrete_functions
					.elementAt(i));
	}

	/*
	 * Join the indexes of the Bucket by marking the variable markers with true.
	 */
	private int join_indexes(boolean variable_markers[]) {
		int i, j, k, n = 0;
		for (i = 0; i < variable_markers.length; i++)
			variable_markers[i] = false;
		for (i = 0; i < ordered_dfs.length; i++) {
			for (j = 0; j < ordered_dfs[i].number_variables(); j++) {
				k = ordered_dfs[i].get_index(j);
				if (variable_markers[k] == false) {
					variable_markers[k] = true;
					n++;
				}
			}
		}
		return (n);
	}

	/*
	 * Construct a DiscreteFunction which holds all the variables in the Bucket
	 * (maybe with the exception of the bucket variable).
	 */
	private DiscreteFunction build_new_function(
			boolean is_bucket_variable_included) {
		int i, j = 0, n, v = 1;
		boolean variable_markers[] = new boolean[bucket_tree.bn
				.number_variables()];

		// Join the indexes in the bucket
		n = join_indexes(variable_markers);
		if (is_bucket_variable_included == false) {
			n--;
			variable_markers[variable.get_index()] = false;
		}

		// If the only variable is the bucket variable, then ignore
		if (n == 0)
			return (null);

		// Calculate necessary quantities
		int joined_indexes[] = new int[n];
		for (i = 0; i < variable_markers.length; i++) {
			if (variable_markers[i] == true) {
				joined_indexes[j] = i;
				j++;
				v *= bucket_tree.bn.get_probability_variable(i).number_values();
			}
		}

		// Create new function to be filled with joined variables
		DiscreteFunction new_df = new DiscreteFunction(n, v);
		build_new_variables(new_df, joined_indexes,
				is_bucket_variable_included, n);

		return (new_df);
	}

	/*
	 * Construct an array of variables that contains the variables in a new
	 * function; if the bucket variable is present, it is the first variable.
	 */
	private void build_new_variables(DiscreteFunction new_df,
			int joined_indexes[], boolean is_bucket_variable_included, int n) {
		// Bucket variable comes first if present
		if (is_bucket_variable_included == true) {
			for (int i = 0, j = 1; i < n; i++) {
				if (joined_indexes[i] == variable.get_index()) {
					new_df.set_variable(0, bucket_tree.bn
							.get_probability_variable(variable.get_index()));
				} else {
					new_df.set_variable(j, bucket_tree.bn
							.get_probability_variable(joined_indexes[i]));
					j++;
				}
			}
		} else {
			for (int i = 0; i < n; i++)
				new_df.set_variable(i, bucket_tree.bn
						.get_probability_variable(joined_indexes[i]));
		}
	}

	/*
	 * Obtain the values for the reduced_function. Attention: the array
	 * ordered_dfs is supposed to be ready!
	 */
	private void sum_out(DiscreteFunction new_df) {
		DiscreteVariable dvs[];
		int i, j, k, l, m, p, p_cluster, last, current;
		int n = variable.number_values();
		int indexes[] = new int[bucket_tree.bn.number_variables()];
		int value_lengths[] = new int[bucket_tree.bn.number_variables()];
		double t, v;

		// Initialize some necessary values.
		dvs = bucket_tree.bn.get_probability_variables();
		for (i = 0; i < bucket_tree.bn.number_variables(); i++) {
			indexes[i] = 0;
			value_lengths[i] = bucket_tree.bn.get_probability_variable(i)
					.number_values();
		}
		if (do_produce_clusters) // If necessary, start up the cluster for the
									// Bucket.
			cluster = build_new_function(true);

		// Do the whole summation.
		last = new_df.number_variables() - 1; // Auxiliary variable to hold last
												// valid index.
		for (i = 0; i < new_df.number_values(); i++) { // Compute all values of
														// the new_df.
			v = 0.0;
			for (l = 0; l < n; l++) { // For each value of the bucket variable,
				indexes[variable.get_index()] = l; // mark the current value in
													// the indexes,
				t = 1.0;
				for (m = 0; m < ordered_dfs.length; m++)
					// loop through the functions in the Bucket.
					t *= ordered_dfs[m].evaluate(dvs, indexes);
				if (do_produce_clusters) { // If necessary, insert value in the
											// cluster.
					p_cluster = cluster.get_position_from_indexes(dvs, indexes);
					cluster.set_value(p_cluster, t);
				}
				v += t; // Finally, do the summation for each value of the
						// new_df.
			}
			// Insert the summation for the value of new_df into new_df.
			p = new_df.get_position_from_indexes(dvs, indexes);
			new_df.set_value(p, v);

			// Update the indexes.
			indexes[new_df.get_index(last)]++; // Increment the last index.
			for (j = last; j > 0; j--) { // Now do the updating of all indexes.
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
	}

	/*
	 * Obtain the values for the reduced_function through maximization.
	 * Attention: the array ordered_dfs is supposed to be ready!
	 */
	private void max_out(DiscreteFunction new_df) {
		int i, j, k, l, m, p, u, last, current;
		int n = variable.number_values();
		int indexes[] = new int[bucket_tree.bn.number_variables()];
		int value_lengths[] = new int[bucket_tree.bn.number_variables()];
		double t, v = 0.0;

		// Initialize some necessary values
		create_backward_pointers(new_df);
		for (i = 0; i < bucket_tree.bn.number_variables(); i++) {
			indexes[i] = 0;
			value_lengths[i] = bucket_tree.bn.get_probability_variable(i)
					.number_values();
		}

		// Run through all the values of the bucket variable
		last = new_df.number_variables() - 1;
		for (i = 0; i < new_df.number_values(); i++) {
			v = 0.0;
			u = BayesNet.INVALID_INDEX;
			for (l = 0; l < n; l++) {
				t = 1.0;
				indexes[variable.get_index()] = l;
				// Combine the values through all the functions in the bucket
				for (m = 0; m < ordered_dfs.length; m++)
					t *= ordered_dfs[m]
							.evaluate(
									bucket_tree.bn.get_probability_variables(),
									indexes);
				// Perform the maximization
				if (v <= t) {
					v = t;
					u = l;
				}
			}
			// Update functions
			p = new_df.get_position_from_indexes(
					bucket_tree.bn.get_probability_variables(), indexes);
			new_df.set_value(p, v);
			backward_pointers.set_value(p, u);

			// Update the indexes
			indexes[new_df.get_index(last)]++;
			for (j = last; j > 0; j--) {
				current = new_df.get_index(j);
				if (indexes[current] >= value_lengths[current]) {
					indexes[current] = 0;
					indexes[new_df.get_index(j - 1)]++;
				} else
					break;
			}
		}
	}

	/*
	 * Allocate and initialize the backward_pointers in the Bucket.
	 */
	private void create_backward_pointers(DiscreteFunction new_df) {
		int i;
		DiscreteVariable new_df_variables[] = new DiscreteVariable[new_df
				.number_variables()];
		double new_df_values[] = new double[new_df.number_values()];

		for (i = 0; i < new_df.number_variables(); i++)
			new_df_variables[i] = new_df.get_variable(i);
		for (i = 0; i < new_df.number_values(); i++)
			new_df_values[i] = new_df.get_value(i);
		backward_pointers = new DiscreteFunction(new_df_variables,
				new_df_values);
	}

}
