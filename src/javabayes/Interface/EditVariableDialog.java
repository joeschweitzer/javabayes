/**
 * EditVariableDialog.java
 * @author Fabio G. Cozman 
 *  Original version by  Sreekanth Nagarajan, rewritten
 *  from scratch by Fabio Cozman.
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
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;

class EditVariableDialog extends Dialog {
	// Network panel, used to repaint screen and access dialogs.
	NetworkPanel npan;

	// The InferenceGraph and InferenceGraphNode objects
	// that hold the variable.
	InferenceGraph ig;
	InferenceGraphNode node;

	// Variables that hold the contents of the dialog.
	int number_extreme_points;
	PropertyManager variable_property_manager;
	PropertyManager function_property_manager;

	// Variables used to construct the dialog.
	int displayed_variable_property_index, displayed_function_property_index;
	Panel np, nvp, tp, ttp, vpp, fpp, npp, cbp, pp, gnp, gncp, okp, qbp, qbpp;
	Label name, new_value, type;
	Label variable_properties, function_properties, local_parameter;
	TextField text_name, text_new_value, text_local_parameter;
	CheckboxGroup types, function_types;
	Checkbox chance_type, explanation_type, no_local_credal_set_type,
			local_credal_set_type;
	Button new_variable_property, next_variable_property;
	Button new_function_property, next_function_property;
	TextField variable_properties_text, function_properties_text;
	Button dist_button, ok_button, dismiss_button;

	// Constants used to construct the dialog.
	private final static int TOP_INSET = 5;
	private final static int LEFT_INSET = 10;
	private final static int RIGHT_INSET = 10;
	private final static int BOTTOM_INSET = 0;

	// Labels for the various elements of the dialog.
	private final static String name_label = "Name:";
	private final static String new_value_label = "Values:";
	private final static String type_label = "Types:";
	private final static String chance_type_label = "Chance node";
	private final static String explanation_type_label = "Explanatory node";
	private final static String no_local_credal_set_label = "Single distribution";
	private final static String local_credal_set_label = "Credal set with extreme points";
	private final static String variable_properties_label = "Variable properties:";
	private final static String function_properties_label = "Function properties:";
	private final static String next_property_label = "Next";
	private final static String new_property_label = "New";
	private final static String edit_function_label = "Edit function";
	private final static String ok_label = "Apply";
	private final static String dismiss_label = "Dismiss";

	/**
	 * Default constructor for an EditVariableDialog object.
	 */
	public EditVariableDialog(NetworkPanel network_panel, Frame parent,
			InferenceGraph i_g, InferenceGraphNode node) {
		super(parent, "Edit: " + node.get_name(), true);
		this.npan = network_panel;
		this.ig = i_g;
		this.node = node;

		// Compose the frame

		// Panel for name, values and type

		// Panel for the name
		np = new Panel();
		np.setLayout(new BorderLayout());
		name = new Label(name_label);
		text_name = new TextField(30);
		np.add("West", name);
		np.add("Center", text_name);

		// Panel for the values
		nvp = new Panel();
		nvp.setLayout(new BorderLayout());
		new_value = new Label(new_value_label);
		text_new_value = new TextField(60);
		nvp.add("West", new_value);
		nvp.add("Center", text_new_value);

		// Panel for the type
		tp = new Panel();
		tp.setLayout(new BorderLayout());
		type = new Label(type_label);

		ttp = new Panel();
		ttp.setLayout(new GridLayout(2, 1));
		types = new CheckboxGroup();
		chance_type = new Checkbox(chance_type_label, types, true);
		explanation_type = new Checkbox(explanation_type_label, types, false);
		ttp.add(chance_type);
		ttp.add(explanation_type);

		qbp = new Panel();
		qbp.setLayout(new GridLayout(2, 1));
		function_types = new CheckboxGroup();
		no_local_credal_set_type = new Checkbox(no_local_credal_set_label,
				function_types, true);
		local_credal_set_type = new Checkbox(local_credal_set_label,
				function_types, false);

		qbp.add(no_local_credal_set_type);
		qbp.add(local_credal_set_type);

		tp.add("North", type);
		tp.add("West", ttp);
		tp.add("East", qbp);

		// Finish panel for name, values and type
		cbp = new Panel();
		cbp.setLayout(new BorderLayout(10, 10));
		cbp.add("North", np);
		cbp.add("Center", nvp);
		cbp.add("South", tp);

		// Panel for properties (variable, function and network)
		pp = new Panel();
		pp.setLayout(new BorderLayout());

		// Variable properties
		vpp = new Panel();
		vpp.setLayout(new BorderLayout());
		variable_properties = new Label(variable_properties_label);
		next_variable_property = new Button(next_property_label);
		new_variable_property = new Button(new_property_label);
		variable_properties_text = new TextField(40);
		vpp.add("North", variable_properties);
		vpp.add("West", next_variable_property);
		vpp.add("Center", variable_properties_text);
		vpp.add("East", new_variable_property);

		// Function properties
		fpp = new Panel();
		fpp.setLayout(new BorderLayout());
		function_properties = new Label(function_properties_label);
		next_function_property = new Button(next_property_label);
		new_function_property = new Button(new_property_label);
		function_properties_text = new TextField(40);
		fpp.add("North", function_properties);
		fpp.add("West", next_function_property);
		fpp.add("Center", function_properties_text);
		fpp.add("East", new_function_property);

		// Finish panel for properties
		pp.add("North", vpp);
		pp.add("Center", fpp);

		// Return buttons
		okp = new Panel();
		okp.setLayout(new FlowLayout(FlowLayout.CENTER));
		dist_button = new Button(edit_function_label);
		okp.add(dist_button);
		ok_button = new Button(ok_label);
		dismiss_button = new Button(dismiss_label);
		okp.add(ok_button);
		okp.add(dismiss_button);
		setLayout(new BorderLayout());
		add("North", cbp);
		add("Center", pp);
		add("South", okp);

		// Pack the whole window
		pack();

		// Initialize values
		fill_dialog();
	}

	/**
	 * Customized show method.
	 */
	@Override
	public void show() {
		Rectangle bounds = getParent().bounds();
		Rectangle abounds = bounds();

		move(bounds.x + (bounds.width - abounds.width) / 2, bounds.y
				+ (bounds.height - abounds.height) / 2);

		super.show();
	}

	/**
	 * Customize insets method.
	 */
	@Override
	public Insets insets() {
		Insets ins = super.insets();
		return (new Insets(ins.top + TOP_INSET, ins.left + LEFT_INSET,
				ins.bottom + BOTTOM_INSET, ins.right + RIGHT_INSET));
	}

	/**
	 * Handle the possible destruction of the window.
	 */
	@Override
	public boolean handleEvent(Event evt) {
		if (evt.id == Event.WINDOW_DESTROY)
			dispose();
		return (super.handleEvent(evt));
	}

	/**
	 * Fill the values in the dialog area.
	 */
	private void fill_dialog() {
		String values[], all_values = "";
		Vector prop;
		String property;

		// Synchronize the network if necessary.
		ig.get_bayes_net();

		// Fill name
		text_name.setText(node.get_name());

		// Fill values
		values = node.get_values();
		for (int i = 0; i < values.length; i++) {
			all_values += values[i];
			if (i != (values.length - 1))
				all_values += ", ";
		}
		text_new_value.setText(all_values);

		// Set type: explanatory or chance.
		if (node.is_explanation())
			types.setCurrent(explanation_type);
		else
			types.setCurrent(chance_type);

		// Set type: standard or credal.
		if (node.is_credal_set())
			function_types.setCurrent(local_credal_set_type);
		else
			function_types.setCurrent(no_local_credal_set_type);

		// Fill and store properties
		variable_property_manager = new PropertyManager(
				node.get_variable_properties(), variable_properties_text);
		function_property_manager = new PropertyManager(
				node.get_function_properties(), function_properties_text);
	}

	/**
	 * Handle the events.
	 */
	@Override
	public boolean action(Event evt, Object arg) {
		Vector prop;
		String values[], property, checked_name;

		if (evt.target == dismiss_button) {
			dispose();
		} else if (evt.target == ok_button) {
			update_dialog();
		} else if (evt.target == new_variable_property) {
			variable_property_manager.new_property();
		} else if (evt.target == next_variable_property) {
			variable_property_manager.next_property();
		} else if (evt.target == new_function_property) {
			function_property_manager.new_property();
		} else if (evt.target == next_function_property) {
			function_property_manager.next_property();
		} else if (evt.target == variable_properties_text) {
			variable_property_manager.update_property();
		} else if (evt.target == function_properties_text) {
			function_property_manager.update_property();
		} else if (evt.target == dist_button) {
			npan.edit_function(node);
		} else
			return super.action(evt, arg);

		return true;
	}

	/**
	 * Parse the values stated in the values TextField.
	 */
	private String[] parse_values(String all_values) {
		String token = null, delimiters = " ,\n\t\r";
		StringTokenizer st = new StringTokenizer(all_values, delimiters);
		String vals[] = new String[st.countTokens()];
		int i = 0;

		while (st.hasMoreTokens()) {
			vals[i] = ig.validate_value(st.nextToken());
			i++;
		}
		return (vals);
	}

	/*
	 * Update the contents of the network when the dialog exits.
	 */
	private void update_dialog() {
		// Update the name of the variable.
		String checked_name = ig.check_name(text_name.getText());
		if (checked_name != null)
			node.set_name(checked_name);
		// Update the values of the variable.
		String[] values = parse_values(text_new_value.getText());
		if (values != null)
			ig.change_values(node, values);
		// Update the explanatory/chance type.
		if (types.getSelectedCheckbox() == chance_type)
			node.set_explanation(false);
		else
			node.set_explanation(true);
		npan.repaint();
		// Update the standard/credal type.
		if (function_types.getSelectedCheckbox() == no_local_credal_set_type)
			node.set_no_local_credal_set();
		else
			node.set_local_credal_set();
		// Update the variable properties (if necessary).
		Vector vprop = variable_property_manager.update_property_on_exit();
		if (vprop != null) {
			node.set_variable_properties(vprop);
			for (Enumeration e = vprop.elements(); e.hasMoreElements();)
				node.update_position_from_property((String) (e.nextElement()));
		}
		// Update the function properties (if necessary).
		Vector fprop = function_property_manager.update_property_on_exit();
		if (fprop != null)
			node.set_function_properties(fprop);
	}
}
