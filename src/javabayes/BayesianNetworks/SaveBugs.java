/** 
 * SaveBUGS.java
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

package javabayes.BayesianNetworks;

import java.io.PrintStream;
import java.util.Date;

/**************************************************************************/

public class SaveBugs {
	BayesNet bn;

	/**
	 * Default constructor for a SaveBUGS object.
	 */
	public SaveBugs(BayesNet b_n) {
		bn = b_n;
	}

	/**
	 * Save a BayesNet in a stream in the BUGS format.
	 */
	public void save(PrintStream pstream) {
		pstream.println("# Bayesian Network in the BUGS Format");
		pstream.println("# Produced by BayesianNetworks package in JavaBayes");
		pstream.println("# Output created " + (new Date()));
		pstream.println("\n");

		save_model(pstream);

		save_variables(pstream);

		print_data_in(pstream);

		save_structure(pstream);

		save_data(pstream);
	}

	/**
	 * Save the name of the network in BUGS format.
	 */
	private void save_model(PrintStream pstream) {
		pstream.println("model " + bn.name + ";");
	}

	/**
	 * Declare all the variables used in the network.
	 */
	private void save_variables(PrintStream pstream) {
		ProbabilityFunction pf;
		ProbabilityVariable pv;
		int i, j;

		pstream.println("var");
		for (i = (bn.probability_variables.length - 1); i >= 0; i--) {
			pstream.println("\t" + bn.probability_variables[i].name + ",");
		}

		for (i = (bn.probability_functions.length - 1); i >= 0; i--) {
			pf = bn.probability_functions[i];
			pv = (ProbabilityVariable) (pf.variables[0]);
			pstream.print("\tp." + pv.name + "[" + pf.values.length + "]");
			if (i > 0)
				pstream.println(",");
		}
		pstream.println(";");
	}

	/**
	 * Indicate which file contains the data; note that the data is appended to
	 * the given pstream, so this space is left to be filled by the user.
	 */
	private void print_data_in(PrintStream pstream) {
		pstream.println("data in <user-defined-name-for-data-file>;");
	}

	/**
	 * The parenthood relationships in the network.
	 */
	private void save_structure(PrintStream pstream) {
		ProbabilityFunction pf;
		ProbabilityVariable pv;
		int i, j;

		pstream.println("{");
		for (i = (bn.probability_functions.length - 1); i >= 0; i--) {
			pf = bn.probability_functions[i];
			pv = (ProbabilityVariable) (pf.variables[0]);
			pstream.print(pv.name + "  ~  dcat(p." + pv.name + "[");
			for (j = 1; j < pf.variables.length; j++) {
				pstream.print(pf.variables[j].name);
				pstream.print(",");
			}
			pstream.println("]);");
		}
		pstream.println("}\n\n");
	}

	/**
	 * The numeric values for the distributions, saved in the Splus format
	 * understood by BUGS. Note that the user has to place this data in a
	 * separate file.
	 */
	private void save_data(PrintStream pstream) {
		ProbabilityFunction pf;
		ProbabilityVariable pv;
		int i, j, k;
		int step;
		double value;

		pstream.println("list(");
		for (i = (bn.probability_functions.length - 1); i >= 0; i--) {
			pf = bn.probability_functions[i];
			pv = (ProbabilityVariable) (pf.variables[0]);
			/**** Put distribution values in the correct format. ****/
			pstream.print("\tp." + pv.name + "  = c(");
			step = 1;
			for (j = 1; j < pf.variables.length; j++)
				step *= pf.variables[j].values.length;
			for (j = 0; j < step; j++) {
				for (k = 0; k < pf.variables[0].values.length; k++) {
					value = pf.values[k * step + j];
					pstream.print(" " + value);
					if (k < (pf.variables[0].values.length - 1))
						pstream.print(",");
				}
				if (j < (step - 1))
					pstream.print(",");
			}
			pstream.print(")");
			if (i > 0)
				pstream.println(",");
		}
		pstream.println(")\n");
	}
}

/*******************************************************************/

