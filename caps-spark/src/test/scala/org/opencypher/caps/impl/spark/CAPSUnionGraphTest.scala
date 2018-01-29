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
package org.opencypher.caps.impl.spark

import org.apache.spark.sql.Row
import org.opencypher.caps.test.CAPSTestSuite
import org.opencypher.caps.test.fixture.{GraphCreationFixture, TeamDataFixture}

import scala.collection.Bag

class CAPSUnionGraphTest extends CAPSTestSuite with GraphCreationFixture with TeamDataFixture {
  import CAPSGraphTestData._

  test("Node scan from single node CAPSRecords") {
    val inputGraph = initGraph(`:Person`)
    val inputNodes = inputGraph.nodes("n")

    val patternGraph = CAPSUnionGraph(CAPSGraph.create(inputNodes, inputGraph.schema))
    val outputNodes = patternGraph.nodes("n")

    outputNodes.toDF().columns should equal(Array(
      "n",
      "____n:Person",
      "____n:Swedish",
      "____n_dot_luckyNumberINTEGER",
      "____n_dot_nameSTRING"
    ))

    outputNodes.toDF().collect().toBag should equal (Bag(
      Row(0L, true, true, 23L, "Mats"),
      Row(1L, true, false, 42L, "Martin"),
      Row(2L, true, false, 1337L, "Max"),
      Row(3L, true, false, 9L, "Stefan"))
    )
  }

  test("Node scan from multiple single node CAPSRecords") {
    val unionGraph = CAPSUnionGraph(initGraph(`:Person`), initGraph(`:Book`))
    val outputNodes = unionGraph.nodes("n")

    outputNodes.toDF().columns should equal(Array(
      "n",
      "____n:Book",
      "____n:Person",
      "____n:Swedish",
      "____n_dot_luckyNumberINTEGER",
      "____n_dot_nameSTRING",
      "____n_dot_titleSTRING",
      "____n_dot_yearINTEGER"
    ))

    outputNodes.toDF().collect().toBag should equal(Bag(
      Row(0L, false, true, true, 23L, "Mats", null, null),
      Row(1L, false, true, false, 42L, "Martin", null, null),
      Row(2L, false, true, false, 1337L, "Max", null, null),
      Row(3L, false, true, false, 9L, "Stefan", null, null),
      Row(0L, true, false, false, null, null, "1984", 1949L),
      Row(1L, true, false, false, null, null, "Cryptonomicon", 1999L),
      Row(2L, true, false, false, null, null, "The Eye of the World", 1990L),
      Row(3L, true, false, false, null, null, "The Circle", 2013L)))
  }

  test("Returns only distinct results") {
    val inputGraph = initGraph(`:Person`)
    val inputNodes = inputGraph.nodes("n")
    val patternGraph = CAPSGraph.create(inputNodes, inputGraph.schema)

    val scanGraph = CAPSGraph.create(personTable)

    val unionGraph = CAPSUnionGraph(patternGraph, scanGraph)
    val outputNodes = unionGraph.nodes("n")

    outputNodes.toDF().columns should equal(Array(
      "n",
      "____n:Person",
      "____n:Swedish",
      "____n_dot_luckyNumberINTEGER",
      "____n_dot_nameSTRING"
    ))

    outputNodes.toDF().collect().toBag should equal(Bag(
      Row(0L, true, true, 23L, "Mats"),
      Row(2L, true, false, 1337L, "Max"),
      Row(1L, true, false, 42L, "Martin"),
      Row(3L, true, false, 9L, "Stefan"))
    )
  }

  private def initPersonReadsBookGraph: CAPSGraph = {
    CAPSUnionGraph(
      initGraph(`:READS`),
      CAPSUnionGraph(initGraph(`:Book`),
        CAPSUnionGraph(initGraph(`:Person`))))
  }
}
