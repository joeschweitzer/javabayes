/**
 * ScrollingPanel.java
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Panel;
import java.awt.Scrollbar;

public class ScrollingPanel extends Panel {
	Scrollbar horizontal, vertical;
	public NetworkPanel netPanel;

	static final int VIRTUAL_WIDTH = 10000;
	static final int VIRTUAL_HEIGHT = 10000;

	ScrollingPanel(EditorFrame editorFrame) {
		// Start the scrollbars without valid parameters since network is not
		// there.
		horizontal = new Scrollbar(Scrollbar.HORIZONTAL, 0, VIRTUAL_WIDTH, 0,
				VIRTUAL_WIDTH);
		vertical = new Scrollbar(Scrollbar.VERTICAL, 0, VIRTUAL_HEIGHT, 0,
				VIRTUAL_HEIGHT);

		netPanel = new NetworkPanel(editorFrame, this);

		setLayout(new BorderLayout());
		add("Center", netPanel);
		add("South", horizontal);
		add("East", vertical);
	}

	/*
	 * Update the scrollbars.
	 */
	void setScrollbars(Dimension d) {
		horizontal.setValues(horizontal.getValue(), d.width,
				horizontal.getMinimum(), horizontal.getMaximum());
		vertical.setValues(vertical.getValue(), d.height,
				vertical.getMinimum(), vertical.getMaximum());
	}

	/**
	 * Handle the scrollbar events (set variables in the NetworkPanel).
	 */
	@Override
	public boolean handleEvent(Event evt) {
		if (evt.target instanceof Scrollbar) {
			if (evt.target == horizontal) {
				netPanel.x_scroll = ((Integer) (evt.arg)).intValue();
			} else if (evt.target == vertical) {
				netPanel.y_scroll = ((Integer) (evt.arg)).intValue();
			}
			netPanel.repaint();
		}
		return (super.handleEvent(evt));
	}
}
