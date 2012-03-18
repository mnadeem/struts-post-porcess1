package com.test.struts;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.InvalidCancelException;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.tiles.TilesRequestProcessor;

public class CustomRequestProcessor extends TilesRequestProcessor {

	public void process(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
		// Wrap multipart requests with a special wrapper
		processMultipart(request);

		// Identify the path component we will use to select a mapping
		String path = processPath(request, response);

		if (path == null) {
			return;
		}

		logPath(request, path);

		// Select a Locale for the current user if requested
		processLocale(request, response);

		// Set the content type and no-caching headers if requested
		processContent(request, response);
		processNoCache(request, response);

		// General purpose preprocessing hook
		if (!processPreprocess(request, response)) {
			return;
		}

		processCachedMessages(request, response);

		// Identify the mapping for this request
		ActionMapping mapping = processMapping(request, response, path);

		if (mapping == null) {
			return;
		}

		// Check for any role required to perform this action
		if (!processRoles(request, response, mapping)) {
			return;
		}

		// Process any ActionForm bean related to this request
		ActionForm form = processActionForm(request, response, mapping);

		processPopulate(request, response, form, mapping);

		// Validate any fields of the ActionForm bean, if applicable
		try {
			if (!processValidate(request, response, form, mapping)) {
				return;
			}
		} catch (InvalidCancelException exception) {
			ActionForward forward = processException(request, response, exception, form, mapping);
			processForwardConfig(request, response, forward);
			return;
		} catch (IOException e) {
			throw e;
		} catch (ServletException e) {
			throw e;
		}

		// Process a forward or include specified by this mapping
		if (!processForward(request, response, mapping)) {
			return;
		}

		if (!processInclude(request, response, mapping)) {
			return;
		}

		// Create or acquire the Action instance to process this request
		Action action = processActionCreate(request, response, mapping);

		if (action == null) {
			return;
		}

		// Call the Action instance itself
		ActionForward forward = processActionPerform(request, response, action, form, mapping);

		postProcess(request);

		// Process the returned ActionForward instance
		processForwardConfig(request, response, forward);
	}

	private void logPath(final HttpServletRequest request, final String path) {
		if (log.isDebugEnabled()) {
			log.debug("Processing a '" + request.getMethod() + "' for path '" + path + "'");
		}
	}

	protected boolean processValidate(final HttpServletRequest request,
			final HttpServletResponse response, final ActionForm form, final ActionMapping mapping)
					throws IOException, ServletException, InvalidCancelException {

		if (form == null) {
			return (true);
		}

		// Has validation been turned off for this mapping?
		if (!mapping.getValidate()) {
			return (true);
		}

		// Was this request cancelled? If it has been, the mapping also
		// needs to state whether the cancellation is permissable; otherwise
		// the cancellation is considered to be a symptom of a programmer
		// error or a spoof.
		if (request.getAttribute(Globals.CANCEL_KEY) != null) {
			if (mapping.getCancellable()) {
				if (log.isDebugEnabled()) {
					log.debug(" Cancelled transaction, skipping validation");
				}
				return (true);
			} else {
				request.removeAttribute(Globals.CANCEL_KEY);
				throw new InvalidCancelException();
			}
		}

		// Call the form bean's validation method
		if (log.isDebugEnabled()) {
			log.debug(" Validating input form properties");
		}

		ActionMessages errors = form.validate(mapping, request);

		if ((errors == null) || errors.isEmpty()) {
			if (log.isTraceEnabled()) {
				log.trace("  No errors detected, accepting input");
			}

			return (true);
		}

		// Special handling for multipart request
		if (form.getMultipartRequestHandler() != null) {
			if (log.isTraceEnabled()) {
				log.trace("  Rolling back multipart request");
			}

			form.getMultipartRequestHandler().rollback();
		}

		// Was an input path (or forward) specified for this mapping?
		String input = mapping.getInput();

		if (input == null) {
			if (log.isTraceEnabled()) {
				log.trace("  Validation failed but no input form available");
			}

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					getInternal().getMessage("noInput", mapping.getPath()));

			return (false);
		}

		// Save our error messages and return to the input form if possible
		if (log.isDebugEnabled()) {
			log.debug(" Validation failed, returning to '" + input + "'");
		}

		request.setAttribute(Globals.ERROR_KEY, errors);

		postProcess(request);

		if (moduleConfig.getControllerConfig().getInputForward()) {
			ForwardConfig forward = mapping.findForward(input);

			processForwardConfig(request, response, forward);
		} else {
			internalModuleRelativeForward(input, request, response);
		}

		return (false);
	}

	private void postProcess(final HttpServletRequest request) {
		ActionMessages errors = (ActionMessages) request.getAttribute(Globals.ERROR_KEY);
		if ((errors != null) && !errors.isEmpty()) {
			logErrors(errors);
		}
	}

	private void logErrors(final ActionMessages errors) {
		@SuppressWarnings("unchecked")
		Iterator<ActionMessage> iterator = errors.get();
		while (iterator.hasNext()) {
			ActionMessage message = iterator.next();
			System.out.println("********* " + message);
		}
	}
}
