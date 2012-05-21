/**
 * BayesNet.java
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

package javabayes.BayesianNetworks;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javabayes.InterchangeFormat.IFException;
import javabayes.InterchangeFormat.InterchangeFormat;

/* ************************************************************************ */

public class BayesNet {
	protected String name;
	protected Vector properties;
	protected ProbabilityVariable probability_variables[];
	protected ProbabilityFunction probability_functions[];
	protected DiscreteFunction utility_function;

	public final static int INVALID_INDEX = -1;

	public final static int BIF = 1;
	public final static int XML = 2;
	public final static int BUGS = 3;

	/**
	 * Default constructor for a BayesNet.
	 */
	public BayesNet() {
	}

	/**
	 * Simple constructor for a BayesNet.
	 * 
	 * @param n_n
	 *            Name of the network.
	 * @param n_v
	 *            Number of variables in the network.
	 * @param n_f
	 *            Number of probability distributions in the network.
	 */
	public BayesNet(String n_n, int n_v, int n_f) {
		this();
		name = n_n;
		probability_variables = new ProbabilityVariable[n_v];
		probability_functions = new ProbabilityFunction[n_f];
	}

	/**
	 * Simple constructor for a BayesNet.
	 * 
	 * @param n_n
	 *            Name of network.
	 * @param p
	 *            Properties of the network.
	 */
	public BayesNet(String n_n, Vector p) {
		this();
		name = n_n;
		properties = p;
	}

	/**
	 * Simple constructor for a BayesNet; creates a copy of a given network.
	 * 
	 * @param bn
	 *            Network to be copied.
	 */
	public BayesNet(BayesNet bn) {
		this(bn.name, bn.probability_variables.length,
				bn.probability_functions.length);

		for (int i = 0; i < bn.probability_variables.length; i++)
			probability_variables[i] = bn.probability_variables[i];
		for (int i = 0; i < bn.probability_functions.length; i++)
			probability_functions[i] = bn.probability_functions[i];

		properties = bn.properties;
	}

	/**
	 * Construct a BayesNet from a textual description in a string.
	 */
	public BayesNet(String s) throws IFException {
		this();
		StringBufferInputStream istream = new StringBufferInputStream(s);
		// Read the BayesNet from the stream
		InterchangeFormat ifo = new InterchangeFormat(istream);
		ifo.CompilationUnit();
		// Transfer information from the parser
		translate(ifo);
	}

	/**
	 * Construct a BayesNet from a textual description in a stream.
	 */
	public BayesNet(InputStream istream) throws IFException {
		this();

		// Read the BayesNet from the stream
		InterchangeFormat ifo = new InterchangeFormat(istream);
		ifo.CompilationUnit();

		// Now transfer information from the parser
		translate(ifo);
	}

	/**
	 * Construct a BayesNet from a textual description in an URL.
	 * 
	 * @param context
	 *            The URL context as defined in the Java libraries.
	 * @param spec
	 *            The URL spec as defined in the Java libraries.
	 */
	public BayesNet(URL context, String spec) throws IFException, IOException {
		this();
		URL url = new URL(context, spec);
		InputStream istream = url.openStream();
		// Read the BayesNet from the stream
		InterchangeFormat ifo = new InterchangeFormat(istream);
		ifo.CompilationUnit();
		// Now transfer information from the parser
		translate(ifo);
		istream.close();
	}

	/**
	 * Construct a BayesNet from a textual description in an URL.
	 */
	public BayesNet(URL url) throws IFException, IOException {
		this();
		InputStream istream = url.openStream();
		// Read the BayesNet from the stream
		InterchangeFormat ifo = new InterchangeFormat(istream);
		ifo.CompilationUnit();
		// Now transfer information from the parser
		translate(ifo);
		istream.close();
	}

