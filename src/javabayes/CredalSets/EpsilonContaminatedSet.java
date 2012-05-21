/**
 * EpsilonContaminatedSet.java
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

import javabayes.BayesianNetworks.DiscreteFunction;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;

public class EpsilonContaminatedSet extends FinitelyGeneratedSet {

	double epsilon;

	/**
	 * Constructor for an EpsilonContaminatedSet ProbabilityFunction object and
	 * given epsilon.
	 */
	public EpsilonContaminatedSet(ProbabilityFunction pf, double eps) {
		super(pf, pf.get_values());
		epsilon = eps;
		if ((epsilon < 0.0) || (epsilon > 1.0))
			epsilon = 0.0;
	}

	/**
	 * Perform calculation of marginal posterior distributions for an
	 * epsilon-contaminated global neighborhood The method assumes that the
	 * values in the EpsilonContaminated are actually unnormalized --- if not,
	 * incorrect results are produced.
	 */
	public ProbabilityFunction posterior_marginal() {
		double one_minus_epsilon = 1.0 - epsilon;

		double lower_values[] = new double[values.length];
		double upper_values[] = new double[values.length];

		// Check the possibility that the query has an observed variable,
		// in which case the marginalization property does not apply.
		if ((variables[0] instanceof ProbabilityVariable)
				&& (((ProbabilityVariable) variables[0]).is_observed() == true)) {
			for (int i = 0; i < values.length; i++) {
				lower_values[i] = values[i];
				upper_values[i] = values[i];
			}
		} // Else, apply the marginalization property.
		else {
			double summation = 0.0;
			for (int i = 0; i < values.length; i++)
				summation += values[i];

			for (int i = 0; i < values.length; i++)
				lower_values[i] = (one_minus_epsilon * values[i])
						/ ((one_minus_epsilon * summation) + epsilon);

			for (int i = 0; i < values.length; i++)
				upper_values[i] = ((one_minus_epsilon * values[i]) + epsilon)
						/ ((one_minus_epsilon * summation) + epsilon);
		}

		return (new QBProbabilityFunction(bn, variables, values, lower_values,
				upper_values, properties));
	}

	/**
	 * Perform calculation of expected value.
	 */
	public double[] expected_values(DiscreteFunction df) {
		double one_minus_epsilon = 1.0 - epsilon;
		double results[] = new double[2];

		// Check the possibility that the query has an observed variable,
		// in which case the marginalization property does not apply.
		if ((variables[0] instanceof ProbabilityVariable)
				&& (((ProbabilityVariable) variables[0]).is_observed() == true)) {
			results[0] = df.get_value(((ProbabilityVariable) variables[0])
					.get_observed_index());
			results[1] = results[0];
		} // Else, apply the marginalization property.
		else {
			// Obtain the summations
			double u_total = 0.0;
			for (int i = 0; i < number_values(); i++)
				u_total += df.get_value(i) * values[i];
			// Obtain the maximum and minimum of functions
			double max_df_value = df.get_value(0);
			double min_df_value = df.get_value(0);
			for (int i = 1; i < df.number_values(); i++) {
				if (max_df_value < df.get_value(i))
					max_df_value = df.get_value(i);
				if (min_df_value > df.get_value(i))
					min_df_value = df.get_value(i);
			}
			// Calculate the values
			results[0] = one_minus_epsilon * u_total + epsilon * max_df_value;
			results[1] = one_minus_epsilon * u_total + epsilon * min_df_value;
		}

		return (results);
	}

	/**
	 * Perform calculation of posterior expected value. Assumes that the
	 * probability values are not normalized; probability values are p(x, e)
	 * where e is the fixed evidence.
	 */
	public double[] posterior_expected_values(DiscreteFunction df) {
		double one_minus_epsilon = 1.0 - epsilon;
		double results[] = new double[2];

		// Check the possibility that the query has an observed variable,
		// in which case the marginalization property does not apply.
		if ((variables[0] instanceof ProbabilityVariable)
				&& (((ProbabilityVariable) variables[0]).is_observed() == true)) {
			results[0] = df.get_value(((ProbabilityVariable) variables[0])
					.get_observed_index());
			results[1] = results[0];
		} // Else, apply the marginalization property.
		else {
			// Obtain the summations
			double p_total = 0.0;
			double u_total = 0.0;
			for (int i = 0; i < values.length; i++) {
				p_total += values[i];
				u_total += df.get_value(i) * values[i];
			}
			// Obtain the maximum and minimum of functions
			double max_df_value = df.get_value(0);
			double min_df_value = df.get_value(0);
			for (int i = 1; i < df.number_values(); i++) {
				if (max_df_value < df.get_value(i))
					max_df_value = df.get_value(i);
				if (min_df_value > df.get_value(i))
					min_df_value = df.get_value(i);
			}
			// Calculate the values
			results[0] = (one_minus_epsilon * u_total + epsilon * min_df_value)
					/ (one_minus_epsilon * p_total + epsilon);
			results[1] = (one_minus_epsilon * u_total + epsilon * max_df_value)
					/ (one_minus_epsilon * p_total + epsilon);
		}

		return (results);
	}
}
