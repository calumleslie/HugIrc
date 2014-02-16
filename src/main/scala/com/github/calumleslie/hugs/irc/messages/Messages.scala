package com.github.calumleslie.hugs.irc.messages

import com.github.calumleslie.hugs.irc.Message

case object NICK extends MessageType {
  val command = "NICK"
}

case object USER extends MessageType {
  val command = "USER"
  def apply(username: String, realname: String): Message = apply(username, "0", "*", realname)
}

case object JOIN extends MessageType {
  val command = "JOIN"
}

case object PING extends MessageType {
  val command = "PING"
}

case object PONG extends MessageType {
  val command = "PONG"
}

case object PRIVMSG extends MessageType {
  val command = "PRIVMSG"
}

case object NAMES extends MessageType {
  val command = "NAMES"
}

case object TOPIC extends MessageType {
  val command = "TOPIC"
}

case object TopicInformation {
  def unapply(message: Message): Option[(String, Option[String])] = message match {
    case TOPIC(_, channel :: topic :: Nil) => Some((channel, Some(topic)))
    case Message(_, Responses.RPL_TOPIC, _ :: channel :: topic :: Nil) => Some((channel, Some(topic)))
    case Message(_, Responses.RPL_NOTOPIC, _ :: channel :: _) => Some(channel, None)
    case _ => None
  }
}

case object Chan {
  def unapply(string: String): Option[String] = if (string.startsWith("#")) Some(string) else None
}

trait MessageType {
  val command: String

  def apply(target: Option[String], params: String*): Message = Message(target, command, List(params: _*))
  def apply(params: String*): Message = apply(None, params: _*)

  def unapply(message: Message): Option[(Option[String], List[String])] = message match {
    case Message(target, `command`, params) => Some((target, params))
    case _ => None
  }
}