	/*
	 * Translate the contents of a IFBayesNet object into a BayesNet object.
	 * 
	 * This method makes modifications to the basic objects supported by the
	 * InterchangeFormat, so that the full functionality of the BayesianNetworks
	 * package can be used. As the InterchangeFormat evolves, probably some of
	 * the objects created through extensions will be created directly by the
	 * parser as it parses an InterchangeFormat stream. Right now the extensions
	 * involve: 1) Detecting observed variables. 2) Detecting explanation
	 * variables.
	 */
	protected void translate(InterchangeFormat ifo) {
		ConvertInterchangeFormat cbn = new ConvertInterchangeFormat(ifo);
		name = cbn.get_name();
		properties = cbn.get_properties();
		probability_variables = cbn.get_probability_variables(this);
		probability_functions = cbn.get_probability_functions(this);

		// Process BayesNet properties
		process_properties();

		// Process ProbabilityVariable properties
		for (int i = 0; i < probability_variables.length; i++)
			process_probability_variable_properties(i);

		// Process ProbabilityFunction properties
		for (int i = 0; i < probability_functions.length; i++)
			process_probability_function_properties(i);
	}

	/*
	 * Make modifications to a BayesNet based on the properties of the BayesNet.
	 */
	protected void process_properties() {
	}

	/*
	 * Process the properties of a ProbabilityVariable.
	 */
	protected void process_probability_variable_properties(int index) {
		probability_variables[index].process_properties();
	}

	/*
	 * Process the properties of a ProbabilityFunction.
	 */
	protected void process_probability_function_properties(int index) {
		probability_functions[index].process_properties();
	}

	/**
	 * Find the ProbabilityFunction that corresponds to a given
	 * ProbabilityVariable. Note: the index of a variable is used by the
	 * function, as it is the only reference to the variable that is guaranteed
	 * to identify the variable uniquely.
	 */
	public ProbabilityFunction get_function(ProbabilityVariable p_v) {
		for (int i = 0; i < probability_functions.length; i++)
			if (p_v.index == probability_functions[i].variables[0].index)
				return (probability_functions[i]);

		return (null);
	}

