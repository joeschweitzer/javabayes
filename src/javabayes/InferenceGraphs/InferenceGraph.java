/**
 * InferenceGraph.java
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
import java.io.PrintStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javabayes.BayesianInferences.Explanation;
import javabayes.BayesianNetworks.BayesNet;
import javabayes.BayesianNetworks.DiscreteVariable;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;
import javabayes.InterchangeFormat.IFException;
import javabayes.QuasiBayesianInferences.QBExpectation;
import javabayes.QuasiBayesianInferences.QBInference;
import javabayes.QuasiBayesianNetworks.QuasiBayesNet;

/*
 * An InferenceGraph contains all the information about the graphical    
 * structure of a Bayesian network. The information that is specific to  
 * the network, such as name, presence of credal sets, etc, is stored at 
 * the variable qbn.                                                     
 * Notes:                                                                
 * 1) qbn is a QuasiBayesNet, either holding a BayesNet or a      
 *    QuasiBayesNet,                                            
 * 2) All the ProbabilityVariable and ProbabilityFunction objects       
 *    of bn need not correspond to the nodes in the InferenceGraph.      
 * 3) Note that the QuasiBayesNet stored at the                   
 *    InferenceGraph is a copy of the original inserted BayesNet,
 *    not just a reference to it.                                        
 */

public class InferenceGraph {
	QuasiBayesNet qbn;
	QBInference qbi;
	QBExpectation qbe;

	Vector nodes = new Vector();

	private final String defaultBayesNetName = "InternalNetwork";

	public final static int MARGINAL_POSTERIOR = 1;
	public final static int EXPECTATION = 2;
	public final static int EXPLANATION = 3;
	public final static int FULL_EXPLANATION = 4;
	public final static int SENSITIVITY_ANALYSIS = 5;

	public final static int NO_CREDAL_SET = QuasiBayesNet.NO_CREDAL_SET;
	public final static int CONSTANT_DENSITY_RATIO = QuasiBayesNet.CONSTANT_DENSITY_RATIO;
	public final static int EPSILON_CONTAMINATED = QuasiBayesNet.EPSILON_CONTAMINATED;
	public final static int CONSTANT_DENSITY_BOUNDED = QuasiBayesNet.CONSTANT_DENSITY_BOUNDED;
	public final static int TOTAL_VARIATION = QuasiBayesNet.TOTAL_VARIATION;

	/*
	 * Default constructor for an InferenceGraph.
	 */
	public InferenceGraph() {
		qbn = new QuasiBayesNet(defaultBayesNetName, 0, 0);
	}

	/*
	 * Simple constructor for an InferenceGraph.
	 */
	public InferenceGraph(BayesNet b_n) {
		qbn = new QuasiBayesNet(b_n);
		convert_bayes_net();
	}

	/*
	 * Constructor for an InferenceGraph.
	 */
	public InferenceGraph(String filename) throws IOException, IFException {
		qbn = new QuasiBayesNet(new java.io.DataInputStream(
				new java.io.FileInputStream(filename)));
		convert_bayes_net();
	}

	/*
	 * Constructor for an InferenceGraph.
	 */
	public InferenceGraph(URL url) throws IOException, IFException {
		qbn = new QuasiBayesNet(url);
		convert_bayes_net();
	}

	/*
	 * Get the contents of the graph.
	 */
	public QuasiBayesNet get_bayes_net() {
		return (convert_graph());
	}

	/*
	 * Convert a QuasiBayesNet object to the InferenceGraph structure; returns
	 * true if the conversion is successful.
	 */
	boolean convert_bayes_net() {
		ProbabilityVariable pv = null;
		ProbabilityFunction pf = null;

		for (int i = 0; i < qbn.number_variables(); i++) {
			pv = qbn.get_probability_variable(i);
			pf = null;
			for (int j = 0; j < qbn.number_probability_functions(); j++) {
				pf = qbn.get_probability_function(j);
				if (pf.get_variable(0) == pv)
					break;
			}
			// The variable does not have a corresponding function
			if (pf == null)
				return (false);

			nodes.addElement(new InferenceGraphNode(this, pv, pf));
		}
		generate_parents_and_children();

		return (true);
	}

	/*
	 * Generate the parents and children for the nodes.
	 */
	private void generate_parents_and_children() {
		int i, j;
		DiscreteVariable variables[];
		ProbabilityFunction pf;
		InferenceGraphNode base_node, node;
		Enumeration e;

		for (e = nodes.elements(); e.hasMoreElements();) {
			base_node = (InferenceGraphNode) (e.nextElement());

			pf = base_node.pf;
			variables = pf.get_variables();

			for (i = 1; i < variables.length; i++) {
				node = get_node(variables[i]);
				if (node == null)
					continue;
				base_node.parents.addElement(node);
				node.children.addElement(base_node);
			}
		}
	}

