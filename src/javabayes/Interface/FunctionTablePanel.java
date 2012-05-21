/**
 * FunctionTablePanel.java
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
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. */

package javabayes.Interface;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

public class FunctionTablePanel extends Panel {
	private String all_variable_names[];
	private String all_variable_values[][];
	private double table_values[];
	private int parents_current_choices[];

	private Panel ap, dp, pp;
	private Label parents_announce;
	private Label parents_labels[];
	private Label separator_label;
	private Choice parent_choices[];
	private TextField fields[][];
	private Label first_parent_name_label;
	private Label first_parent_values_labels[];
	private Label node_values_labels[];

	private final static Color color_parents = Color.red;

	private final static String parents_announce_label = "Values for parents:";

	/**
	 * Default constructor for an FunctionTablePanel.
	 */
	public FunctionTablePanel(String avn[], String avv[][], double tv[]) {
		this.all_variable_names = avn;
		this.all_variable_values = avv;
		this.table_values = tv;

		// Construct the relevant panels.
		build_panels();

		// Store the initial choices.
		if (all_variable_names.length > 2) {
			parents_current_choices = new int[parent_choices.length];
			for (int i = 0; i < parents_current_choices.length; i++)
				parents_current_choices[i] = parent_choices[i]
						.getSelectedIndex();
		}

		// Finally set the panel with all choices and fields.
		setLayout(new BorderLayout());
		add("North", ap);
		add("Center", dp);
	}

	/**
	 * Return the current table.
	 */
	public double[] get_table() {
		update_table_values();
		return (table_values);
	}

	/**
	 * Insert a new table.
	 */
	public void insert_table(double new_table[]) {
		table_values = new_table;

		int i, j, k;
		if (all_variable_names.length == 1) {
			for (j = 0; j < all_variable_values[0].length; j++)
				fields[j][0].setText(String.valueOf(table_values[j]));
		} else if (all_variable_names.length == 2) {
			for (i = 0; i < all_variable_values[0].length; i++)
				for (j = 0; j < all_variable_values[1].length; j++)
					fields[i][j].setText(String.valueOf(table_values[i
							* all_variable_values[1].length + j]));
		} else {
			double value_set = 0.0;
			int parent_indexes[] = new int[all_variable_names.length];
			for (k = 0; k < parent_choices.length; k++)
				parent_indexes[k + 2] = parent_choices[k].getSelectedIndex();
			for (i = 0; i < all_variable_values[0].length; i++) {
				for (j = 0; j < all_variable_values[1].length; j++) {
					parent_indexes[0] = i;
					parent_indexes[1] = j;
					fields[i][j]
							.setText(String
									.valueOf(table_values[get_location_from_indexes(parent_indexes)]));
				}
			}
			for (k = 0; k < parents_current_choices.length; k++)
				parents_current_choices[k] = parent_choices[k]
						.getSelectedIndex();
		}
	}

	/*
	 * Build the table panel.
	 */
	void build_panels() {
		build_parents_panel();
		build_distribution_panel();
	}

	/*
	 * Build parents panel.
	 */
	private void build_parents_panel() {
		if (all_variable_names.length <= 2) {
			ap = new Panel();
			return;
		}
		// Construct the panel with the values of the parents.
		pp = new Panel();
		pp.setLayout(new GridLayout(all_variable_names.length - 2, 2));
		parents_labels = new Label[all_variable_names.length - 2];
		parent_choices = new Choice[all_variable_names.length - 2];

		// Fill the labels and choices for the values of the parents.
		for (int i = 2; i < all_variable_names.length; i++) {
			// Get a parent
			parents_labels[i - 2] = new Label(all_variable_names[i]);
			parents_labels[i - 2].setForeground(color_parents);
			parent_choices[i - 2] = new Choice();
			parent_choices[i - 2].setForeground(color_parents);
			// Fill the choice item with the values for the parent.
			for (int j = 0; j < all_variable_values[i].length; j++) {
				parent_choices[i - 2].addItem(all_variable_values[i][j]);
			}
			// Insert the label and choice
			pp.add(parents_labels[i - 2]);
			pp.add(parent_choices[i - 2]);
		}
		// Just use a panel to put the title in the parent choices.
		ap = new Panel();
		ap.setLayout(new BorderLayout());
		parents_announce = new Label(parents_announce_label, Label.CENTER);
		parents_announce.setForeground(color_parents);
		ap.add("North", parents_announce);
		ap.add("Center", pp);
		separator_label = new Label("");
		ap.add("South", separator_label);
	}

