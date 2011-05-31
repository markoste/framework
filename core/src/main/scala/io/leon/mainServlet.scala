/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.leon

import javax.servlet._
import http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.io._
import java.util.logging.Logger
import com.google.inject.{AbstractModule, Inject}
import com.google.inject.servlet.ServletModule


class MainServletWebModule extends ServletModule {
  override def configureServlets() {
    install(new MainServletModule)
    serve("/*").`with`(classOf[MainServlet])
  }
}

class MainServletModule extends AbstractModule {
  def configure() {
    bind(classOf[MainServlet]).asEagerSingleton()
  }
}

class MainServlet extends HttpServlet {

  val logger = Logger.getLogger(getClass.getName)

  @Inject
  private var config: SJSConfig = _

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    val contextPath = req.getContextPath
    val requestUri = req.getRequestURI
    val url = requestUri.substring(contextPath.size).split('/').toList dropWhile { _ == "" }
    logger.info("Processing request " + url)
    
    url match {
      case "leon" :: "browser" :: "jquery.js" :: Nil =>
        doResource(req, res, "/leon/browser/jquery-1.5.1.min.js")

      case "leon" :: "browser" :: "knockout.js" :: Nil =>
        doResource(req, res, "/leon/browser/knockout-1.2.0.debug.js")

      case "leon" :: "browser" :: "application.js" :: Nil =>
        doString(req, res, config.createApplicationJavaScript())

      case "leon" :: "fc" :: Nil =>
        doFunctionCall(req, res)

      case xs =>
        doResource(req, res, "/" + xs.mkString("/"))
    }
  }

  private def doResource(req: HttpServletRequest, res: HttpServletResponse, path: String) {
    logger.info("Loading resource: " + path)
    val in = getClass.getClassLoader.getResourceAsStream(path)
    val out = res.getOutputStream
    val buffer = new Array[Byte](1024)
    var bytesRead = 0
    while (bytesRead != -1) {
      out.write(buffer, 0, bytesRead)
      bytesRead = in.read(buffer)
    }
    out.close()
  }
  
  private def doFunctionCall(req: HttpServletRequest, res: HttpServletResponse) {
    val fnName = req.getParameter("fnName")
    val args = req.getParameter("args")

    val fn = config.getJavaScriptFunction(fnName)
    val result = fn(args)
    val out = new BufferedOutputStream(res.getOutputStream)
    out.write(result.getBytes)
    out.close()
  }

  private def doString(req: HttpServletRequest, res: HttpServletResponse, string: String) {
    val out = new PrintWriter(new BufferedOutputStream(res.getOutputStream))
    out.write(string)
    out.close()
  }

}