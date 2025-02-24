package io.shiftleft.semanticcpg.language.types.expressions.generalizations

import io.shiftleft.codepropertygraph.generated.nodes._
import io.shiftleft.codepropertygraph.generated.{EdgeTypes, NodeTypes}
import io.shiftleft.semanticcpg.language._
import overflowdb.traversal.help.Doc
import overflowdb.traversal.{Traversal, help}

@help.Traversal(elementType = classOf[AstNode])
class AstNodeTraversal[A <: AstNode](val traversal: Traversal[A]) extends AnyVal {

  /**
    * Nodes of the AST rooted in this node, including the node itself.
    * */
  @Doc("All nodes of the abstract syntax tree")
  def ast: Traversal[AstNode] =
    traversal.repeat(_.out(EdgeTypes.AST))(_.emit).cast[AstNode]

  /**
    * All nodes of the abstract syntax tree rooted in this node,
    * which match `predicate`. Equivalent of `match` in the original
    * CPG paper.
    * */
  def ast(predicate: AstNode => Boolean): Traversal[AstNode] =
    ast.filter(predicate)

  def containsCallTo(regex: String): Traversal[A] =
    traversal.filter(_.ast.isCall.name(regex).size > 0)

  @Doc("Depth of the abstract syntax tree")
  def depth: Traversal[Int] =
    traversal.map(_.depth)

  def depth(p: AstNode => Boolean): Traversal[Int] =
    traversal.map(_.depth(p))

  def isCallTo(regex: String): Traversal[Call] =
    isCall.name(regex)

  /**
    * Nodes of the AST rooted in this node, minus the node itself
    * */
  def astMinusRoot: Traversal[AstNode] =
    traversal.repeat(_.out(EdgeTypes.AST))(_.emitAllButFirst).cast[AstNode]

  /**
    * Direct children of node in the AST. Siblings are ordered by their `order` fields
    * */
  def astChildren: Traversal[AstNode] =
    traversal.out(EdgeTypes.AST).cast[AstNode].sortBy(_.order)

  /**
    * Parent AST node
    * */
  def astParent: Traversal[AstNode] =
    traversal.in(EdgeTypes.AST).cast[AstNode]

  /**
    * Nodes of the AST obtained by expanding AST edges backwards until the method root is reached
    * */
  def inAst: Traversal[AstNode] =
    inAst(null)

  /**
    * Nodes of the AST obtained by expanding AST edges backwards until the method root is reached, minus this node
    * */
  def inAstMinusLeaf: Traversal[AstNode] =
    inAstMinusLeaf(null)

  /**
    * Nodes of the AST obtained by expanding AST edges backwards until `root` or the method root is reached
    * */
  def inAst(root: AstNode): Traversal[AstNode] =
    traversal
      .repeat(_.in(EdgeTypes.AST))(
        _.emit
          .until(
            _.or(
              _.hasLabel(NodeTypes.METHOD),
              _.filter(n => root != null && root == n)
            )))
      .cast[AstNode]

  /**
    * Nodes of the AST obtained by expanding AST edges backwards until `root` or the method root is reached,
    * minus this node
    * */
  def inAstMinusLeaf(root: AstNode): Traversal[AstNode] =
    traversal
      .repeat(_.in(EdgeTypes.AST))(
        _.emitAllButFirst
          .until(
            _.or(
              _.hasLabel(NodeTypes.METHOD),
              _.filter(n => root != null && root == n)
            )))
      .cast[AstNode]

  /**
    * Traverse only to those AST nodes that are also control flow graph nodes
    * */
  def isCfgNode: Traversal[CfgNode] =
    traversal.collectAll[CfgNode]

  /**
    * Traverse only to those AST nodes that are blocks
    * */
  def isBlock: Traversal[Block] =
    traversal.hasLabel(NodeTypes.BLOCK).cast[Block]

