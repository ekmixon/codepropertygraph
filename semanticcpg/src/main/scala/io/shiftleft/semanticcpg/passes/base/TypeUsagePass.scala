package io.shiftleft.semanticcpg.passes.base

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.{EdgeTypes, NodeTypes, PropertyNames}
import io.shiftleft.passes.{CpgPass, DiffGraph}
import io.shiftleft.semanticcpg.passes.callgraph.MethodRefLinker.{
  linkToSingle,
  typeDeclFullNameToNode,
  typeFullNameToNode
}

class TypeUsagePass(cpg: Cpg) extends CpgPass(cpg) {

  override def run(): Iterator[DiffGraph] = {

    val dstGraph = DiffGraph.newBuilder

    // Create REF edges from TYPE nodes to TYPE_DECL

    linkToSingle(
      cpg,
      srcLabels = List(NodeTypes.TYPE),
      dstNodeLabel = NodeTypes.TYPE_DECL,
      edgeType = EdgeTypes.REF,
      dstNodeMap = typeDeclFullNameToNode(cpg, _),
      dstFullNameKey = PropertyNames.TYPE_DECL_FULL_NAME,
      dstGraph,
      None
    )

    // Create EVAL_TYPE edges from nodes of various types
    // to TYPE

    linkToSingle(
      cpg,
      srcLabels = List(
        NodeTypes.METHOD_PARAMETER_IN,
        NodeTypes.METHOD_PARAMETER_OUT,
        NodeTypes.METHOD_RETURN,
        NodeTypes.MEMBER,
        NodeTypes.LITERAL,
        NodeTypes.CALL,
        NodeTypes.LOCAL,
        NodeTypes.IDENTIFIER,
        NodeTypes.BLOCK,
        NodeTypes.METHOD_REF,
        NodeTypes.TYPE_REF,
        NodeTypes.UNKNOWN
      ),
      dstNodeLabel = NodeTypes.TYPE,
      edgeType = EdgeTypes.EVAL_TYPE,
      dstNodeMap = typeFullNameToNode(cpg, _),
      dstFullNameKey = "TYPE_FULL_NAME",
      dstGraph,
      None
    )

    Iterator(dstGraph.build())
  }
}
