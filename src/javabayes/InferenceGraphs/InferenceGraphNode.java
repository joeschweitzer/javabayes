/**
 * InferenceGraphNode.java
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

package javabayes.InferenceGraphs;

import java.awt.Point;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringBufferInputStream;
import java.util.Enumeration;
import java.util.Vector;

import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.DiscreteVariable;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;
import javabayes.CredalSets.QBProbabilityFunction;
import javabayes.CredalSets.VertexSet;

public class InferenceGraphNode {
	InferenceGraph ig;

	ProbabilityVariable pv;
	ProbabilityFunction pf;

	Vector parents = new Vector();
	Vector children = new Vector();

	Point pos;

	private final String defaultInferenceGraphNodeValues[] = { "true", "false" };
	private final BayesNet defaultInferenceGraphNodeBayesNet = null;
	private final Vector defaultInferenceGraphNodeProperties = null;

	/*
	 * Default constructor for an InferenceGraphNode.
	 */
	InferenceGraphNode(InferenceGraph i_g, String name) {
		this(i_g, name, new Point(100, 100));
	}

	/*
	 * Constructor for a InferenceGraphNode object. The created node is in an
	 * incomplete state; the constructor assumes the node is new and not
	 * necessarily attached to the current network in the InferenceGraph; no
	 * parents nor children are defined for such a node.
	 */
	InferenceGraphNode(InferenceGraph i_g, String name, Point position) {
		ig = i_g;

		// Initialize the variable
		pv = new ProbabilityVariable(defaultInferenceGraphNodeBayesNet, name,
				BayesNet.INVALID_INDEX, defaultInferenceGraphNodeValues,
				defaultInferenceGraphNodeProperties);
		// Initialize the probability function
		init_dists();
		// Initialize the position of the node
		pos = position;
	}

	/*
	 * Constructor for a InferenceGraphNode object. Note that parents and
	 * children are not properly set here.
	 */
	InferenceGraphNode(InferenceGraph i_g, ProbabilityVariable p_v,
			ProbabilityFunction p_f) {
		ig = i_g;
		pv = p_v;
		pf = p_f;
		pos = parse_position(p_v);
	}

	/*
	 * Constructor for a InferenceGraphNode object. Note that parents and
	 * children are not properly set here.
	 */
	InferenceGraphNode(InferenceGraph i_g, ProbabilityVariable p_v,
			ProbabilityFunction p_f, Point position) {
		ig = i_g;
		pv = p_v;
		pf = p_f;
		pos = position;
	}

	/*
	 * Initialization for the probability function in the InferenceGraphNode.
	 */
	void init_dists() {
		int i, total_values;
		double new_value;
		Enumeration e;
		InferenceGraphNode pnode;

		// Create the probability_variables
		ProbabilityVariable pvs[] = new ProbabilityVariable[parents.size() + 1];
		pvs[0] = pv;

		total_values = pv.number_values();
		new_value = 1.0 / (total_values);

		for (i = 1, e = parents.elements(); e.hasMoreElements(); i++) {
			pnode = (InferenceGraphNode) (e.nextElement());
			pvs[i] = pnode.pv;
			total_values *= pnode.pv.number_values();
		}

		// Compute the default (uniformly distributed) values
		double dists[] = new double[total_values];
		for (i = 0; i < dists.length; i++)
			dists[i] = new_value;

		// Construct the ProbabilityFunction
		pf = new ProbabilityFunction(defaultInferenceGraphNodeBayesNet, pvs,
				dists, defaultInferenceGraphNodeProperties);
	}

	/*
	 * Update the position property.
	 */
	void update_position() {
		Vector properties = pv.get_properties();
		Vector properties_to_remove = new Vector();
		String final_property = null;
		String s, ss;

		if ((properties != null) && (properties.size() > 0)) {
			for (Enumeration e = properties.elements(); e.hasMoreElements();) {
				ss = (String) e.nextElement();
				s = ss.trim();

				// If property is not position, skip it
				if (!s.startsWith("position"))
					continue;

				// Schedule the current position property for removal
				properties_to_remove.addElement(ss);
			}

			// Remove the old position properties
			for (Enumeration e = properties_to_remove.elements(); e
					.hasMoreElements();) {
				ss = (String) (e.nextElement());
				pv.remove_property(ss);
			}
		}

		// Build the new position property
		final_property = new String("position = (" + pos.x + ", " + pos.y + ")");
		// Insert the new position
		pv.add_property(final_property);
	}

	/*
	 * Get the position of a InferenceGraphNode from the properties in the
	 * variable.
	 */
	private Point parse_position(ProbabilityVariable p_v) {
		Vector properties = p_v.get_properties();
		Point final_position = null;
		String s, ss;

		// get position values from the list of properties
		if (properties.size() == 0)
			return (new Point(100, 100));

		try {
			for (Enumeration e = properties.elements(); e.hasMoreElements();) {
				ss = (String) e.nextElement();
				s = ss.trim();

				// If property is not position, skip it
				if (!s.startsWith("position"))
					continue;

				// Parse the position property
				StreamTokenizer st = new StreamTokenizer(
						new StringBufferInputStream(s));
				st.parseNumbers();
				int tok;
				int x = -1, y = 0;
				while ((tok = st.nextToken()) != StreamTokenizer.TT_EOF) {
					if (tok != StreamTokenizer.TT_NUMBER)
						continue;
					if (x == -1)
						x = (int) st.nval;
					else {
						y = (int) st.nval;
						final_position = new Point(x, y);
					}
				}
				break;
			}
		} catch (IOException e) {
			final_position = new Point(100, 100);
		}
		if (final_position == null)
			final_position = new Point(100, 100);

		return (final_position);
	}

	/**
	 * Get a single value of the probability function in the node given a list
	 * of pairs (Variable Value). The list specifies which element of the
	 * function is referred to.
	 */
	public double get_function_value(String variable_value_pairs[][],
			int index_extreme_point) {
		if (pf instanceof VertexSet)
			return (((VertexSet) pf).evaluate(variable_value_pairs,
					index_extreme_point));
		else
			return (pf.evaluate(variable_value_pairs));
	}

	/**
	 * Get an array containing probability values.
	 */
	public double[] get_function_values() {
		if (pf instanceof VertexSet) {
			double[][] ep = ((VertexSet) pf).get_extreme_points();
			return (ep[0]);
		} else
			return (pf.get_values());
	}

	/**
	 * Get an array containing probability values; if credal set, return the
	 * first extreme point.
	 */
	public double[] get_function_values(int index) {
		if (pf instanceof VertexSet) {
			double[][] ep = ((VertexSet) pf).get_extreme_points();
			return (ep[index]);
		} else
			return (pf.get_values());
	}

	/**
	 * Set an array containing probability values; if credal set, insert the
	 * array in the first extreme point.
	 */
	public void set_function_values(double[] fv) {
		if (pf instanceof VertexSet)
			((VertexSet) pf).set_extreme_point(0, fv);
		else
			pf.set_values(fv);
	}

	/**
	 * Set an array containing an extreme point of the credal set.
	 */
	public void set_function_values(int iep, double[] fv) {
		if (pf instanceof VertexSet)
			((VertexSet) pf).set_extreme_point(iep, fv);
		else {
			if (iep == 0)
				pf.set_values(fv);
		}
	}

	/**
	 * Get a single value of the probability function in the node given the
	 * index of the value and the index of the extreme point.
	 */
	// public double get_function_value(int index, int index_extreme_point) {
	// if (pf instanceof VertexQBProbabilityFunction)
	// return( ((VertexQBProbabilityFunction)pf).get_value(index,
	// index_extreme_point) );
	// else
	// return(pf.get_value(index));
	// }

	/**
	 * Get a single value of the probability function in the node given the
	 * index of the value.
	 */
	// public double get_function_value(int index) {
	// if (pf instanceof VertexQBProbabilityFunction)
	// return( ((VertexQBProbabilityFunction)pf).get_value(index, 0) );
	// else
	// return(pf.get_value(index));
	// }

	/**
	 * Set a single value of the probability function in the node given a list
	 * of pairs (Variable Value). The list specifies which element of the
	 * function is referred to.
	 */
	public void set_function_value(String variable_value_pairs[][], double val,
			int index_extreme_point) {
		if (pf instanceof VertexSet)
			((VertexSet) pf).set_value(variable_value_pairs, val,
					index_extreme_point);
		else
			pf.set_value(variable_value_pairs, val);
	}

	/* ******************** Public methods ******************** */

	/**
	 * Return the name of the variable in the node.
	 */
	public String get_name() {
		return (pv.get_name());
	}

	/**
	 * Set the name of the variable.
	 */
	public void set_name(String n) {
		pv.set_name(n);
	}

	/**
	 * Get the name of all variables in the probability function.
	 */
	public String[] get_all_names() {
		String[] ns = new String[pf.number_variables()];
		for (int i = 0; i < ns.length; i++)
			ns[i] = pf.get_variable(i).get_name();
		return (ns);
	}

	/**
	 * Return the values of the variable in the node.
	 */
	public String[] get_values() {
		return (pv.get_values());
	}

	/**
	 * Get all values for variables in the function in the node.
	 */
	public String[][] get_all_values() {
		int i, j;
		String all_values[][] = new String[pf.number_variables()][];
		DiscreteVariable dv;
		for (i = 0; i < pf.number_variables(); i++) {
			dv = pf.get_variable(i);
			all_values[i] = new String[dv.number_values()];
			for (j = 0; j < all_values[i].length; j++) {
				all_values[i][j] = dv.get_value(j);
			}
		}
		return (all_values);
	}

	/**
	 * Return the number of values in the variable in the node.
	 */
	public int get_number_values() {
		return (pv.number_values());
	}

	/**
	 * Indicate whether the node has parents.
	 */
	public boolean hasParent() {
		return (pf.number_variables() > 1);
	}

	/**
	 * Return the parents of a node as an Enumeration object.
	 */
	public Vector get_parents() {
		return (parents);
	}

	/**
	 * Return the children of a node as an Enumeration object.
	 */
	public Vector get_children() {
		return (children);
	}

	/**
	 * Indicate whether the variable in the node is observed.
	 */
	public boolean is_observed() {
		return (pv.is_observed());
	}

	/**
	 * Indicate whether the variable in the node is an explanatory variable.
	 */
	public boolean is_explanation() {
		return (pv.is_explanation());
	}

	/**
	 * Return the observed value for the variable in the node.
	 */
	public int get_observed_value() {
		return (pv.get_observed_index());
	}

	/**
	 * Return the X position of the node.
	 */
	public int get_pos_x() {
		return (pos.x);
	}

	/**
	 * Return the Y position of the node.
	 */
	public int get_pos_y() {
		return (pos.y);
	}

	/**
	 * Return the variable properties
	 */
	public Vector get_variable_properties() {
		return (pv.get_properties());
	}

	/**
	 * Set the variable properties.
	 */
	public void set_variable_properties(Vector prop) {
		pv.set_properties(prop);
	}

	/**
	 * Return the function properties.
	 */
	public Vector get_function_properties() {
		return (pf.get_properties());
	}

	/**
	 * Set the function properties.
	 */
	public void set_function_properties(Vector prop) {
		pf.set_properties(prop);
	}

	/**
	 * Whether or not the node represents a convex set of distributions (credal
	 * set).
	 */
	public boolean is_credal_set() {
		if (pf instanceof QBProbabilityFunction)
			return (true);
		else
			return (false);
	}

	/**
	 * Number of distributions that are represented by a node.
	 */
	public int number_extreme_distributions() {
		if (pf instanceof VertexSet)
			return (((VertexSet) pf).get_extreme_points().length);
		else
			return (1);
	}

	/**
	 * Make sure the node represents a single distribution.
	 */
	public void set_no_local_credal_set() {
		if (pf instanceof QBProbabilityFunction) {
			if (pf instanceof VertexSet)
				((VertexSet) pf).compose_values();
			pf = new ProbabilityFunction(pf, pf.get_values());
		}
	}

	/**
	 * Make sure the node represents a VertexSet a given number of extreme
	 * distributions.
	 */
	public void set_local_credal_set(int number_extreme_points) {
		if (!(pf instanceof VertexSet)) {
			pf = new VertexSet(pf);
		}
		((VertexSet) pf).set_local_credal_set(number_extreme_points);
	}

	/**
	 * Make sure the node represents a VertexSet.
	 */
	public void set_local_credal_set() {
		if (!(pf instanceof VertexSet)) {
			pf = new VertexSet(pf);
		}
	}

	/**
	 * Set the observation for the node.
	 */
	public void set_observation_value(String value) {
		pv.set_observed_value(value);
	}

	/**
	 * Clear the observation for the node.
	 */
	public void clear_observation() {
		pv.set_invalid_observed_index();
	}

	/**
	 * Set the explanatory status of the node.
	 */
	public void set_explanation(boolean flag) {
		if (flag == true)
			pv.set_explanation_value(0);
		else
			pv.set_explanation_value(BayesNet.INVALID_INDEX);
	}

	/**
	 * Remove a property from a variable.
	 */
	public void remove_variable_property(int index) {
		pv.remove_property(index);
	}

	/**
	 * Remove a property from a function.
	 */
	public void remove_function_property(int index) {
		pf.remove_property(index);
	}

	/**
	 * Add a property to a variable.
	 */
	public void add_variable_property(String s) {
		pv.add_property(s);
		update_position_from_property(s);
	}

	/*
	 * Update the position of a node given a property.
	 */
	public void update_position_from_property(String s) {
		// If property is position:
		if (s.startsWith("position")) {
			Point final_position = null;
			// Parse the position property
			try {
				StreamTokenizer st = new StreamTokenizer(
						new StringBufferInputStream(s));
				st.parseNumbers();
				int tok;
				int x = -1, y = 0;
				while ((tok = st.nextToken()) != StreamTokenizer.TT_EOF) {
					if (tok != StreamTokenizer.TT_NUMBER)
						continue;
					if (x == -1)
						x = (int) st.nval;
					else {
						y = (int) st.nval;
						final_position = new Point(x, y);
					}
				}
			} catch (IOException e) {
				final_position = new Point(100, 100);
			}
			if (final_position == null)
				final_position = new Point(100, 100);
			// Update the position property.
			pos = final_position;
		}
	}

	/**
	 * Add a property from to function.
	 */
	public void add_function_property(String prop) {
		pf.add_property(prop);
	}

}
