/**
 * ProbabilityVariable.java
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

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

/*******************************************************************/

public class ProbabilityVariable extends DiscreteVariable {
	protected int type = ProbabilityVariable.CHANCE;

	protected int observed_index = BayesNet.INVALID_INDEX;
	protected int explanation_index = BayesNet.INVALID_INDEX;

	protected Vector properties;
	protected BayesNet bn;

	public final static int CHANCE = 0;
	public final static int TRANSPARENT = 1;

	final static String observed_property_name = "observed";
	final static String explanation_property_name = "explanation";

	/**
	 * Default constructor for a ProbabilityVariable.
	 */
	public ProbabilityVariable() {
		super();
	}

	/**
	 * Constructor for ProbabilityVariable.
	 */
	public ProbabilityVariable(BayesNet b_n, String n_vb, Vector p) {
		super(n_vb);
		properties = p;
		bn = b_n;
	}

	/**
	 * Constructor for ProbabilityVariable.
	 */
	public ProbabilityVariable(BayesNet b_n, String n_vb, int vi, String vl[],
			Vector p) {
		super(n_vb, vi, vl);
		properties = p;
		bn = b_n;
	}

	/**
	 * Constructor for ProbabilityVariable.
	 */
	public ProbabilityVariable(ProbabilityVariable pv) {
		super(pv);

		observed_index = pv.observed_index;
		explanation_index = pv.explanation_index;
		type = pv.type;

		properties = pv.properties;
		bn = pv.bn;
	}

	/**
	 * Constructor for ProbabilityVariable.
	 */
	public ProbabilityVariable(BayesNet b_n, ProbabilityVariable pv) {
		super(pv);

		observed_index = pv.observed_index;
		explanation_index = pv.explanation_index;
		type = pv.type;

		properties = pv.properties;
		bn = b_n;
	}

	/**
	 * Determine: 1) whether a variable is observed 2) whether a variable is a
	 * explanation variable
	 */
	void process_properties() {
		int index_of_observed_value, index_of_explanation_value;
		String pp, property, property_value, keyword;
		Vector properties_to_remove = new Vector();

		// Get all properties one by one
		for (Enumeration e = properties.elements(); e.hasMoreElements();) {
			pp = (String) (e.nextElement());
			property = pp.trim();
			// If the property is an "observed" property
			keyword = observed_property_name;
			if ((property.startsWith(keyword)) || (property.equals(keyword))) {
				properties_to_remove.addElement(pp);
				property_value = property.substring(keyword.length()).trim();
				observed_index = index_of_value(property_value);
				continue;
			}
			// If the property is a "explanation" property
			keyword = explanation_property_name;
			if ((property.startsWith(keyword)) || (property.equals(keyword))) {
				properties_to_remove.addElement(pp);
				property_value = property.substring(keyword.length()).trim();
				explanation_index = index_of_value(property_value);
				if (explanation_index == BayesNet.INVALID_INDEX)
					explanation_index = 0;
				continue;
			}
		}

		for (Enumeration e = properties_to_remove.elements(); e
				.hasMoreElements();) {
			property = (String) (e.nextElement());
			properties.removeElement(property);
		}
	}

	/**
	 * Save the contents of a ProbabilityVariable object into a PrintStream
	 * using the XMLBIF v0.3 format.
	 */
	public void save_xml_0_3(PrintStream out) {
		String property;

		if (this == null)
			return;
		out.println("<VARIABLE TYPE=\"nature\">");
		if (name != null)
			out.println("\t<NAME>" + name + "</NAME>");

		if (values != null) {
			for (int i = 0; i < values.length; i++)
				out.println("\t<OUTCOME>" + values[i] + "</OUTCOME>");
		}

		if (is_explanation())
			out.println("\t<PROPERTY>" + explanation_property_name + " "
					+ values[explanation_index] + "</PROPERTY>");

		if (is_observed())
			out.println("\t<PROPERTY>" + observed_property_name + " "
					+ values[observed_index] + "</PROPERTY>");

		if ((properties != null) && (properties.size() > 0)) {
			for (Enumeration e = properties.elements(); e.hasMoreElements();) {
				property = (String) (e.nextElement());
				out.println("\t<PROPERTY>" + property + "</PROPERTY>");
			}
		}
		out.println("</VARIABLE>\n");
	}

