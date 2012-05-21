/**
 * TotalVariationSet.java
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

import javabayes.BayesianNetworks.ProbabilityFunction;

public class TotalVariationSet extends TwoMonotoneCapacity {

	private double epsilon;

	/**
	 * Constructor for an TotalVariationQBProbabilityFunction
	 * ProbabilityFunction object and given epsilon.
	 */
	public TotalVariationSet(ProbabilityFunction pf, double eps) {
		super(pf);
		epsilon = eps;
		if ((epsilon < 0.0) || (epsilon > 1.0))
			epsilon = 0.0;
	}

	/**
	 * Obtain the lower probability of an event given the base probability for
	 * the event.
	 */
	@Override
	public double get_lower_probability_from_base(double p) {
		return (Math.max(p - epsilon, 0.0));
	}

	/**
	 * Obtain the upper probability of an event given the base probability for
	 * the event.
	 */
	@Override
	public double get_upper_probability_from_base(double p) {
		return (Math.min(p + epsilon, 1.0));
	}

	/**
	 * Get a base probability value for an atom.
	 */
	@Override
	public double get_atom_probability(int index) {
		return (values[index]);
	}
}