/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import javax.script.{Invocable, ScriptEngine}
import com.google.inject.{Inject, Provider}
import io.leon.web.ajax.AjaxHandler

object RhinoUtils {

  def json2RhinoObject(engine: ScriptEngine, string: String): AnyRef = {
    val invocable = engine.asInstanceOf[Invocable]
    val json = engine.get("JSON")
    invocable.invokeMethod(json, "parse", string)
  }

  def rhinoObject2Json(engine: ScriptEngine, obj: AnyRef): String = {
    val invocable = engine.asInstanceOf[Invocable]
    val json = engine.get("JSON")
    invocable.invokeMethod(json, "stringify", obj).asInstanceOf[String]
  }

}

class JavaScriptAjaxHandlerProvider(objName: String) extends Provider[AjaxHandler] {

  @Inject
  var engine: ScriptEngine = _

  private lazy val jsObject = new JavaScriptObject(engine, objName)

  private lazy val handler = new AjaxHandler {
    def jsonApply(members: List[String], args: String) = jsObject.jsonApply(members, args)
  }

  def get() = handler

}

class JavaScriptObject(val engine: ScriptEngine, val objName: String) {

  private def invocable = engine.asInstanceOf[Invocable]

  def jsonApply(members: List[String], args: String): String = {
    val argsParsed = RhinoUtils.json2RhinoObject(engine, args)
    val function = engine.eval((objName :: members).mkString("."))
    val objResult = invocable.invokeMethod(function, "apply", function, argsParsed)
    RhinoUtils.rhinoObject2Json(engine, objResult)
  }

}