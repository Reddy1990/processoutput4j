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
 * AbstractProcessOutput.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.processoutput4j.output;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Serializable;

/**
 * Ancestor for classes that give access to the output generated by a process.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6502 $
 */
public abstract class AbstractProcessOutput
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 1902809285333524039L;

  /** the command. */
  protected String[] m_Command;

  /** the environment variables. */
  protected String[] m_Environment;

  /** the exit code. */
  protected int m_ExitCode;

  /** the process. */
  protected transient Process m_Process;

  /**
   * Starts the monitoring process.
   */
  public AbstractProcessOutput() {
    initialize();
  }

  /**
   * For initializing the members.
   */
  protected void initialize() {
    m_Command     = new String[0];
    m_Environment = null;
    m_ExitCode    = 0;
    m_Process     = null;
  }

  /**
   * Performs the actual process monitoring.
   *
   * @param builder 	the process builder to monitor
   * @throws Exception	if writing to stdin fails
   */
  public void monitor(ProcessBuilder builder) throws Exception {
    monitor(null, builder);
  }

  /**
   * Performs the actual process monitoring.
   *
   * @param builder 	the process builder to monitor
   * @throws Exception	if writing to stdin fails
   */
  public void monitor(String input, ProcessBuilder builder) throws Exception {
    monitor(builder.command().toArray(new String[0]), null, input, builder.start());
  }

  /**
   * Performs the actual process monitoring.
   *
   * @param cmd		the command that was used
   * @param env		the environment
   * @param process 	the process to monitor
   * @throws Exception	if writing to stdin fails
   */
  public void monitor(String cmd, String[] env, Process process) throws Exception {
    monitor(cmd, env, null, process);
  }

  /**
   * Performs the actual process monitoring.
   *
   * @param cmd		the command that was used
   * @param env		the environment
   * @param input	the input to be written to the process via stdin, ignored if null
   * @param process 	the process to monitor
   * @throws Exception	if writing to stdin fails
   */
  public void monitor(String cmd, String[] env, String input, Process process) throws Exception {
    monitor(new String[]{cmd}, env, input, process);
  }

  /**
   * Performs the actual process monitoring.
   *
   * @param cmd		the command that was used
   * @param env		the environment
   * @param input	the input to be written to the process via stdin, ignored if null
   * @param process 	the process to monitor
   * @throws Exception	if writing to stdin fails
   */
  public void monitor(String cmd[], String[] env, String input, Process process) throws Exception {
    m_Command     = cmd;
    m_Environment = env;
    m_Process     = process;

    // stderr
    Thread threade = new Thread(configureStdErr(m_Process));
    threade.start();

    // stdout
    Thread threado = new Thread(configureStdOut(m_Process));
    threado.start();

    // writing the input to the standard input of the process
    if (input != null) {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
	m_Process.getOutputStream()));
      writer.write(input);
      writer.close();
    }

    m_ExitCode = m_Process.waitFor();

    // wait for threads to finish
    while (threade.isAlive() || threado.isAlive()) {
      try {
	synchronized (this) {
	  wait(100);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    m_Process = null;
  }

  /**
   * Configures the thread for stderr.
   *
   * @param process 	the process to monitor
   * @return		the configured thread, not yet started
   */
  protected abstract Thread configureStdErr(Process process);

  /**
   * Configures the thread for stdout.
   *
   * @param process 	the process to monitor
   * @return		the configured thread, not yet started
   */
  protected abstract Thread configureStdOut(Process process);

  /**
   * Returns the command that was used for the process.
   *
   * @return the command
   */
  public String[] getCommand() {
    return m_Command;
  }

  /**
   * Returns the environment.
   *
   * @return the environment, null if process inherited current one
   */
  public String[] getEnvironment() {
    return m_Environment;
  }

  /**
   * Returns whether the process has succeeded.
   *
   * @return true if succeeded, i.e., exit code = 0
   */
  public boolean hasSucceeded() {
    return (m_ExitCode == 0);
  }

  /**
   * Returns the exit code.
   *
   * @return the exit code
   */
  public int getExitCode() {
    return m_ExitCode;
  }

  /**
   * Returns the process.
   *
   * @return  the process, null if not available
   */
  public Process getProcess() {
    return m_Process;
  }

  /**
   * Destroys the process if possible.
   */
  public void destroy() {
    if (m_Process != null)
      m_Process.destroy();
  }

  /**
   * Returns a short description string.
   *
   * @return the description
   */
  @Override
  public String toString() {
    return "exit code=" + m_ExitCode;
  }
}