	/*
	 * Get the node corresponding to a given variable.
	 */
	private InferenceGraphNode get_node(DiscreteVariable dv) {
		InferenceGraphNode node;
		for (Enumeration e = nodes.elements(); e.hasMoreElements();) {
			node = (InferenceGraphNode) e.nextElement();
			if (node.pv == dv)
				return (node);
		}
		return (null);
	}

	/*
	 * Convert the InferenceGraph structure to a QuasiBayesNet object.
	 */
	QuasiBayesNet convert_graph() {
		int i;
		Enumeration e;
		InferenceGraphNode node;

		// Create the arrays of variables and functions
		ProbabilityVariable pvs[] = new ProbabilityVariable[nodes.size()];
		ProbabilityFunction pfs[] = new ProbabilityFunction[nodes.size()];

		// Insert the empty arrays
		qbn.set_probability_variables(pvs);
		qbn.set_probability_functions(pfs);

		// Collect all variables and functions in the nodes
		// into the new QuasiBayesNet
		for (i = 0, e = nodes.elements(); e.hasMoreElements(); i++) {
			node = (InferenceGraphNode) (e.nextElement());
			node.update_position();
			qbn.set_probability_variable(i, node.pv);
			qbn.set_probability_function(i, node.pf);
		}

		return (qbn);
	}

	/*
	 * Generate a valid name for a new variable.
	 */
	private String generate_name(int i) {
		InferenceGraphNode no;

		// generate names of the form a..z, a1..z1, a2..z2, etc.
		char namec = (char) ('a' + i % 26);
		int suffix = i / 26;
		String name;
		if (suffix > 0)
			name = new String("" + namec + suffix);
		else
			name = new String("" + namec);
		// check whether there is a variable with this name
		for (Enumeration e = nodes.elements(); e.hasMoreElements();) {
			no = (InferenceGraphNode) (e.nextElement());
			if (no.get_name().equals(name))
				return (generate_name(i + 1));
		}
		return (name);
	}

	/* *************** PUBLIC METHODS ********************* */

	/**
	 * Get the name of the network.
	 */
	public String get_name() {
		return (qbn.get_name());
	}

	/**
	 * Set the name of the network.
	 */
	public void set_name(String n) {
		qbn.set_name(n);
	}

	/**
	 * Get the properties of the network.
	 */
	public Vector get_network_properties() {
		return (qbn.get_properties());
	}

	/**
	 * Set the properties of the network.
	 */
	public void set_network_properties(Vector prop) {
		qbn.set_properties(prop);
	}

	/**
	 * Get the type of global neighborhood modeled by the network.
	 */
	public int get_global_neighborhood_type() {
		return (qbn.get_global_neighborhood_type());
	}

	/**
	 * Set the global neighborhood type.
	 */
	public void set_global_neighborhood(int type) {
		qbn.set_global_neighborhood_type(type);
	}

	/**
	 * Get the parameter for the global neighborhood modeled by the network.
	 */
	public double get_global_neighborhood_parameter() {
		return (qbn.get_global_neighborhood_parameter());
	}

	/**
	 * Set the parameter for the global neighborhood modeled by the network.
	 */
	public void set_global_neighborhood_parameter(double parameter) {
		qbn.set_global_neighborhood_parameter(parameter);
	}

	/**
	 * Remove a property from the network.
	 */
	public void remove_network_property(int index) {
		qbn.remove_property(index);
	}

	/**
	 * Add a property to the network.
	 */
	public void add_network_property(String prop) {
		qbn.add_property(prop);
	}

	/**
	 * Determine whether or not a name is valid and/or repeated.
	 */
	public String check_name(String n) {
		InferenceGraphNode no;
		String nn = validate_value(n);
		for (Enumeration e = nodes.elements(); e.hasMoreElements();) {
			no = (InferenceGraphNode) (e.nextElement());
			if (no.get_name().equals(nn))
				return (null);
		}
		return (nn);
	}