	/**
	 * Save a BayesNet object in a stream, in the BIF InterchangeFormat.
	 */
	public void save_bif(PrintStream out) {
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
	 * Save a BayesNet object in a stream for the EBayes engine.
	 */
	public void save_embayes(PrintStream out) {
		int i, j;
		out.println("import ebayes.data.*");
		out.println("class " + name + " extends BayesNet {");
		out.println("\tpublic " + name + "() {");
		out.println("\tsetName(\"" + name + "\");");

		for (i = 0; i < probability_variables.length; i++) {
			out.println("\tCategoricalVariable "
					+ probability_variables[i].name + " = ");
			out.println("\t\tnew CategoricalVariable(\""
					+ probability_variables[i].name + "\",");
			out.print("\t\t\tnew String[] {");
			for (j = 0; j < probability_variables[i].values.length; j++) {
				out.print("\"" + probability_variables[i].values[j] + "\"");
				if (j != (probability_variables[i].values.length - 1))
					out.print(",");
			}
			out.println("});\n");
		}
		out.println("\n\n");
		for (i = 0; i < probability_functions.length; i++) {
			out.println("\tCategoricalProbability p" + i + " = ");
			out.println("\t\tnew CategoricalProbability("
					+ probability_functions[i].variables[0].get_name() + ",");
			if (probability_functions[i].variables.length > 1) {
				out.print("\t\t\tnew CategoricalVariable[] {");
				for (j = 1; j < probability_functions[i].variables.length; j++) {
					out.print(probability_functions[i].variables[j].get_name());
					if (j != (probability_functions[i].variables.length - 1))
						out.print(", ");
				}
				out.println("}, ");
			}
			out.print("\t\t\tnew double[] {");
			for (j = 0; j < probability_functions[i].values.length; j++) {
				out.print(probability_functions[i].values[j]);
				if (j != (probability_functions[i].values.length - 1))
					out.print(", ");
			}
			out.println("});\n");
		}
		out.println("\tsetVariables(");
		out.println("\t\tnew CategoricalVariable[]");
		out.print("\t\t\t{");
		for (i = 0; i < probability_variables.length; i++) {
			out.print(probability_variables[i].get_name());
			if (i != (probability_variables.length - 1))
				out.print(", ");
		}
		out.println("} );\n");

		out.println("\tsetProbabilities(");
		out.println("\t\tnew CategoricalProbability[]");
		out.print("\t\t\t{");
		for (i = 0; i < probability_functions.length; i++) {
			out.print("p" + i);
			if (i != (probability_functions.length - 1))
				out.print(", ");
		}
		out.println("} );\n");

		out.println("\n}");
	}

	/**
	 * Save a BayesNet object in a stream, in the XMLBIF format version 0.3
	 * (most recent version).
	 */
	public void save_xml(PrintStream pstream) {
		int i;
		String property;

		// Heading for the file
		pstream.println("<?xml version=\"1.0\" encoding=\"US-ASCII\"?>\n\n");
		pstream.println("<!--");
		pstream.println("\tBayesian network in XMLBIF v0.3 (BayesNet Interchange Format)");
		pstream.println("\tProduced by JavaBayes (http://www.cs.cmu.edu/~javabayes/");
		pstream.println("\tOutput created " + (new Date()));
		pstream.println("-->\n\n\n");

		pstream.println("<!-- DTD for the XMLBIF 0.3 format -->");
		pstream.println("<!DOCTYPE BIF [\n"
				+ "\t<!ELEMENT BIF ( NETWORK )*>\n"
				+ "\t      <!ATTLIST BIF VERSION CDATA #REQUIRED>\n"
				+ "\t<!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n"
				+ "\t<!ELEMENT NAME (#PCDATA)>\n"
				+ "\t<!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n"
				+ "\t      <!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n"
				+ "\t<!ELEMENT OUTCOME (#PCDATA)>\n"
				+ "\t<!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n"
				+ "\t<!ELEMENT FOR (#PCDATA)>\n"
				+ "\t<!ELEMENT GIVEN (#PCDATA)>\n"
				+ "\t<!ELEMENT TABLE (#PCDATA)>\n"
				+ "\t<!ELEMENT PROPERTY (#PCDATA)>\n" + "]>\n\n");

		// Start of Bayes net
		pstream.println("<BIF VERSION=\"0.3\">");

		// Bayes net description
		pstream.println("<NETWORK>");
		if (name != null)
			pstream.println("<NAME>" + name + "</NAME>");
		if ((properties != null) && (properties.size() > 0)) {
			for (Enumeration e = properties.elements(); e.hasMoreElements();) {
				property = (String) (e.nextElement());
				pstream.println("\t<PROPERTY>" + property + "</PROPERTY>");
			}
		}
		pstream.println();

		// Variables
		pstream.println("<!-- Variables -->");
		if (probability_variables != null)
			for (i = 0; i < probability_variables.length; i++)
				if (probability_variables[i] != null)
					probability_variables[i].save_xml_0_3(pstream);
		pstream.println();

		// Probability distributions.
		pstream.println("<!-- Probability distributions -->");
		if (probability_functions != null)
			for (i = 0; i < probability_functions.length; i++)
				if (probability_functions[i] != null)
					probability_functions[i].save_xml_0_3(pstream);
		pstream.println();

		// End of Bayes net description.
		pstream.println("</NETWORK>");

		// End of Bayes net.
		pstream.println("</BIF>");
	}

	/**
	 * Save a BayesNet object in a stream, in the XMLBIF format version 0.2.
	 */
	public void save_xml_0_2(PrintStream pstream) {
		int i;
		String property;

		// Heading for the file
		pstream.println("<?XML VERSION=\"1.0\"?>\n\n");
		pstream.println("<!--");
		pstream.println("\tBayesian network in BIF (BayesNet Interchange Format)");
		pstream.println("\tProduced by JavaBayes (http://www.cs.cmu.edu/~javabayes/");
		pstream.println("\tOutput created " + (new Date()));
		pstream.println("-->\n\n\n");

		pstream.println("<!-- DTD for the BIF format -->");
		pstream.println("<!DOCTYPE BIF [\n"
				+ "\t<!ELEMENT BIF ( NETWORK )*>\n"
				+ "\t<!ELEMENT PROPERTY (#PCDATA)>\n"
				+ "\t<!ELEMENT TYPE (#PCDATA)>\n"
				+ "\t<!ELEMENT VALUE (#PCDATA)>\n"
				+ "\t<!ELEMENT NAME (#PCDATA)>\n"
				+ "\t<!ELEMENT NETWORK\n"
				+ "\t    ( NAME, ( PROPERTY | VARIABLE | PROBABILITY )* )>\n"
				+ "\t<!ELEMENT VARIABLE ( NAME, TYPE, ( VALUE |  PROPERTY )* ) >\n"
				+ "\t<!ELEMENT PROBABILITY\n"
				+ "\t    ( FOR | GIVEN | TABLE | ENTRY | DEFAULT | PROPERTY )* >\n"
				+ "\t<!ELEMENT FOR (#PCDATA)>\n"
				+ "\t<!ELEMENT GIVEN (#PCDATA)>\n"
				+ "\t<!ELEMENT TABLE (#PCDATA)>\n"
				+ "\t<!ELEMENT DEFAULT (TABLE)>\n"
				+ "\t<!ELEMENT ENTRY ( VALUE* , TABLE )>\n" + "]>\n\n");

		// Start of Bayes net
		pstream.println("<BIF>");

		// Bayes net description
		pstream.println("<NETWORK>");
		if (name != null)
			pstream.println("<NAME>" + name + "</NAME>");
		if ((properties != null) && (properties.size() > 0)) {
			for (Enumeration e = properties.elements(); e.hasMoreElements();) {
				property = (String) (e.nextElement());
				pstream.println("\t<PROPERTY>" + property + "</PROPERTY>");
			}
		}
		pstream.println();

		// Variables
		pstream.println("<!-- Variables -->");
		if (probability_variables != null)
			for (i = 0; i < probability_variables.length; i++)
				if (probability_variables[i] != null)
					probability_variables[i].save_xml(pstream);
		pstream.println();

		// Probability distributions.
		pstream.println("<!-- Probability distributions -->");
		if (probability_functions != null)
			for (i = 0; i < probability_functions.length; i++)
				if (probability_functions[i] != null)
					probability_functions[i].save_xml(pstream);
		pstream.println();

		// End of Bayes net description.
		pstream.println("</NETWORK>");

		// End of Bayes net.
		pstream.println("</BIF>");
	}

	/**
	 * Save a BayesNet object into a stream, in the BUGS format.
	 */
	public void save_bugs(PrintStream pstream) {
		SaveBugs sb = new SaveBugs(this);
		sb.save(pstream);
	}

	/**
	 * Get all the evidence contained in the network variables.
	 */
	public String[][] get_all_evidence() {
		int i, j, aux;
		ProbabilityVariable pv;
		Vector evs = new Vector();
		Enumeration e;
		String all_evs[][] = null;

		for (i = 0; i < probability_variables.length; i++) {
			pv = probability_variables[i];
			if (pv.observed_index != BayesNet.INVALID_INDEX)
				evs.addElement(pv);
		}

		all_evs = new String[evs.size()][];
		for (i = 0; i < all_evs.length; i++)
			all_evs[i] = new String[2];

		j = 0;
		for (e = evs.elements(); e.hasMoreElements();) {
			pv = (ProbabilityVariable) (e.nextElement());
			all_evs[j][0] = pv.name;
			aux = pv.observed_index;
			all_evs[j][1] = pv.values[aux];
		}

		return (all_evs);
	}

	/**
	 * Determine the position of a variable given its name.
	 */

	public int index_of_variable(String n_vb) {
		int i;
		for (i = 0; i < probability_variables.length; i++) {
			if (probability_variables[i].name.equals(n_vb))
				return (i);
		}
		return (-1); // Returns -1 if name is not valid!
	}

	/**
	 * Print a BayesNet in the standard output.
	 */
	public void print() {
		print(System.out);
	}

	/**
	 * Print a BayesNet in a given stream.
	 */
	public void print(PrintStream out) {
		save_bif(out);
	}

	/* *************************************************************** *
	 * Methods that allow basic manipulation of non-public variables *
	 * ***************************************************************
	 */

	/**
	 * Get the name of the network.
	 */
	public String get_name() {
		return (name);
	}

	/**
	 * Set the name of the network.
	 */
	public void set_name(String n) {
		name = n;
	}

	/**
	 * Get the properties.
	 */
	public Vector get_properties() {
		return (properties);
	}

	/**
	 * Set the properties.
	 */
	public void set_properties(Vector prop) {
		properties = prop;
	}

	/**
	 * Add a property.
	 */
	public void add_property(String prop) {
		if (properties == null)
			properties = new Vector();
		properties.addElement(prop);
	}

	/**
	 * Remove a property.
	 */
	public void remove_property(String prop) {
		properties.removeElement(prop);
	}

	/**
	 * Remove a property.
	 */
	public void remove_property(int i) {
		properties.removeElementAt(i);
	}

	/**
	 * Get the number of variables in the network.
	 */
	public int number_variables() {
		if (probability_variables == null)
			return (BayesNet.INVALID_INDEX);
		return (probability_variables.length);
	}

	/**
	 * Get the number of distributions in the network.
	 */
	public int number_probability_functions() {
		if (probability_functions == null)
			return (BayesNet.INVALID_INDEX);
		return (probability_functions.length);
	}

	/**
	 * Get the probability variable at a given index.
	 */
	public ProbabilityVariable get_probability_variable(int index) {
		if (index <= probability_variables.length)
			return (probability_variables[index]);
		else
			return (null);
	}

	/**
	 * Get the probability function at a given index.
	 */
	public ProbabilityFunction get_probability_function(int index) {
		if (index <= probability_functions.length)
			return (probability_functions[index]);
		else
			return (null);
	}

	/**
	 * Get the probability variables.
	 */
	public ProbabilityVariable[] get_probability_variables() {
		return (probability_variables);
	}

	/**
	 * Get the probability functions.
	 */
	public ProbabilityFunction[] get_probability_functions() {
		return (probability_functions);
	}

	/**
	 * Get the utility function.
	 */
	public DiscreteFunction get_utility_function() {
		return (utility_function);
	}

	/**
	 * Set a probability variable given its constituents.
	 */
	public void set_probability_variable(int index, String name, String v[],
			Vector vec) {
		if (index <= probability_variables.length) {
			probability_variables[index] = new ProbabilityVariable(this, name,
					index, v, vec);
		}
	}

	/**
	 * Set a probability function given its constituents.
	 */
	public void set_probability_function(int index,
			ProbabilityVariable[] variables, double values[], Vector vec) {
		if (index <= probability_functions.length) {
			probability_functions[index] = new ProbabilityFunction(this,
					variables, values, vec);
		}
	}

	/**
	 * Set a probability variable given its index.
	 */
	public void set_probability_variable(int index, ProbabilityVariable p_v) {
		p_v.bn = this;
		p_v.index = index;
		probability_variables[index] = p_v;
	}

	/**
	 * Set a probability variable given its index.
	 */
	public void set_probability_function(int index, ProbabilityFunction p_f) {
		p_f.bn = this;
		probability_functions[index] = p_f;
	}

	/**
	 * Set the vector of probability variables.
	 */
	public void set_probability_variables(ProbabilityVariable pvs[]) {
		probability_variables = pvs;
	}

	/**
	 * Set the vector of probability functions.
	 */
	public void set_probability_functions(ProbabilityFunction pfs[]) {
		probability_functions = pfs;
	}
}
