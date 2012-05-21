/**
 * QBInference.java
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

package javabayes.QuasiBayesianInferences;

import java.util.Enumeration;
import java.util.Vector;

import javabayes.BayesianInferences.Inference;
import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.DiscreteFunction;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;
import javabayes.CredalSets.ConstantDensityBoundedSet;
import javabayes.CredalSets.ConstantDensityRatioSet;
import javabayes.CredalSets.EpsilonContaminatedSet;
import javabayes.CredalSets.QBProbabilityFunction;
import javabayes.CredalSets.TotalVariationSet;
import javabayes.CredalSets.VertexSet;
import javabayes.QuasiBayesianNetworks.QuasiBayesNet;

/*
 * There are three cases to be considered:                          
 * Either the models are single distributions, local credal sets    
 *    or global credal sets: NONE, LOCAL and GLOBAL.                 
 * Note that when explanations are requested and there are no       
 * explanatory variables, the final result is the posterior         
 * marginal but the possibly present local credal sets are ignored. 
 * This is a method to turn local credal sets off without           
 * necessarily deleting them.                                       
 *                                                                  
 * Rule: LOCAL dominates NONE; GLOBAL dominates LOCAL. 
 *       -> When there is a local credal set, compute with the      
 *          local credal set.              
 *       -> When there is a global credal set specification,        
 *          compute with that (ignore local credal sets).           
 */

public class QBInference extends Inference {
	BayesNet original_bn;
	ProbabilityFunction list_of_local_neighborhood_results;

	boolean is_inference_without_local_neighborhoods;

	/**
	 * Constructor for a QBInference.
	 */
	public QBInference(BayesNet b_n, boolean dpc) {
		super(b_n, dpc);
		original_bn = b_n;
		transform_network();
	}

	/*
	 * Code for basic transformation.
	 */

	/*
	 * Create all the values and transparent variables for the credal sets, but
	 * do not include the transparent variables in the probability_variables
	 * array. Local neighborhoods are only used if there are local credal sets
	 * and no global credal set.
	 */
	private void transform_network() {
		// Decide whether transformation is necessary
		if (bn instanceof QuasiBayesNet)
			is_inference_without_local_neighborhoods = (((QuasiBayesNet) bn)
					.get_global_neighborhood_type() != QuasiBayesNet.NO_CREDAL_SET)
					|| (!(((QuasiBayesNet) bn).are_local_credal_sets_present()));
		else
			is_inference_without_local_neighborhoods = true;

		// Generate the transformed_bn.
		bn = new QuasiBayesNet(bn);

		// If no transformation, then return.
		if (is_inference_without_local_neighborhoods)
			return;

		// Else, copy all relevant content from bn to transformed_bn
		bn.set_name("Transformed-Network");
		Vector auxiliary_variables = transform_probability_functions_array();
		transform_probability_variables_array(auxiliary_variables);
	}

	/*
	 * Create all the values and transparent variables for the credal sets.
	 */
	private Vector transform_probability_functions_array() {
		VertexSet qbpf, new_qbpf;
		ProbabilityFunction pf, new_probability_function;
		Vector auxiliary_variables = new Vector();

		// Process every ProbabilityFunction
		for (int i = 0; i < bn.number_probability_functions(); i++) {
			pf = bn.get_probability_function(i);
			if (pf instanceof VertexSet) {
				qbpf = (VertexSet) pf;
				new_qbpf = qbpf.prepare_auxiliary_variable(bn);
				auxiliary_variables.addElement(new_qbpf
						.get_auxiliary_variable());
				bn.set_probability_function(i, new_qbpf);
			} else {
				new_probability_function = new ProbabilityFunction(bn,
						pf.get_variables(), pf.get_values(), (Vector) null);
				bn.set_probability_function(i, new_probability_function);
			}
		}
		return (auxiliary_variables);
	}

	/*
	 * Copy all the regular and auxiliary variables into a new
	 * probability_variables array, making the auxiliary variables available for
	 * calculation of marginals
	 */
	private void transform_probability_variables_array(Vector auxs) {
		ProbabilityVariable new_probability_variable;
		ProbabilityVariable new_probability_variables[];
		Enumeration e;
		int i, j, new_array_size;

		// Create the new probability_variables array
		new_array_size = bn.number_variables() + auxs.size();
		new_probability_variables = new ProbabilityVariable[new_array_size];
		// Insert regular variables into new array
		for (i = 0; i < bn.number_variables(); i++) {
			new_probability_variable = new ProbabilityVariable(bn,
					bn.get_probability_variable(i));
			new_probability_variables[i] = new_probability_variable;
		}
		// Insert auxiliary variables into new array
		for (e = auxs.elements(), j = i; j < new_probability_variables.length; j++) {
			// Insert auxiliary variable
			new_probability_variables[j] = (ProbabilityVariable) (e
					.nextElement());
			// Update the index of auxiliary variable
			new_probability_variables[j].set_index(j);
		}

		// Replace probability_variables
		bn.set_probability_variables(new_probability_variables);
	}

