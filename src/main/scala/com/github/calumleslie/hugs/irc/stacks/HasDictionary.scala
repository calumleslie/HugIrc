package com.github.calumleslie.hugs.irc.stacks

import scala.collection.immutable.SortedMap
import com.github.calumleslie.hugs.stacks.Definition

trait HasDictionary {
  def dictionary: SortedMap[String, Definition]
  def addWord(name: String, definition: Definition)
}