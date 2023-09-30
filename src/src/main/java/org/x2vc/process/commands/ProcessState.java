package org.x2vc.process.commands;

/**
 * The state of processing of a single stylesheet.
 */
public enum ProcessState {
	/**
	 * The process has just been created and not started yet.
	 */
	NEW,

	/**
	 * The processing is being initialized. Among other things, the schema for the stylesheet is being loaded or
	 * generated.
	 */
	INITIALIZE,

	/**
	 * Sample documents are being processed in order to determine whether the stylesheet attempts to access document
	 * elements that are not yet represented in the schema.
	 */
	EXPLORE_SCHEMA,

	/**
	 * The results of the schema exploration phase are being consolidated and the schema is being adjusted.
	 */
	EVOLVE_SCHEMA,

	/**
	 * The XSS vulnerability check is being performed (both initial and follow-up pass).
	 */
	CHECK_XSS,

	/**
	 * The report for the stylesheet is being compiled,
	 */
	COMPILE_REPORT,

	/**
	 * The processing has been completed.
	 */
	DONE
}