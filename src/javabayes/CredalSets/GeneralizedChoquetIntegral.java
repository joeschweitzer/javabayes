/**
 * GeneralizedChoquetIntegral.java
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

import java.util.Enumeration;
import java.util.Vector;

import javabayes.BayesianNetworks.DiscreteFunction;

/**
 * Use Walley generalization of the Choquet lower and upper integrals to obtain
 * the lower and upper expectations of 2-monotone capacities.
 */

public class GeneralizedChoquetIntegral {
	double results[];

	/**
	 * Calculate the lower and upper Choquet integrals using Walley's
	 * generalization, for a total variation neighborhood.
	 */
	public GeneralizedChoquetIntegral(TwoMonotoneCapacity tmc,
			DiscreteFunction df) {
		int i;
		double positive_side, negative_side;

		// Allocate the results
		results = new double[2];

		// Order the non-negative values of df in increasing order
		// Zero is the first element of the vector
		Vector positive = sort_positive(df);

		// Order the non-positive values of df in decreasing order
		// Zero is the first element of the vector
		Vector negative = sort_negative(df);

		// Create an array of value differences from the positive side
		double df_positive[] = new double[positive.size() - 1];
		for (i = 1; i <= df_positive.length; i++) {
			df_positive[i - 1] = ((Double) (positive.elementAt(i)))
					.doubleValue()
					- ((Double) (positive.elementAt(i - 1))).doubleValue();
		}

		// Create an array of value differences from the negative side
		double df_negative[] = new double[negative.size() - 1];
		for (i = 1; i <= df_negative.length; i++) {
			df_negative[i - 1] = ((Double) (negative.elementAt(i)))
					.doubleValue()
					- ((Double) (negative.elementAt(i - 1))).doubleValue();
		}

		// Create arrays of lower and upper probability
		// values for the positive side
		double lp_positive[] = new double[df_positive.length];
		double up_positive[] = new double[df_positive.length];
		bound_positive(tmc, df, positive, lp_positive, up_positive);

		// Create arrays of lower and upper probability
		// values for the positive side
		double lp_negative[] = new double[df_negative.length];
		double up_negative[] = new double[df_negative.length];
		bound_negative(tmc, df, negative, lp_negative, up_negative);

		// First obtain the lower Walley integral
		positive_side = 0.0;
		negative_side = 0.0;
		for (i = 0; i < df_positive.length; i++)
			positive_side += df_positive[i] * lp_positive[i];
		for (i = 0; i < df_negative.length; i++)
			negative_side += df_negative[i] * up_negative[i];
		results[0] = positive_side + negative_side;

		// Now obtain the upper Walley integral
		positive_side = 0.0;
		negative_side = 0.0;
		for (i = 0; i < df_positive.length; i++)
			positive_side += df_positive[i] * up_positive[i];
		for (i = 0; i < df_negative.length; i++)
			negative_side += df_negative[i] * lp_negative[i];
		results[1] = positive_side + negative_side;
	}

	/**
	 * Collect the positive values in df and sort them in increasing order
	 * (first value is assumed zero).
	 */
	private Vector sort_positive(DiscreteFunction df) {
		Vector sorted = new Vector();

		// First element is zero, regardless of df values
		sorted.addElement(new Double(0.0));

		// Go through df values
		for (int i = 0; i < df.number_values(); i++) {
			// Process only positive values
			if (df.get_value(i) <= 0.0)
				continue;

			// Insert value in vector
			for (int j = 0; j < sorted.size(); j++) {
				if (df.get_value(i) < ((Double) sorted.elementAt(j))
						.doubleValue()) {
					sorted.addElement(new Double(df.get_value(i)));
					continue;
				}
			}

			// Insert value last in vector if it is not inserted at this point
			sorted.insertElementAt(new Double(df.get_value(i)), sorted.size());
		}
		return (sorted);
	}

	/**
	 * Collect the negative values in df and sort them in decreasing order
	 * (first value is assumed zero).
	 */
	private Vector sort_negative(DiscreteFunction df) {
		Vector sorted = new Vector();

		// First element is zero, regardless of df values
		sorted.addElement(new Double(0.0));

		// Go through df values
		for (int i = 0; i < df.number_values(); i++) {
			// Process only negative values
			if (df.get_value(i) >= 0.0)
				continue;

			// Insert value in vector
			for (int j = 0; j < sorted.size(); j++) {
				if (df.get_value(i) > ((Double) sorted.elementAt(j))
						.doubleValue()) {
					sorted.addElement(new Double(df.get_value(i)));
					continue;
				}
			}

			// Insert value last in vector if it is not inserted at this point
			sorted.insertElementAt(new Double(df.get_value(i)), sorted.size());
		}
		return (sorted);
	}

	/**
	 * Obtain the lower and upper probability for the event { df(x) >
	 * sorted_value[i] }
	 */
	private void bound_positive(TwoMonotoneCapacity tmc, DiscreteFunction df,
			Vector sorted_values, double lps[], double ups[]) {
		int i, j;
		double lp;
		double sorted_value;
		Enumeration e;

		for (i = 0, e = sorted_values.elements(); i < (sorted_values.size() - 1); i++) {
			lp = 0.0; // Initialize
			sorted_value = ((Double) e.nextElement()).doubleValue();
			// Collect the base probability for
			// all atoms such that df(x_j > sorted_values[i])
			for (j = 0; j < df.number_values(); j++) {
				if (df.get_value(j) > sorted_value) {
					// Add base probability of this atom
					lp += tmc.get_atom_probability(j);
				}
			}
			// Calculate the lower and upper probabilities
			lps[i] = tmc.get_lower_probability_from_base(lp);
			ups[i] = tmc.get_upper_probability_from_base(lp);
		}
	}

	/**
	 * Obtain the lower and upper probability for the event { df(x) <
	 * sorted_value[i] }
	 */
	private void bound_negative(TwoMonotoneCapacity tmc, DiscreteFunction df,
			Vector sorted_values, double lps[], double ups[]) {
		int i, j;
		double lp;
		double sorted_value;
		Enumeration e;

		for (i = 0, e = sorted_values.elements(); i < (sorted_values.size() - 1); i++) {
			lp = 0.0; // Initialize
			sorted_value = ((Double) e.nextElement()).doubleValue();
			// Collect the base probability for
			// all atoms such that df(x_j > sorted_values[i])
			for (j = 0; j < df.number_values(); j++) {
				if (df.get_value(j) < sorted_value) {
					// Add base probability of this atom
					lp += tmc.get_atom_probability(j);
				}
			}
			// Calculate the lower and upper probabilities
			lps[i] = tmc.get_lower_probability_from_base(lp);
			ups[i] = tmc.get_upper_probability_from_base(lp);
		}
	}
}
