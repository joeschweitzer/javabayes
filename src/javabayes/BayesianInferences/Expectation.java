/*
 * Expectation.java
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
import javabayes.BayesianNetworks.DiscreteFunction;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;

/*
 * Expectation provides methods for calculation of univariate        
 * moments and expectations.                                         
 * An important fact is that the calculation of Expectation relies   
 * on the algorithms in the Inference class.                         
 */

public class Expectation {
	protected BayesNet bn;
	protected Inference inference;
	protected double results[];
	protected boolean do_produce_clusters;

	protected DiscreteFunction current_function;

	final static int EXPECTED_VALUE = 1;
	final static int SECOND_MOMENT = 2;
	final static int THIRD_MOMENT = 3;
	final static int FOURTH_MOMENT = 4;

	/**
	 * Constructor for an Expectation.
	 */
	public Expectation(BayesNet b_n, boolean dpc) {
		bn = b_n;
		do_produce_clusters = dpc;
		initialize_inference();
	}

	/*
	 * Initialize the Inference object.
	 */
	protected void initialize_inference() {
		inference = new Inference(bn, do_produce_clusters);
	}

	/*
	 * Expectations in all possible ways: 1) Specifying or not the queried
	 * variable and the ordering 2) Specifying or not the moment order
	 */

	/**
	 * Calculation of Expectation.
	 */
	public void expectation() {
		// Construct the function with the values.
		ProbabilityVariable pv = bn.get_probability_variable(0);
		DiscreteFunction df = construct_values(pv, Expectation.EXPECTED_VALUE);
		// Calculate expectation.
		expectation(df);
	}

	/**
	 * Calculation of Expectation.
	 */
	public void expectation(String queried_variable_name) {
		// Construct the function with the values
		int index = bn.index_of_variable(queried_variable_name);
		if (index == BayesNet.INVALID_INDEX) {
			expectation();
			return;
		}
		ProbabilityVariable pv = bn.get_probability_variable(index);
		DiscreteFunction df = construct_values(pv, Expectation.EXPECTED_VALUE);
		// Calculate expectation.
		expectation(df, queried_variable_name);
	}

	/**
	 * Perform calculation of expectation given order.
	 */
	public void expectation(String order[]) {
		// Construct the function with the values
		int index = bn.index_of_variable(order[order.length - 1]);
		if (index == BayesNet.INVALID_INDEX) {
			expectation();
			return;
		}
		ProbabilityVariable pv = bn.get_probability_variable(index);
		DiscreteFunction df = construct_values(pv, Expectation.EXPECTED_VALUE);
		// Calculate expectation.
		expectation(df, order);
	}

	/**
	 * Calculation of Expectation.
	 */
	public void expectation(int moment_order) {
		// Construct the function with the values
		ProbabilityVariable pv = bn.get_probability_variable(0);
		DiscreteFunction df = construct_values(pv, moment_order);
		// Calculate expectation.
		expectation(df);
	}

	/**
	 * Calculation of Expectation.
	 */
	public void expectation(int moment_order, String queried_variable_name) {
		// Construct the function with the values
		int index = bn.index_of_variable(queried_variable_name);
		if (index == BayesNet.INVALID_INDEX) {
			expectation();
			return;
		}
		ProbabilityVariable pv = bn.get_probability_variable(index);
		DiscreteFunction df = construct_values(pv, moment_order);
		// Calculate expectation.
		expectation(df, queried_variable_name);
	}

	/**
	 * Calculation of expectation given order.
	 */
	public void expectation(int moment_order, String order[]) {
		// Construct the function with the values
		int index = bn.index_of_variable(order[order.length - 1]);
		if (index == BayesNet.INVALID_INDEX) {
			expectation();
			return;
		}
		ProbabilityVariable pv = bn.get_probability_variable(index);
		DiscreteFunction df = construct_values(pv, moment_order);
		// Calculate expectation.
		expectation(df, order);
	}

	/**
	 * Do the Expectation, assuming the input DiscreteFunction is a function
	 * only of the queried variable.
	 */
	public void expectation(DiscreteFunction df) {
		inference.inference();
		do_expectation_from_inference(df);
	}

	/**
	 * Do the Expectation, assuming the input DiscreteFunction is a function
	 * only of the queried variable.
	 */
	public void expectation(DiscreteFunction df, String queried_variable_name) {
		inference.inference(queried_variable_name);
		do_expectation_from_inference(df);
	}

	/**
	 * Do the Expectation given order, assuming the input DiscreteFunction is a
	 * function only of the queried variable.
	 */
	public void expectation(DiscreteFunction df, String order[]) {
		inference.inference(order);
		do_expectation_from_inference(df);
	}

	/*
	 * Construct the utility function that produces the requested moment.
	 */
	private DiscreteFunction construct_values(ProbabilityVariable pv,
			int moment_order) {
		DiscreteFunction df = pv.get_numeric_values();
		if (moment_order > 1) {
			for (int i = 0; i < df.number_values(); i++)
				df.set_value(i, Math.pow(df.get_value(i), moment_order));
		}
		return (df);
	}

	/*
	 * Do the expectations from inference.
	 */
	protected void do_expectation_from_inference(DiscreteFunction df) {
		current_function = df;

		ProbabilityFunction res = inference.get_result();
		results = new double[1];
		results[0] = res.expected_value(df);
	}

	/*
	 * Generic, auxiliary methods.
	 */

	/**
	 * Print Expectation.
	 */
	public void print() {
		print(System.out, true);
	}

	/**
	 * Print Expectation.
	 */
	public void print(PrintStream out) {
		print(out, true);
	}

	/**
	 * Print Expectation.
	 */
	public void print(boolean should_print_bucket_tree) {
		print(System.out, should_print_bucket_tree);
	}

	/**
	 * Print Expectation.
	 */
	public void print(PrintStream out, boolean should_print_bucket_tree) {
		int i, bp[];
		ProbabilityVariable pv;

		// Print it all.
		out.print("Posterior expectation: [");
		for (i = 0; i < results.length; i++)
			out.print(results[i] + " ");
		out.println("], for function:");
		current_function.print(out);
		out.println();

		if (should_print_bucket_tree == true)
			inference.bucket_tree.print(out);
	}

	/* ************************************************************* */
	/* Methods that allow basic manipulation of non-public variables */
	/* ************************************************************* */

	/**
	 * Get the results of Expectation.
	 */
	public double[] get_results() {
		return (results);
	}
}
