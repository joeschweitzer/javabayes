/**
 * JavaBayesHelpSystem.java
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

import javabayes.JavaBayes;

public class JavaBayesHelpMessages {
	// Static JavaBayes object; there must be a single JavaBayesHelpMessages
	static JavaBayes jb;

	// Constructor
	public static void insert(JavaBayes java_bayes) {
		jb = java_bayes;
	}

	// Basic method to display messages
	public static void show(String message) {
		jb.appendText(message);
	}

	// Help messages

	static final String version_number_message = "Version 0.346";

	public static final String about_message = new String("JavaBayes "
			+ version_number_message + "\n"
			+ "Copyright 1996 - 1997 Carnegie Mellon University \n"
			+ "Copyright 1998 - 2000 Fabio Gagliardi Cozman \n"
			+ "<fgcozman@usp.br>\n"
			+ "<http://www.cs.cmu.edu/~fgcozman/home.html>\n"
			+ "JavaBayes is a system for inferences with Bayesian \n"
			+ "networks entirely written in Java.\n"
			+ "More documentation at\n"
			+ "<http://www.cs.cmu.edu/~javabayes/>\n\n");

	public static final String start_message = new String(
			"JavaBayes starts in Move mode.\n"
					+ "To start editing networks, press the Create button and\n"
					+ "click on the JavaBayes editor, or load a network using\n"
					+ "the Network->Open menu.\n\n");

	static final String create_message = new String(
			"To create a new node, click the mouse button once\n"
					+ "on the area above.\n"
					+ "To connect two nodes, click on the parent node\n"
					+ "drag to the child node, and then release.\n"
					+ "To edit node attributes, click on Edit button.\n"
					+ "To move or delete nodes, click on appropriate button.\n\n");

	static final String move_message = new String(
			"To move a node, click on it and drag it to the new position.\n\n");

	static final String delete_message = new String(
			"To delete a node, click on it.\n"
					+ "To delete an arrow, click on the arrow's head.\n\n");

	static final String edit_message = new String(
			"To edit attributes of a node, click on it.\n\n");

	static final String observe_message = new String(
			"To observe a node, click on it.\n\n");

	static final String query_message = new String(
			"To query on a particular node, click on it.\n\n");

	// Error and exception messages

	static final String unexpected_end_of_input = new String(
			"Unable to complete load: Unexpected end of input!\n\n");

	static final String incorrect_file_format = new String(
			"Unable to complete load: Incorrect file format.\n\n");

	static final String unable_read_file = new String(
			"Unable to read file!\n\n");

	static final String unable_generate_parents_dialog = new String(
			"Unable to generate parent values dialog!\n\n");

	static final String duplicate_values = new String("Duplicate value!\n\n");

	static final String node_name_change_failed = new String(
			"Node name change failed.\n\n");

	static final String no_value_to_replace = new String(
			"No new value to replace!\n\n");

	static final String no_value_selected_to_replace = new String(
			"No value selected for replace!\n\n");

	static final String no_value_to_add = new String("No value to add!\n\n");

	static final String no_value_selected_to_delete = new String(
			"No value selected for delete!\n\n");

	static final String observe_error = new String(
			"No value selected for Observe!\n\n");

	static final String notnode = new String("Please click on a node.\n\n");

	static final String maxnodes = new String(
			"Reached limit on maximum number of nodes.\n\n");

	static final String selfarc = new String("Can not create arc to self.\n\n");

	static final String circular = new String(
			"Circular parent relations not allowed.\n\n");
}
