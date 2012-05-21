/**
 * ClearDialog.java
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

import java.awt.Dialog;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;

import javabayes.JavaBayes;

public class ClearDialog extends Dialog {
	JavaBayes jb;

	public ClearDialog(Frame parent, JavaBayes java_bayes, String title,
			boolean modal) {
		super(parent, title, true);
		jb = java_bayes;

		// {{INIT_CONTROLS
		setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		addNotify();
		resize(insets().left + insets().right + 295, insets().top
				+ insets().bottom + 92);
		yesButton = new java.awt.Button("Yes");
		yesButton.reshape(insets().left + 51, insets().top + 20, 60, 40);
		add(yesButton);
		noButton = new java.awt.Button("No");
		noButton.reshape(insets().left + 165, insets().top + 20, 60, 40);
		add(noButton);
		setResizable(false);
		// }}

	}

	@Override
	public void show() {
		Rectangle bounds = getParent().bounds();
		Rectangle abounds = bounds();

		move(bounds.x + (bounds.width - abounds.width) / 2, bounds.y
				+ (bounds.height - abounds.height) / 2);

		super.show();
	}

	@Override
	public boolean handleEvent(Event event) {
		if (event.target == noButton && event.id == Event.ACTION_EVENT) {
			dispose();
		}
		if (event.target == yesButton && event.id == Event.ACTION_EVENT) {
			jb.clear();
			dispose();
		}
		return super.handleEvent(event);
	}

	// {{DECLARE_CONTROLS
	java.awt.Button yesButton;
	java.awt.Button noButton;
	// }}
}
