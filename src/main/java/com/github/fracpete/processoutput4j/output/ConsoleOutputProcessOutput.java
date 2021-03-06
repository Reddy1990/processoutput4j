/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * ConsoleOutputProcessOutput.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.processoutput4j.output;

import com.github.fracpete.processoutput4j.reader.ConsoleOutputProcessReader;

/**
 * A container class for the results obtained from executing a process.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6502 $
 */
public final class ConsoleOutputProcessOutput
  extends AbstractProcessOutput {

  /** for serialization. */
  private static final long serialVersionUID = 1902809285333524039L;

  /**
   * Configures the thread for stderr.
   *
   * @param process 	the process to monitor
   * @return		the configured thread, not yet started
   */
  protected Thread configureStdErr(Process process) {
    return new Thread(new ConsoleOutputProcessReader(process, false, ""));
  }

  /**
   * Configures the thread for stdout.
   *
   * @param process 	the process to monitor
   * @return		the configured thread, not yet started
   */
  protected Thread configureStdOut(Process process) {
    return new Thread(new ConsoleOutputProcessReader(process, true, ""));
  }
}
