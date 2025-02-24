package io.shiftleft.x2cpg

import io.shiftleft.codepropertygraph.generated.EdgeTypes
import io.shiftleft.codepropertygraph.generated.nodes.{AstNodeNew, ExpressionNew, NewNode}
import io.shiftleft.passes.DiffGraph

case class AstEdge(src: NewNode, dst: NewNode)

object Ast {
  def apply(node: NewNode): Ast = Ast(List(node))
  def apply(): Ast = new Ast(List())

  /** Copy nodes/edges of given `AST` into the given `diffGraph`.
    */
  def storeInDiffGraph(ast: Ast, diffGraph: DiffGraph.Builder): Unit = {
    ast.nodes.foreach { node =>
      diffGraph.addNode(node)
    }
    ast.edges.foreach { edge =>
      diffGraph.addEdge(edge.src, edge.dst, EdgeTypes.AST)
    }
    ast.conditionEdges.foreach { edge =>
      diffGraph.addEdge(edge.src, edge.dst, EdgeTypes.CONDITION)
    }
    ast.receiverEdges.foreach { edge =>
      diffGraph.addEdge(edge.src, edge.dst, EdgeTypes.RECEIVER)
    }
    ast.refEdges.foreach { edge =>
      diffGraph.addEdge(edge.src, edge.dst, EdgeTypes.REF)
    }
    ast.bindsEdges.foreach { edge =>
      diffGraph.addEdge(edge.src, edge.dst, EdgeTypes.BINDS)
    }
    ast.argEdges.foreach { edge =>
      diffGraph.addEdge(edge.src, edge.dst, EdgeTypes.ARGUMENT)
    }
  }

}

case class Ast(nodes: List[NewNode],
               edges: List[AstEdge] = List(),
               conditionEdges: List[AstEdge] = List(),
               refEdges: List[AstEdge] = List(),
               bindsEdges: List[AstEdge] = List(),
               receiverEdges: List[AstEdge] = List(),
               argEdges: List[AstEdge] = List()) {

  def root: Option[NewNode] = nodes.headOption

  def rightMostLeaf: Option[NewNode] = nodes.lastOption

  /** AST that results when adding `other` as a child to this AST.
    * `other` is connected to this AST's root node.
    */
  def withChild(other: Ast): Ast = {
    Ast(
      nodes ++ other.nodes,
      edges = edges ++ other.edges ++ root.toList.flatMap(r =>
        other.root.toList.map { rc =>
          AstEdge(r, rc)
      }),
      conditionEdges = conditionEdges ++ other.conditionEdges,
      argEdges = argEdges ++ other.argEdges,
      receiverEdges = receiverEdges ++ other.receiverEdges,
      refEdges = refEdges ++ other.refEdges,
      bindsEdges = bindsEdges ++ other.bindsEdges
    )
  }

  def merge(other: Ast): Ast = {
    Ast(
      nodes ++ other.nodes,
      edges = edges ++ other.edges,
      conditionEdges = conditionEdges ++ other.conditionEdges,
      argEdges = argEdges ++ other.argEdges,
      receiverEdges = receiverEdges ++ other.receiverEdges,
      refEdges = refEdges ++ other.refEdges,
      bindsEdges = bindsEdges ++ other.bindsEdges
    )
  }

  /** AST that results when adding all ASTs in `asts` as children,
    * that is, connecting them to the root node of this AST.
    */
  def withChildren(asts: Seq[Ast]): Ast = {
    if (asts.isEmpty) {
      this
    } else {
      // we do this iteratively as a recursive solution which will fail with
      // a StackOverflowException if there are too many elements in .tail.
      var ast = withChild(asts.head)
      asts.tail.foreach(c => ast = ast.withChild(c))
      ast
    }
  }

  def withConditionEdge(src: NewNode, dst: NewNode): Ast = {
    this.copy(conditionEdges = conditionEdges ++ List(AstEdge(src, dst)))
  }

  def withRefEdge(src: NewNode, dst: NewNode): Ast = {
    this.copy(refEdges = refEdges ++ List(AstEdge(src, dst)))
  }

  def withBindsEdge(src: NewNode, dst: NewNode): Ast = {
    this.copy(bindsEdges = bindsEdges ++ List(AstEdge(src, dst)))
  }

  def withReceiverEdge(src: NewNode, dst: NewNode): Ast = {
    this.copy(receiverEdges = receiverEdges ++ List(AstEdge(src, dst)))
  }

  def withArgEdge(src: NewNode, dst: NewNode): Ast = {
    this.copy(argEdges = argEdges ++ List(AstEdge(src, dst)))
  }

  def withArgEdges(src: NewNode, dsts: Seq[NewNode]): Ast = {
    this.copy(argEdges = argEdges ++ dsts.map(AstEdge(src, _)))
  }

  def withConditionEdges(src: NewNode, dsts: List[NewNode]): Ast = {
    this.copy(conditionEdges = conditionEdges ++ dsts.map(AstEdge(src, _)))
  }

  def withRefEdges(src: NewNode, dsts: List[NewNode]): Ast = {
    this.copy(refEdges = refEdges ++ dsts.map(AstEdge(src, _)))
  }

  def withBindsEdges(src: NewNode, dsts: List[NewNode]): Ast = {
    this.copy(bindsEdges = bindsEdges ++ dsts.map(AstEdge(src, _)))
  }

  def withReceiverEdges(src: NewNode, dsts: List[NewNode]): Ast = {
    this.copy(receiverEdges = receiverEdges ++ dsts.map(AstEdge(src, _)))
  }

  /**
    * Returns a deep copy of the sub tree rooted in `node`. If `order`
    * is set, then the `order` and `argumentIndex` fields of the
    * new root node are set to `order`.
    * */
  def subTreeCopy(node: AstNodeNew, order: Int = -1): Ast = {
    val newNode = node.copy
    if (order != -1) {
      newNode.order = order
      newNode match {
        case expr: ExpressionNew =>
          expr.argumentIndex = order
        case _ =>
      }
    }

    val astChildren = edges.filter(_.src == node).map(_.dst)
    val newChildren = astChildren.map { x =>
      this.subTreeCopy(x.asInstanceOf[AstNodeNew])
    }

    val oldToNew = astChildren.zip(newChildren).map { case (old, n) => old -> n.root.get }.toMap
    def newIfExists(x: NewNode) = {
      oldToNew.get(x).getOrElse(x)
    }

    val newArgEdges = argEdges.filter(_.src == node).map(x => AstEdge(newNode, newIfExists(x.dst)))
    val newConditionEdges = conditionEdges.filter(_.src == node).map(x => AstEdge(newNode, newIfExists(x.dst)))
    val newRefEdges = refEdges.filter(_.src == node).map(x => AstEdge(newNode, newIfExists(x.dst)))
    val newBindsEdges = bindsEdges.filter(_.src == node).map(x => AstEdge(newNode, newIfExists(x.dst)))
    val newReceiverEdges = receiverEdges.filter(_.src == node).map(x => AstEdge(newNode, newIfExists(x.dst)))

    Ast(newNode)
      .copy(argEdges = newArgEdges,
            conditionEdges = newConditionEdges,
            refEdges = newRefEdges,
            bindsEdges = newBindsEdges,
            receiverEdges = newReceiverEdges)
      .withChildren(newChildren)
  }

}
