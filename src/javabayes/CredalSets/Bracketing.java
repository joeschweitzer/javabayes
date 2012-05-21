/**
 * Bracketing.java
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

package javabayes.CredalSets;

/**
 * Perform bisection to find the root of a function (described by an object that
 * implements the MappingDouble interface), which must lie between x1 and x2.
 * The root is refined until its accuracy is +/- x_accuracy.
 */

class Bracketing {

	int status;

	private final static int MAXIMUM_ITERATIONS = 40;

	private final static int ERROR = -1;
	private final static int TOO_MANY_BISECTIONS = 0;
	private final static int EXACT_ROOT_FOUND = 1;
	private final static int APPROXIMATE_ROOT_FOUND = 2;

	/**
	 * Perform bisection.
	 */
	double perform(MappingDouble function, double x1, double x2,
			double x_accuracy) {
		return (perform(function, 0, x1, x2, x_accuracy));
	}

	/**
	 * Perform bisection.
	 */
	double perform(MappingDouble function, int function_type, double x1,
			double x2, double x_accuracy) {
		int j;
		double f1, f2;
		double dx, x_middle, current_solution_point;

		// Initialize variables with the function values at endpoints
		f1 = function.map(function_type, x1);
		f2 = function.map(function_type, x2);

		// Check whether endpoints are solution
		if (f1 == 0.0) {
			status = EXACT_ROOT_FOUND;
			return (x1);
		}

		if (f2 == 0.0) {
			status = EXACT_ROOT_FOUND;
			return (x2);
		}

		// Error: both endpoints have same sign
		if ((f1 * f2) > 0.0) {
			status = ERROR;
			return (0.0);
		}

		// Bisection goes from x (where f(x)<=0) to x + dx
		if (f1 < 0.0) {
			dx = x2 - x1;
			current_solution_point = x1;
		} else {
			dx = x1 - x2;
			current_solution_point = x2;
		}

		// Bisection loop
		for (j = 1; j <= MAXIMUM_ITERATIONS; j++) {
			dx *= 0.5;
			x_middle = current_solution_point + dx;
			f2 = function.map(function_type, x_middle);
			if (f2 <= 0.0)
				current_solution_point = x_middle;
			// Check whether stop conditions are met
			if (f2 == 0.0) {
				status = EXACT_ROOT_FOUND;
				return (current_solution_point);
			}
			if (Math.abs(dx) < x_accuracy) {
				status = APPROXIMATE_ROOT_FOUND;
				return (current_solution_point);
			}
		}

		status = TOO_MANY_BISECTIONS;
		return (0.0);
	}
}