	/*
	 * Build distribution panel.
	 */
	private void build_distribution_panel() {
		int i, j;
		dp = new Panel();
		node_values_labels = new Label[all_variable_values[0].length];
		if (all_variable_names.length == 1) {
			// For a node with no parent, go directly to the distribution panel.
			dp.setLayout(new GridLayout(all_variable_values[0].length, 2));
			fields = new TextField[all_variable_values[0].length][1];
			for (j = 0; j < all_variable_values[0].length; j++) {
				node_values_labels[j] = new Label(all_variable_values[0][j]);
				dp.add(node_values_labels[j]);
				fields[j][0] = new TextField();
				fields[j][0].setText(String.valueOf(table_values[j]));
				dp.add(fields[j][0]);
			}
		} else {
			// For one or more parents, create a two-dimensional table.
			dp.setLayout(new GridLayout(all_variable_values[0].length + 1,
					all_variable_values[1].length + 1));
			fields = new TextField[all_variable_values[0].length][all_variable_values[1].length];
			first_parent_name_label = new Label(all_variable_names[1]);
			first_parent_name_label.setForeground(color_parents);
			dp.add(first_parent_name_label);
			first_parent_values_labels = new Label[all_variable_values[1].length];
			for (i = 0; i < all_variable_values[1].length; i++) {
				first_parent_values_labels[i] = new Label(
						all_variable_values[1][i]);
				first_parent_values_labels[i].setForeground(color_parents);
				dp.add(first_parent_values_labels[i]);
			}
			// Auxiliary jump; used to compute location of table values.
			int jump = 1;
			if (all_variable_names.length > 1) {
				for (i = 2; i < all_variable_names.length; i++)
					jump *= all_variable_values[i].length;
			}
			for (j = 0; j < all_variable_values[0].length; j++) {
				node_values_labels[j] = new Label(all_variable_values[0][j]);
				dp.add(node_values_labels[j]);
				for (i = 0; i < all_variable_values[1].length; i++) {
					fields[j][i] = new TextField();
					fields[j][i].setText(String.valueOf(table_values[(j
							* all_variable_values[1].length + i)
							* jump]));
					dp.add(fields[j][i]);
				}
			}
		}
	}

	/**
	 * Handle events in the panel.
	 */
	@Override
	public boolean action(Event evt, Object arg) {
		int i, j, k;
		int starting_index;
		double value_set = 0.0;

		// In case the node has more than one
		// parent, check whether parent values
		// have been changed.
		if (all_variable_names.length > 2) {
			for (i = 0; i < parent_choices.length; i++) {
				if (evt.target == parent_choices[i]) {
					update_table_for_parents();
					return (true);
				}
			}
		}
		return (super.action(evt, arg));
	}

	/*
	 * Update the table when the parents change.
	 */
	private void update_table_for_parents() {
		int i, j, k;
		if (all_variable_names.length == 1) {
			for (j = 0; j < all_variable_values[0].length; j++)
				fields[j][0].setText(String.valueOf(table_values[j]));
		} else if (all_variable_names.length == 2) {
			for (i = 0; i < all_variable_values[0].length; i++)
				for (j = 0; j < all_variable_values[1].length; j++)
					fields[i][j].setText(String.valueOf(table_values[i
							* all_variable_values[1].length + j]));
		} else {
			double value_set = 0.0;
			int parent_indexes[] = new int[all_variable_names.length];
			for (k = 0; k < parent_choices.length; k++)
				parent_indexes[k + 2] = parents_current_choices[k];
			for (i = 0; i < fields.length; i++) {
				for (j = 0; j < fields[i].length; j++) {
					try {
						value_set = (new Double(fields[i][j].getText())
								.doubleValue());
						parent_indexes[0] = i;
						parent_indexes[1] = j;
						table_values[get_location_from_indexes(parent_indexes)] = value_set;
					} catch (NumberFormatException e) {
					}
				}
			}
			for (k = 0; k < parent_choices.length; k++)
				parent_indexes[k + 2] = parent_choices[k].getSelectedIndex();
			for (i = 0; i < all_variable_values[0].length; i++) {
				for (j = 0; j < all_variable_values[1].length; j++) {
					parent_indexes[0] = i;
					parent_indexes[1] = j;
					fields[i][j]
							.setText(String
									.valueOf(table_values[get_location_from_indexes(parent_indexes)]));
				}
			}
			for (k = 0; k < parents_current_choices.length; k++)
				parents_current_choices[k] = parent_choices[k]
						.getSelectedIndex();
		}
	}

	/*
	 * Update the table values.
	 */
	private void update_table_values() {
		int i, j, k;
		if (all_variable_names.length == 1) {
			for (i = 0; i < fields.length; i++) {
				try {
					table_values[i] = (new Double(fields[i][0].getText())
							.doubleValue());
				} catch (NumberFormatException e) {
				}
			}
		} else if (all_variable_names.length == 2) {
			for (i = 0; i < fields.length; i++) {
				for (j = 0; j < fields[i].length; j++) {
					try {
						table_values[i * all_variable_values[1].length + j] = (new Double(
								fields[i][j].getText()).doubleValue());
					} catch (NumberFormatException e) {
					}
				}
			}
		} else {
			double value_set = 0.0;
			int parent_indexes[] = new int[all_variable_names.length];
			for (k = 0; k < parent_choices.length; k++)
				parent_indexes[k + 2] = parent_choices[k].getSelectedIndex();
			for (i = 0; i < fields.length; i++) {
				for (j = 0; j < fields[i].length; j++) {
					try {
						value_set = (new Double(fields[i][j].getText())
								.doubleValue());
						parent_indexes[0] = i;
						parent_indexes[1] = j;
						table_values[get_location_from_indexes(parent_indexes)] = value_set;
					} catch (NumberFormatException e) {
					}
				}
			}
		}
	}

	/*
	 * Determine the index of a given location.
	 */
	private int get_location_from_indexes(int indexes[]) {
		int pos = 0, jump = 1;
		for (int i = (all_variable_names.length - 1); i >= 0; i--) {
			pos += indexes[i] * jump;
			jump *= all_variable_values[i].length;
		}
		return (pos);
	}
}
