package com.github.calumleslie.hugs.irc.context

import scala.collection.immutable.Set

case class ChannelContext(name: String, topic: Option[String], members: Set[String]) {
  def withTopic(newTopic: Option[String]) = ChannelContext(name, newTopic, members)
  def withMembers(newMembers: Set[String]) = ChannelContext(name, topic, newMembers)
}

object ChannelContext {
  def empty(name: String) = ChannelContext(name, None, Set())
}