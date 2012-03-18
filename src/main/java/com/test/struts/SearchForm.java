package com.test.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class SearchForm extends ActionForm {

	private static final long serialVersionUID = 1L;
	private String name 			= null;
	private String ssNum 			= null;
	private List<Employee> results	= null;

	// Reset form fields.
	public void reset(final ActionMapping mapping, final HttpServletRequest request) {
		this.name = null;
		this.ssNum = null;
		this.results = null;
	}

	// Validate form data.
	public ActionErrors validate(final ActionMapping mapping, final HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		boolean nameEntered = false;
		boolean ssNumEntered = false;

		// Determine if name has been entered.
		if (this.name != null && this.name.length() > 0) {
			nameEntered = true;
		}

		// Determine if social security number has been entered.
		if (this.ssNum != null && this.ssNum.length() > 0) {
			ssNumEntered = true;
		}

		/*
		 * Validate that either name or social security number has been entered.
		 */
		if (!nameEntered && !ssNumEntered) {
			errors.add(null, new ActionMessage("error.search.criteria.missing"));
		}

		/*
		 * Validate format of social security number if it has been entered.
		 */
		if (ssNumEntered && !isValidSsNum(ssNum.trim())) {
			errors.add("ssNum", new ActionMessage("error.search.ssNum.invalid"));
		}

		return errors;
	}

	// Validate format of social security number.
	private static boolean isValidSsNum(final String ssNum) {
		if (ssNum.length() < 11) {
			return false;
		}

		for (int i = 0; i < 11; i++) {
			if (i == 3 || i == 6) {
				if (ssNum.charAt(i) != '-') {
					return false;
				}
			} else if ("0123456789".indexOf(ssNum.charAt(i)) == -1) {
				return false;
			}
		}

		return true;
	}
	
	public void setName(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setSsNum(final String ssNum) {
		this.ssNum = ssNum;
	}

	public String getSsNum() {
		return this.ssNum;
	}

	public void setResults(final List<Employee> results) {
		this.results = results;
	}

	public List<Employee> getResults() {
		return this.results;
	}

}
