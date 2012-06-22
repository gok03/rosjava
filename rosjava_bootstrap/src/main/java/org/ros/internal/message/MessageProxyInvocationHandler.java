/*
 * Copyright (C) 2012 Google Inc.
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

package org.ros.internal.message;

import org.ros.internal.message.context.MessageContext;
import org.ros.internal.message.field.Field;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MessageProxyInvocationHandler implements InvocationHandler {

  private final MessageImpl messageImpl;

  MessageProxyInvocationHandler(MessageImpl messageImpl) {
    this.messageImpl = messageImpl;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String methodName = method.getName();
    MessageContext messageContext = messageImpl.getMessageContext();
    // TODO(damonkohler): Use a lookup table instead.
    List<Field> fields = messageImpl.getFields();
    for (Field field : fields) {
      if (field.isConstant()) {
        // Only primitive types may be constant and they are defined as static
        // in the message interface.
        continue;
      }
      String fieldJavaName = messageContext.getFieldJavaName(field.getName());
      if (!methodName.endsWith(fieldJavaName)) {
        continue;
      }
      String getter = "get" + fieldJavaName;
      String setter = "set" + fieldJavaName;
      if (methodName.equals(getter) && method.getParameterTypes().length == 0) {
        return field.getValue();
      }
      if (methodName.equals(setter) && method.getParameterTypes().length == 1) {
        field.setValue(args[0]);
        return null;
      }
    }
    return method.invoke(messageImpl, args);
  }
}