package com.github.calumleslie.hugs.irc.matchers

import com.github.calumleslie.hugs.irc.Message
import com.github.calumleslie.hugs.irc.messages.PRIVMSG

object BotRequest {
  def unapply(message: Message): Option[(String, Option[String])] = message match {
    case PRIVMSG(_, _ :: RequestFormat(request, rawArg) :: Nil) => {
      val arg = rawArg.trim()

      Some((request, if (arg.isEmpty()) None else Some(arg)))
    }
  }

  private[this] val RequestFormat = """(\S+)( ?.*)|$""".r.anchored
}