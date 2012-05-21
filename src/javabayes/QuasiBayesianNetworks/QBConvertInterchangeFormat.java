/**
 * QBConvertInterchangeFormat.java
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

package javabayes.QuasiBayesianNetworks;

import java.util.Enumeration;
import java.util.Vector;

import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.ConvertInterchangeFormat;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;
import javabayes.CredalSets.VertexSet;
import javabayes.InterchangeFormat.IFProbabilityEntry;
import javabayes.InterchangeFormat.IFProbabilityFunction;
import javabayes.InterchangeFormat.InterchangeFormat;

/************************************************************
 * Class that converts the contents of an InterchangeFormat * object to the
 * BayesNet object in the BayesianNetworks * package *
 ************************************************************/

public class QBConvertInterchangeFormat extends ConvertInterchangeFormat {

	public QBConvertInterchangeFormat(InterchangeFormat inter_format) {
		super(inter_format);
	}

	/************************************************************
	 * Method that does all the work involved in creating a *
	 * ProbabilityFunction object out of the definition * found by the parser
	 * and the information contained in * the BayesNet object; the
	 * ProbabilityFunction object may * in fact be a Quasi-Bayesian model. *
	 ************************************************************/
	@Override
	protected ProbabilityFunction get_probability_function(BayesNet bn,
			IFProbabilityFunction upf) {
		int i, jump, number_of_values;
		double values[];
		double extreme_points[][] = null;
		ProbabilityVariable pv, variables[];

		// Check and insert the probability variable indexes
		variables = create_variables(bn, upf);

		// Calculate the jump, i.e., the number of values
		// in the conditional distribution table for each value
		// of the first variable (this is used for default/entries)
		jump = 1;
		for (i = 1; i < variables.length; i++) {
			pv = variables[i];
			jump *= pv.number_values();
		}

		// Calculate the number of values in the distribution
		number_of_values = jump * variables[0].number_values();

		// Allocate values and initialize
		values = new double[number_of_values];
		for (i = 0; i < values.length; i++)
			values[i] = -1.0;

		// Process tables
		extreme_points = process_extreme_tables(upf, values);

		// Process defaults
		process_defaults(upf, values, extreme_points, jump);

		// Process entries
		process_entries(bn, upf, variables, values, extreme_points, jump);

		// Finish calculating the values
		finish_values(values, extreme_points);

		// Insert the data
		if (extreme_points == null)
			return (new ProbabilityFunction(bn, variables, values,
					upf.get_properties()));
		else
			return (new VertexSet(bn, variables, extreme_points,
					upf.get_properties()));
	}

	/************************************************************
	 * Fill the values with the contents of the tables * in the upf object. *
	 ************************************************************/
	double[][] process_extreme_tables(IFProbabilityFunction upf,
			double values[]) {
		int i, j;
		double table[], extreme_points[][];

		// Put the table values
		Vector tables = upf.get_tables();
		int n = tables.size();

		// If there are no available tables
		if (n == 0)
			return (null);

		// If there is a single table, no extreme_points are created
		// and it just acts as a standard Bayesian model
		if (n == 1) {
			table = (double[]) (tables.firstElement());
			copy_table_to_values(table, values);
			return (null);
		}

		// Else, if there are several extreme_points in the credal set
		extreme_points = new double[n][];
		for (i = 0; i < extreme_points.length; i++) {
			extreme_points[i] = new double[values.length];
			for (j = 0; j < extreme_points[i].length; j++)
				extreme_points[i][j] = -1.0;
		}
		i = 0;
		for (Enumeration e = tables.elements(); e.hasMoreElements();) {
			table = (double[]) (e.nextElement());
			copy_table_to_values(table, extreme_points[i]);
			i++;
		}
		return (extreme_points);
	}

	/************************************************************
	 * Insert default values from the contents of the first * specification of
	 * defaults in the upf object. *
	 ************************************************************/
	void process_defaults(IFProbabilityFunction upf, double values[],
			double extreme_points[][], int jump) {
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

	/************************************************************
	 * Insert entries specified in the upf object. *
	 ************************************************************/
	void process_entries(BayesNet bn, IFProbabilityFunction upf,
			ProbabilityVariable variables[], double values[],
			double extreme_points[][], int jump) {
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
					step *= variables[k].number_values();
				}
				pv = variables[0];
				for (i = 0; i < pv.number_values(); i++) {
					k = i * jump + pos;
					values[k] = eentry_entries[i];
				}
			}
		}
	}

	/************************************************************
	 * Perform final calculations in the values *
	 ************************************************************/
	void finish_values(double values[], double extreme_points[][]) {
		int i, j;

		// First case: more than one distribution specifies a credal set
		if (extreme_points != null) {
			// Fill with zeros where needed for all distributions
			for (j = 0; j < extreme_points.length; j++) {
				for (i = 0; i < extreme_points[j].length; i++)
					if (extreme_points[j][i] == -1.0)
						extreme_points[j][i] = 0.0;
			}
		} else { // Second case: single distribution; just fill zeros where
					// needed
			for (i = 0; i < values.length; i++)
				if (values[i] == -1.0)
					values[i] = 0.0;
		}
	}
}
