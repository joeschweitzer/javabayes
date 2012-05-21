/*
 * ConvertInterchangeFormat.java
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

import java.util.Enumeration;
import java.util.Vector;

import javabayes.InterchangeFormat.IFBayesNet;
import javabayes.InterchangeFormat.IFProbabilityEntry;
import javabayes.InterchangeFormat.IFProbabilityFunction;
import javabayes.InterchangeFormat.IFProbabilityVariable;
import javabayes.InterchangeFormat.InterchangeFormat;

/* ********************************************************** *
 * Class that converts the contents of an InterchangeFormat   *
 * object to the BayesNet object in the BayesianNetworks      *
 * package                                                    *
 * ********************************************************** */

public class ConvertInterchangeFormat {
	InterchangeFormat ifo;

	/**
	 * Default constructor that loads an InterchangeFormat.
	 */
	public ConvertInterchangeFormat(InterchangeFormat inter_format) {
		ifo = inter_format;
	}

	/**
	 * Get the name of the IFBayesNet in the InterchangeFormat.
	 */
	public String get_name() {
		IFBayesNet ifbn = ifo.get_ifbn();
		if (ifbn == null)
			return (null);
		return (ifbn.get_name());
	}

	/**
	 * Get the properties of the IFBayesNet in the InterchangeFormat.
	 */
	public Vector get_properties() {
		IFBayesNet ifbn = ifo.get_ifbn();
		if (ifbn == null)
			return (null);
		return (ifbn.get_properties());
	}

	/**
	 * Generate an array of ProbabilityVariable objects from the data structures
	 * in the IFBayesNet.
	 * 
	 * @param bn
	 *            The BayesNet that will receive the ProbabilityVariable
	 *            objects.
	 */
	public ProbabilityVariable[] get_probability_variables(BayesNet bn) {
		int i;
		Enumeration e;
		IFProbabilityVariable ipv;
		IFBayesNet ifbn = ifo.get_ifbn();
		if (ifbn == null)
			return (null);
		Vector pvs = ifbn.get_pvs();

		ProbabilityVariable probability_variables[] = new ProbabilityVariable[pvs
				.size()];

		for (e = pvs.elements(), i = 0; e.hasMoreElements(); i++) {
			ipv = (IFProbabilityVariable) (e.nextElement());
			probability_variables[i] = new ProbabilityVariable(bn,
					ipv.get_name(), i, ipv.get_values(), ipv.get_properties());
		}

		return (probability_variables);
	}

	/**
	 * Generate an array of ProbabilityFunction objects from the data structures
	 * in the IFBayesNet.
	 * 
	 * @param bn
	 *            The BayesNet that will receive the ProbabilityVariable
	 *            objects.
	 */
	public ProbabilityFunction[] get_probability_functions(BayesNet bn) {
		int i;
		Enumeration e;
		IFProbabilityFunction upf;
		IFBayesNet ifbn = ifo.get_ifbn();
		if (ifbn == null)
			return (null);
		Vector upfs = ifbn.get_upfs();

		ProbabilityFunction probability_functions[] = new ProbabilityFunction[upfs
				.size()];

		for (e = upfs.elements(), i = 0; e.hasMoreElements(); i++) {
			upf = (IFProbabilityFunction) (e.nextElement());
			probability_functions[i] = get_probability_function(bn, upf);
		}

		return (probability_functions);
	}

	/*
	 * Create a ProbabilityFunction out of the definition found by the parser
	 * and the information contained in the BayesNet.
	 */
	protected ProbabilityFunction get_probability_function(BayesNet bn,
			IFProbabilityFunction upf) {
		int i, jump, number_of_values;
		double values[];
		ProbabilityVariable pv, variables[];

		// Check and insert the probability variable indexes
		variables = create_variables(bn, upf);

		// Calculate the jump, i.e., the number of numeric values
		// in the conditional distribution table for each value
		// of the first variable (this is used for default/entries)
		jump = 1;
		for (i = 1; i < variables.length; i++) {
			pv = variables[i];
			jump *= pv.values.length;
		}

		// Calculate the number of values in the distribution
		number_of_values = jump * variables[0].values.length;

		// Allocate values and initialize
		values = new double[number_of_values];
		for (i = 0; i < values.length; i++)
			values[i] = -1.0;

		// Process tables
		process_tables(upf, values);

		// Process defaults
		process_defaults(upf, values, jump);

		// Process entries
		process_entries(bn, upf, variables, values, jump);

		// Finish calculating the values
		finish_values(values);

		// Return the ProbabilityFunction
		return (new ProbabilityFunction(bn, variables, values,
				upf.get_properties()));
	}