  /**
    * Traverse only to those AST nodes that are control structures
    * */
  def isControlStructure: Traversal[ControlStructure] =
    traversal.hasLabel(NodeTypes.CONTROL_STRUCTURE).cast[ControlStructure]

  /**
    * Traverse only to AST nodes that are expressions
    * */
  def isExpression: Traversal[Expression] =
    traversal.collectAll[Expression]

  /**
    * Traverse only to AST nodes that are calls
    * */
  def isCall: Traversal[Call] =
    traversal.hasLabel(NodeTypes.CALL).cast[Call]

  /**
  Cast to call if applicable and filter on call code `calleeRegex`
    */
  def isCall(calleeRegex: String): Traversal[Call] =
    isCall.where(_.code(calleeRegex))

  /**
    * Traverse only to AST nodes that are literals
    * */
  def isLiteral: Traversal[Literal] =
    traversal.hasLabel(NodeTypes.LITERAL).cast[Literal]

  def isLocal: Traversal[Local] =
    traversal.hasLabel(NodeTypes.LOCAL).cast[Local]

  /**
    * Traverse only to AST nodes that are identifier
    * */
  def isIdentifier: Traversal[Identifier] =
    traversal.hasLabel(NodeTypes.IDENTIFIER).cast[Identifier]

  /**
    * Traverse only to FILE AST nodes
    * */
  def isFile: Traversal[File] =
    traversal.hasLabel(NodeTypes.FILE).cast[File]

  /**
    * Traverse only to AST nodes that are field identifier
    * */
  def isFieldIdentifier: Traversal[FieldIdentifier] =
    traversal.hasLabel(NodeTypes.FIELD_IDENTIFIER).cast[FieldIdentifier]

  /**
    * Traverse only to AST nodes that are return nodes
    * */
  def isReturn: Traversal[Return] =
    traversal.hasLabel(NodeTypes.RETURN).cast[Return]

  /**
    * Traverse only to AST nodes that are MEMBER
    */
  def isMember: Traversal[Member] =
    traversal.hasLabel(NodeTypes.MEMBER).cast[Member]

  /**
    * Traverse only to AST nodes that are method reference
    */
  def isMethodRef: Traversal[MethodRef] =
    traversal.hasLabel(NodeTypes.METHOD_REF).cast[MethodRef]

  /**
    * Traverse only to AST nodes that are type reference
    */
  def isTypeRef: Traversal[MethodRef] =
    traversal.hasLabel(NodeTypes.TYPE_REF).cast[MethodRef]

  /**
    * Traverse only to AST nodes that are METHOD
    */
  def isMethod: Traversal[Method] =
    traversal.hasLabel(NodeTypes.METHOD).cast[Method]

  /**
    * Traverse only to AST nodes that are MODIFIER
    */
  def isModifier: Traversal[Modifier] =
    traversal.hasLabel(NodeTypes.MODIFIER).cast[Modifier]

  /**
    * Traverse only to AST nodes that are NAMESPACE_BLOCK
    */
  def isNamespaceBlock: Traversal[NamespaceBlock] =
    traversal.hasLabel(NodeTypes.NAMESPACE_BLOCK).cast[NamespaceBlock]

  /**
    * Traverse only to AST nodes that are METHOD_PARAMETER_IN
    */
  def isParameter: Traversal[MethodParameterIn] =
    traversal.hasLabel(NodeTypes.METHOD_PARAMETER_IN).cast[MethodParameterIn]

  /**
    * Traverse only to AST nodes that are TYPE_DECL
    */
  def isTypeDecl: Traversal[TypeDecl] =
    traversal.hasLabel(NodeTypes.TYPE_DECL).cast[TypeDecl]

  def walkAstUntilReaching(labels: List[String]): Traversal[StoredNode] =
    traversal
      .repeat(_.out(EdgeTypes.AST))(_.emitAllButFirst.until(_.hasLabel(labels: _*)))
      .dedup
      .cast[StoredNode]

}
