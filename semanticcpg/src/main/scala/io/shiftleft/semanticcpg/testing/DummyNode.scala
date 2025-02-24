package io.shiftleft.semanticcpg.testing

import io.shiftleft.codepropertygraph.generated.nodes.StoredNode

/** mixin trait for test nodes */
trait DummyNodeImpl extends StoredNode {
  // Members declared in io.shiftleft.codepropertygraph.generated.nodes.AbstractNode
  def label: String = ???

  // Members declared in overflowdb.Element
  def graph(): overflowdb.Graph = ???
  def property[A](x$1: overflowdb.PropertyKey[A]): A = ???
  def property(x$1: String): Object = ???
  def propertyKeys(): java.util.Set[String] = ???
  def propertiesMap(): java.util.Map[String, Object] = ???
  def propertyOption(x$1: String): java.util.Optional[Object] = ???
  def propertyOption[A](x$1: overflowdb.PropertyKey[A]): java.util.Optional[A] = ???
  def remove(): Unit = ???
  def removeProperty(x$1: String): Unit = ???
  def setProperty(x$1: overflowdb.Property[_]): Unit = ???
  def setProperty[A](x$1: overflowdb.PropertyKey[A], x$2: A): Unit = ???
  def setProperty(x$1: String, x$2: Object): Unit = ???

  // Members declared in scala.Equals
  def canEqual(that: Any): Boolean = ???

  // Members declared in overflowdb.Node
  def addEdge(x$1: String, x$2: overflowdb.Node, x$3: java.util.Map[String, Object]): overflowdb.Edge = ???
  def addEdge(x$1: String, x$2: overflowdb.Node, x$3: Object*): overflowdb.Edge = ???
  def addEdgeSilent(x$1: String, x$2: overflowdb.Node, x$3: java.util.Map[String, Object]): Unit = ???
  def addEdgeSilent(x$1: String, x$2: overflowdb.Node, x$3: Object*): Unit = ???
  def both(x$1: String*): java.util.Iterator[overflowdb.Node] = ???
  def both(): java.util.Iterator[overflowdb.Node] = ???
  def bothE(x$1: String*): java.util.Iterator[overflowdb.Edge] = ???
  def bothE(): java.util.Iterator[overflowdb.Edge] = ???
  def id(): Long = ???
  def in(x$1: String*): java.util.Iterator[overflowdb.Node] = ???
  def in(): java.util.Iterator[overflowdb.Node] = ???
  def inE(x$1: String*): java.util.Iterator[overflowdb.Edge] = ???
  def inE(): java.util.Iterator[overflowdb.Edge] = ???
  def out(x$1: String*): java.util.Iterator[overflowdb.Node] = ???
  def out(): java.util.Iterator[overflowdb.Node] = ???
  def outE(x$1: String*): java.util.Iterator[overflowdb.Edge] = ???
  def outE(): java.util.Iterator[overflowdb.Edge] = ???

  // Members declared in scala.Product
  def productArity: Int = ???
  def productElement(n: Int): Any = ???

  // Members declared in io.shiftleft.codepropertygraph.generated.nodes.StoredNode
  def productElementLabel(n: Int): String = ???
  def valueMap: java.util.Map[String, AnyRef] = ???
}
