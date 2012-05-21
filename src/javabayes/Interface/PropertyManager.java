/**
 * PropertyManager.java
 * @author Fabio G. Cozman 
 * Copyright 1996 - 1999, Fabio G. Cozman,
 *          Carnergie Mellon University, Universidade de Sao Paulo
 * fgcozman@usp.br, http://www.cs.cmu.edu/~fgcozman/home.html
 *
 * The JavaBayes distribution is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, provided
 * that this notice and the name of the author appear in all copies.
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

import java.awt.TextField;
import java.util.Enumeration;
import java.util.Vector;

class PropertyManager {
	// The original properties.
	private Vector original_properties;
	// The vector holding a copy of the relevant properties.
	private Vector modified_properties;
	// A flag indicating whether the original properties are valid.
	private boolean are_original_properties_still_valid;
	// The index of the displayed property;
	private int displayed_property_index;
	// The text field that displays the property;
	private TextField text_field;

	/**
	 * Default constructor for PropertyManager.
	 */
	PropertyManager(Vector op, TextField tf) {
		String property;

		// Copy the inputs into internal variables.
		original_properties = op;
		text_field = tf;
		// Make the copy of the properties.
		modified_properties = new Vector();
		if (op != null) {
			for (Enumeration e = op.elements(); e.hasMoreElements();)
				modified_properties.addElement(e.nextElement());
		}
		are_original_properties_still_valid = true;
		// Now display if possible.
		if (modified_properties.size() > 0) {
			property = (String) (modified_properties.firstElement());
			text_field.setText(property);
			displayed_property_index = 0;
		} else
			displayed_property_index = -1;
	}

	/*
	 * Create a new property.
	 */
	void new_property() {
		String property;

		if (displayed_property_index != -1)
			property = (String) (modified_properties
					.elementAt(displayed_property_index));
		else
			property = "";
		if (!(property.equals(text_field.getText())))
			update_property();

		displayed_property_index = -1;
		text_field.setText("");
	}

	/*
	 * Go to the next property.
	 */
	void next_property() {
		String property;

		if (displayed_property_index != -1)
			property = (String) (modified_properties
					.elementAt(displayed_property_index));
		else
			property = "";
		if (!(property.equals(text_field.getText())))
			update_property();

		if (modified_properties.size() > 0) {
			displayed_property_index++;
			if (displayed_property_index >= modified_properties.size())
				displayed_property_index = 0;
			property = (String) (modified_properties
					.elementAt(displayed_property_index));
			text_field.setText(property);
		}
	}

	/*
	 * Modify a property when changes were detected.
	 */
	void update_property() {
		if (displayed_property_index != -1)
			modified_properties.removeElementAt(displayed_property_index);
		if (text_field.getText().equals("")) // Property was deleted.
			displayed_property_index = -1;
		else { // Property is new or modified.
			modified_properties.addElement(text_field.getText());
			displayed_property_index = modified_properties.indexOf(text_field
					.getText());
		}
		are_original_properties_still_valid = false;
	}

	/*
	 * Modify a property when the dialog exits.
	 */
	Vector update_property_on_exit() {
		String property;

		if (displayed_property_index != -1)
			property = (String) (modified_properties
					.elementAt(displayed_property_index));
		else
			property = "";
		if (!(property.equals(text_field.getText())))
			update_property();
		if (are_original_properties_still_valid == false)
			return (modified_properties);
		else
			return (null);
	}
}