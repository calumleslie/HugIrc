package com.github.calumleslie.hugs.irc.stacks

import com.github.calumleslie.hugs.stacks.Definition
import com.github.calumleslie.hugs.stacks.lib
import scala.collection.immutable.SortedMap

trait HasEphemeralDictionary extends HasDictionary {
  protected var dict: SortedMap[String, Definition] = lib.complete

  override def dictionary = dict
  override def addWord(name: String, definition: Definition) = {
    dict = dict + (name -> definition)
  }
}