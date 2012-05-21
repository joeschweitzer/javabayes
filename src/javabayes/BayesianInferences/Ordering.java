/**
 * Ordering.java
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

package javabayes.BayesianInferences;

import java.util.Enumeration;
import java.util.Vector;

import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.DiscreteVariable;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;

/*******************************************************************/

public class Ordering {
	BayesNet bn;
	String order[];
	int explanation_status = Inference.IGNORE_EXPLANATION;
	int ordering_type = MINIMUM_WEIGHT;

	public static final int USER_DEFINED = 0;
	public static final int USER_ORDER = 1;
	public static final int MINIMUM_WEIGHT = 2;

	/**
	 * Basic constructor for Ordering.
	 */
	public Ordering(BayesNet b_n, String objective, int ot) {
		bn = b_n;
		explanation_status = obtain_explanation_status(b_n);
		ordering_type = ot;
		order = ordering(objective);
	}

	/**
	 * Basic constructor for Ordering.
	 */
	public Ordering(BayesNet b_n, String or[]) {
		bn = b_n;
		order = or;
		explanation_status = obtain_explanation_status(b_n);
	}

	/**
	 * Basic constructor for Ordering.
	 */
	public Ordering(BayesNet b_n, String objective, int ds, int ot) {
		bn = b_n;
		explanation_status = ds;
		ordering_type = ot;
		order = ordering(objective);
	}

	/**
	 * Basic constructor for Ordering.
	 */
	public Ordering(BayesNet b_n, String or[], int ds) {
		bn = b_n;
		order = or;
		explanation_status = ds;
	}

	/*
	 * Obtain explanation_status: unless there are explanations the status is
	 * IGNORE_EXPLANATION.
	 */
	private int obtain_explanation_status(BayesNet b_n) {
		int explanation_status_flag = Inference.IGNORE_EXPLANATION;
		for (int i = 0; i < b_n.number_variables(); i++) {
			if ((!(b_n.get_probability_variable(i).is_observed()))
					&& (b_n.get_probability_variable(i).is_explanation())) {
				explanation_status_flag = Inference.EXPLANATION;
				break;
			}
		}
		return (explanation_status_flag);
	}

	/*
	 * Call the appropriate ordering depending on the type of ordering.
	 */
	private String[] ordering(String objective) {
		int i;
		Vector variables_to_order = new Vector();

		int objective_index = bn.index_of_variable(objective);
		if (objective_index == BayesNet.INVALID_INDEX)
			objective_index = 0;

		if (bn.get_probability_variable(objective_index).is_observed()) {
			String one_order[] = { bn.get_probability_variable(objective_index)
					.get_name() };
			return (one_order);
		}

		if (ordering_type == USER_ORDER) {
			// For user order, just collect all variables.
			for (i = 0; i < bn.number_variables(); i++)
				variables_to_order.addElement(bn.get_probability_variable(i));
			return (user_order(variables_to_order, objective_index));
		} else {
			// For explanations, just collect all variables.
			if (explanation_status != Inference.IGNORE_EXPLANATION) {
				for (i = 0; i < bn.number_variables(); i++)
					variables_to_order.addElement(bn
							.get_probability_variable(i));
			} else { // For inference, get only the affecting variables.
				DSeparation dsep = new DSeparation(bn);
				variables_to_order = dsep.all_affecting(objective_index);
			}
			return (heuristic_order(variables_to_order, objective_index,
					ordering_type));
		}
	}

