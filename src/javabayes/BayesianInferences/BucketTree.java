/*
 * BucketTree.java
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

import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.DiscreteFunction;
import javabayes.BayesianNetworks.DiscreteVariable;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;

/*******************************************************************/

public class BucketTree {
	Bucket bucket_tree[]; // Array of Bucket objects.
	BayesNet bn; // BayesNet that contains the variables.

	int backward_pointers[]; // Array that stores the index of variables for
								// minimization.

	DiscreteFunction unnormalized_result;

	Ordering ordering;
	int explanation_status;
	boolean do_produce_clusters;

	private int active_bucket;

	final static int MAX_OUT = 2;
	final static int SUM_OUT = 1;

	/**
	 * Simple constructor for BucketTree.
	 */
	public BucketTree(Ordering ord) {
		this(ord, false);
	}

	/**
	 * Constructor for BucketTree. Does the whole initialization; it should be
	 * the only method that deals with symbolic names for variables.
	 */
	public BucketTree(Ordering ord, boolean dpc) {
		int i, j, markers[];
		ProbabilityFunction pf;
		ProbabilityVariable pv;
		DiscreteVariable aux_pv;
		DiscreteFunction ut;
		String order[];

		do_produce_clusters = dpc;
		ordering = ord;

		// Collect information from the Ordering object.
		bn = ord.bn;
		explanation_status = ord.explanation_status;
		order = ord.order;

		// Indicate the first bucket to process
		active_bucket = 0;

		// Check the possibility that the query has an observed variable
		i = bn.index_of_variable(order[order.length - 1]);
		pv = bn.get_probability_variable(i);
		if (pv.is_observed() == true) {
			pf = transform_to_probability_function(bn, pv);
			bucket_tree = new Bucket[1];
			bucket_tree[0] = new Bucket(this, pv, do_produce_clusters);
			insert(pf);
		} else {
			// Initialize the bucket objects
			bucket_tree = new Bucket[order.length];
			for (i = 0; i < order.length; i++) {
				j = bn.index_of_variable(order[i]);
				bucket_tree[i] = new Bucket(this,
						bn.get_probability_variable(j), do_produce_clusters);
			}
			// Insert the probability functions into the bucket_tree;
			// first mark all functions that are actually going
			// into the bucket_tree.
			markers = new int[bn.number_variables()];
			for (i = 0; i < order.length; i++)
				markers[bn.index_of_variable(order[i])] = 1;
			// Now insert functions that are marked and non-null.
			for (i = 0; i < bn.number_probability_functions(); i++) {
				if (markers[bn.get_probability_function(i).get_index(0)] == 1) {
					pf = check_evidence(bn.get_probability_function(i));
					if (pf != null) {
						aux_pv = (bn.get_probability_function(i))
								.get_variable(0);
						insert(pf, !pf.memberOf(aux_pv.get_index()));
					}
				}
			}
			// Insert the utility_function.
			ut = bn.get_utility_function();
			if (ut != null)
				insert(ut);
		}
	}

	/*
	 * Transform an observed ProbabilityVariable into a ProbabilityFunction to
	 * handle the case where the query involves an observed variable.
	 */
	private ProbabilityFunction transform_to_probability_function(BayesNet bn,
			ProbabilityVariable pv) {
		ProbabilityFunction pf = new ProbabilityFunction(bn, 1,
				pv.number_values(), null);
		pf.set_variable(0, pv);
		int index_of_value = pv.get_observed_index();
		pf.set_value(index_of_value, 1.0);
		return (pf);
	}

