package com.github.calumleslie.hugs.irc.context

import scala.collection.immutable.Map
import com.github.calumleslie.hugs.irc.ConnectionId
import com.github.calumleslie.hugs.irc.Command
import com.github.calumleslie.hugs.irc.SendMessage
import com.github.calumleslie.hugs.irc.messages.PRIVMSG

trait Context {
  val connectionId: ConnectionId
  val global: GlobalContext
}
trait ReplyableContext extends Context {
  def reply(reply: String): Command
}
object ReplyableContext {
  def unapply(context: Context): Option[ReplyableContext] = context match {
    case replyable: ReplyableContext => Some(replyable)
    case _ => None
  }
}
case class InChannel(connectionId: ConnectionId, channel: ChannelContext, global: GlobalContext) extends ReplyableContext {
  def reply(reply: String) = SendMessage(connectionId, PRIVMSG(channel.name, reply))
}
case class InPrivateMessage(connectionId: ConnectionId, user: String, global: GlobalContext) extends ReplyableContext {
  def reply(reply: String) = SendMessage(connectionId, PRIVMSG(user, reply))
}
case class Other(connectionId: ConnectionId, global: GlobalContext) extends Context