/**
 * QuasiBayesNet.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javabayes.BayesianNetworks.BayesNet;
import javabayes.CredalSets.QBProbabilityFunction;
import javabayes.InterchangeFormat.IFException;
import javabayes.InterchangeFormat.InterchangeFormat;

/**************************************************************************/

public class QuasiBayesNet extends BayesNet {
	int global_neighborhood_type; // do not set this here; set by translate()
	double global_neighborhood_parameter; // do not set this here; set by
											// translate()

	final static String[] global_neighborhood_keywords = { "none",
			"credal-set", "constant-density-ratio", "epsilon-contaminated",
			"constant-density-bounded", "total-variation" };

	public final static int NO_CREDAL_SET = 0;
	final static int CREDAL_SET = 1;
	public final static int CONSTANT_DENSITY_RATIO = 2;
	public final static int EPSILON_CONTAMINATED = 3;
	public final static int CONSTANT_DENSITY_BOUNDED = 4;
	public final static int TOTAL_VARIATION = 5;

	/***************************************************************/
	/* Default constructor for a QuasiBayesian network */
	/***************************************************************/
	public QuasiBayesNet() {
		super();
		global_neighborhood_type = NO_CREDAL_SET;
		global_neighborhood_parameter = 0.0;
	}

	/***************************************************************/
	/* Simple constructor for a Quasi-Bayesian network: */
	/* just give it the */
	/* number of variables/functions and the name of the network. */
	/***************************************************************/
	public QuasiBayesNet(String n_n, int n_v, int n_f) {
		super(n_n, n_v, n_f);
		global_neighborhood_type = NO_CREDAL_SET;
		global_neighborhood_parameter = 0.0;
	}

	/***************************************************************/
	/* Simple constructor for a Quasi-Bayesian network: just give */
	/* it the name of network and properties */
	/***************************************************************/
	public QuasiBayesNet(String n_n, Vector p) {
		super(n_n, p);
		global_neighborhood_type = NO_CREDAL_SET;
		global_neighborhood_parameter = 0.0;
	}

	/***************************************************************/
	/* Simple constructor for a Quasi-Bayesian network: just give */
	/* it a Bayesian Network and it creates a new copy */
	/***************************************************************/
	public QuasiBayesNet(BayesNet bn) {
		super(bn);
		if (bn instanceof QuasiBayesNet) {
			global_neighborhood_type = ((QuasiBayesNet) bn).global_neighborhood_type;
			global_neighborhood_parameter = ((QuasiBayesNet) bn).global_neighborhood_parameter;
		}
	}

	/***************************************************************/
	/* Constructor for a Quasi-Bayesian network from a string */
	/***************************************************************/
	public QuasiBayesNet(String s) throws IFException {
		super(s);
	}

	/***************************************************************/
	/* Constructor for a Bayesian network from an input stream */
	/***************************************************************/
	public QuasiBayesNet(InputStream istream) throws IFException {
		super(istream);
	}

	/***************************************************************/
	/* Constructor for a Bayesian network from a URL */
	/***************************************************************/
	public QuasiBayesNet(URL context, String spec) throws IFException,
			IOException {
		super(context, spec);
	}

	/***************************************************************/
	/* Constructor for a Bayesian network from a URL */
	/***************************************************************/
	public QuasiBayesNet(URL url) throws IFException, IOException {
		super(url);
	}

	/***************************************************************/
	/* Method that translates the contents of a */
	/* IFBayesNet object into a */
	/* QuasiBayesNet object */
	/* (method works by overriding method in BayesNet class). */
	/* The method makes modifications to the basic objects */
	/* supported by the InterchangeFormat, so that the full */
	/* functionality of the BayesianNetworks package can be used. */
	/* As the InterchangeFormat evolves, probably some of the */
	/* objects created through extensions will be created directly */
	/* by the parser as it parses an InterchangeFormat stream. */
	/* Right now the extensions involve: */
	/* 1) Detecting observed variables */
	/* 2) Detecting explanation variables */
	/***************************************************************/
	@Override
	protected void translate(InterchangeFormat ifo) {
		QBConvertInterchangeFormat qbcbn = new QBConvertInterchangeFormat(ifo);
		name = qbcbn.get_name();
		properties = qbcbn.get_properties();
		probability_variables = qbcbn.get_probability_variables(this);
		probability_functions = qbcbn.get_probability_functions(this);

		// Process QuasiBayesNet properties
		process_properties();

		// Process ProbabilityVariable properties
		for (int i = 0; i < probability_variables.length; i++)
			process_probability_variable_properties(i);

		// Process ProbabilityFunction properties: create QB functions if
		// necessary
		for (int i = 0; i < probability_functions.length; i++)
			process_probability_function_properties(i);
	}

