/**
 * NetworkPanel.java
 * @author Fabio G. Cozman 
 *  Original version by Sreekanth Nagarajan, totally rewritten by Fabio Cozman.
 *  The panel code was originally based on an applet written by Carla Laffra
 *  (see  http://www.cs.pace.edu/~carla/dijkstra.html  for the applet);
 *  adapted with permission from the author.
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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;

public class NetworkPanel extends Canvas {
	private EditorFrame frame; // Used for changing mouse cursor
	private ScrollingPanel scrollPanel; // Used to control scrolling
	private int mode; // Store the mode for events in the panel
	private InferenceGraph ig; // The object with the Bayes net
	private Point group_start, group_end; // The region that is considered the
											// group

	// Variables that store quantities shared among event handling functions
	boolean new_arc = false;
	Point new_arc_head = null;
	boolean modify_group = false;
	InferenceGraphNode movenode = null;
	Vector moving_nodes = null;
	InferenceGraphNode arcbottomnode = null;
	InferenceGraphNode archeadnode = null;

	// for scrolling
	int x_scroll, y_scroll;

	// Constants for drawing entities.
	private static final int NODE_SIZE = 26;
	private static final int NODE_RADIUS = 13;
	private static final int SPACE_DRAW_NODE_NAME = 24;
	private static final double ARROW_SIZE = 6.0;
	private static final double ARROW_HALF_SIZE = 3.0;
	private static final double DISTANCE_HIT_ARC = 200.0;

	// Color constants for various graphical elements.
	private static final Color nodeColor = Color.green;
	private static final Color observedNodeColor = Color.blue;
	private static final Color explanationNodeColor = Color.orange;

	private static final Color nodeBorderColor = Color.black;
	private static final Color nodenameColor = Color.black;
	private static final Color arcColor = Color.gray;
	private static final Color backgroundColor = Color.white;

	// Network editing modes.
	private static final int CREATE_MODE = 1;
	private static final int MOVE_MODE = 2;
	private static final int DELETE_MODE = 3;
	private static final int OBSERVE_MODE = 4;
	private static final int QUERY_MODE = 5;
	private static final int EDIT_VARIABLE_MODE = 6;
	private static final int EDIT_FUNCTION_MODE = 7;
	private static final int EDIT_NETWORK_MODE = 8;

	// Fonts.
	private Font roman = new Font("TimesRoman", Font.BOLD, 12);
	private Font helvetica = new Font("Helvetica", Font.BOLD, 15);
	private FontMetrics fmetrics = getFontMetrics(roman);
	private int h = fmetrics.getHeight() / 3;

	// For double buffering.
	private Image offScreenImage;
	private Graphics offScreenGraphics;
	private Dimension offScreenSize;

	/**
	 * Default constructor for NetworkPanel.
	 */
	NetworkPanel(EditorFrame frame, ScrollingPanel scroll) {
		this.frame = frame;
		this.scrollPanel = scroll;

		// Create default InferenceGraph
		ig = new InferenceGraph();

		// Create the group object.
		group_start = new Point(0, 0);
		group_end = new Point(0, 0);

		// set initial mode to be MOVE.
		mode = MOVE_MODE;
		frame.setCursor(Frame.MOVE_CURSOR);

		// set color for background
		setBackground(backgroundColor);
	}

	/**
	 * Process mouse down events.
	 */
	@Override
	public boolean mouseDown(Event evt, int x, int y) {
		x += x_scroll;
		y += y_scroll;

		InferenceGraphNode node = nodehit(x, y);

		if (node == null) { // If no node was clicked on.
			if ((mode == DELETE_MODE) && (archit(x, y))) { // Delete arc
				delete_arc();
				archeadnode = null;
				arcbottomnode = null;
			} else if (mode == CREATE_MODE) { // Create a node
				create_node(x, y);
			} else {
				// Start the creation of a group.
				group_start.move(x, y);
				group_end.move(x, y);
				modify_group = true;
			}
		} else { // If a node was clicked on.
			if (mode == OBSERVE_MODE) { // Observe node
				observe(node);
			} else if (mode == QUERY_MODE) { // Query node
				frame.process_query(ig, node.get_name());
			} else if (mode == MOVE_MODE) { // Move node
				movenode = node;
				generate_moving_nodes();
			} else if (mode == DELETE_MODE) { // Delete node
				delete_node(node);
			} else if (mode == CREATE_MODE) { // Create arc
				new_arc = true;
				arcbottomnode = node;
				new_arc_head = new Point(x, y);
			} else if (mode == EDIT_VARIABLE_MODE) { // Edit variable node
				edit_variable(node);
			} else if (mode == EDIT_FUNCTION_MODE) { // Edit function node
				edit_function(node);
			}
		}

		repaint();
		return true;
	}

	/**
	 * Process mouse drag events.
	 */
	@Override
	public boolean mouseDrag(Event evt, int x, int y) {
		x += x_scroll;
		y += y_scroll;

		if (movenode != null) {
			move_node(x, y);
		} else if (new_arc == true) {
			new_arc_head = new Point(x, y);
		} else if (modify_group == true) {
			group_end.move(x, y);
		}

		repaint();
		return true;
	}

	/**
	 * Process mouse up events.
	 */
	@Override
	public boolean mouseUp(Event evt, int x, int y) {
		x += x_scroll;
		y += y_scroll;

		if (movenode != null) {
			ig.set_pos(movenode, new Point(x, y));
			movenode = null;
		} else if (new_arc == true) {
			archeadnode = nodehit(x, y);
			if ((archeadnode != null) && (arcbottomnode != null)) {
				if (archeadnode == arcbottomnode) {
					JavaBayesHelpMessages.show(JavaBayesHelpMessages.selfarc);
				} else if (ig.hasCycle(arcbottomnode, archeadnode)) {
					JavaBayesHelpMessages.show(JavaBayesHelpMessages.circular);
				} else {
					create_arc();
				}
			}
			archeadnode = null;
			arcbottomnode = null;
			new_arc_head = null;
			new_arc = false;
		} else if (modify_group == true) {
			modify_group = false;
		}

		repaint();
		return true;
	}

	/*
	 * Determine whether a node was hit by a mouse click.
	 */
	private InferenceGraphNode nodehit(int x, int y) {
		InferenceGraphNode node;
		for (Enumeration e = ig.elements(); e.hasMoreElements();) {
			node = (InferenceGraphNode) (e.nextElement());
			if ((x - node.get_pos_x()) * (x - node.get_pos_x())
					+ (y - node.get_pos_y()) * (y - node.get_pos_y()) < NODE_RADIUS
					* NODE_RADIUS) {
				return (node);
			}
		}
		return (null);
	}

	/*
	 * Determine whether an arc was hit by a mouse click.
	 */
	boolean archit(int x, int y) {
		InferenceGraphNode hnode, pnode;
		double sdpa;

		for (Enumeration e = ig.elements(); e.hasMoreElements();) {
			hnode = (InferenceGraphNode) (e.nextElement());
			for (Enumeration ee = (hnode.get_parents()).elements(); ee
					.hasMoreElements();) {
				pnode = (InferenceGraphNode) (ee.nextElement());
				sdpa = square_distance_point_arc(hnode, pnode, x, y);
				if ((sdpa >= 0.0) && (sdpa <= DISTANCE_HIT_ARC)) {
					archeadnode = hnode;
					arcbottomnode = pnode;
				}
			}
		}
		if ((archeadnode != null) && (arcbottomnode != null))
			return true;
		else
			return (false);
	}

	/*
	 * Determine whether a point is close to the segment between two nodes
	 * (hnode and pnode); if the point does not lie over or above the segment,
	 * return -1.0
	 */
	double square_distance_point_arc(InferenceGraphNode hnode,
			InferenceGraphNode pnode, int x3, int y3) {
		int x1, y1, x2, y2;
		double area, square_base, square_height, square_hyp;

		x1 = hnode.get_pos_x();
		y1 = hnode.get_pos_y();
		x2 = pnode.get_pos_x();
		y2 = pnode.get_pos_y();

		// Area of the triangle defined by the three points
		area = 0.5 * (x1 * y2 + y1 * x3 + x2 * y3 - x3 * y2 - y3 * x1 - x2 * y1);
		// Base of the triangle
		square_base = ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		// Height of the triangle
		square_height = 4.0 * (area * area) / square_base;

		// Maximum possible distance from point to extreme points
		square_hyp = square_base + square_height;
		// Check first extreme point
		if (square_hyp < ((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1)))
			return (-1.0);
		// Check second extreme point
		if (square_hyp < ((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2)))
			return (-1.0);

		// Requested distance is the height of the triangle
		return (square_height);
	}

	/**
	 * Update the screen with the network.
	 */
	@Override
	public void update(Graphics g) {
		// Prepare new offscreen image, for double buffering.
		Dimension d = size();
		MediaTracker tracker;

		if ((offScreenImage == null) || (d.width != offScreenSize.width)
				|| (d.height != offScreenSize.height)) {
			offScreenImage = createImage(d.width, d.height);
			tracker = new MediaTracker(this);
			try { // Wait to image to be constructed.
				tracker.addImage(offScreenImage, 0);
				tracker.waitForID(0, 0);
			} catch (InterruptedException e) {
			}
			offScreenSize = d;
			offScreenGraphics = offScreenImage.getGraphics();
		}

		// Generate the contents of the image.
		offScreenGraphics.setColor(backgroundColor);
		offScreenGraphics.fillRect(0, 0, d.width, d.height);
		paint(offScreenGraphics);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	/**
	 * Paint the network. This is not nearly as efficient as it should be,
	 * because the whole graph is redrawn every time there is a call to paint().
	 * A much more efficient approach would be to only add/move/delete nodes and
	 * arcs as needed, in response to user commands.
	 * 
	 * The rendering engine changed in Java2, and the circles are not drawn
	 * correctly. The following workaround for the change in the rendering
	 * engine in Java2 was proposed by Michael Becke, Nov 21 2000. try {
	 * ((Graphics2D) g).setRenderingHint( RenderingHints.KEY_ANTIALIASING,
	 * RenderingHints.VALUE_ANTIALIAS_ON); } catch
	 * (java.lang.NoClassDefFoundError e) { // Does nothing if new engine fails.
	 * } Instead of using this Java2-specific code, the solution used here and
	 * also proposed by Michael Becke is to fill first a whole oval with the
	 * border color and then fill the inside of the circles.
	 */
	@Override
	public void paint(Graphics g) {
		InferenceGraphNode node, parent;
		Enumeration e, ee;
		int explanation_status = frame.get_mode();

		if (ig == null)
			return;

		// Draw a new arc upto current mouse position.
		g.setColor(arcColor);

		if (new_arc)
			g.drawLine(arcbottomnode.get_pos_x() - x_scroll,
					arcbottomnode.get_pos_y() - y_scroll, new_arc_head.x
							- x_scroll, new_arc_head.y - y_scroll);

		// Draw all arcs.
		for (e = ig.elements(); e.hasMoreElements();) {
			node = (InferenceGraphNode) (e.nextElement());
			for (ee = (node.get_parents()).elements(); ee.hasMoreElements();) {
				parent = (InferenceGraphNode) (ee.nextElement());
				draw_arc(g, node, parent);
			}
		}

		// Draw the nodes.
		g.setFont(helvetica);

		for (e = ig.elements(); e.hasMoreElements();) {
			node = (InferenceGraphNode) e.nextElement();

			g.setColor(nodeBorderColor);
			if ((node.get_pos_x() - x_scroll) >= 0)
				g.fillOval((node.get_pos_x() - x_scroll) - NODE_RADIUS - 1,
						(node.get_pos_y() - y_scroll) - NODE_RADIUS - 1,
						NODE_SIZE + 2, NODE_SIZE + 2);

			switch (explanation_status) {
			case InferenceGraph.FULL_EXPLANATION:
				g.setColor(explanationNodeColor);
				break;
			case InferenceGraph.EXPLANATION:
				if (node.is_explanation())
					g.setColor(explanationNodeColor);
				else
					g.setColor(nodeColor);
				break;
			case InferenceGraph.SENSITIVITY_ANALYSIS:
			case InferenceGraph.MARGINAL_POSTERIOR:
			case InferenceGraph.EXPECTATION:
				g.setColor(nodeColor);
			}

			if (node.is_observed())
				g.setColor(observedNodeColor);

			if ((node.get_pos_x() - x_scroll) >= 0)
				g.fillOval((node.get_pos_x() - x_scroll) - NODE_RADIUS,
						(node.get_pos_y() - y_scroll) - NODE_RADIUS, NODE_SIZE,
						NODE_SIZE);

			g.setColor(nodenameColor);
			g.drawString(node.get_name(), (node.get_pos_x() - x_scroll)
					- SPACE_DRAW_NODE_NAME, (node.get_pos_y() - y_scroll)
					+ SPACE_DRAW_NODE_NAME);
		}

		// Draw the group.
		g.setXORMode(backgroundColor);
		int group_x, group_y, group_width, group_height;
		if (group_start.x < group_end.x) {
			group_x = group_start.x;
			group_width = group_end.x - group_start.x;
		} else {
			group_x = group_end.x;
			group_width = group_start.x - group_end.x;
		}
		if (group_start.y < group_end.y) {
			group_y = group_start.y;
			group_height = group_end.y - group_start.y;
		} else {
			group_y = group_end.y;
			group_height = group_start.y - group_end.y;
		}
		g.drawRect(group_x - x_scroll, group_y - y_scroll, group_width,
				group_height);
		g.setPaintMode();

		// Resize the scrollbars.
		scrollPanel.setScrollbars(size());
	}

	/*
	 * Auxiliary function that draws an arc.
	 */
	private void draw_arc(Graphics g, InferenceGraphNode node,
			InferenceGraphNode parent) {
		int node_x, node_y, parent_x, parent_y;
		int x1, x2, x3, y1, y2, y3;
		double dir_x, dir_y, distance;
		double head_x, head_y, bottom_x, bottom_y;

		// calculate archead
		node_x = node.get_pos_x() - x_scroll;
		node_y = node.get_pos_y() - y_scroll;
		parent_x = parent.get_pos_x() - x_scroll;
		parent_y = parent.get_pos_y() - y_scroll;

		dir_x = (node_x - parent_x);
		dir_y = (node_y - parent_y);

		distance = Math.sqrt(dir_x * dir_x + dir_y * dir_y);

		dir_x /= distance;
		dir_y /= distance;

		head_x = node_x - (NODE_RADIUS + ARROW_SIZE) * dir_x;
		head_y = node_y - (NODE_RADIUS + ARROW_SIZE) * dir_y;

		bottom_x = parent_x + NODE_RADIUS * dir_x;
		bottom_y = parent_y + NODE_RADIUS * dir_y;

		x1 = (int) (head_x - ARROW_HALF_SIZE * dir_x + ARROW_SIZE * dir_y);
		x2 = (int) (head_x - ARROW_HALF_SIZE * dir_x - ARROW_SIZE * dir_y);
		x3 = (int) (head_x + ARROW_SIZE * dir_x);

		y1 = (int) (head_y - ARROW_HALF_SIZE * dir_y - ARROW_SIZE * dir_x);
		y2 = (int) (head_y - ARROW_HALF_SIZE * dir_y + ARROW_SIZE * dir_x);
		y3 = (int) (head_y + ARROW_SIZE * dir_y);

		int archead_x[] = { x1, x2, x3, x1 };
		int archead_y[] = { y1, y2, y3, y1 };

		// draw archead
		g.drawLine((int) bottom_x, (int) bottom_y, (int) head_x, (int) head_y);
		g.fillPolygon(archead_x, archead_y, 4);
	}

	/*
	 * Set the mode for the NetworkPanel.
	 */
	void set_mode(String label) {
		if (label.equals(EditorFrame.createLabel))
			mode = CREATE_MODE;
		else if (label.equals(EditorFrame.moveLabel))
			mode = MOVE_MODE;
		else if (label.equals(EditorFrame.deleteLabel))
			mode = DELETE_MODE;
		else if (label.equals(EditorFrame.queryLabel))
			mode = QUERY_MODE;
		else if (label.equals(EditorFrame.observeLabel))
			mode = OBSERVE_MODE;
		else if (label.equals(EditorFrame.editVariableLabel))
			mode = EDIT_VARIABLE_MODE;
		else if (label.equals(EditorFrame.editFunctionLabel))
			mode = EDIT_FUNCTION_MODE;
		else
			// default mode;
			mode = CREATE_MODE;
	}

	/*
	 * Return the QuasiBayesNet object displayed int the NetworkPanel.
	 */
	InferenceGraph get_inference_graph() {
		return (ig);
	}

	/*
	 * Store the QuasiBayesNet object to be displayed in the NetworkPanel.
	 */
	void load(InferenceGraph inference_graph) {
		ig = inference_graph;
		repaint();
	}

	/*
	 * Clear the NetworkPanel.
	 */
	void clear() {
		ig = new InferenceGraph();
		repaint();
	}

	/*
	 * Observe a node.
	 */
	void observe(InferenceGraphNode node) {
		ig.reset_marginal();
		Dialog d = new ObserveDialog(this, frame, ig, node);
		d.show();
	}

	/*
	 * Create a node.
	 */
	void create_node(int x, int y) {
		ig.create_node(x, y);
		ig.reset_marginal();
	}

	/*
	 * Create an arc. The bottom and head nodes of the arc are stored in the
	 * variables arcbottomnode and archeadnode.
	 */
	void create_arc() {
		boolean flag_created = ig.create_arc(arcbottomnode, archeadnode);
		if (flag_created == true)
			ig.reset_marginal();
	}

	/*
	 * Make a list of all moving nodes.
	 */
	void generate_moving_nodes() {
		InferenceGraphNode node;

		if (!inside_group(movenode)) {
			moving_nodes = null;
			return;
		} else {
			moving_nodes = new Vector();
			for (Enumeration e = ig.elements(); e.hasMoreElements();) {
				node = (InferenceGraphNode) e.nextElement();
				if (inside_group(node))
					moving_nodes.addElement(node);
			}
		}
	}

	/*
	 * Move a node.
	 */
	void move_node(int x, int y) {
		InferenceGraphNode node;
		int delta_x = movenode.get_pos_x() - x;
		int delta_y = movenode.get_pos_y() - y;

		// Check whether the movenode is in the group.
		if (moving_nodes == null)
			ig.set_pos(movenode, new Point(x, y)); // Move only the movenode.
		else {
			group_start.x -= delta_x;
			group_end.x -= delta_x;
			group_start.y -= delta_y;
			group_end.y -= delta_y;
			for (Enumeration e = moving_nodes.elements(); e.hasMoreElements();) {
				node = (InferenceGraphNode) e.nextElement();
				ig.set_pos(node, // Move all nodes in the group.
						new Point(node.get_pos_x() - delta_x, node.get_pos_y()
								- delta_y));
			}
		}
	}

	/*
	 * Delete a node.
	 */
	void delete_node(InferenceGraphNode node) {
		InferenceGraphNode dnode;
		Enumeration e;
		Vector nodes_to_delete;

		// Check whether the node is in the group.
		if (!inside_group(node))
			ig.delete_node(node); // Delete only the movenode.
		else {
			nodes_to_delete = new Vector();
			for (e = ig.elements(); e.hasMoreElements();) {
				dnode = (InferenceGraphNode) e.nextElement();
				if (inside_group(dnode))
					nodes_to_delete.addElement(dnode);
			}
			for (e = nodes_to_delete.elements(); e.hasMoreElements();) {
				dnode = (InferenceGraphNode) e.nextElement();
				ig.delete_node(dnode); // Delete node.
			}
		}
		ig.reset_marginal();
	}

	/*
	 * Determine whether a given InferenceGraphNode is inside the group.
	 */
	boolean inside_group(InferenceGraphNode node) {
		return ((node.get_pos_x() > Math.min(group_start.x, group_end.x))
				&& (node.get_pos_x() < Math.max(group_start.x, group_end.x))
				&& (node.get_pos_y() > Math.min(group_start.y, group_end.y)) && (node
				.get_pos_y() < Math.max(group_start.y, group_end.y)));
	}

	/*
	 * Delete an arc. The bottom and head nodes of the arc are stored in the
	 * variables arcbottomnode and archeadnode.
	 */
	void delete_arc() {
		ig.delete_arc(arcbottomnode, archeadnode);
		ig.reset_marginal();
	}

	/*
	 * Edit the components of a node.
	 */
	void edit_variable(InferenceGraphNode node) {
		ig.reset_marginal();
		Dialog d = new EditVariableDialog(this, frame, ig, node);
		d.show();
	}

	/*
	 * Edit the function in a node.
	 */
	void edit_function(InferenceGraphNode node) {
		ig.reset_marginal();
		Dialog d = new EditFunctionDialog(frame, ig, node);
		d.show();
	}

	/*
	 * Edit the network.
	 */
	void edit_network() {
		ig.reset_marginal();
		Dialog d = new EditNetworkDialog(frame, ig);
		d.show();
	}

}
