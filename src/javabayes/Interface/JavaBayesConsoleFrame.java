/**
 * JavaBayesConsoleFrame.java
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

import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javabayes.JavaBayes;

public class JavaBayesConsoleFrame extends Frame {
	private JavaBayes jb;

	// Declare controls
	FileDialog OpenFileDialog;
	FileDialog SaveFileDialog;
	TextArea textArea1;

	// Declare menus
	MenuBar mainMenuBar;
	Menu menu1, menu2, menu3, menu4, menu5, menu6, menu7;

	// Declare checkboxes
	CheckboxMenuItem show_buckets, show_bayes_net;
	CheckboxMenuItem algorithm_variable_elimination, algorithm_bucket_tree;
	CheckboxMenuItem bif_format, bugs_format, xml_format;
	CheckboxMenuItem posterior_marginal, posterior_expectation;
	CheckboxMenuItem explanation, best_configuration, sensitivity_analysis;

	static final String applet_invalid_operation = "This operation is not allowed in an applet!";
	// Labels
	static final String fileLabel = "File";
	static final String optionsLabel = "Options";
	static final String saveLabel = "Save format";
	static final String bifSaveLabel = "BIF format";
	static final String xmlSaveLabel = "XML format";
	final static String bugsSaveLabel = "BUGS format";
	final static String open_dialog_title = "Open";
	final static String save_dialog_title = "Save";
	final static String open_menuitem_title = "Open...";
	final static String open_url_menuitem_title = "Open URL...";
	final static String save_menuitem_title = "Save";
	final static String save_as_menuitem_title = "Save as...";
	final static String clear_menuitem_title = "Clear";
	final static String dump_console_menuitem_title = "Dump console...";
	final static String quit_menuitem_title = "Quit";
	final static String show_bayes_net_title = "Bayesian network";
	final static String show_buckets_title = "Bucket tree";
	final static String algorithm_variable_elimination_title = "Variable elimination";
	final static String algorithm_bucket_tree_title = "Junction tree";
	final static String what_to_show_title = "What to show";
	final static String algorithm_title = "Algorithm";
	final static String inference_mode_title = "Inference mode";
	final static String posterior_marginal_title = "Posterior marginal";
	final static String posterior_expectation_title = "Posterior expectation";
	final static String explanation_title = "Estimate explanatory variables";
	final static String best_configuration_title = "Find complete explanation";
	final static String sensitivity_analysis_title = "Sensitivity analysis";
	final static String help_title = "Help";
	final static String about_title = "About";

	void BucketTree_Action() {
		jb.what_to_show_bucket_tree_action(show_buckets.getState());
	}

	void BayesianNetwork_Action() {
		jb.what_to_show_bayesian_network_action(show_bayes_net.getState());
	}

	void PosteriorExpectation_Action() {
		CheckboxMenuItem active_item = update_checkbox_menu(menu2,
				posterior_expectation, posterior_marginal);
		if (active_item == posterior_expectation)
			jb.posterior_expectation_action();
		else
			jb.posterior_marginal_action();
	}

	void PosteriorMarginal_Action() {
		CheckboxMenuItem active_item = update_checkbox_menu(menu2,
				posterior_marginal, posterior_expectation);
		if (active_item == posterior_expectation)
			jb.posterior_expectation_action();
		else
			jb.posterior_marginal_action();
	}

	void EstimateBestConfiguration_Action() {
		CheckboxMenuItem active_item = update_checkbox_menu(menu2,
				best_configuration, posterior_marginal);
		if (active_item == posterior_marginal)
			jb.posterior_marginal_action();
		else
			jb.estimate_best_configuration_action();
	}

	void SensitivityAnalysis_Action() {
		CheckboxMenuItem active_item = update_checkbox_menu(menu2,
				sensitivity_analysis, posterior_marginal);
		if (active_item == posterior_marginal)
			jb.posterior_marginal_action();
		else
			jb.sensitivity_analysis_action();
	}

	void EstimateExplanationVariables_Action() {
		CheckboxMenuItem active_item = update_checkbox_menu(menu2, explanation,
				posterior_marginal);
		if (active_item == posterior_marginal)
			jb.posterior_marginal_action();
		else
			jb.estimate_explanation_variables_action();
	}

	void BifFormat_Action() {
		CheckboxMenuItem active_item = update_checkbox_menu(menu5, bif_format,
				xml_format);
		if (active_item == bif_format)
			jb.bif_format_action();
		else
			jb.xml_format_action();
	}

	void XmlFormat_Action() {
		CheckboxMenuItem active_item = update_checkbox_menu(menu5, xml_format,
				bif_format);
		if (active_item == xml_format)
			jb.xml_format_action();
		else
			jb.bif_format_action();
	}

	void BugsFormat_Action() {
		CheckboxMenuItem active_item = update_checkbox_menu(menu5, bugs_format,
				bif_format);
		if (active_item == bugs_format)
			jb.bugs_format_action();
		else
			jb.bif_format_action();
	}

	void AlgorithmVariableElimination_Action() {
		CheckboxMenuItem active_item = update_checkbox_menu(menu7,
				algorithm_variable_elimination, algorithm_bucket_tree);
		if (active_item == algorithm_variable_elimination)
			jb.set_algorithm_variable_elimination();
		else
			jb.set_algorithm_bucket_tree();
	}

	void AlgorithmBucketTree_Action() {
		CheckboxMenuItem active_item = update_checkbox_menu(menu7,
				algorithm_bucket_tree, algorithm_variable_elimination);
		if (active_item == algorithm_bucket_tree)
			jb.set_algorithm_bucket_tree();
		else
			jb.set_algorithm_variable_elimination();
	}

	void DumpConsoleToFile_Action() {
		if (jb.is_applet) {
			textArea1.setText(applet_invalid_operation);
			return;
		}
		SaveFileDialog.show();
		String filename = SaveFileDialog.getFile();
		if (filename == null)
			return;
		filename = SaveFileDialog.getDirectory() + filename;
		try {
			FileOutputStream fileout = new FileOutputStream(filename);
			PrintStream out = new PrintStream(fileout);
			String t = textArea1.getText();
			textArea1.setText("");
			out.print(t);
			out.close();
			fileout.close();
		} catch (IOException e) {
			appendText("Dump aborted: " + e + "\n");
			return;
		}
		appendText("\tConsole dumped.\n\n");
	}

	void Clear_Action() {
		(new ClearDialog(this, jb, "Clear the Bayesian network?", true)).show();
	}

	void Save_Action() {
		if (jb.is_applet) {
			appendText(applet_invalid_operation);
			return;
		}
		if (jb.get_current_save_filename() == null)
			SaveAs_Action();
		else {
			jb.save();
			appendText("\tFile saved.\n\n");
		}
	}

	void SaveAs_Action() {
		if (jb.is_applet) {
			appendText(applet_invalid_operation);
			return;
		}
		SaveFileDialog.show();
		String filename = SaveFileDialog.getFile();
		if (filename == null)
			return;
		filename = SaveFileDialog.getDirectory() + filename;
		if (jb.save(filename) == true)
			appendText("\tFile saved.\n\n");
		else
			appendText("\tFile not saved correctly.\n\n");
		jb.set_current_save_filename(filename);
	}

	void Open_Action() {
		if (jb.is_applet) {
			textArea1.append(applet_invalid_operation);
			return;
		}
		OpenFileDialog.show();
		String filename = OpenFileDialog.getFile();
		if (filename == null)
			return;
		filename = OpenFileDialog.getDirectory() + filename;
		if (jb.open(filename) == true)
			appendText("\tFile loaded.\n\n");
		else
			appendText("\tFile not loaded correctly.\n\n");
	}

	void Open_URL_Action() {
		(new OpenURLDialog(this, jb, "Insert URL of network", true)).show();
	}

	void Quit_Action() {
		(new QuitDialog(this, jb, "Quit JavaBayes?", false)).show();
	}

	void About_Action() {
		JavaBayesHelpMessages.show(JavaBayesHelpMessages.about_message);
	}

	/*
	 * End of Network Actions.
	 */

	/**
	 * Constructor for JavaBayesConsoleFrame.
	 */
	public JavaBayesConsoleFrame(JavaBayes java_bayes, String title) {
		jb = java_bayes;
		setTitle(title);

		// Initialize controls.
		OpenFileDialog = new java.awt.FileDialog(this, open_dialog_title,
				FileDialog.LOAD);
		SaveFileDialog = new java.awt.FileDialog(this, save_dialog_title,
				FileDialog.SAVE);
		textArea1 = new java.awt.TextArea();
		add("Center", textArea1);

		// Menus.
		mainMenuBar = new java.awt.MenuBar();

		menu1 = new java.awt.Menu(fileLabel);
		menu1.add(open_menuitem_title);
		menu1.add(open_url_menuitem_title);
		menu1.add(save_menuitem_title);
		menu1.add(save_as_menuitem_title);
		menu1.add(clear_menuitem_title);
		menu1.add(dump_console_menuitem_title);
		menu1.addSeparator();
		menu1.add(quit_menuitem_title);
		mainMenuBar.add(menu1);

		menu4 = new Menu(optionsLabel);

		menu6 = new Menu(what_to_show_title);
		menu6.add(show_bayes_net = new CheckboxMenuItem(show_bayes_net_title));
		menu6.add(show_buckets = new CheckboxMenuItem(show_buckets_title));
		menu4.add(menu6);

		menu7 = new Menu(algorithm_title);
		menu7.add(algorithm_variable_elimination = new CheckboxMenuItem(
				algorithm_variable_elimination_title));
		menu7.add(algorithm_bucket_tree = new CheckboxMenuItem(
				algorithm_bucket_tree_title));
		menu4.add(menu7);

		menu2 = new Menu(inference_mode_title);
		menu2.add(posterior_marginal = new CheckboxMenuItem(
				posterior_marginal_title));
		menu2.add(posterior_expectation = new CheckboxMenuItem(
				posterior_expectation_title));
		menu2.add(explanation = new CheckboxMenuItem(explanation_title));
		menu2.add(best_configuration = new CheckboxMenuItem(
				best_configuration_title));
		// menu2.add( sensitivity_analysis = new
		// CheckboxMenuItem(sensitivity_analysis_title));
		menu4.add(menu2);

		menu5 = new Menu(saveLabel);
		menu5.add(bif_format = new CheckboxMenuItem(bifSaveLabel));
		menu5.add(xml_format = new CheckboxMenuItem(xmlSaveLabel));
		menu5.add(bugs_format = new CheckboxMenuItem(bugsSaveLabel));
		menu4.add(menu5);

		mainMenuBar.add(menu4);

		menu3 = new java.awt.Menu(help_title);
		menu3.add(about_title);
		// The following try block was contributed
		// by Jason Townsend, Nov 12 2000.
		try {
			mainMenuBar.setHelpMenu(menu3);
		} catch (Exception e) {
			mainMenuBar.add(menu3);
		}
		setMenuBar(mainMenuBar);

		// Initialize the inference menu
		posterior_marginal.setState(true); // Simulate a true state.
		PosteriorMarginal_Action();

		// Initialize the save format menu
		bif_format.setState(true); // Simulate a true state.
		BifFormat_Action();

		// Initialize the algorithm menu
		algorithm_variable_elimination.setState(true); // Simulate a true state.
		AlgorithmVariableElimination_Action();

		// Resize the frame.
		Toolkit t = Toolkit.getDefaultToolkit();
		Dimension d = t.getScreenSize();

		d.width = d.width / 2;
		d.height = d.height / 2;
		resize(d);
	}

	/**
	 * Constructor for JavaBayesConsoleFrame.
	 */
	public JavaBayesConsoleFrame(JavaBayes jb) {
		this(jb, ((String) null));
	}

	/**
	 * Override show() so that Console does not superimpose EditorFrame
	 * directly.
	 */
	@Override
	public void show() {
		move(50, 50);
		super.show();
	}

	/**
	 * Override action() to get events.
	 */
	@Override
	public boolean action(Event event, Object arg) {
		if (event.target instanceof MenuItem) {
			String label = (((MenuItem) event.target).getLabel());
			if (label.equals(show_buckets_title)) {
				BucketTree_Action();
				return true;
			} else if (label.equals(show_bayes_net_title)) {
				BayesianNetwork_Action();
				return true;
			} else if (label.equals(posterior_expectation_title)) {
				PosteriorExpectation_Action();
				return true;
			} else if (label.equals(posterior_marginal_title)) {
				PosteriorMarginal_Action();
				return true;
			} else if (label.equals(best_configuration_title)) {
				EstimateBestConfiguration_Action();
				return true;
			} else if (label.equals(sensitivity_analysis_title)) {
				SensitivityAnalysis_Action();
				return true;
			} else if (label.equals(explanation_title)) {
				EstimateExplanationVariables_Action();
				return true;
			} else if (label.equals(bifSaveLabel)) {
				BifFormat_Action();
				return true;
			} else if (label.equals(xmlSaveLabel)) {
				XmlFormat_Action();
				return true;
			} else if (label.equals(bugsSaveLabel)) {
				BugsFormat_Action();
				return true;
			} else if (label.equals(algorithm_variable_elimination_title)) {
				AlgorithmVariableElimination_Action();
				return true;
			} else if (label.equals(algorithm_bucket_tree_title)) {
				AlgorithmBucketTree_Action();
				return true;
			} else if (label.equals(clear_menuitem_title)) {
				Clear_Action();
				return true;
			} else if (label.equals(dump_console_menuitem_title)) {
				DumpConsoleToFile_Action();
			} else if (label.equals(save_menuitem_title)) {
				Save_Action();
				return true;
			} else if (label.equals(save_as_menuitem_title)) {
				SaveAs_Action();
				return true;
			} else if (label.equals(open_menuitem_title)) {
				Open_Action();
				return true;
			}
			if (label.equals(open_url_menuitem_title)) {
				Open_URL_Action();
				return true;
			} else if (label.equals(quit_menuitem_title)) {
				Quit_Action();
				return true;
			} else if (label.equals(about_title)) {
				About_Action();
				return true;
			}
		}
		return super.action(event, arg);
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
	 * Place text in the text area.
	 */
	public void appendText(String text) {
		textArea1.appendText(text);
	}

	/*
	 * Create the "radiobutton" behavior for the checkbox menu items. It returns
	 * the checkbox that got on.
	 */
	private CheckboxMenuItem update_checkbox_menu(Menu m, CheckboxMenuItem cur,
			CheckboxMenuItem def) {
		boolean s = cur.getState();

		if (s == false) { // If cur was on, then cur is still off and def is on.
			def.setState(true);
			return (def);
		} else { // If cur was off, then cur is on and all others are off.
			for (int i = 0; i < m.countItems(); i++)
				// Set all menu items to off,
				((CheckboxMenuItem) (m.getItem(i))).setState(false);
			cur.setState(true); // then set cur back to on.
			return (cur);
		}
	}
}