	/*
	 * Simple ordering for the variables: 1) Transparent variables are not
	 * included; note that transparent variables are only present in cases where
	 * explanation variables are not to be considered. 2) Explanation variables
	 * come last in the order they were input 3) Non-explanation variables come
	 * in the order they were input. When there are no explanation variables (or
	 * explanation variables are to be ignored), the objective variable comes
	 * last. Note that this violates the inserted by the user, but the bucket
	 * elimination algorithm requires the ordering to have this property
	 * (objective variable last for inference).
	 */
	private String[] user_order(Vector variables_to_order, int objective_index) {
		int i, j;
		boolean is_variable_explanation_flag = false;
		ProbabilityVariable pv;
		Vector non_explanation_variables = new Vector();
		Vector explanation_variables = new Vector();
		Enumeration e;
		String ord[];

		// Collect variables into related vectors
		for (e = variables_to_order.elements(); e.hasMoreElements();) {
			pv = (ProbabilityVariable) (e.nextElement());
			// Skip transparent variables
			if (pv.get_type() == ProbabilityVariable.TRANSPARENT)
				continue;
			// Check the status of the variable as a explanatory variable
			switch (explanation_status) {
			case Inference.IGNORE_EXPLANATION:
				is_variable_explanation_flag = false;
				break;
			case Inference.EXPLANATION:
				is_variable_explanation_flag = pv.is_explanation();
				break;
			case Inference.FULL_EXPLANATION:
				is_variable_explanation_flag = true;
				break;
			}

			// Observed variables are not explanation variables
			// (evidence has precedence over explanations).
			if (pv.is_observed())
				is_variable_explanation_flag = false;

			// Put the variable in the correct vector
			if (is_variable_explanation_flag)
				explanation_variables.addElement(pv.get_name());
			else
				non_explanation_variables.addElement(pv.get_name());
		}

		ord = new String[non_explanation_variables.size()
				+ explanation_variables.size()];

		if (explanation_variables.size() == 0) {
			for (i = 0, e = non_explanation_variables.elements(); e
					.hasMoreElements(); i++) {
				ord[i] = (String) (e.nextElement());
				if (ord[i].equals(bn.get_probability_variable(objective_index)
						.get_name()))
					i--;
			}
			ord[i] = bn.get_probability_variable(objective_index).get_name();
		} else {
			for (i = 0, e = non_explanation_variables.elements(); e
					.hasMoreElements(); i++)
				ord[i] = (String) (e.nextElement());
			for (j = i, e = explanation_variables.elements(); e
					.hasMoreElements(); j++)
				ord[j] = (String) (e.nextElement());
		}

		return (ord);
	}

	/*
	 * Heuristic ordering for the variables: 1) Transparent variables are not
	 * included 2) Decision variables come last in the order they were input 3)
	 * Non-explanation variables come in the order they were input, except
	 * objective variable which is the last of all non-explanation variables
	 * Produce an ordering for the variables in variables_to_order, assuming
	 * that all variables are in the BayesNet bn object. The ordering_type
	 * indicates which heuristic to use in the elimination procedure.
	 */
	private String[] heuristic_order(Vector vo, int objective_index,
			int ordering_type) {
		int i, j;
		int PHASE_ONE = 1;
		int PHASE_TWO = 2;
		int phase;
		long value, min_value;
		int min_index;
		int number_variables_in_phase;
		int number_variables_in_phase_two = 0;

		Enumeration e;
		ProbabilityVariable pv;
		ProbabilityVariable neighbors[];
		ProbabilityFunction pf;

		// The vector with the filtered variables to order.
		Vector variables_to_order = new Vector();

		// The vector that will contain the final ordering.
		Vector elimination_ordering = new Vector();

		// Phase markers: indicates in which phase of the
		// algorithm a variable will be eliminated.
		int phase_markers[] = new int[bn.number_variables()];
		for (i = 0; i < phase_markers.length; i++)
			phase_markers[i] = PHASE_ONE;

		// Filter the incoming variables
		for (e = vo.elements(); e.hasMoreElements();) {
			pv = (ProbabilityVariable) (e.nextElement());
			if (pv.is_observed()) { // Put observed variables at the beginning
				elimination_ordering.addElement(pv);
			} else { // Skip transparent variables
				if (pv.get_type() != ProbabilityVariable.TRANSPARENT) {
					// Order all other variables
					variables_to_order.addElement(pv);
					// Check the status of the variable as an explanatory
					// variable
					if ((explanation_status == Inference.FULL_EXPLANATION)
							|| ((explanation_status == Inference.EXPLANATION) && (pv
									.is_explanation()))) {
						phase_markers[pv.get_index()] = PHASE_TWO;
						number_variables_in_phase_two++;
					}
				}
			}
		}

		// Define whether the objective variable will be
		// processed in the second phase.
		if (number_variables_in_phase_two == 0) {
			phase_markers[objective_index] = PHASE_TWO;
			number_variables_in_phase_two = 1;
		}

		// Each variable is associated to a vector (the vector contains
		// all variables that are linked to the variable).
		Vector vectors[] = new Vector[bn.number_variables()];
		// Initialize the vectors only for the variables that are to be ordered.
		for (e = variables_to_order.elements(); e.hasMoreElements();) {
			pv = (ProbabilityVariable) (e.nextElement());
			vectors[pv.get_index()] = new Vector();
		}

		// Moralize the network: build an undirected graph where each variable
		// is linked to its parents, children, and parents of its children.
		// The idea is to go through the variables and, for each variable,
		// interconnect the variable and all its parents. That connects
		// all variables to its parents and "moralizes" the graph
		// simultaneously;
		// since all variables are analyzed, every variable ends up connected
		// to its children.
		for (e = variables_to_order.elements(); e.hasMoreElements();) {
			pv = (ProbabilityVariable) (e.nextElement());
			pf = bn.get_function(pv);
			vectors[pv.get_index()].addElement(pv);
			interconnect(bn, vectors, pf.get_variables());
		}

		// Decide which phase to start;
		if (number_variables_in_phase_two == variables_to_order.size())
			phase = PHASE_TWO;
		else
			phase = PHASE_ONE;

		// Eliminate the variable that has the smallest value for
		// the heuristic of interest, until all variables are eliminated.
		// As a variable is eliminated, it is removed from all other
		// links and all its neighbors are interconnected.

		for (i = 0; i < variables_to_order.size(); i++) {
			// Get the variable with minimum heuristic value.
			min_value = -1;
			min_index = -1;

			number_variables_in_phase = 0;
			for (j = 0; j < vectors.length; j++) { // Go through all the
													// variables
				// Only proceed if variable is to be ordered in this phase.
				if ((vectors[j] != null) && (phase_markers[j] == phase)) {
					number_variables_in_phase++;
					value = obtain_value(vectors[j], ordering_type); // Get the
																		// value
																		// for
																		// the
																		// heuristic.
					if ((value < min_value) || (min_index == -1)) { // Minimize
																	// the
																	// heuristic.
						min_index = j;
						min_value = value;
					}
				}
			}
			if ((phase == PHASE_ONE) && (number_variables_in_phase == 1))
				phase = PHASE_TWO;

			// Add the variable with minimum value for the heuristic
			// to the ordering.
			pv = bn.get_probability_variable(min_index);
			elimination_ordering.addElement(pv);

			// Now remove the variable:
			// Remove it from every other list of variables
			for (j = 0; j < vectors.length; j++) { // Go through all lists of
													// variables.
				if (vectors[j] != null) // Only proceed is list is non-null.
					vectors[j].removeElement(pv); // Now remove the variable
													// from the vector
			}
			// Interconnect all its neighbors
			neighbors = new ProbabilityVariable[vectors[min_index].size()];
			for (j = 0, e = vectors[min_index].elements(); e.hasMoreElements(); j++) {
				pv = (ProbabilityVariable) (e.nextElement());
				neighbors[j] = pv;
			}
			interconnect(bn, vectors, neighbors);
			// Erase its list of neighbors.
			vectors[min_index] = null;
		}

		// Return the ordering
		String return_ordering[] = new String[elimination_ordering.size()];
		for (i = 0, e = elimination_ordering.elements(); e.hasMoreElements(); i++) {
			pv = (ProbabilityVariable) (e.nextElement());
			return_ordering[i] = pv.get_name();
		}
		return (return_ordering);
	}

