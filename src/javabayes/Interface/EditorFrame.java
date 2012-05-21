/**
 * EditorFrame.java
 * @author Fabio G. Cozman 
 *  First version written by Sreekanth Nagarajan,
 *  totally rewritten by Fabio Cozman.
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
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import javabayes.JavaBayes;
import javabayes.InferenceGraphs.InferenceGraph;

public class EditorFrame extends Frame {
	JavaBayes jb;

	Panel cmdPanel, editPanel;
	public ScrollingPanel scrollPanel;

	// Constants
	final static public int BIF_FORMAT = 0;
	final static public int XML_FORMAT = 1;
	final static public int BUGS_FORMAT = 2;
	final static public int ALGORITHM_VARIABLE_ELIMINATION = 0;
	final static public int ALGORITHM_BUCKET_TREE = 1;

	// Options (controlled by menus in JavaBayesConsoleFrame)
	int mode_menu_choice = InferenceGraph.MARGINAL_POSTERIOR;
	boolean what_to_show_bayesian_network_state = false;
	boolean what_to_show_bucket_tree_state = false;
	int save_format = BIF_FORMAT;
	private String current_save_filename;
	private int algorithm_type = ALGORITHM_VARIABLE_ELIMINATION;

	// constants for caption text of all buttons
	public static final String createLabel = "Create";
	public static final String moveLabel = "Move";
	public static final String deleteLabel = "Delete";
	public static final String queryLabel = "Query";
	public static final String observeLabel = "Observe";
	public static final String editVariableLabel = "Edit Variable";
	public static final String editFunctionLabel = "Edit Function";
	public static final String editNetworkLabel = "Edit Network";

	/**
	 * Default constructor for an EditorFrame.
	 */
	public EditorFrame(JavaBayes java_bayes, String title) {
		super(title);

		jb = java_bayes;

		scrollPanel = new ScrollingPanel(this);

		cmdPanel = new Panel();
		cmdPanel.setLayout(new GridLayout(1, 5));
		cmdPanel.add(new Button(createLabel));
		cmdPanel.add(new Button(moveLabel));
		cmdPanel.add(new Button(deleteLabel));
		cmdPanel.add(new Button(queryLabel));
		cmdPanel.add(new Button(observeLabel));

		editPanel = new Panel();
		editPanel.setLayout(new GridLayout(1, 3));
		editPanel.add(new Button(editVariableLabel));
		editPanel.add(new Button(editFunctionLabel));
		editPanel.add(new Button(editNetworkLabel));

		setLayout(new BorderLayout(0, 0));
		add("North", cmdPanel);
		add("Center", scrollPanel);
		add("South", editPanel);

		// Resize the frame.
		Toolkit t = Toolkit.getDefaultToolkit();
		Dimension d = t.getScreenSize();

		d.width = d.width / 2;
		d.height = d.height * 3 / 4;
		resize(d);
	}

	/**
	 * Handle the possible destruction of the window.
	 */
	@Override
	public boolean handleEvent(Event evt) {
		if (evt.id == Event.WINDOW_DESTROY)
			if (jb != null)
				(new QuitDialog(this, jb, "Quit JavaBayes?", false)).show();
		return (super.handleEvent(evt));
	}

	/**
	 * Handle button events.
	 */
	@Override
	public boolean action(Event evt, Object arg) {
		if (evt.target instanceof Button) {
			String label = ((Button) (evt.target)).getLabel();

			if (((String) arg).equals(createLabel)) {
				scrollPanel.netPanel.set_mode(label);
				JavaBayesHelpMessages
						.show(JavaBayesHelpMessages.create_message);
				setCursor(Frame.DEFAULT_CURSOR);
			} else if (((String) arg).equals(moveLabel)) {
				scrollPanel.netPanel.set_mode(label);
				JavaBayesHelpMessages.show(JavaBayesHelpMessages.move_message);
				setCursor(Frame.MOVE_CURSOR);
			} else if (((String) arg).equals(deleteLabel)) {
				scrollPanel.netPanel.set_mode(label);
				JavaBayesHelpMessages
						.show(JavaBayesHelpMessages.delete_message);
				setCursor(Frame.HAND_CURSOR);
			} else if (((String) arg).equals(queryLabel)) {
				set_query_mode();
			} else if (((String) arg).equals(observeLabel)) {
				set_observe_mode();
			} else if (((String) arg).equals(editVariableLabel)) {
				set_edit_variable_mode();
			} else if (((String) arg).equals(editFunctionLabel)) {
				set_edit_function_mode();
			} else if (((String) arg).equals(editNetworkLabel)) {
				set_edit_network_mode();
			}
		}
		return true;
	}

	/**
	 * Open a file and read the network in it.
	 */
	public boolean open(String filename) {
		InferenceGraph ig;

		try {
			if (jb.is_applet)
				return (false);
			else {
				jb.appendText("\nLoading " + filename + "\n");
				ig = new InferenceGraph(filename);
			}
		} catch (Exception e) {
			jb.appendText(e + "\n");
			return (false);
		}

		// Put the network into the graphical interface
		set_inference_graph(ig);

		return (true);
	}

	/**
	 * Open a URL and read the network in it.
	 */
	public boolean open_url(String filename) {
		InferenceGraph ig;

		try {
			jb.appendText("\nLoading " + filename + "\n");
			ig = new InferenceGraph(new URL(filename));
		} catch (Exception e) {
			jb.appendText("Exception: " + e + "\n");
			return (false);
		}

		// Put the network into the graphical interface
		set_inference_graph(ig);

		return (true);
	}

	/**
	 * Save the network.
	 */
	public boolean save() {
		return (save(current_save_filename));
	}

	/**
	 * Save the network.
	 */
	public boolean save(String filename) {
		InferenceGraph ig = get_inference_graph();

		if (filename == null) {
			jb.appendText("\n Filename invalid!");
			return (false);
		}

		if (ig == null) {
			jb.appendText("\n No Bayesian network to be saved.\n\n");
			return (false);
		}

		try {
			FileOutputStream fileout = new FileOutputStream(filename);
			PrintStream out = new PrintStream(fileout);
			switch (save_format) {
			case BIF_FORMAT:
				ig.save_bif(out);
				break;
			case XML_FORMAT:
				ig.save_xml(out);
				break;
			case BUGS_FORMAT:
				ig.save_bugs(out);
				break;
			}
			out.close();
			fileout.close();
		} catch (IOException e) {
			jb.appendText("Exception: " + e + "\n");
			return (false);
		}
		return (true);
	}

	/**
	 * Clear the network screen.
	 */
	public void clear() {
		scrollPanel.netPanel.clear();
	}

	/**
	 * Process a query.
	 */
	public void process_query(InferenceGraph ig, String queried_variable) {
		// Check whether inference is possible
		if (ig == null) {
			jb.appendText("\nLoad Bayesian network.\n\n");
			return;
		}

		// This makes the whole inference
		ByteArrayOutputStream bstream = new ByteArrayOutputStream();
		PrintStream pstream = new PrintStream(bstream);

		// Print the Bayes net.
		if (what_to_show_bayesian_network_state)
			print_bayes_net(pstream, ig);

		// Perform inference
		switch (mode_menu_choice) {
		case InferenceGraph.MARGINAL_POSTERIOR:
			print_marginal(pstream, ig, queried_variable);
			break;
		case InferenceGraph.EXPECTATION:
			print_expectation(pstream, ig, queried_variable);
			break;
		case InferenceGraph.EXPLANATION:
			print_explanation(pstream, ig);
			break;
		case InferenceGraph.FULL_EXPLANATION:
			print_full_explanation(pstream, ig);
			break;
		case InferenceGraph.SENSITIVITY_ANALYSIS:
			print_sensitivity_analysis(pstream, ig);
			break;
		}

		// Print results to test window
		jb.appendText(bstream.toString());

		// Close streams
		try {
			bstream.close();
			pstream.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Print the QuasiBayesNet in the InferenceGraph.
	 */
	protected void print_bayes_net(PrintStream pstream, InferenceGraph ig) {
		ig.print_bayes_net(pstream);
	}

	/**
	 * Compute and print a posterior marginal distribution for the
	 * InferenceGraph.
	 */
	protected void print_marginal(PrintStream pstream, InferenceGraph ig,
			String queried_variable) {
		if (algorithm_type == ALGORITHM_VARIABLE_ELIMINATION)
			ig.print_marginal(pstream, queried_variable, false,
					what_to_show_bucket_tree_state);
		else if (algorithm_type == ALGORITHM_BUCKET_TREE)
			ig.print_marginal(pstream, queried_variable, true,
					what_to_show_bucket_tree_state);
		else
			return;
	}

	/**
	 * Compute and print a posterior expectation for the InferenceGraph.
	 */
	protected void print_expectation(PrintStream pstream, InferenceGraph ig,
			String queried_variable) {
		if (algorithm_type == ALGORITHM_VARIABLE_ELIMINATION)
			ig.print_expectation(pstream, queried_variable, false,
					what_to_show_bucket_tree_state);
		else if (algorithm_type == ALGORITHM_BUCKET_TREE)
			ig.print_expectation(pstream, queried_variable, true,
					what_to_show_bucket_tree_state);
		else
			return;
	}

	/**
	 * Compute and print an explanation for the InferenceGraph.
	 */
	protected void print_explanation(PrintStream pstream, InferenceGraph ig) {
		ig.print_explanation(pstream, what_to_show_bucket_tree_state);
	}

	/**
	 * Compute and print a full explanation for the InferenceGraph.
	 */
	protected void print_full_explanation(PrintStream pstream, InferenceGraph ig) {
		ig.print_full_explanation(pstream, what_to_show_bucket_tree_state);
	}

	/**
	 * Compute and print the metrics for sensitivity analysis of the
	 * InferenceGraph.
	 */
	protected void print_sensitivity_analysis(PrintStream pstream,
			InferenceGraph ig) {
		ig.print_sensitivity_analysis(pstream);
	}

	/**
	 * Get the InferenceGraph in the NetworkPanel.
	 */
	public InferenceGraph get_inference_graph() {
		return (scrollPanel.netPanel.get_inference_graph());
	}

	/**
	 * Load an InferenceGraph.
	 */
	public void set_inference_graph(InferenceGraph ig) {
		scrollPanel.netPanel.load(ig);
	}

	/**
	 * Interact with menu options: observe variables.
	 */
	public void set_observe_mode() {
		setCursor(Frame.CROSSHAIR_CURSOR);
		scrollPanel.netPanel.set_mode(observeLabel);
		JavaBayesHelpMessages.show(JavaBayesHelpMessages.observe_message);
	}

	/**
	 * Interact with menu options: edit variable.
	 */
	public void set_edit_variable_mode() {
		setCursor(Frame.TEXT_CURSOR);
		scrollPanel.netPanel.set_mode(editVariableLabel);
		JavaBayesHelpMessages.show(JavaBayesHelpMessages.edit_message);
	}

	/**
	 * Interact with menu options: edit function.
	 */
	public void set_edit_function_mode() {
		setCursor(Frame.TEXT_CURSOR);
		scrollPanel.netPanel.set_mode(editFunctionLabel);
		JavaBayesHelpMessages.show(JavaBayesHelpMessages.edit_message);
	}

	/**
	 * Interact with menu options: edit network.
	 */
	public void set_edit_network_mode() {
		scrollPanel.netPanel.edit_network();
	}

	/**
	 * Interact with menu options: queries are processed.
	 */
	public void set_query_mode() {
		setCursor(Frame.DEFAULT_CURSOR);
		scrollPanel.netPanel.set_mode(queryLabel);
		JavaBayesHelpMessages.show(JavaBayesHelpMessages.query_message);
	}

	/**
	 * Return the mode.
	 */
	public int get_mode() {
		return (mode_menu_choice);
	}

	/**
	 * Get the current filename for saving.
	 */
	public String get_current_save_filename() {
		return (current_save_filename);
	}

	/**
	 * Set the current filename for saving.
	 */
	public void set_current_save_filename(String csf) {
		current_save_filename = csf;
	}

	/**
	 * Interact with menu options: whether to show BucketTree.
	 */
	public void what_to_show_bucket_tree_action(boolean what_to_show_bucket_tree) {
		what_to_show_bucket_tree_state = what_to_show_bucket_tree;
	}

	/**
	 * Interact with menu options: whether to show bayesian networks. *
	 */
	public void what_to_show_bayesian_network_action(
			boolean what_to_show_bayesian_network) {
		what_to_show_bayesian_network_state = what_to_show_bayesian_network;
	}

	/**
	 * Inferences produce expectations.
	 */
	public void posterior_expectation_action() {
		mode_menu_choice = InferenceGraph.EXPECTATION;
		scrollPanel.netPanel.repaint();
	}

	/**
	 * Inferences produce posterior marginals.
	 */
	public void posterior_marginal_action() {
		mode_menu_choice = InferenceGraph.MARGINAL_POSTERIOR;
		scrollPanel.netPanel.repaint();
	}

	/**
	 * Estimate explanation variables.
	 */
	public void estimate_explanation_variables_action() {
		mode_menu_choice = InferenceGraph.EXPLANATION;
		scrollPanel.netPanel.repaint();
	}

	/**
	 * Produce the estimates for the best configuration.
	 */
	public void estimate_best_configuration_action() {
		mode_menu_choice = InferenceGraph.FULL_EXPLANATION;
		scrollPanel.netPanel.repaint();
	}

	/**
	 * Produce the metrics for sensitivity analysis.
	 */
	public void sensitivity_analysis_action() {
		mode_menu_choice = InferenceGraph.SENSITIVITY_ANALYSIS;
		scrollPanel.netPanel.repaint();
	}

	/**
	 * Set the format for saving.
	 */
	public void set_save_format(int sf) {
		save_format = sf;
	}

	/**
	 * Set the algorithm type.
	 */
	public void set_algorithm(int type) {
		algorithm_type = type;
	}
}