	/**
	 * Check whether a string is a valid name.
	 */
	public String validate_value(String value) {
		StringBuffer str = new StringBuffer(value);
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ' ')
				str.setCharAt(i, '_');
		}
		return str.toString();
	}

	/**
	 * Print the QuasiBayesNet.
	 */
	public void print_bayes_net(PrintStream out) {
		QuasiBayesNet qb_n = get_bayes_net();
		qb_n.print(out);
	}

	/**
	 * Print information about a posterior marginal for the Bayesian network
	 * into the given PrintStream.
	 * 
	 * @param queried_variable
	 *            indicates the variable of interest.
	 * @param show_bucket_tree
	 *            determines whether or not to present a description of the
	 *            BucketTree.
	 */
	public void print_marginal(PrintStream pstream, String queried_variable,
			boolean do_compute_clusters, boolean show_bucket_tree) {
		if ((do_compute_clusters == false) || (qbi == null)
				|| (qbi.areClustersProduced() == false))
			qbi = new QBInference(get_bayes_net(), do_compute_clusters);
		qbi.inference(queried_variable);
		qbi.print(pstream, show_bucket_tree);
	}

	/**
	 * Reset the QBInference.
	 */
	public void reset_marginal() {
		qbi = null;
	}

	/**
	 * Print information about a posterior expectation for the Bayesian network
	 * into the given PrintStream.
	 * 
	 * @param queried_variable
	 *            indicates the variable of interest.
	 * @param show_bucket_tree
	 *            determines whether or not to present a description of the
	 *            BucketTree.
	 */
	public void print_expectation(PrintStream pstream, String queried_variable,
			boolean do_compute_clusters, boolean show_bucket_tree) {
		if ((do_compute_clusters == false) || (qbe == null)
				|| (qbi.areClustersProduced() == false))
			qbe = new QBExpectation(get_bayes_net(), do_compute_clusters);
		qbe.expectation(queried_variable);
		qbe.print(pstream, show_bucket_tree);
	}

	/**
	 * Reset the QBExpectation.
	 */
	public void reset_expectation() {
		qbe = null;
	}

	/**
	 * Print information about an explanation for the Bayesian network into the
	 * given PrintStream.
	 * 
	 * @param show_bucket_tree
	 *            determines whether or not to present a description of the
	 *            BucketTree.
	 */
	public void print_explanation(PrintStream pstream, boolean show_bucket_tree) {
		Explanation ex = new Explanation(get_bayes_net());
		ex.explanation();
		ex.print(pstream, show_bucket_tree);
	}

	/**
	 * Print information about a full explanation for the Bayesian network into
	 * the given PrintStream.
	 * 
	 * @param show_bucket_tree
	 *            determines whether or not to present a description of the
	 *            BucketTree.
	 */
	public void print_full_explanation(PrintStream pstream,
			boolean show_bucket_tree) {
		Explanation fex = new Explanation(get_bayes_net());
		fex.full_explanation();
		fex.print(pstream, show_bucket_tree);
	}

	/**
	 * Print the metrics for sensitivity analysis of the Bayesian network into
	 * the given PrintStream.
	 */
	public void print_sensitivity_analysis(PrintStream pstream) {
		// SensitivityAnalysis sa = new SensitivityAnalysis( get_bayes_net() );
		// sa.compute(queried_variable);
		// sa.print(pstream);
		/*** FOR NOW: ***/
		System.out.print("HEY! Sensitivity analysis not implemented yet!");
	}

	/**
	 * Save the Bayesian network into a PrintStream in the BIF
	 * InterchangeFormat.
	 */
	public void save_bif(PrintStream out) {
		QuasiBayesNet qb_n = get_bayes_net();
		qb_n.save_bif(out);
	}

	/**
	 * Save the Bayesian network into a PrintStream in the XML
	 * InterchangeFormat.
	 */
	public void save_xml(PrintStream out) {
		QuasiBayesNet qb_n = get_bayes_net();
		qb_n.save_xml(out);
	}

	/**
	 * Save the Bayesian networks in BUGS format into a PrintStream.
	 */
	public void save_bugs(PrintStream out) {
		QuasiBayesNet qb_n = get_bayes_net();
		qb_n.save_bugs(out);
	}

	/**
	 * Print method for an InferenceGraph
	 */
	public void print() {
		print(System.out);
	}

	/**
	 * Print method for an InferenceGraph
	 */
	public void print(PrintStream out) {
		QuasiBayesNet qb_n = get_bayes_net();
		qb_n.print(out);
	}

	/**
	 * Get the nodes in the network.
	 */
	public Vector get_nodes() {
		return (nodes);
	}

	/**
	 * Get the nodes in the network as an Enumeration object.
	 */
	public Enumeration elements() {
		return (nodes.elements());
	}

	/**
	 * Get the number of variables in the network
	 */
	public int number_nodes() {
		return (nodes.size());
	}

	/**
	 * Create a new node in the network.
	 */
	public void create_node(int x, int y) {
		Point p = new Point(x, y);
		String n = generate_name(nodes.size());
		nodes.addElement(new InferenceGraphNode(this, n, p));

		// Synchronize the QuasiBayesNet object and the graph.
		convert_graph();
	}

	/**
	 * Create an arc from parent to child.
	 */
	public boolean create_arc(InferenceGraphNode parent,
			InferenceGraphNode child) {
		// Check whether the given parent is already a parent of the
		// given child.
		for (Enumeration e = child.parents.elements(); e.hasMoreElements();) {
			if (parent == ((InferenceGraphNode) (e.nextElement())))
				return (false);
		}

		// First put child into the children of parent
		parent.children.addElement(child);
		// Second put parent into the parents of child
		child.parents.addElement(parent);

		// The parent is not further affected by the arc.
		// The child must have its ProbabilityFunction
		// object updated.
		child.init_dists();

		// Synchronize the QuasiBayesNet object and the graph.
		convert_graph();

		// Return true.
		return (true);
	}

	/**
	 * Delete a node in the network.
	 */
	public void delete_node(InferenceGraphNode node) {
		Enumeration e;
		InferenceGraphNode parent, child;

		// First, remove node from all its childrem
		for (e = node.children.elements(); e.hasMoreElements();) {
			child = (InferenceGraphNode) (e.nextElement());
			child.parents.removeElement(node);
			child.init_dists();
		}

		// Second remove parent into the parents of child
		for (e = node.parents.elements(); e.hasMoreElements();) {
			parent = (InferenceGraphNode) (e.nextElement());
			parent.children.removeElement(node);
		}

		// Third remove the node itself
		nodes.removeElement(node);

		// Synchronize the QuasiBayesNet object and the graph.
		convert_graph();
	}

	/**
	 * Delete the arc from parent to child.
	 */
	public void delete_arc(InferenceGraphNode parent, InferenceGraphNode child) {
		// First remove child into the children of parent
		parent.children.removeElement(child);
		// Second remove parent into the parents of child
		child.parents.removeElement(parent);

		// The parent is not further affected by the arc.
		// The child must have its ProbabilityFunction
		// object updated.
		child.init_dists();

		// Synchronize the QuasiBayesNet object and the graph.
		convert_graph();
	}

	/**
	 * Determines whether the connection of bottom_node to head_node would cause
	 * the network to have a cycle.
	 */
	public boolean hasCycle(InferenceGraphNode bottom_node,
			InferenceGraphNode head_node) {
		Vector children;
		Enumeration e;
		InferenceGraphNode next_node, child_node;

		// Array with enough space to have all nodes
		InferenceGraphNode listed_nodes[] = new InferenceGraphNode[nodes.size()];

		// Hashtable for efficient lookup of already listed nodes
		Hashtable hashed_nodes = new Hashtable();

		// Index of last node in listed_nodes
		int last_listed_node_index = 0;

		// Initialize: head_node is marked and inserted
		int current_listed_node_index = 0;
		listed_nodes[0] = head_node;
		hashed_nodes.put(head_node.pv.get_name(), head_node);

		// Now expand for children until no more children, or
		// when a child is equal to bottom_node
		while (current_listed_node_index <= last_listed_node_index) {
			// Select the next node to be expanded
			next_node = listed_nodes[current_listed_node_index];
			// Update the index that indicates nodes to be expanded
			current_listed_node_index++;

			// Get all children of the node being expanded
			children = next_node.children;
			// Expand the node: put all its children into list
			for (e = children.elements(); e.hasMoreElements();) {
				child_node = (InferenceGraphNode) (e.nextElement());
				if (child_node == bottom_node) // Cycle is detected
					return (true);
				if (!hashed_nodes.containsKey(child_node.pv.get_name())) {
					hashed_nodes.put(child_node.pv.get_name(), child_node);
					last_listed_node_index++;
					listed_nodes[last_listed_node_index] = child_node;
				}
			}
		}
		return (false);
	}

	/**
	 * Change the values of a variable. Note that, if the number of new values
	 * is different from the number of current values, this operation resets the
	 * probability values of the variable and all its children.
	 */
	public void change_values(InferenceGraphNode node, String values[]) {
		InferenceGraphNode cnode;
		Vector children;
		Enumeration e;

		if (node.pv.number_values() == values.length) {
			node.pv.set_values(values);
			return;
		}

		node.pv.set_values(values);
		node.init_dists();

		children = node.get_children();
		for (e = children.elements(); e.hasMoreElements();) {
			cnode = (InferenceGraphNode) (e.nextElement());
			cnode.init_dists();
		}

		// Synchronize the QuasiBayesNet object and the graph.
		convert_graph();
	}

	/**
	 * Set a value for the position of the node.
	 */
	public void set_pos(InferenceGraphNode node, Point position) {
		node.pos = position;
		convert_graph();
	}
}
