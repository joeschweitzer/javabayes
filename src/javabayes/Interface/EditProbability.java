/**
 * EditProbability.java
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

package javabayes.Interface;

import java.awt.BorderLayout;
import java.awt.Label;
import java.util.Enumeration;
import java.util.Vector;

import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;

class EditProbability extends EditFunctionPanel {
	EditFunctionDialog parent_dialog;

	// The graph and node that contain the probability function.
	InferenceGraph ig;
	InferenceGraphNode node;

	// Variables that hold the relevant information from the node.
	String all_variable_names[];
	String all_variable_values[][];
	double probability_values[];

	FunctionTablePanel probability_table;

	/**
	 * Default constructor for an EditProbability.
	 */
	public EditProbability(EditFunctionDialog parent_dialog,
			InferenceGraph i_g, InferenceGraphNode i_g_n) {
		this.parent_dialog = parent_dialog;
		this.ig = i_g;
		this.node = i_g_n;

		// Copy the probability values in the node.
		double original_probability_values[] = node.get_function_values();
		probability_values = new double[original_probability_values.length];
		for (int i = 0; i < probability_values.length; i++)
			probability_values[i] = original_probability_values[i];

		// Get the variable names.
		all_variable_names = node.get_all_names();

		// Get the variable values.
		all_variable_values = node.get_all_values();

		// Construct the name of the probability function.
		Label probability_name = create_probability_name();

		// Construct the table of probability values.
		probability_table = new FunctionTablePanel(all_variable_names,
				all_variable_values, probability_values);

		// Set the final layout
		setLayout(new BorderLayout());
		add("North", probability_name);
		add("Center", probability_table);
	}

	/*
	 * Create a Label containing a description of the probability function.
	 */
	private Label create_probability_name() {
		StringBuffer name = new StringBuffer("p(");
		name.append(node.get_name());
		if (node.hasParent()) {
			name.append(" |");
			Vector parents = node.get_parents();
			for (Enumeration e = parents.elements(); e.hasMoreElements();)
				name.append(" "
						+ ((InferenceGraphNode) (e.nextElement())).get_name()
						+ ",");
			name.setCharAt(name.length() - 1, ')');
		} else
			name.append(")");
		return (new Label(name.toString(), Label.CENTER));
	}

	@Override
	void accept() {
		double EPSILON = 1e-6;
		// Get the values from the table.
		probability_values = probability_table.get_table();
		// Check whether things add up to one.
		int number_values = node.get_number_values();
		int number_conditioning_values = probability_values.length
				/ number_values;
		double verification_counters[] = new double[number_conditioning_values];
		for (int i = 0; i < probability_values.length; i++)
			verification_counters[i % number_conditioning_values] += probability_values[i];
		for (int j = 0; j < verification_counters.length; j++) {
			if (Math.abs(verification_counters[j] - 1.0) >= EPSILON) {
				EditorFrame ef;
				if (parent_dialog.parent instanceof EditorFrame) {
					ef = (EditorFrame) (parent_dialog.parent);
					ef.jb.appendText("Some of the probability values "
							+ "you have edited add up to "
							+ verification_counters[j]
							+ ". Please check it.\n\n");
				}
			}
		}
		// Set the values.
		node.set_function_values(probability_values);
	}

	@Override
	void dismiss() {
		// No-op.
	}
}