	/*
	 * Eliminates all variables defined as evidence. The order of the variables
	 * that are not eliminated is the same order in the original function.
	 */
	private ProbabilityFunction check_evidence(ProbabilityFunction pf) {
		int i, j, k, v, aux_i;
		boolean markers[] = new boolean[bn.number_variables()];
		int n = build_evidence_markers(pf, markers);

		// Handle special cases
		if (n == 0)
			return (null); // No variable remains
		if (n == pf.number_variables())
			return (pf); // No relevant evidence

		// Calculate necessary quantities in such a
		// way that the order of variables in the original
		// function is not altered.
		int joined_indexes[] = new int[n];
		for (i = 0, j = 0, v = 1; i < pf.number_variables(); i++) {
			aux_i = pf.get_variable(i).get_index();
			if (markers[aux_i] == true) {
				joined_indexes[j] = aux_i;
				j++;
				v *= bn.get_probability_variable(aux_i).number_values();
			}
		}

		// Create new function to be filled with joined variables
		ProbabilityFunction new_pf = new ProbabilityFunction(bn, n, v, null);
		for (i = 0; i < n; i++)
			new_pf.set_variable(i,
					bn.get_probability_variable(joined_indexes[i]));

		// Loop through the values
		check_evidence_loop(new_pf, pf);

		return (new_pf);
	}

	/*
	 * Build an array of markers. The marker for a variable is true only if the
	 * variable is present in the input ProbabilityFunction pf and is not
	 * observed. Even explanatory variables can be observed and taken as
	 * evidence.
	 */
	private int build_evidence_markers(ProbabilityFunction pf,
			boolean markers[]) {
		int i, n;
		// Initialize the markers
		for (i = 0; i < markers.length; i++)
			markers[i] = false;
		// Insert the variables of the ProbabilityFunction
		for (i = 0; i < pf.number_variables(); i++)
			markers[pf.get_index(i)] = true;
		// Take the evidence out
		for (i = 0; i < bn.number_variables(); i++) {
			if (bn.get_probability_variable(i).is_observed())
				markers[i] = false;
		}
		// Count how many variables remain
		n = 0;
		for (i = 0; i < markers.length; i++) {
			if (markers[i] == true)
				n++;
		}
		return (n);
	}

	/*
	 * Obtain the values for the evidence plus function.
	 */
	private void check_evidence_loop(ProbabilityFunction new_pf,
			ProbabilityFunction pf) {
		int i, j, k, l, m, p, last, current;
		int indexes[] = new int[bn.number_variables()];
		int value_lengths[] = new int[bn.number_variables()];

		for (i = 0; i < bn.number_variables(); i++) {
			indexes[i] = 0;
			value_lengths[i] = bn.get_probability_variable(i).number_values();
		}
		for (i = 0; i < bn.number_variables(); i++) {
			if (bn.get_probability_variable(i).is_observed()) {
				indexes[i] = bn.get_probability_variable(i)
						.get_observed_index();
			}
		}
		last = new_pf.number_variables() - 1;
		for (i = 0; i < new_pf.number_values(); i++) {
			p = new_pf.get_position_from_indexes(indexes);
			new_pf.set_value(p, pf.evaluate(indexes));

			indexes[new_pf.get_index(last)]++;
			for (j = last; j > 0; j--) {
				current = new_pf.get_index(j);
				if (indexes[current] >= value_lengths[current]) {
					indexes[current] = 0;
					indexes[new_pf.get_index(j - 1)]++;
				} else
					break;
			}
		}
	}

	/**
	 * Variable elimination in the BucketTree.
	 */
	public void reduce() {
		int i;
		// Reduce all Bucket objects.
		for (i = 0; i < (bucket_tree.length - 1); i++) {
			active_bucket = i;
			bucket_tree[i].reduce();
			insert(bucket_tree[i]);
		}
		// Now reduce the last Bucket.
		unnormalized_result = bucket_tree[i].combine();
		// Mark the last Bucket as DISTRIBUTED.
		bucket_tree[i].bucket_status = Bucket.DISTRIBUTED;
		// Generate the backward_pointers if necessary.
		backward_pointers = backward_maximization();
	}

