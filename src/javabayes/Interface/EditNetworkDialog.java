/**
 * EditNetworkDialog.java
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
import java.util.Vector;

import javabayes.InferenceGraphs.InferenceGraph;

class EditNetworkDialog extends Dialog {
	// The InferenceGraph object that contains the network.
	InferenceGraph ig;

	// Variables that hold the properties in the dialog.
	PropertyManager property_manager;

	// Variables used to construct the dialog.
	int displayed_network_property_index;
	Panel np, npp, gnp, gncp, gnpp, tp, okp;
	Label name, network_properties;
	TextField text_name, text_global_parameter;
	Label global, global_parameter;
	CheckboxGroup globals;
	Checkbox no_global, epsilon_global, ratio_global, total_global,
			bounded_global;
	Button new_network_property, next_network_property;
	TextField network_properties_text;
	Button ok_button, dismiss_button;

	// Constants used to construct the dialog.
	private final static int TOP_INSET = 5;
	private final static int LEFT_INSET = 10;
	private final static int RIGHT_INSET = 10;
	private final static int BOTTOM_INSET = 0;

	// Labels for the various elements of the dialog.
	private final static String dialog_title = "Edit Network";
	private final static String name_label = "Name:";
	private final static String network_properties_label = "Network properties:";
	private final static String next_property_label = "Next";
	private final static String new_property_label = "New";
	private final static String global_label = "Network neighborhood model:";
	private final static String no_global_label = "No global neighborhood";
	private final static String epsilon_global_label = "Epsilon contaminated neighborhood";
	private final static String ratio_global_label = "Constant density ratio neighborhood";
	private final static String total_global_label = "Total variation neighborhood";
	private final static String bounded_global_label = "Constant density bounded neighborhood";
	private final static String global_parameter_label = "Global neighborhood parameter:";
	private final static String ok_label = "Apply";
	private final static String dismiss_label = "Dismiss";

	/**
	 * Default constructor for an EditNetworkDialog object.
	 */
	public EditNetworkDialog(Frame parent, InferenceGraph i_g) {
		super(parent, dialog_title, true);
		this.ig = i_g;

		// Compose the whole frame.

		// Panel for the name.
		np = new Panel();
		np.setLayout(new BorderLayout());
		name = new Label(name_label);
		text_name = new TextField(30);
		np.add("West", name);
		np.add("Center", text_name);

		// Network properties.
		npp = new Panel();
		npp.setLayout(new BorderLayout());
		network_properties = new Label(network_properties_label);
		next_network_property = new Button(next_property_label);
		new_network_property = new Button(new_property_label);
		network_properties_text = new TextField(40);

		npp.add("North", network_properties);
		npp.add("West", next_network_property);
		npp.add("Center", network_properties_text);
		npp.add("East", new_network_property);

		// Global neighborhood parameters
		gnp = new Panel();
		gnp.setLayout(new BorderLayout());
		global = new Label(global_label);

		gncp = new Panel();
		gncp.setLayout(new GridLayout(5, 1));
		globals = new CheckboxGroup();
		no_global = new Checkbox(no_global_label, globals, true);
		epsilon_global = new Checkbox(epsilon_global_label, globals, false);
		ratio_global = new Checkbox(ratio_global_label, globals, false);
		total_global = new Checkbox(total_global_label, globals, false);
		bounded_global = new Checkbox(bounded_global_label, globals, false);
		gncp.add(no_global);
		gncp.add(epsilon_global);
		gncp.add(ratio_global);
		gncp.add(total_global);
		gncp.add(bounded_global);

		gnpp = new Panel();
		gnpp.setLayout(new BorderLayout());
		global_parameter = new Label(global_parameter_label);
		text_global_parameter = new TextField(10);
		gnpp.add("West", global_parameter);
		gnpp.add("Center", text_global_parameter);

		gnp.add("North", global);
		gnp.add("Center", gncp);
		gnp.add("South", gnpp);

		// All the network parameters
		tp = new Panel();
		tp.setLayout(new BorderLayout());
		tp.add("North", np);
		tp.add("Center", npp);
		tp.add("South", gnp);

		// Return buttons
		okp = new Panel();
		okp.setLayout(new FlowLayout(FlowLayout.CENTER));
		ok_button = new Button(ok_label);
		dismiss_button = new Button(dismiss_label);
		okp.add(ok_button);
		okp.add(dismiss_button);

		setLayout(new BorderLayout());
		add("North", tp);
		add("Center", okp);

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
	 * Customized insets method.
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

	/*
	 * Fill the values in the dialog area.
	 */
	private void fill_dialog() {
		String values[], all_values = "";
		Vector prop;
		String property;
		double par;

		// Synchronize the network if necessary.
		ig.get_bayes_net();

		// Fill the name.
		text_name.setText(ig.get_name());

		// Fill and store network properties
		property_manager = new PropertyManager(ig.get_network_properties(),
				network_properties_text);

		// Set global neighborhood
		switch (ig.get_global_neighborhood_type()) {
		case InferenceGraph.NO_CREDAL_SET:
			globals.setCurrent(no_global);
			break;
		case InferenceGraph.CONSTANT_DENSITY_RATIO:
			globals.setCurrent(ratio_global);
			break;
		case InferenceGraph.EPSILON_CONTAMINATED:
			globals.setCurrent(epsilon_global);
			break;
		case InferenceGraph.CONSTANT_DENSITY_BOUNDED:
			globals.setCurrent(bounded_global);
			break;
		case InferenceGraph.TOTAL_VARIATION:
			globals.setCurrent(total_global);
			break;
		}

		par = ig.get_global_neighborhood_parameter();
		text_global_parameter.setText(String.valueOf(par));
	}

	/**
	 * Handle the possible events.
	 */
	@Override
	public boolean action(Event evt, Object arg) {
		if (evt.target == dismiss_button) {
			dispose();
		} else if (evt.target == ok_button) {
			update_dialog();
		} else if (evt.target == new_network_property) {
			property_manager.new_property();
		} else if (evt.target == next_network_property) {
			property_manager.next_property();
		} else if (evt.target == network_properties_text) {
			property_manager.update_property();
		} else
			return super.action(evt, arg);

		return (true);
	}

	/*
	 * Update the contents of the network when the dialog exits.
	 */
	private void update_dialog() {
		// Update the name of the network.
		String new_network_name = text_name.getText();
		if (!(new_network_name.equals(ig.get_name()))) {
			new_network_name = ig.check_name(new_network_name);
			if (new_network_name != null)
				ig.set_name(new_network_name);
		}

		// Update the properties (if necessary).
		Vector prop = property_manager.update_property_on_exit();
		if (prop != null)
			ig.set_network_properties(prop);

		// Update the global neighborhood parameters.
		Checkbox selected_global_neighborhood = globals.getCurrent();
		if (selected_global_neighborhood == no_global)
			ig.set_global_neighborhood(InferenceGraph.NO_CREDAL_SET);
		else if (selected_global_neighborhood == epsilon_global)
			ig.set_global_neighborhood(InferenceGraph.EPSILON_CONTAMINATED);
		else if (selected_global_neighborhood == ratio_global)
			ig.set_global_neighborhood(InferenceGraph.CONSTANT_DENSITY_RATIO);
		else if (selected_global_neighborhood == bounded_global)
			ig.set_global_neighborhood(InferenceGraph.CONSTANT_DENSITY_BOUNDED);
		else if (selected_global_neighborhood == total_global)
			ig.set_global_neighborhood(InferenceGraph.TOTAL_VARIATION);

		try {
			double par = (new Double(text_global_parameter.getText())
					.doubleValue());
			if (par <= 0.0)
				par = 0.0;
			ig.set_global_neighborhood_parameter(par);
		} catch (NumberFormatException e) {
		} // Leave parameter as is if in error.
	}
}
