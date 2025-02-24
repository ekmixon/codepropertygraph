package io.shiftleft.semanticcpg.layers

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.passes.CpgPassBase
import io.shiftleft.semanticcpg.passes.typerelations.{AliasLinkerPass, TypeHierarchyPass}

import scala.annotation.nowarn

object TypeRelations {
  val overlayName: String = "typerel"
  val description: String = "Type relations layer (hierarchy and aliases)"
  def defaultOpts = new LayerCreatorOptions()

  def passes(cpg: Cpg): Iterator[CpgPassBase] = Iterator(
    new TypeHierarchyPass(cpg),
    new AliasLinkerPass(cpg),
  )

}

@nowarn
class TypeRelations(optionsUnused: LayerCreatorOptions = null) extends LayerCreator {
  override val overlayName: String = TypeRelations.overlayName
  override val description: String = TypeRelations.description
  override val dependsOn = List(Base.overlayName)

  override def create(context: LayerCreatorContext, storeUndoInfo: Boolean): Unit = {
    val cpg = context.cpg
    TypeRelations.passes(cpg).zipWithIndex.foreach {
      case (pass, index) =>
        runPass(pass, context, storeUndoInfo, index)
    }
  }

}
