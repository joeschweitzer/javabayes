/**
 * IFProbabilityVariable.java
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

package javabayes.InterchangeFormat;

import java.util.Vector;

/************************************************************
 * Auxiliar class that stores the temporary information * about a
 * ProbabilityVariable. *
 ************************************************************/

public class IFProbabilityVariable {
	String name;
	Vector properties;
	String values[];

	public void set_name(String n) {
		name = n;
	}

	public void set_properties(Vector p) {
		properties = p;
	}

	public void set_values(String vs[]) {
		values = vs;
	}

	public String get_name() {
		return (name);
	}

	public Vector get_properties() {
		return (properties);
	}

	public String[] get_values() {
		return (values);
	}
}