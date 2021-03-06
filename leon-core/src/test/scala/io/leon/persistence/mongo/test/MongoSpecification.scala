package io.leon.persistence.mongo.test

import org.specs2.mutable.Specification
import io.leon.javascript.{LeonScriptEngine, LeonJavaScriptModule}
import io.leon.persistence.mongo.{LeonMongoManager, LeonMongoModule}
import com.google.inject.{Inject, Guice, AbstractModule}
import io.leon.resources.ResourcesModule
import org.mozilla.javascript.Context


class MongoSpecification extends Specification {

  override def is = args(sequential = true) ^ super.is

  private val module = new AbstractModule {
    def configure() {
      install(new ResourcesModule)
      install(new LeonJavaScriptModule)
      install(new LeonMongoModule())
      bind(classOf[MongoTestModuleInit]).asEagerSingleton()
    }
  }

  private val injector = Guice.createInjector(module)

  def getLeonScriptEngine = {
    injector.getInstance(classOf[LeonScriptEngine])
  }

  def getManager(): LeonMongoManager = {
    injector.getInstance(classOf[LeonMongoManager])
  }

  def invokeJsTest(func: String) = {
    val engine = getLeonScriptEngine

    engine.invokeFunction("io.leon.persistence.mongo.test." + func).asInstanceOf[Boolean]
  }
}

class MongoTestModuleInit @Inject()(engine: LeonScriptEngine) {
  engine.loadResource("/io/leon/persistence/mongo/test/mongo_test.js")
}