	/*
	 * Obtain the heuristic value of eliminating a variable, represented by the
	 * list of variables linked to it.
	 */
	private long obtain_value(Vector v, int ordering_type) {
		ProbabilityVariable pv;
		long value = 0;

		if (ordering_type == Ordering.MINIMUM_WEIGHT) {
			long weight = 1;
			for (Enumeration e = v.elements(); e.hasMoreElements();) {
				pv = (ProbabilityVariable) (e.nextElement());
				weight *= pv.number_values();
			}
			value = weight;
		}

		return (value);
	}

	/*
	 * Interconnect a group of variables; each variable connected to all the
	 * others.
	 */
	private void interconnect(BayesNet bn, Vector vectors[],
			DiscreteVariable variables_to_be_interconnected[]) {
		int i, j;
		for (i = 0; i < (variables_to_be_interconnected.length - 1); i++) {
			for (j = (i + 1); j < variables_to_be_interconnected.length; j++) {
				interconnect(bn, vectors, variables_to_be_interconnected[i],
						variables_to_be_interconnected[j]);
			}
		}
	}

	/*
	 * Connect two variables.
	 */
	private void interconnect(BayesNet bn, Vector vectors[],
			DiscreteVariable pvi, DiscreteVariable pvj) {
		Vector iv = vectors[pvi.get_index()];
		Vector jv = vectors[pvj.get_index()];

		// Avoid problems if parent is observed or transparent.
		if ((iv == null) || (jv == null))
			return;

		// Now interconnect.
		if (!iv.contains(pvj))
			iv.addElement(pvj);
		if (!jv.contains(pvi))
			jv.addElement(pvi);
	}
}