	/***************************************************************/
	/* Method that makes modifications to the QuasiBayesNet based */
	/* on its properties (works by overriding method in BayesNet) */
	/***************************************************************/
	@Override
	protected void process_properties() {
		boolean is_property_value_available;
		String property, property_value, keyword, token;
		StringTokenizer st;
		String delimiters = " \n\t\r\f";
		Enumeration e;
		Vector properties_to_remove = new Vector();

		// Go through the properties
		for (e = properties.elements(); e.hasMoreElements();) {
			property = (String) (e.nextElement());
			st = new StringTokenizer(property, delimiters);

			// Extension: global neighborhoods
			token = st.nextToken();
			keyword = global_neighborhood_keywords[CREDAL_SET];
			if (!token.equals(keyword))
				continue;

			// The credal-set property is removed
			properties_to_remove.addElement(property);

			// Cycle through keywords for global neighborhood type
			token = st.nextToken();
			is_property_value_available = false;
			for (int i = 2; i < 6; i++) {
				keyword = global_neighborhood_keywords[i];
				if (token.equals(keyword)) {
					global_neighborhood_type = i;
					is_property_value_available = true;
					break;
				}
			}

			// Get the property if necessary
			if (is_property_value_available) {
				property_value = st.nextToken();
				global_neighborhood_parameter = Double.valueOf(property_value)
						.doubleValue();
			}
		}

		for (e = properties_to_remove.elements(); e.hasMoreElements();) {
			property = (String) (e.nextElement());
			properties.removeElement(property);
		}
	}

	/**
	 * Indicate whether or not there are local credal sets defined in the
	 * network.
	 */
	public boolean are_local_credal_sets_present() {
		for (int i = 0; i < probability_functions.length; i++) {
			if (probability_functions[i] instanceof QBProbabilityFunction)
				return (true);
		}
		return (false);
	}

	/**
	 * Print method for a QuasiBayesNet object.
	 */
	@Override
	public void print(PrintStream out) {
		int i;
		String property;

		out.println("// Bayesian network ");
		if (name != null)
			out.print("network \"" + name + "\" {");
		if (probability_variables != null)
			out.print(" //" + probability_variables.length + " variables");
		if (probability_functions != null)
			out.print(" and " + probability_functions.length
					+ " probability distributions");

		out.println();

		if (global_neighborhood_type != NO_CREDAL_SET) {
			out.println("\tproperty \""
					+ global_neighborhood_keywords[CREDAL_SET] + " "
					+ global_neighborhood_keywords[global_neighborhood_type]
					+ " " + global_neighborhood_parameter + "\" ;");
		}

		if ((properties != null) && (properties.size() > 0)) {
			for (Enumeration e = properties.elements(); e.hasMoreElements();) {
				property = (String) (e.nextElement());
				out.println("\tproperty \"" + property + "\" ;");
			}
		}
		out.println("}");

		if (probability_variables != null)
			for (i = 0; i < probability_variables.length; i++)
				if (probability_variables[i] != null)
					probability_variables[i].print(out);
		if (probability_functions != null)
			for (i = 0; i < probability_functions.length; i++)
				if (probability_functions[i] != null)
					probability_functions[i].print(out);
	}

	/**
	 * Get the type of global neighborhood.
	 */
	public int get_global_neighborhood_type() {
		return (global_neighborhood_type);
	}

	/**
	 * Set the type of global neighborhood.
	 */
	public void set_global_neighborhood_type(int type) {
		global_neighborhood_type = type;
	}

	/**
	 * Get the parameter for the global neighborhood modeled by the network.
	 */
	public double get_global_neighborhood_parameter() {
		return (global_neighborhood_parameter);
	}

	/**
	 * Set the parameter for the global neighborhood modeled by the network.
	 */
	public void set_global_neighborhood_parameter(double p) {
		global_neighborhood_parameter = p;
	}

}

/*******************************************************************/

