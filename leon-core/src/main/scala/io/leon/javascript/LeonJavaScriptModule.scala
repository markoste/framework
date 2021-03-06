/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import com.google.inject.PrivateModule

class LeonJavaScriptModule extends PrivateModule {

  def configure() {
    bind(classOf[LeonWrapFactory]).asEagerSingleton()
    bind(classOf[LeonScriptEngine]).asEagerSingleton()
    bind(classOf[JavaScriptWebBindings]).asEagerSingleton()

    expose(classOf[LeonScriptEngine])
  }

}