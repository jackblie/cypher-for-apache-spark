/*
 * Copyright (c) 2016-2018 "Neo4j Sweden, AB" [https://neo4j.com]
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
 *
 * Attribution Notice under the terms of the Apache License 2.0
 *
 * This work was created by the collective efforts of the openCypher community.
 * Without limiting the terms of Section 6, any Derivative Work that is not
 * approved by the public consensus process of the openCypher Implementers Group
 * should not be described as “Cypher” (and Cypher® is a registered trademark of
 * Neo4j Inc.) or as "openCypher". Extensions by implementers or prototypes or
 * proposals for change that have been documented or implemented should only be
 * described as "implementation extensions to Cypher" or as "proposed changes to
 * Cypher that are not yet approved by the openCypher community".
 */
package org.opencypher.spark.api.value

import org.opencypher.okapi.api.value.CypherValue._
import org.opencypher.okapi.testing.BaseTestSuite

class CAPSValueToStringTest extends BaseTestSuite {

  test("node") {
    CAPSNode(1L, Set.empty, CypherMap.empty).toCypherString should equal("()")
    CAPSNode(1L, Set("A"), CypherMap.empty).toCypherString should equal("(:A)")
    CAPSNode(1L, Set("A", "B"), CypherMap.empty).toCypherString should equal("(:A:B)")
    CAPSNode(1L, Set("A", "B"), CypherMap("a" -> "b")).toCypherString should equal("(:A:B {a: 'b'})")
    CAPSNode(1L, Set("A", "B"), CypherMap("a" -> "b", "b" -> 1)).toCypherString should equal("(:A:B {a: 'b', b: 1})")
    CAPSNode(1L, Set.empty, CypherMap("a" -> "b", "b" -> 1)).toCypherString should equal("({a: 'b', b: 1})")
  }

  test("relationship") {
    CAPSRelationship(1L, 1L, 1L, "A", CypherMap.empty).toCypherString should equal("[:A]")
    CAPSRelationship(1L, 1L, 1L, "A", CypherMap("a" -> "b")).toCypherString should equal("[:A {a: 'b'}]")
    CAPSRelationship(1L, 1L, 1L, "A", CypherMap("a" -> "b", "b" -> 1)).toCypherString should equal("[:A {a: 'b', b: 1}]")
  }

  test("literals") {
    CypherInteger(1L).toCypherString should equal("1")
    CypherFloat(3.14).toCypherString should equal("3.14")
    CypherString("foo").toCypherString should equal("'foo'")
    CypherString("").toCypherString should equal("''")
    CypherBoolean(true).toCypherString should equal("true")
  }

  test("list") {
    CypherList().toCypherString should equal("[]")
    CypherList("A", "B", 1L).toCypherString should equal("['A', 'B', 1]")
  }

  test("map") {
    CypherMap().toCypherString should equal("{}")
    CypherMap("a" -> 1).toCypherString should equal("{a: 1}")
    CypherMap("a" -> 1, "b" -> true).toCypherString should equal("{a: 1, b: true}")
  }

  test("should escape apostrophes in strings") {
    CypherMap("street" -> "59 rue de l'Abbaye").toCypherString should equal("{street: '59 rue de l\\'Abbaye'}")
    CypherMap("street" -> "59 rue de l\"Abbaye'").toCypherString should equal("{street: '59 rue de l\\\"Abbaye\\''}")
    CAPSNode(1, Set.empty, CypherMap("street" -> "59 rue de l\"Abbaye'")).toCypherString should equal("({street: '59 rue de l\\\"Abbaye\\''})")
    CAPSRelationship(1, 2, 3, "FOO", CypherMap("street" -> "59 rue de l\"Abbaye'")).toCypherString should equal("[:FOO {street: '59 rue de l\\\"Abbaye\\''}]")
  }
}
