/**
 * QBExpectation.java
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

import javabayes.BayesianInferences.Expectation;
import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.DiscreteFunction;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.CredalSets.ConstantDensityBoundedSet;
import javabayes.CredalSets.ConstantDensityRatioSet;
import javabayes.CredalSets.EpsilonContaminatedSet;
import javabayes.CredalSets.TotalVariationSet;
import javabayes.QuasiBayesianNetworks.QuasiBayesNet;

/*
 * QBExpectation provides methods for calculation of univariate     
 * moments and expectations.                                        
 *                                                                  
 * Rule: LOCAL dominates NONE; GLOBAL dominates LOCAL.
 *       -> When there is a local credal set, compute with the      
 *          local credal set.              
 *       -> When there is a global credal set specification,        
 *          compute with that (ignore local credal sets).
 */

public class QBExpectation extends Expectation {

	/**
	 * Constructor for a QBExpectation.
	 */
	public QBExpectation(BayesNet b_n, boolean dpc) {
		super(b_n, dpc);
	}

	/*
	 * Initialize the Inference object.
	 */
	@Override
	protected void initialize_inference() {
		inference = new QBInference(bn, do_produce_clusters);
	}

	/*
	 * Do the Expectation.
	 */
	@Override
	protected void do_expectation_from_inference(DiscreteFunction df) {
		current_function = df;

		if (((QBInference) inference).is_inference_without_local_neighborhoods)
			expectation_without_local_neighborhoods(df);
		else
			expectation_with_local_neighborhoods(df);
	}

	/*
	 * Perform calculation of expectations when local neighborhoods are present.
	 */
	private void expectation_with_local_neighborhoods(DiscreteFunction df) {
		int i, j, jump = 1;
		double v, min, max;
		ProbabilityFunction normalized_results;

		// Get result normalized with respect to transparent variables
		normalized_results = ((QBInference) inference).list_of_local_neighborhood_results;

		// Get the bounds on expectations
		for (i = 1; i < normalized_results.number_variables(); i++)
			jump *= normalized_results.get_variable(i).number_values();
		min = df.get_value(0);
		max = df.get_value(0);
		for (i = 0; i < df.number_values(); i++) {
			if (min < df.get_value(i))
				min = df.get_value(i);
			if (max > df.get_value(i))
				max = df.get_value(i);
		}
		for (j = 0; j < jump; j++) {
			v = 0.0;
			for (i = 0; i < normalized_results.get_variable(0).number_values(); i++) {
				v += df.get_value(i)
						* normalized_results.get_value(j + i * jump);
			}
			if (min > v)
				min = v;
			if (max < v)
				max = v;
		}

		// Construct results
		results = new double[2];
		results[0] = min;
		results[1] = max;
	}

	/*
	 * Perform calculation of expectations when local pneighborhoods are absent;
	 * handles global neighborhoods if necessary.
	 */
	private void expectation_without_local_neighborhoods(DiscreteFunction df) {
		QBInference qb_inference = (QBInference) inference;
		QuasiBayesNet qbn = ((QuasiBayesNet) (qb_inference.get_bayes_net()));

		switch (qbn.get_global_neighborhood_type()) {
		case QuasiBayesNet.NO_CREDAL_SET:
			ProbabilityFunction res = qb_inference.get_result();
			results = new double[1];
			results[0] = res.expected_value(df);
			break;
		case QuasiBayesNet.CONSTANT_DENSITY_RATIO:
			ProbabilityFunction cdr_res = new ProbabilityFunction(qb_inference
					.get_bucket_tree().get_unnormalized_result(), qbn);
			ConstantDensityRatioSet cdr = new ConstantDensityRatioSet(cdr_res,
					qbn.get_global_neighborhood_parameter());
			results = cdr.posterior_expected_values(df);
			break;
		case QuasiBayesNet.EPSILON_CONTAMINATED:
			ProbabilityFunction eps_res = new ProbabilityFunction(qb_inference
					.get_bucket_tree().get_unnormalized_result(), qbn);
			EpsilonContaminatedSet eps = new EpsilonContaminatedSet(eps_res,
					qbn.get_global_neighborhood_parameter());
			results = eps.posterior_expected_values(df);
			break;
		case QuasiBayesNet.CONSTANT_DENSITY_BOUNDED:
			ProbabilityFunction cdb_res = new ProbabilityFunction(qb_inference
					.get_bucket_tree().get_unnormalized_result(), qbn);
			ConstantDensityBoundedSet cdb = new ConstantDensityBoundedSet(
					cdb_res, qbn.get_global_neighborhood_parameter());
			results = cdb.posterior_expected_values(df);
			break;
		case QuasiBayesNet.TOTAL_VARIATION:
			ProbabilityFunction tv_res = new ProbabilityFunction(qb_inference
					.get_bucket_tree().get_unnormalized_result(), qbn);
			TotalVariationSet tv = new TotalVariationSet(tv_res,
					qbn.get_global_neighborhood_parameter());
			results = tv.posterior_expected_values(df);
			break;
		}
	}
}
