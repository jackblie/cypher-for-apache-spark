/*
 * Copyright (c) 2016-2018 "Neo4j, Inc." [https://neo4j.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencypher.caps.api.io

import org.opencypher.caps.api.graph.PropertyGraph
import org.opencypher.caps.api.schema.Schema

/**
  * The Property Graph Data Source (PGDS) is used to read and write property graphs from and to an external storage
  * (e.g., a database system, a file system or an memory-based collections of graph data).
  *
  * The PGDS is the main interface for implementing custom data sources for specific openCypher implementations
  * (e.g., for Apache Spark, etc.).
  *
  * The (PGDS) is able to handle multiple property graphs and  distinguishes between them using [[GraphName]]s.
  * Furthermore, a PGDS can be registered at a [[org.opencypher.caps.api.graph.CypherSession]] using a specific
  * [[Namespace]] which enables accessing a [[PropertyGraph]] within a Cypher query.
  */
trait PropertyGraphDataSource {

  /**
    * Returns {{true}} if the data source stores a graph under the given [[GraphName]].
    *
    * @param name name of the graph within the data source
    * @return {{true}}, iff the graph is stored within the data source
    */
  def hasGraph(name: GraphName): Boolean

  /**
    * Returns the [[PropertyGraph]] that is stored under the given name.
    *
    * @param name name of the graph within the data source
    * @return property graph
    */
  def graph(name: GraphName): PropertyGraph

  /**
    * Returns the [[Schema]] of the graph that is stored under the given name.
    *
    * This method gives implementers the ability to efficiently retrieve a graph schema from the data source directly.
    * If an efficient retrieval is not possible, the call is typically forwarded to the graph using the
    * [[PropertyGraph.schema]] call.
    *
    * @param name name of the graph within the data source
    * @return graph schema
    */
  def schema(name: GraphName): Option[Schema]

  /**
    * Stores the given [[PropertyGraph]] under the given [[GraphName]] within the data source.
    *
    * @param name  name under which the graph shall be stored
    * @param graph property graph
    */
  def store(name: GraphName, graph: PropertyGraph): Unit

  /**
    * Deletes the [[PropertyGraph]] within the data source that is stored under the given [[GraphName]].
    *
    * @param name name under which the graph is stored
    */
  def delete(name: GraphName): Unit

  /**
    * Returns the [[GraphName]]s of all [[PropertyGraph]]s stored within the data source.
    *
    * @return names of stored graphs
    */
  def graphNames: Set[GraphName]

}

// TODO: Move to another file QualifiedGraphName in graph package
// TODO: Remove companions and use normal case classes
object GraphName {
  def from(graphName: String) = GraphName(graphName)
}

/**
  * A graph name is used to address a specific graph withing a [[Namespace]] and is used for lookups in the
  * [[org.opencypher.caps.api.graph.CypherSession]].
  *
  * @param value string representing the graph name
  */
case class GraphName private(value: String) extends AnyVal {
  override def toString: String = value
}

object Namespace {
  def from(namespace: String) = Namespace(namespace)
}

/**
  * A namespace is used to address different [[PropertyGraphDataSource]] implementations within a
  * [[org.opencypher.caps.api.graph.CypherSession]].
  *
  * @param value string representing the namespace
  */
case class Namespace private(value: String) extends AnyVal {
  override def toString: String = value
}

object QualifiedGraphName {
  def from(namespace: String, graphName: String) =
    QualifiedGraphName(Namespace.from(namespace), GraphName.from(graphName))
}
/**
  * A qualified graph name is used in a Cypher query to address a specific graph within a namespace.
  *
  * Example:
  *
  * {{{
  * FROM GRAPH AT 'myNamespace.myGraphName' MATCH (n) RETURN n
  * }}}
  *
  * Here, {{myNamespace.myGraphName}} represents a qualified graph name.
  *
  * @param namespace namespace part of the qualified graph name
  * @param graphName graph name part of the qualified graph name
  */
case class QualifiedGraphName private(namespace: Namespace, graphName: GraphName) {
  override def toString: String = s"$namespace.$graphName"
}