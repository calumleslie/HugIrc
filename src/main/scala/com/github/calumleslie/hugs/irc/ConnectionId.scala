package com.github.calumleslie.hugs.irc

import java.util.UUID

case class ConnectionId(host: String, port: Int, unique: UUID)

object ConnectionId {
  def apply( host: String, port: Int ): ConnectionId = ConnectionId( host, port, UUID.randomUUID() )
}