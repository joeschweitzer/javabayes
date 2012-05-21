/*
 * Inference.java
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
import java.util.Vector;

import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;

public class Inference {
	protected BayesNet bn;
	protected BucketTree bucket_tree;
	protected Bucket bucket_for_variable[];
	protected Vector bucket_forest;
	protected ProbabilityFunction result;
	protected boolean do_produce_clusters;

	protected static final int IGNORE_EXPLANATION = 0;
	protected static final int EXPLANATION = 1;
	protected static final int FULL_EXPLANATION = 2;

	/*
	 * Constructor for an Inference.
	 */
	public Inference(BayesNet b_n, boolean dpc) {
		bn = b_n;
		bucket_for_variable = new Bucket[b_n.number_variables()];
		bucket_forest = new Vector();
		do_produce_clusters = dpc;
	}

	/**
	 * Calculation of marginal posterior distribution.
	 */
	public void inference() {
		inference((String) null);
	}

	/**
	 * Calculation of marginal posterior distribution for an arbitrary BayesNet.
	 */
	protected void inference(String queried_variable_name) {
		if (do_produce_clusters) { // If clusters are generated:
			int index_queried = bn.index_of_variable(queried_variable_name);
			if (index_queried != BayesNet.INVALID_INDEX) { // If the
															// queried_variable_name
															// is valid:
				Bucket buck = bucket_for_variable[index_queried];
				// If the variable has no Bucket or a Bucket without valid
				// cluster:
				if ((buck == null) || (buck.cluster == null)) {
					inference(new Ordering(bn, queried_variable_name,
							IGNORE_EXPLANATION, Ordering.MINIMUM_WEIGHT));
				} else { // If variable already has a Bucket:
					// Get the BucketTree.
					bucket_tree = buck.bucket_tree;
					// Note that the method bucket_tree.distribute() below must
					// return true:
					// - the bucket_tree is constructed with IGNORE_EXPLANATION.
					// - this block only runs if do_produce_clusters is true.
					if (buck.bucket_status != Bucket.DISTRIBUTED)
						if (buck == bucket_tree.bucket_tree[bucket_tree.bucket_tree.length - 1])
							bucket_tree.reduce(); // If Bucket is the last
													// bucket, then just reduce;
						else
							// if not, then distribute.
							bucket_tree.distribute();
					// Now process the cluster in the Bucket.
					System.out.println("ARRIVED HERE!");
					buck.reduce_cluster();
					// And then get the result
					System.out.println("AND HERE TOO!");
					result = bucket_tree.get_normalized_result();
				}
			} else { // If the queried_variable_name is invalid:
				inference(new Ordering(bn, (String) null, IGNORE_EXPLANATION,
						Ordering.MINIMUM_WEIGHT));
			}
		} else { // If no cluster is generated:
			inference(new Ordering(bn, queried_variable_name,
					IGNORE_EXPLANATION, Ordering.MINIMUM_WEIGHT));
		}
	}

	/**
	 * Calculation of marginal posterior distribution using a given ordering,
	 * and an arbitrary BayesNet.
	 */
	protected void inference(String order[]) {
		inference(new Ordering(bn, order, IGNORE_EXPLANATION));
	}

	/*
	 * Calculation of marginal posterior distribution.
	 */
	private void inference(Ordering or) {
		// Create the Ordering and the BucketTree.
		bucket_tree = new BucketTree(or, do_produce_clusters);
		// Add the new BucketTree to the bucket_forest and update
		// bucket_for_variable.
		if (do_produce_clusters)
			add_bucket_tree();
		// Generate the result by reducing the BucketTree.
		bucket_tree.reduce();
		result = bucket_tree.get_normalized_result();
	}

	/*
	 * Add a BucketTree to the bucket_forest and update the bucket_for_variable
	 * array.
	 */
	private void add_bucket_tree() {
		Bucket buck;
		// Add the current BucketTree to the bucket_forest.
		bucket_forest.addElement(bucket_tree);
		// Put the buckets in correspondence with the variables.
		for (int i = 0; i < bucket_tree.bucket_tree.length; i++) {
			buck = bucket_tree.bucket_tree[i];
			bucket_for_variable[buck.variable.get_index()] = buck;
		}
	}

	/*
	 * Generic, auxiliary methods.
	 */

	/**
	 * Print the Inference.
	 */
	public void print() {
		print(System.out, true);
	}

	/**
	 * Print the Inference.
	 */
	public void print(PrintStream out) {
		print(out, true);
	}

	/**
	 * Print the Inference.
	 */
	public void print(boolean should_print_bucket_tree) {
		print(System.out, should_print_bucket_tree);
	}

	/**
	 * Print the Inference.
	 */
	public void print(PrintStream out, boolean should_print_bucket_tree) {
		int i, bp[];
		ProbabilityVariable pv;

		// Do inference if Inference is null.
		if (result == null)
			inference();

		// Print it all.
		out.print("Posterior distribution:");

		if (should_print_bucket_tree == true)
			bucket_tree.print(out);
		out.println();

		result.print(out);
	}

	/* ************************************************************* */
	/* Methods that allow basic manipulation of non-public variables */
	/* ************************************************************* */

	/**
	 * Get the BucketTree.
	 */
	public BucketTree get_bucket_tree() {
		return (bucket_tree);
	}

	/**
	 * Get the BayesNet.
	 */
	public BayesNet get_bayes_net() {
		return (bn);
	}

	/**
	 * Get the current result of the Inference.
	 */
	public ProbabilityFunction get_result() {
		return (result);
	}

	/**
	 * Get the status of the clustering process.
	 */
	public boolean areClustersProduced() {
		return (do_produce_clusters);
	}
}
