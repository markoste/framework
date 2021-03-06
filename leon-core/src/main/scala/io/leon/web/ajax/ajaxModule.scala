/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.ajax

import com.google.inject.servlet.ServletModule
import javax.servlet.http.{HttpServlet, HttpServletResponse, HttpServletRequest}
import com.google.inject._
import name.{Named, Names}
import java.io.{BufferedWriter, BufferedOutputStream}

class AjaxWebModule extends ServletModule {
  override def configureServlets() {
    install(new AjaxModule)
    serve("/leon/ajax").`with`(classOf[AjaxCallServlet])
    serve("/leon/browser.js").`with`(classOf[BrowserJsFileServlet])
  }
}

class AjaxModule extends AbstractModule {
  def configure() {
    bind(classOf[AjaxCallServlet]).asEagerSingleton()
    bind(classOf[BrowserJsFileServlet]).asEagerSingleton()
  }
}

trait AjaxHandler {
  def jsonApply(member: String, args: String): String
}

class AjaxCallServlet @Inject()(injector: Injector) extends HttpServlet {

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    val targetName = req.getParameter("target")
    val args = req.getParameter("args")

    val (obj, member) = targetName.splitAt(targetName.lastIndexOf('.'))

    val handler = injector.getInstance(Key.get(classOf[AjaxHandler], Names.named(obj)))
    val result = handler.jsonApply(member.substring(1), args)

    res.setContentType("application/json")
    val out = new BufferedOutputStream(res.getOutputStream)
    out.write(result.getBytes)
    out.close()
  }

}

class BrowserJsFileServlet @Inject()(injector: Injector) extends HttpServlet {

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    import scala.collection.JavaConverters._

    res.setContentType("text/javascript")
    val out = new BufferedWriter(res.getWriter)

    val serverObjects = injector.findBindingsByType(new TypeLiteral[AjaxHandler]() {})
    serverObjects.asScala foreach { o =>
      val browserName = o.getKey.getAnnotation.asInstanceOf[Named].value()
      out.write(createJavaScriptFunctionDeclaration(browserName))
    }
    out.close()
  }

  private def createJavaScriptFunctionDeclaration(name: String): String = {
    """
    leon.utils.createVar("%s");
    %s = function (methodName) {
      return function() {
        var argLength = arguments.length - 1;
        var args = Array(argLength);
        for (var i = 0; i < argLength; i++) {
          args[i] = arguments[i];
        }
        var callback = arguments[arguments.length - 1];

        leon.call("%s." + methodName, args, callback);
      };
    }
    """.format(name, name, name)
  }

}