	/*
	 * Calculation of Inference.
	 */
	@Override
	public void inference(String queried_variable_name) {
		super.inference(queried_variable_name);
		do_quasi_bayesian_inference();
	}

	/**
	 * Calculation of marginal posterior envelope using a given ordering.
	 */
	@Override
	public void inference(String order[]) {
		inference(order);
		do_quasi_bayesian_inference();
	}

	/*
	 * Do the Inference.
	 */
	protected void do_quasi_bayesian_inference() {
		// Process result
		if (is_inference_without_local_neighborhoods)
			inference_without_local_neighborhoods();
		else
			inference_with_local_neighborhoods();
	}

	/*
	 * Perform calculation of marginal posterior distributions when local
	 * neighborhoods are present. Note that the distributions for the queried
	 * variable, for all transparent variables, is stored at results.
	 */
	private void inference_with_local_neighborhoods() {
		int i, j, jump = 1;
		double v, min[], max[];
		DiscreteFunction unnormalized_results;
		ProbabilityFunction normalized_results;

		// Normalize with respect to transparent variables
		unnormalized_results = bucket_tree.get_unnormalized_result();
		normalized_results = new ProbabilityFunction(bn,
				unnormalized_results.get_variables(),
				unnormalized_results.get_values(), (Vector) null);
		normalized_results.normalize_first();

		// Get the bounds on probability
		for (i = 1; i < normalized_results.number_variables(); i++)
			jump *= normalized_results.get_variable(i).number_values();
		min = new double[normalized_results.get_variable(0).number_values()];
		max = new double[normalized_results.get_variable(0).number_values()];
		for (i = 0; i < normalized_results.get_variable(0).number_values(); i++) {
			min[i] = 1.0;
			max[i] = 0.0;
			for (j = 0; j < jump; j++) {
				v = normalized_results.get_value(j + i * jump);
				if (v < min[i])
					min[i] = v;
				if (v > max[i])
					max[i] = v;
			}

		}
		// Construct results
		result = new QBProbabilityFunction(normalized_results, (double[]) null,
				min, max);
		list_of_local_neighborhood_results = normalized_results;
	}

	/*
	 * Perform calculation of marginal posterior distributions when local
	 * neighborhoods are absent; handles global neighborhoods if necessary.
	 */
	private void inference_without_local_neighborhoods() {
		DiscreteFunction unnormalized = bucket_tree.get_unnormalized_result();

		switch (((QuasiBayesNet) bn).get_global_neighborhood_type()) {
		case QuasiBayesNet.NO_CREDAL_SET:
			result = new ProbabilityFunction(unnormalized, bn);
			result.normalize();
			break;
		case QuasiBayesNet.CONSTANT_DENSITY_RATIO:
			ProbabilityFunction cdr_res = new ProbabilityFunction(unnormalized,
					bn);
			ConstantDensityRatioSet cdr = new ConstantDensityRatioSet(cdr_res,
					((QuasiBayesNet) bn).get_global_neighborhood_parameter());
			result = cdr.posterior_marginal();
			break;
		case QuasiBayesNet.EPSILON_CONTAMINATED:
			ProbabilityFunction eps_res = new ProbabilityFunction(unnormalized,
					bn);
			EpsilonContaminatedSet eps = new EpsilonContaminatedSet(eps_res,
					((QuasiBayesNet) bn).get_global_neighborhood_parameter());
			result = eps.posterior_marginal();
			break;
		case QuasiBayesNet.CONSTANT_DENSITY_BOUNDED:
			ProbabilityFunction cdb_res = new ProbabilityFunction(unnormalized,
					bn);
			ConstantDensityBoundedSet cdb = new ConstantDensityBoundedSet(
					cdb_res,
					((QuasiBayesNet) bn).get_global_neighborhood_parameter());
			result = cdb.posterior_marginal();
			break;
		case QuasiBayesNet.TOTAL_VARIATION:
			ProbabilityFunction tv_res = new ProbabilityFunction(unnormalized,
					bn);
			TotalVariationSet tv = new TotalVariationSet(tv_res,
					((QuasiBayesNet) bn).get_global_neighborhood_parameter());
			result = tv.posterior_marginal();
			break;
		}
	}
}
