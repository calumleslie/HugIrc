package com.github.calumleslie.hugs.irc

import java.util.UUID

sealed trait Command
case class Connect( connection: ConnectionId ) extends Command
case class SendMessage( connection: ConnectionId, message: Message ) extends Command

case object Connect {
  def apply( host: String, port: Int ): Connect = Connect( ConnectionId( host, port ) )
}