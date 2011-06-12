/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.samples.mixed

import io.leon.AbstractLeonConfiguration
import io.leon.resources.ResourceLocation
import java.io.{File, FileInputStream}
import io.leon.persistence.mongo.LeonMongoModule

class HomeDirResourceLocation extends ResourceLocation {
  def getInputStreamOption(fileName: String) = {
    val file = new File("/home/roman/" + fileName)
    file.exists() match {
      case true => Some(new FileInputStream(file))
      case false => None
    }

  }
}


class Module extends AbstractLeonConfiguration {

  def config() {
    install(new LeonMongoModule())

    loadFile("/io/leon/samples/mixed/person.js")

    browser("person").linksToServer()

    server("leon.browser").linksToAllPages("leon")
  }

}