	/**
	 * Distribute evidence in the BucketTree.
	 * 
	 * @return true if successful; false if not.
	 */
	public boolean distribute() {
		int i, j;
		boolean mark_non_conditioning[] = new boolean[bn.number_variables()];

		// First make sure the BucketTree has been reduced.
		if (unnormalized_result == null)
			reduce();
		// Second make sure there is more than one Bucket in the BucketTree.
		int last = bucket_tree.length - 1;
		if (last < 1)
			return (true);
		// Third, this method is used only if do_produce_clusters is true.
		if (do_produce_clusters == false)
			return (false);
		// Fourth, this method is use only if no explanatory variable was max'ed
		// out.
		if (backward_pointers != null)
			return (false);

		// Go through the Bucket objects, from bottom to top,
		// to compute the new separator and cluster for each bucket.
		for (i = (last - 1); i >= 0; i--) { // Start from (last-1); last does
											// not have child.
			// Check whether the Bucket has any valid content.
			if (bucket_tree[i].cluster == null)
				break;
			// Take the non-conditioning variables in a boolean array.
			for (j = 0; j < mark_non_conditioning.length; j++)
				mark_non_conditioning[j] = true;
			// OBS: The following piece of code will actually be less efficient
			// than
			// necessary. It will count as "conditioning" any variable in the
			// cluster
			// except the bucket variable. This will imply that some variables
			// in the
			// separator will be normalized over without need, and the separator
			// will
			// be larger than necessary.
			// OBS: this code was contributed by Wei Zhou (wei@cs.ualberta.ca),
			// who also detected the problem with the original code.
			// if (bucket_tree[i].cluster.number_variables() >
			// bucket_tree[i].non_conditioning_variables.size())
			for (j = 1; j < bucket_tree[i].cluster.number_variables(); j++) {
				mark_non_conditioning[(bucket_tree[i].cluster.get_variables())[j]
						.get_index()] = false;
			}

			// The following piece of code does the right thing (compared to the
			// piece of code above): it selects the
			// minimum number of non-conditioning variables. To use this piece
			// of code, it will be necessary to create a "normalize" method that
			// normalizes with respect to a number of variables at at time.
			/*
			 * for (j=0; j<bucket_tree[i].cluster.number_variables(); j++) {
			 * mark_non_conditioning[
			 * (bucket_tree[i].cluster.get_variables())[j].get_index() ] =
			 * false; } for (Enumeration e =
			 * bucket_tree[i].non_conditioning_variables.elements();
			 * e.hasMoreElements(); ) { ProbabilityVariable pv =
			 * (ProbabilityVariable)(e.nextElement());
			 * mark_non_conditioning[pv.get_index() ] = true; }
			 */

			// Update the separator.
			bucket_tree[i].separator = bucket_tree[i].child.cluster.sum_out(
					bn.get_probability_variables(), mark_non_conditioning);

			// Compute cluster using new separator (note that if separator
			// is null, the cluster had all variables already processed).
			if (bucket_tree[i].separator != null) {
				// OBS: the method here should normalize with respect to more
				// than one variable, to allow this algorithm to be more
				// efficient!
				bucket_tree[i].cluster.normalize_first();
				// Now combine the cluster and the separator.
				bucket_tree[i].cluster = bucket_tree[i].cluster.multiply(
						bn.get_probability_variables(),
						bucket_tree[i].separator);
			}

			// Mark the Bucket as DISTRIBUTED.
			bucket_tree[i].bucket_status = Bucket.DISTRIBUTED;
		}
		// Indicate success.
		return (true);
	}

	/*
	 * Recover the maximizing variables going back through the maximizing
	 * bucket_tree; the variables are returned as an array of markers
	 * (non-explanation variables get INVALID_INDEX).
	 */
	private int[] backward_maximization() {
		int i, j;
		int bi = bucket_tree.length - 1;
		DiscreteFunction back_df;
		Bucket b = bucket_tree[bi];

		// If there are no explanation variables in the BayesNet, return null
		if (b.backward_pointers == null)
			return (null);

		// Initialize the markers for backward pointers with INVALID_INDEX
		int backward_markers[] = new int[bn.number_variables()];
		for (i = 0; i < backward_markers.length; i++)
			backward_markers[i] = BayesNet.INVALID_INDEX;

		// Initialize the marker for the last bucket
		backward_markers[b.variable.get_index()] = (int) (b.backward_pointers
				.get_value(0) + 0.5);

		// Go backwards through the bucket_tree
		for (i = (bi - 1); i >= 0; i--) {
			if (!bucket_tree[i].is_explanation())
				break;
			back_df = bucket_tree[i].backward_pointers;
			// Skip null pointers (caused by evidence)
			if (back_df == null)
				continue;
			// Special treatment for bucket with only one value,
			// since it can be a bucket with only the bucket variable left
			if (back_df.number_values() == 1) {
				backward_markers[bucket_tree[i].variable.get_index()] = (int) (back_df
						.get_value(0) + 0.5);
				continue;
			}
			// Process the bucket
			j = back_df.get_position_from_indexes(
					bn.get_probability_variables(), backward_markers);
			backward_markers[bucket_tree[i].variable.get_index()] = (int) (back_df
					.get_value(j) + 0.5);
		}

		return (backward_markers);
	}

