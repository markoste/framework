
import sbt._


class LeonProject(info: ProjectInfo) extends ParentProject(info) with UnpublishedProject {

  // ===================================================================================================================
  // Repositories
  // ===================================================================================================================

  lazy val ossSonatypeRepo = MavenRepository("Sonatype OSS Repo", "http://oss.sonatype.org/content/repositories/releases")

  lazy val atmosphereModuleConfig = ModuleConfiguration("org.atmosphere", ossSonatypeRepo)

  lazy val scalaToolsRelRepo = MavenRepository("Scala Tools Releases Repo", "http://scala-tools.org/repo-releases")

  lazy val sjsonModuleConfig = ModuleConfiguration("net.debasishg", scalaToolsRelRepo)

  lazy val repo2MavenRepo = MavenRepository("Official Maven2 Repo", "http://repo2.maven.org/maven2")

  lazy val guiceMaven2RepoModuleConfig = ModuleConfiguration("com.google.inject", repo2MavenRepo)

  lazy val guiceExtMaven2RepoModuleConfig = ModuleConfiguration("com.google.inject.extensions", repo2MavenRepo)

  // ===================================================================================================================
  // Dependencies for subprojects: Intentionally defs!
  // ===================================================================================================================

  def servletApi = "org.mortbay.jetty" % "servlet-api" % "2.5-20081211" % "provided"

  def jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.v20100331" % "test" withSources()

  def sjson = "net.debasishg" % "sjson_2.8.1" % "0.9.1" withSources()

  def atmosphere_runtime = "org.atmosphere" % "atmosphere-runtime" % "0.7.1" withSources()
  def atmosphere_runtimejq = "org.atmosphere" % "atmosphere-jquery" % "0.7.1"

  def logback_classic = "ch.qos.logback" % "logback-classic" % "0.9.24"

  def logback_core = "ch.qos.logback" % "logback-core" % "0.9.24"

  //def slf4s = "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.2" withSources

  //def slf4jLog4j(scope: String) = "org.slf4j" % "slf4j-log4j12" % "1.6.1" % scope

  def guice = "com.google.inject" % "guice" % "3.0" withSources()

  def guiceServlet = "com.google.inject.extensions" % "guice-servlet" % "3.0" withSources()

  // ===================================================================================================================
  // Publishing
  // ===================================================================================================================

  override def managedStyle = ManagedStyle.Maven
  // override def deliverAction = super.deliverAction dependsOn(publishLocal) // Fix for issue 99!
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)
  // lazy val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  // lazy val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/"
  val publishTo = Resolver.file("Local Test Repository", Path fileProperty "java.io.tmpdir" asFile)

  // ===================================================================================================================
  // core subproject
  // ===================================================================================================================

  val coreProject = project("core", "core", new CoreProject(_))

  class CoreProject(info: ProjectInfo) extends DefaultProject(info) {

    override def libraryDependencies =
      Set(logback_classic, logback_core, servletApi, atmosphere_runtime, atmosphere_runtimejq, guice, guiceServlet, sjson)

    override def defaultExcludes = super.defaultExcludes || "*-sources.jar"

    override def packageSrcJar = defaultJarPath("-sources.jar")
    lazy val sourceArtifact = sources(artifactID) // lazy is important here!
    override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)

    //override def testClasspath = super.testClasspath --- providedClasspath // Needed because of crippled javaee-(web-)api!
  }

  // ===================================================================================================================
  // dummyapp subproject
  // ===================================================================================================================

  val dummyAppProject = project("dummyapp", "dummyapp", new DummyAppProject(_), coreProject)

  class DummyAppProject(info: ProjectInfo) extends DefaultWebProject(info) with UnpublishedProject {

    override def libraryDependencies = Set(logback_classic, logback_core, jetty7)

  }
}

trait UnpublishedProject extends BasicManagedProject {
   override def publishLocalAction = task { None }
   override def deliverLocalAction = task { None }
   override def publishAction = task { None }
   override def deliverAction = task { None }
   override def artifacts = Set.empty
}


