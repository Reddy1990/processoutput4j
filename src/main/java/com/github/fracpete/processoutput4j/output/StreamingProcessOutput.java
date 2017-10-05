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
 * StreamingProcessOutput.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.processoutput4j.output;

import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import com.github.fracpete.processoutput4j.reader.StreamingProcessReader;

/**
 * Streams the data into the owning {@link StreamingProcessOwner} object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StreamingProcessOutput
  extends AbstractProcessOutput {

  private static final long serialVersionUID = 3891848111786764283L;

  /** the owner. */
  protected StreamingProcessOwner m_Owner;

  /**
   * Initializes the process output with the specified owning object.
   *
   * @param owner	the owning object
   */
  public StreamingProcessOutput(StreamingProcessOwner owner) {
    super();
    m_Owner = owner;
  }

  /**
   * Configures the thread for stderr.
   *
   * @param process 	the process to monitor
   * @return		the configured thread, not yet started
   */
  @Override
  protected Thread configureStdErr(Process process) {
    return new Thread(new StreamingProcessReader(m_Owner, process, false));
  }

  /**
   * Configures the thread for stdout.
   *
   * @param process 	the process to monitor
   * @return		the configured thread, not yet started
   */
  @Override
  protected Thread configureStdOut(Process process) {
    return new Thread(new StreamingProcessReader(m_Owner, process, true));
  }
}
