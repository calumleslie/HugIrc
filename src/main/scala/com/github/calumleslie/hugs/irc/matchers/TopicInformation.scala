package com.github.calumleslie.hugs.irc.matchers

import com.github.calumleslie.hugs.irc.messages.TOPIC
import com.github.calumleslie.hugs.irc.Message
import com.github.calumleslie.hugs.irc.messages.Responses

case object TopicInformation {
  def unapply(message: Message): Option[(String, Option[String])] = message match {
    case TOPIC(_, channel :: topic :: Nil) => Some((channel, Some(topic)))
    case Message(_, Responses.RPL_TOPIC, _ :: channel :: topic :: Nil) => Some((channel, Some(topic)))
    case Message(_, Responses.RPL_NOTOPIC, _ :: channel :: _) => Some(channel, None)
    case _ => None
  }
}