	/**
	 * Save the contents of a ProbabilityVariable object into a PrintStream.
	 */
	public void save_xml(PrintStream out) {
		String property;

		if (this == null)
			return;
		out.println("<VARIABLE>");
		if (name != null)
			out.println("\t<NAME>" + name + "</NAME>");

		if (values != null) {
			out.println("\t<TYPE>discrete</TYPE>");
			for (int i = 0; i < values.length; i++)
				out.println("\t<VALUE>" + values[i] + "</VALUE>");
		}

		if (is_explanation())
			out.println("\t<PROPERTY>" + explanation_property_name + " "
					+ values[explanation_index] + "</PROPERTY>");

		if (is_observed())
			out.println("\t<PROPERTY>" + observed_property_name + " "
					+ values[observed_index] + "</PROPERTY>");

		if ((properties != null) && (properties.size() > 0)) {
			for (Enumeration e = properties.elements(); e.hasMoreElements();) {
				property = (String) (e.nextElement());
				out.println("\t<PROPERTY>" + property + "</PROPERTY>");
			}
		}
		out.println("</VARIABLE>\n");
	}

	/**
	 * Print method for ProbabilityVariable.
	 */
	@Override
	public void print(PrintStream out) {
		String property;

		if (this == null)
			return;
		out.print("variable ");
		if (name != null)
			out.print(" \"" + name + "\" ");
		out.print("{");
		if (values != null) {
			out.println(" //" + values.length + " values");
			out.print("\ttype discrete[" + values.length + "] { ");
			for (int i = 0; i < values.length; i++)
				out.print(" \"" + values[i] + "\" ");
			out.println("};");
		}

		if (is_explanation())
			out.println("\tproperty \"" + explanation_property_name + " "
					+ values[explanation_index] + "\" ;");

		if (is_observed())
			out.println("\tproperty \"" + observed_property_name + " "
					+ values[observed_index] + "\" ;");

		if ((properties != null) && (properties.size() > 0)) {
			for (Enumeration e = properties.elements(); e.hasMoreElements();) {
				property = (String) (e.nextElement());
				out.println("\tproperty \"" + property + "\" ;");
			}
		}
		out.println("}");
	}

	/* *************************************************************** */
	/* Methods that allow basic manipulation of non-public variables */
	/* *************************************************************** */

	/**
	 * Get the type of the ProbabilityVariable.
	 */
	public int get_type() {
		return (type);
	}

	/**
	 * Indicate whether the current ProbabilityVariable is an explanatory
	 * variable or not.
	 */
	public boolean is_explanation() {
		return (explanation_index != BayesNet.INVALID_INDEX);
	}

	/**
	 * Indicate whether the current ProbabilityVariable has been observed or
	 * not.
	 */
	public boolean is_observed() {
		return (observed_index != BayesNet.INVALID_INDEX);
	}

	/**
	 * Set a value of the current ProbabilityVariable as observed.
	 * 
	 * @param v
	 *            Observed value.
	 */
	public void set_observed_value(String v) {
		int index = index_of_value(v);
		if (index == BayesNet.INVALID_INDEX)
			return;
		observed_index = index;
	}

	/**
	 * Set the variable as explanatory with a given value.
	 * 
	 * @param i
	 *            Index of the value that is assigned to the variable.
	 */
	public void set_explanation_value(int i) {
		explanation_index = i;
	}

	/**
	 * Add a property to the current ProbabilityVariable.
	 */
	public void add_property(String prop) {
		if (properties == null)
			properties = new Vector();
		properties.addElement(prop);
	}

	/**
	 * Remove a property from the current ProbabilityVariable.
	 */
	public void remove_property(String prop) {
		if (properties == null)
			return;
		properties.removeElement(prop);
	}

	/**
	 * Remove a property from the current ProbabilityVariable given the position
	 * of the property.
	 */
	public void remove_property(int i) {
		if (properties == null)
			return;
		properties.removeElementAt(i);
	}

	/**
	 * Get the index of the observed value.
	 */
	public int get_observed_index() {
		return (observed_index);
	}

	/**
	 * Get the index of the assigned value in the variable.
	 */
	public int get_explanation_index() {
		return (explanation_index);
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
	 * Get an Enumeration with the properties.
	 */
	public Enumeration get_enumerated_properties() {
		return (properties.elements());
	}

	/**
	 * Set the index of the variable.
	 */
	public void set_index(int ind) {
		index = ind;
	}

	/**
	 * Set the index of the current ProbabilityVariable as invalid (variable is
	 * not observed).
	 */
	public void set_invalid_index() {
		index = BayesNet.INVALID_INDEX;
	}

	/**
	 * Set the ProbabilityVariable as not observed..
	 */
	public void set_invalid_observed_index() {
		observed_index = BayesNet.INVALID_INDEX;
	}

	/**
	 * Set the type of the current ProbabilityVariable.
	 */
	public void set_type(int t) {
		type = t;
	}
}
