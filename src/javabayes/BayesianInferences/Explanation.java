/*
 * Explanation.java
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

import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;

/*
 * There are two cases to be considered:                             
 *     EXPLANATION or FULL_EXPLANATION                               
 * the only difference is which variables are considered             
 * explanation variables.                                            
 *                                                                   
 * In FULL_EXPLANATION all variables are                             
 * explanation variables except variables that are observed.         
 *                                                                   
 * In EXPLANATION only variables that are marked as explanation      
 * variables are used in the maximization; an observed variable is   
 * not used even if it is marked as explanation variable.            
 * Note that in EXPLANATION mode, if there are no variables          
 * marked as explanation variables, the final result is the posterior
 * marginal.                                                         
 */

public class Explanation {
	BayesNet bn;
	BucketTree bucket_tree;
	ProbabilityFunction results[];

	/**
	 * Constructor for an Explanation.
	 */
	public Explanation(BayesNet b_n) {
		bn = b_n;
	}

	/*
	 * Methods for calculation of explanations.
	 */

	/**
	 * Calculation of an Explanation.
	 */
	public void explanation() {
		explanation(Inference.EXPLANATION);
	}

	/**
	 * Calculation of a full Explanation.
	 */
	public void full_explanation() {
		explanation(Inference.FULL_EXPLANATION);
	}

	/**
	 * Calculation of an Explanation accordingly to the flag explanation_status.
	 */
	public void explanation(int explanation_status) {
		bucket_tree = new BucketTree(new Ordering(bn, (String) null,
				explanation_status, Ordering.MINIMUM_WEIGHT));
		do_inference_from_bucket_tree();
	}

	/**
	 * Calculation of an Explanation given order.
	 */
	public void explanation(String order[]) {
		explanation(order, Inference.EXPLANATION);
	}

	/**
	 * Calculation of a full Explanation given order.
	 */
	public void full_explanation(String order[]) {
		explanation(order, Inference.FULL_EXPLANATION);
	}

	/**
	 * Calculation of an Explanation accordingly to the flag explanation_status.
	 */
	public void explanation(String order[], int explanation_status) {
		bucket_tree = new BucketTree(
				new Ordering(bn, order, explanation_status));
		do_inference_from_bucket_tree();
	}

	/*
	 * Do the Explanation.
	 */
	void do_inference_from_bucket_tree() {
		results = new ProbabilityFunction[1];
		bucket_tree.reduce();
		results[0] = bucket_tree.get_normalized_result();
	}

	/*
	 * Generic, auxiliary methods.
	 */

	/**
	 * Print Explanation.
	 */
	public void print() {
		print(System.out, true);
	}

	/**
	 * Print Explanation.
	 */
	public void print(PrintStream out) {
		print(out, true);
	}

	/**
	 * Print Explanation.
	 */
	public void print(boolean should_print_bucket_tree) {
		print(System.out, should_print_bucket_tree);
	}

	/**
	 * Print Explanation.
	 */
	public void print(PrintStream out, boolean should_print_bucket_tree) {
		int i, bp[];
		ProbabilityVariable pv;

		// Do explanation if Explanation is null.
		if (results == null)
			explanation();

		// Print it all.
		out.println("Explanation:");

		if (should_print_bucket_tree == true)
			bucket_tree.print(out);

		if (bucket_tree.backward_pointers == null) {
			out.println("No explanatory variable; posterior distribution:");
			for (i = 0; i < results.length; i++) {
				results[i].print(out);
			}
		} else {
			bp = bucket_tree.backward_pointers;
			for (i = 0; i < bp.length; i++) {
				if (bp[i] != BayesNet.INVALID_INDEX) {
					pv = bn.get_probability_variable(i);
					out.println("Variable " + pv.get_name() + ": "
							+ pv.get_value(bp[i]));
				}
			}
		}
	}

	/* ************************************************************* */
	/* Methods that allow basic manipulation of non-public variables */
	/* ************************************************************* */

	/**
	 * Get the results in the Explanation.
	 */
	public ProbabilityFunction[] get_results() {
		return (results);
	}
}
