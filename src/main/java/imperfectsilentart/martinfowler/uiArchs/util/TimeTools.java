/*
 * Copyright 2021 Imperfect Silent Art
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package imperfectsilentart.martinfowler.uiArchs.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;

/**
 * Beschreibung
 *
 * @author Imperfect Silent Art
 *
 */
public class TimeTools {
	/**
	 * @return DateTimeFormatter    Formatter for generating a date format compatible to MySQL timestamp data type.
	 * TODO Heed Oracle timestamp format for "DATE"-type. untested whether the time zone offset works
	 */
	public final static DateTimeFormatter getReadingTimestampFormat() {
		final DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
		final DateTimeFormatter result = b.appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD).appendPattern("-MM-dd' 'HH:mm:ss[.SSS][Z]").toFormatter();
		return result;
	}
	
	
	/**
	 * @param readingTimestamp
	 * @return
	 * @throws TimeProcessingException
	 */
	public final static LocalDateTime parseReadingTimestamp(final String readingTimestamp) throws TimeProcessingException {
		LocalDateTime result = null;
		
		try {
			result = LocalDateTime.parse(readingTimestamp, getReadingTimestampFormat());
		}catch(DateTimeParseException e) {
			throw new TimeProcessingException("Given timestamp \""+readingTimestamp+"\" doesn't have the required format \""+getReadingTimestampFormat()+"\"", e);
		}
		return result;
	}
}
