/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.dummyapp.person

import io.leon.LeonConfig
import com.google.inject.name.Names
import io.leon.web.comet.{UplinkFunctionProvider, UplinkFunction}

class Config extends LeonConfig {

  def config() {

    loadJsFile("io/leon/dummyapp/person/person.js")

    expose("person") via "person"

    bind(classOf[TestService]).annotatedWith(Names.named("testService")).to(classOf[TestService])

    bind(classOf[UplinkFunction]).annotatedWith(Names.named("uplinkAlert")).toProvider(
      new UplinkFunctionProvider("leon.alert")).asEagerSingleton()
  }

}
