package de.pcfreak9000.se2d.util;

import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class Se2Dlog {

	public static void log(Object msg) {
		Logger.log(msg);
	}

	public static void log(LogLevel logLevel, Object msg) {
		Logger.log(msg, logLevel);
	}

	public static void logErr(Object msg, Exception ex) {
		Logger.logErr(msg, ex);
	}

}