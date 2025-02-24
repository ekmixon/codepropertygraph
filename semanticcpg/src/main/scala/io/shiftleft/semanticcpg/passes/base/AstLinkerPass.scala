package io.shiftleft.semanticcpg.passes.base

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{HasAstParentFullName, HasAstParentType, StoredNode}
import io.shiftleft.codepropertygraph.generated.{EdgeTypes, NodeTypes, Properties}
import io.shiftleft.passes.{CpgPass, DiffGraph}
import io.shiftleft.semanticcpg.language._
import io.shiftleft.semanticcpg.passes.callgraph.MethodRefLinker
import io.shiftleft.semanticcpg.passes.callgraph.MethodRefLinker.{
  methodFullNameToNode,
  namespaceBlockFullNameToNode,
  typeDeclFullNameToNode
}

class AstLinkerPass(cpg: Cpg) extends CpgPass(cpg) {

  import MethodRefLinker.{logFailedSrcLookup, logger}

  override def run(): Iterator[DiffGraph] = {
    val dstGraph = DiffGraph.newBuilder
    cpg.method.whereNot(_.inE(EdgeTypes.AST)).foreach(addAstEdge(_, dstGraph))
    cpg.typeDecl.whereNot(_.inE(EdgeTypes.AST)).foreach(addAstEdge(_, dstGraph))
    Iterator(dstGraph.build())
  }

  /**
    * For the given method or type declaration, determine its parent in the AST
    * via the AST_PARENT_TYPE and AST_PARENT_FULL_NAME fields and create an
    * AST edge from the parent to it. AST creation to methods and type declarations
    * is deferred in frontends in order to allow them to process methods/type-
    * declarations independently.
    * */
  private def addAstEdge(methodOrTypeDecl: HasAstParentType with HasAstParentFullName with StoredNode,
                         dstGraph: DiffGraph.Builder): Unit = {
    val astParentOption: Option[StoredNode] =
      methodOrTypeDecl.astParentType match {
        case NodeTypes.METHOD          => methodFullNameToNode(cpg, methodOrTypeDecl.astParentFullName)
        case NodeTypes.TYPE_DECL       => typeDeclFullNameToNode(cpg, methodOrTypeDecl.astParentFullName)
        case NodeTypes.NAMESPACE_BLOCK => namespaceBlockFullNameToNode(cpg, methodOrTypeDecl.astParentFullName)
        case _ =>
          logger.warn(
            s"Invalid AST_PARENT_TYPE=${methodOrTypeDecl.propertyOption(Properties.AST_PARENT_FULL_NAME)};" +
              s" astChild LABEL=${methodOrTypeDecl.label};" +
              s" astChild FULL_NAME=${methodOrTypeDecl.propertyOption(Properties.FULL_NAME)}")
          None
      }

    astParentOption match {
      case Some(astParent) =>
        dstGraph.addEdgeInOriginal(astParent, methodOrTypeDecl, EdgeTypes.AST)
      case None =>
        logFailedSrcLookup(EdgeTypes.AST,
                           methodOrTypeDecl.astParentType,
                           methodOrTypeDecl.astParentFullName,
                           methodOrTypeDecl.label,
                           methodOrTypeDecl.id.toString)
    }
  }
}
