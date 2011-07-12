/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.node;

import org.ros.exception.RosRuntimeException;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class NodeRunner {

  private final Executor executor;

  public static NodeRunner createDefault() {
    return new NodeRunner(Executors.newCachedThreadPool());
  }

  public NodeRunner(Executor executor) {
    this.executor = executor;
  }

  public void run(final NodeMain node, final NodeConfiguration nodeConfiguration) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          node.main(nodeConfiguration);
        } catch (Exception e) {
          throw new RosRuntimeException(e);
        }
      }
    });
  }

}
