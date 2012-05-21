/**
 * EditCredalSet.java
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
import java.awt.Choice;
import java.awt.Event;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.util.Enumeration;
import java.util.Vector;

import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;

class EditCredalSet extends EditFunctionPanel {
	// The graph and node that contain the probability function.
	private InferenceGraph ig;
	private InferenceGraphNode node;

	// Variables that hold the relevant information from the node.
	private String all_variable_names[];
	private String all_variable_values[][];
	private double all_probability_values[][];
	private int index_extreme_point;

	// Components used to construct the panel.
	private FunctionTablePanel probability_table;
	private Panel csp, ics, qbpp;
	private Choice credal_set_choice;
	private Label local_parameter;
	private TextField text_local_parameter;

	// Constants used to construct the panel.
	private final static String credal_set_specification = "Credal set specification";
	private final static String credal_set = "Index of extreme distribution:";
	private final static String number_extreme_points_label = "Number of extreme points:";

	/**
	 * Default constructor for an EditCredalSet.
	 */
	public EditCredalSet(InferenceGraph i_g, InferenceGraphNode i_g_n) {
		this.ig = i_g;
		this.node = i_g_n;

		// Copy the probability values in the node.
		copy_probability_values();

		// Get the variable names.
		all_variable_names = node.get_all_names();

		// Get the variable values.
		all_variable_values = node.get_all_values();

		// Construct the name of the probability function.
		Label probability_name = create_credal_set_name();

		// Construct the table of probability values.
		index_extreme_point = 0;
		probability_table = new FunctionTablePanel(all_variable_names,
				all_variable_values,
				all_probability_values[index_extreme_point]);

		// Credal set panel.
		generate_credal_set_panel();

		// Set the final layout
		setLayout(new BorderLayout());
		add("North", probability_name);
		add("Center", probability_table);
		add("South", csp);
	}

	/*
	 * Copy the probability values into internal variables.
	 */
	private void copy_probability_values() {
		double original_probability_values[];
		all_probability_values = new double[node.number_extreme_distributions()][];
		for (int i = 0; i < all_probability_values.length; i++) {
			original_probability_values = node.get_function_values(i);
			all_probability_values[i] = new double[original_probability_values.length];
			for (int j = 0; j < all_probability_values[i].length; j++)
				all_probability_values[i][j] = original_probability_values[j];
		}
	}

	/*
	 * Create a Label containing a description of the credal set.
	 */
	private Label create_credal_set_name() {
		StringBuffer name = new StringBuffer("K(");
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
		int i, k;
		all_probability_values[index_extreme_point] = probability_table
				.get_table();
		for (i = 0; i < all_probability_values.length; i++)
			node.set_function_values(i, all_probability_values[i]);
		// Update the number of extreme points.
		try {
			int old_number_extreme_points = all_probability_values.length;
			int number_extreme_points = (new Integer(
					text_local_parameter.getText())).intValue();
			if (number_extreme_points != all_probability_values.length) {
				node.set_local_credal_set(number_extreme_points);
				copy_probability_values();
				if (index_extreme_point >= number_extreme_points)
					index_extreme_point = number_extreme_points - 1;
				probability_table
						.insert_table(all_probability_values[index_extreme_point]);
				if (number_extreme_points > old_number_extreme_points)
					for (k = old_number_extreme_points; k < number_extreme_points; k++)
						credal_set_choice.addItem(String.valueOf(k));
				if (old_number_extreme_points > number_extreme_points)
					for (k = (old_number_extreme_points - 1); k >= number_extreme_points; k--)
						credal_set_choice.remove(k);
				credal_set_choice.select(index_extreme_point);
			}
		} catch (NumberFormatException ex) {
		}
	}

	@Override
	void dismiss() {
		// No-op.
	}

	/**
	 * Generate a panel for credal set.
	 */
	private void generate_credal_set_panel() {
		csp = new Panel();
		csp.setLayout(new BorderLayout());

		Label credal_set_specification_label = new Label(
				credal_set_specification, Label.CENTER);

		ics = new Panel();
		ics.setLayout(new BorderLayout());
		Label credal_set_label = new Label(credal_set);
		credal_set_choice = new Choice();
		for (int i = 0; i < node.number_extreme_distributions(); i++)
			credal_set_choice.addItem(String.valueOf(i));
		ics.add("West", credal_set_label);
		ics.add("Center", credal_set_choice);

		qbpp = new Panel();
		qbpp.setLayout(new BorderLayout());
		local_parameter = new Label(number_extreme_points_label);
		text_local_parameter = new TextField(5);
		int number_extreme_points = node.number_extreme_distributions();
		text_local_parameter.setText(String.valueOf(number_extreme_points));
		qbpp.add("West", local_parameter);
		qbpp.add("Center", text_local_parameter);

		csp.add("North", credal_set_specification_label);
		csp.add("Center", qbpp);
		csp.add("South", ics);
	}

	/**
	 * Handle the events.
	 */
	@Override
	public boolean action(Event evt, Object arg) {
		if (evt.target == credal_set_choice) {
			all_probability_values[index_extreme_point] = probability_table
					.get_table();
			index_extreme_point = credal_set_choice.getSelectedIndex();
			probability_table
					.insert_table(all_probability_values[index_extreme_point]);
			return (true);
		}
		return (super.action(evt, arg));
	}
}
