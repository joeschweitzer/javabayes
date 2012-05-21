/**
 * QBProbabilityFunction.java
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

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.DiscreteFunction;
import javabayes.BayesianNetworks.DiscreteVariable;
import javabayes.BayesianNetworks.ProbabilityFunction;

/*******************************************************************/

public class QBProbabilityFunction extends ProbabilityFunction {
	double lower_envelope[];
	double upper_envelope[];

	/**
	 * Default constructor for a QBProbabilityFunction.
	 */
	public QBProbabilityFunction() {
	}

	/**
	 * Constructor for QBProbabilityFunction.
	 */
	public QBProbabilityFunction(BayesNet b_n, int n_vb, int n_vl, Vector prop) {
		super(b_n, n_vb, n_vl, prop);
		lower_envelope = new double[n_vl];
		upper_envelope = new double[n_vl];
	}

	/**
	 * Constructor for ProbabilityFunction.
	 */
	public QBProbabilityFunction(BayesNet b_n, DiscreteVariable dvs[],
			double v[], double lp[], double up[], Vector prop) {
		super(b_n, dvs, v, prop);
		lower_envelope = lp;
		upper_envelope = up;
	}

	/**
	 * Constructor for QBProbabilityFunction.
	 */
	public QBProbabilityFunction(DiscreteFunction df, double new_values[],
			double new_lp[], double new_up[]) {
		super(df, new_values);
		lower_envelope = new_lp;
		upper_envelope = new_up;
	}

	/**
	 * Print QBProbabilityFunction.
	 */
	@Override
	public void print() {
		print(System.out);
	}

	/**
	 * Print QBProbabilityFunction.
	 */
	@Override
	public void print(PrintStream out) {
		int j;
		String property;

		if (variables != null) {
			out.print(" envelope ( ");
			for (j = 0; j < variables.length; j++) {
				out.print(" \"" + variables[j].get_name() + "\" ");
			}
			out.print(") {");
			if (lower_envelope != null) {
				out.println(" //" + variables.length + " variable(s) and "
						+ lower_envelope.length + " values");
				out.print("\ttable lower-envelope ");
				for (j = 0; j < lower_envelope.length; j++)
					out.print(lower_envelope[j] + " ");
				out.print(";");
			}
			out.println();
			if (upper_envelope != null) {
				out.print("\ttable upper-envelope ");
				for (j = 0; j < upper_envelope.length; j++)
					out.print(upper_envelope[j] + " ");
				out.print(";");
			}
		}
		out.println();
		if ((properties != null) && (properties.size() > 0)) {
			for (Enumeration e = properties.elements(); e.hasMoreElements();) {
				property = (String) (e.nextElement());
				out.println("\tproperty \"" + property + "\" ;");
			}
		}
		out.println("}");
	}

	/* ************************************************************* */
	/* Methods that allow basic manipulation of non-public variables */
	/* ************************************************************* */

	/**
	 * Get the lower_envelope array.
	 */
	public double[] get_lower_envelope() {
		return (lower_envelope);
	}

	/**
	 * Get the upper_envelope array.
	 */
	public double[] get_upper_envelope() {
		return (upper_envelope);
	}
}
