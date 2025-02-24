package io.shiftleft.semanticcpg.testfixtures

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.semanticcpg.layers.{Base, CallGraph, ControlFlow, LayerCreatorContext, TypeRelations}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{File, PrintWriter}
import java.nio.file.Files

class CodeToCpgFixture(val frontend: LanguageFrontend) extends AnyWordSpec with Matchers with BeforeAndAfterAll {

  val code = ""
  var cpg: Cpg = _
  def passes(cpg: Cpg): Unit = createEnhancements(cpg)

  override def beforeAll(): Unit = {
    val tmpDir = writeCodeToFile(code)
    buildCpgForDir(tmpDir)
  }

  def createEnhancements(cpg: Cpg): Unit = {
    val context = new LayerCreatorContext(cpg)
    new Base().run(context)
    new ControlFlow().run(context)
    new TypeRelations().run(context)
    new CallGraph().run(context)
  }

  private def buildCpgForDir[T](dir: File): Unit = {
    cpg = frontend.execute(dir)
    passes(cpg)
  }

  protected def writeCodeToFile(sourceCode: String): File = {
    val tmpDir = Files.createTempDirectory("semanticcpgtest").toFile
    tmpDir.deleteOnExit()
    val codeFile = File.createTempFile("Test", frontend.fileSuffix, tmpDir)
    codeFile.deleteOnExit()
    new PrintWriter(codeFile) { write(sourceCode); close() }
    tmpDir
  }

  override def afterAll(): Unit = {
    cpg.close()
  }

}
