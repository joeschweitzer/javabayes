/**
 * TwoMonotoneCapacity.java
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

/*
 * This abstract class provides the infra-structure for calculation 
 * of posterior marginals, generalized Choquet integrals and        
 * posterior expectations for two-monotone capacities.              
 * Particular classes of capacities must provide implementations    
 * for the abstract methods in this class.                          
 */

public abstract class TwoMonotoneCapacity extends FinitelyGeneratedSet
		implements MappingDouble {

	// Auxiliary variable that holds a discrete function for bracketing
	private DiscreteFunction temporary_discrete_function;

	private final static int LOWER_EXPECTATION_BRACKET = 0;
	private final static int UPPER_EXPECTATION_BRACKET = 1;

	private final static double ACCURACY = 10E-8;

	/**
	 * Constructor for an TwoMonotoneCapacity ProbabilityFunction object and
	 * given epsilon.
	 */
	public TwoMonotoneCapacity(ProbabilityFunction pf) {
		super(pf, pf.get_values());
	}

	/**
	 * Obtain the lower probability of an event given the base probability for
	 * the event.
	 */
	public abstract double get_lower_probability_from_base(double p);

	/***************************************************************/
	/* Obtain the upper probability of an event given the base */
	/* probability for the event. */
	/***************************************************************/
	public abstract double get_upper_probability_from_base(double p);

	/***************************************************************/
	/* Get a base probability value for an atom. */
	/***************************************************************/
	public abstract double get_atom_probability(int index);

	/***************************************************************/
	/* Perform calculation of marginal posterior distributions for */
	/* a total variation global neighborhood */
	/***************************************************************/
	public ProbabilityFunction posterior_marginal() {
		double lower_values[] = new double[values.length];
		double upper_values[] = new double[values.length];
		DiscreteFunction df = new DiscreteFunction(1, values.length);

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
			double total = 0.0; // Probability p(e)
			for (int i = 0; i < values.length; i++)
				total += values[i];

			for (int i = 0; i < values.length; i++)
				lower_values[i] = (get_lower_probability_from_base(values[i]) / (get_lower_probability_from_base(values[i]) + get_upper_probability_from_base(total
						- values[i])));
			for (int i = 0; i < values.length; i++)
				upper_values[i] = (get_upper_probability_from_base(values[i]) / (get_upper_probability_from_base(values[i]) + get_lower_probability_from_base(total
						- values[i])));
		}

		return (new QBProbabilityFunction(this, (double[]) null, lower_values,
				upper_values));
	}

	/***************************************************************/
	/* Perform calculation of expected value for density ratio */
	/***************************************************************/
	public double[] expected_values(DiscreteFunction df) {
		double results[] = new double[2];

		// Check the possibility that the query has an observed variable,
		// in which case the marginalization property does not apply.
		if ((variables[0] instanceof ProbabilityVariable)
				&& (((ProbabilityVariable) variables[0]).is_observed() == true)) {
			results[0] = df.get_value(((ProbabilityVariable) variables[0])
					.get_observed_index());
			results[1] = results[0];
			return (results);
		}

		// Else, apply the marginalization property and the
		// fact that total variation neighborhoods are 2-monotone.
		GeneralizedChoquetIntegral gci = new GeneralizedChoquetIntegral(this,
				df);

		return (gci.results);
	}

	/***************************************************************/
	/* Perform calculation of posterior expected value. */
	/* Assumes that the probability values are not */
	/* normalized; probability values are p(x, e) where e is */
	/* the fixed evidence */
	/***************************************************************/
	public double[] posterior_expected_values(DiscreteFunction df) {
		Bracketing bracket = new Bracketing();
		double results[] = new double[2];

		// Check the possibility that the query has an observed variable,
		// in which case the marginalization property does not apply.
		if ((variables[0] instanceof ProbabilityVariable)
				&& (((ProbabilityVariable) variables[0]).is_observed() == true)) {
			results[0] = df.get_value(((ProbabilityVariable) variables[0])
					.get_observed_index());
			results[1] = results[0];
			return (results);
		}

		// Else, apply the marginalization property and the
		// fact that total variation neighborhoods are 2-monotone.

		// Obtain the maximum and minimum of functions
		double max_df_value = df.get_value(0);
		double min_df_value = df.get_value(0);
		for (int i = 1; i < df.number_values(); i++) {
			if (max_df_value < df.get_value(i))
				max_df_value = df.get_value(i);
			if (min_df_value > df.get_value(i))
				min_df_value = df.get_value(i);
		}

		// Prepare the temporary_discrete_function variable for bracketing
		temporary_discrete_function = df;

		// Bracket the lower expectation
		double lower_expectation = bracket
				.perform(this, LOWER_EXPECTATION_BRACKET, min_df_value,
						max_df_value, ACCURACY);

		// Bracket the upper expectation
		double upper_expectation = bracket
				.perform(this, UPPER_EXPECTATION_BRACKET, min_df_value,
						max_df_value, ACCURACY);

		// Calculate the values
		results[0] = lower_expectation;
		results[1] = upper_expectation;

		return (results);
	}

	/***************************************************************/
	/* To conform to the MappingDouble interface demanded by the */
	/* Bracketing class, the method map() must be present. */
	/***************************************************************/
	@Override
	public double map(int map_type, double map_input) {
		// Get temporary_discrete_function
		DiscreteFunction tdf = temporary_discrete_function;

		// Create new discrete function
		double new_values[] = new double[tdf.number_values()];
		for (int i = 0; i < new_values.length; i++)
			new_values[i] = tdf.get_value(i) - map_input;
		DiscreteFunction mtdf = new DiscreteFunction(tdf.get_variables(),
				new_values);

		// Obtain Walley's formula for lower and upper expectations
		GeneralizedChoquetIntegral gci = new GeneralizedChoquetIntegral(this,
				mtdf);

		if (map_type == LOWER_EXPECTATION_BRACKET)
			return (gci.results[0]); // LOWER_EXPECTATION_BRACKET
		else
			return (gci.results[1]); // UPPER_EXPECTATION_BRACKET
	}
}
