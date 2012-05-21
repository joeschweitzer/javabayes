/**
 * FinitelyGeneratedSet.java
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

import java.util.Vector;

import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.DiscreteFunction;
import javabayes.BayesianNetworks.DiscreteVariable;

abstract class FinitelyGeneratedSet extends QBProbabilityFunction {

	/**
	 * Default constructor for a FinitelyGeneratedSet.
	 */
	FinitelyGeneratedSet() {
	}

	/**
	 * Constructor for FinitelyGeneratedSet.
	 */
	FinitelyGeneratedSet(BayesNet b_n, int n_vb, int n_vl, Vector prop) {
		super(b_n, n_vb, n_vl, prop);
	}

	/**
	 * Constructor for FinitelyGeneratedSet.
	 */
	FinitelyGeneratedSet(BayesNet b_n, DiscreteVariable pvs[], double v[],
			double lp[], double up[], Vector prop) {
		super(b_n, pvs, v, lp, up, prop);
	}

	/**
	 * Constructor for FinitelyGeneratedSet.
	 */
	FinitelyGeneratedSet(BayesNet b_n, DiscreteVariable pvs[], double v[],
			Vector prop) {
		this(b_n, pvs, v, (double[]) null, (double[]) null, prop);
	}

	/**
	 * Constructor for FinitelyGeneratedSet.
	 */
	FinitelyGeneratedSet(DiscreteFunction df, double new_values[],
			double new_lp[], double new_up[]) {
		super(df, new_values, new_lp, new_up);
	}

	/**
	 * Constructor for FinitelyGeneratedSet.
	 */
	FinitelyGeneratedSet(DiscreteFunction df, double new_values[]) {
		super(df, new_values, (double[]) null, (double[]) null);
	}
}
