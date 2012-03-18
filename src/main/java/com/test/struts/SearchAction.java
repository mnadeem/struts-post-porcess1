package com.test.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public final class SearchAction extends Action {

	public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		SearchForm searchForm = (SearchForm) form;

		// Place search results in SearchForm for access by JSP.
		searchForm.setResults(getEmployee(searchForm));

		addErrorForTesting(request);

		// Forward control to this Action's input page.
		return mapping.getInputForward();
	}

	private List<Employee> getEmployee(final SearchForm searchForm ) {
		EmployeeSearchService service = new EmployeeSearchService();
		String name = searchForm.getName();
		if (name != null && name.trim().length() > 0) {
			return service.searchByName(name);
		} else {
			return service.searchBySsNum(searchForm.getSsNum().trim());
		}
	}

	private void addErrorForTesting(final HttpServletRequest request) {
		ActionMessages messages = new ActionMessages();
		ActionMessage message = new ActionMessage("test");
		messages.add("test", message);
		addErrors(request, messages);
	}
}
