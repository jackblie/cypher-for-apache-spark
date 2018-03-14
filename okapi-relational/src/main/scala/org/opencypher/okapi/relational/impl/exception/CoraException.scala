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
package org.opencypher.okapi.relational.impl.exception

import org.opencypher.okapi.impl.exception.InternalException
import org.opencypher.okapi.ir.api.expr.Var

abstract class CoraException(msg: String) extends InternalException(msg)

final case class RecordHeaderException(msg: String) extends CoraException(msg)

final case class DuplicateSourceColumnException(columnName: String, entity: Var)
    extends CoraException(
          s"The source column '$columnName' is used more than once to describe the mapping of $entity")