	/*
	 * Put the separator function of a Bucket buck into the BucketTree beyond
	 * the current active_bucket.
	 */
	private void insert(Bucket buck) {
		int i, index;
		Bucket b;

		if (buck.separator == null)
			return;

		for (i = active_bucket; i < bucket_tree.length; i++) {
			// Get the index for current Bucket's variable.
			index = bucket_tree[i].variable.get_index();
			// If separator contains a variable in the current Bucket, then join
			// buckets.
			if (buck.separator.memberOf(index)) {
				// Add separator to bucket.
				bucket_tree[i].discrete_functions.addElement(buck.separator);
				// Update the non_conditioning variables.
				// Go through the non-conditioning variables in the inserted
				// Bucket.
				for (Enumeration e = buck.non_conditioning_variables.elements(); e
						.hasMoreElements();)
					bucket_tree[i].non_conditioning_variables.addElement(e
							.nextElement());
				// Take the inserted Bucket variable out by making it
				// CONDITIONING:
				// Must take the variable out as it has been eliminated already.
				bucket_tree[i].non_conditioning_variables
						.removeElement(buck.variable);
				// Mark parent/child relationship.
				buck.child = bucket_tree[i];
				bucket_tree[i].parents.addElement(buck);
				return;
			}
		}
	}

	/*
	 * Put a DiscreteFunction into the BucketTree beyond the current
	 * active_bucket.
	 */
	private void insert(DiscreteFunction df) {
		insert(df, false);
	}

	/*
	 * Put a DiscreteFunction into the BucketTree beyond the current
	 * active_bucket. If was_first_variable_cancelled_by_evidence is true, then
	 * mark the bucket accordingly.
	 */
	private void insert(DiscreteFunction df,
			boolean was_first_variable_cancelled_by_evidence) {
		int i, index;
		Bucket b;
		for (i = active_bucket; i < bucket_tree.length; i++) {
			index = bucket_tree[i].variable.get_index();
			if (df.memberOf(index)) {
				bucket_tree[i].discrete_functions.addElement(df);
				// If the function is a ProbabilityFunction, store its
				// first variable appropriately (assuming for now that
				// the first variable is the only possible non-conditioning
				// variable).
				if ((df instanceof ProbabilityFunction)
						&& (!was_first_variable_cancelled_by_evidence)) {
					bucket_tree[i].non_conditioning_variables.addElement(df
							.get_variable(0));
				}
				return;
			}
		}
	}

	/**
	 * Print method for BucketTree.
	 */
	public void print() {
		print(System.out);
	}

	/**
	 * Print method for BucketTree.
	 */
	public void print(PrintStream out) {
		out.println("BucketTree:" + "\n\tActive Bucket is " + active_bucket
				+ ".");
		for (int i = 0; i < bucket_tree.length; i++)
			bucket_tree[i].print(out);
		out.println("Bucket result: ");
		unnormalized_result.print(out);
	}

	/**
	 * Get the normalized result for the BucketTree.
	 */
	public ProbabilityFunction get_normalized_result() {
		ProbabilityFunction aux_pf = new ProbabilityFunction(
				unnormalized_result, bn);
		aux_pf.normalize();
		return (aux_pf);
	}

	/* *************************************************************** */
	/* Methods that allow basic manipulation of non-public variables */
	/* *************************************************************** */

	/**
	 * Get the unnormalized_result for the BucketTree.
	 */
	public DiscreteFunction get_unnormalized_result() {
		return (unnormalized_result);
	}
}
