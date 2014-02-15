package com.github.calumleslie.hugs.irc

sealed trait Event
case class MessageReceived( val connectionId: ConnectionId, val msg: Message ) extends Event
case class Disconnected( val connectionId: ConnectionId ) extends Event
case class Connected( val connectionId: ConnectionId ) extends Event