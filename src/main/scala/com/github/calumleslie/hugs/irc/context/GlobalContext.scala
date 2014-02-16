package com.github.calumleslie.hugs.irc.context

import scala.collection.immutable._

case class GlobalContext(channels: Map[String, ChannelContext]) {
  def updatedChannel(name: String, transformation: ChannelContext => ChannelContext) = {
    val updatedChannel = transformation(channels(name))

    GlobalContext(channels.updated(name, updatedChannel))
  }
}

object GlobalContext {
  val empty = GlobalContext(Map().withDefault(name => ChannelContext.empty(name)))
}