	/*
	 * Create the variables in the ProbabilityFunction object from the variables
	 * indicated in the IFProbabilityFunction.
	 */
	protected ProbabilityVariable[] create_variables(BayesNet bn,
			IFProbabilityFunction upf) {
		int index;
		String ss_variables[] = upf.get_variables();

		ProbabilityVariable variables[] = new ProbabilityVariable[ss_variables.length];
		for (int i = 0; i < ss_variables.length; i++) {
			index = bn.index_of_variable(ss_variables[i]);
			if (index != BayesNet.INVALID_INDEX)
				variables[i] = bn.probability_variables[index];
		}
		return (variables);
	}

	/*
	 * Fill the values with the contents of the first table in the tables
	 * contained in the upf object.
	 */
	protected void process_tables(IFProbabilityFunction upf, double values[]) {
		// Put the table values
		Vector ttables = upf.get_tables();
		if (ttables.size() > 0) {
			double ttable[] = (double[]) (ttables.firstElement());
			copy_table_to_values(ttable, values);
		}
	}

	/*
	 * Copy content from a table to another.
	 */
	protected void copy_table_to_values(double table[], double values[]) {
		for (int i = 0; (i < table.length) && (i < values.length); i++)
			values[i] = table[i];
	}

	/*
	 * Insert default values from the contents of the first specification of
	 * defaults in the IFProbabilityFunction.
	 */
	void process_defaults(IFProbabilityFunction upf, double values[], int jump) {
		int i, j, k;

		// Process the default values
		Vector ddefaultss = upf.get_defaults();
		if (ddefaultss.size() > 0) {
			double ddefaults[] = (double[]) (ddefaultss.firstElement());
			for (i = 0; i < values.length; i++) {
				for (j = 0; j < jump; j++) {
					k = i * jump + j;
					if (values[k] == -1.0) {
						values[k] = ddefaults[i];
					}
				}
			}
		}
	}

	/*
	 * Insert entries specified in the IFProbabilityFunction.
	 */
	void process_entries(BayesNet bn, IFProbabilityFunction upf,
			ProbabilityVariable variables[], double values[], int jump) {
		int i, j, k, pos, step;
		int entry_value_indexes[];
		double eentry_entries[];
		String eentry_values[];
		Enumeration e;
		ProbabilityVariable pv;
		IFProbabilityEntry entry;

		// Process the entries
		Vector eentries = upf.get_entries();
		if ((eentries != null) && (eentries.size() > 0)) {
			for (e = eentries.elements(); e.hasMoreElements();) {
				entry = (IFProbabilityEntry) (e.nextElement());
				eentry_values = entry.get_values();
				eentry_entries = entry.get_entries();
				entry_value_indexes = new int[eentry_values.length];
				for (i = 0; i < entry_value_indexes.length; i++) {
					pv = variables[i + 1];
					entry_value_indexes[i] = pv
							.index_of_value(eentry_values[i]);
				}
				pos = 0;
				step = 1;
				for (k = (entry_value_indexes.length); k > 0; k--) {
					pos += entry_value_indexes[k - 1] * step;
					step *= variables[k].values.length;
				}
				pv = variables[0];
				for (i = 0; i < pv.values.length; i++) {
					k = i * jump + pos;
					values[k] = eentry_entries[i];
				}
			}
		}
	}

	/*
	 * Perform final calculations in the values
	 */
	void finish_values(double values[]) {
		// Put zeroes in the values that were not defined
		for (int i = 0; i < values.length; i++)
			if (values[i] == -1.0)
				values[i] = 0.0;
	}

}
