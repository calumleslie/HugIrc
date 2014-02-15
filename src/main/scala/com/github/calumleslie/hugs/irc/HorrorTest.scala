package com.github.calumleslie.hugs.irc

import com.typesafe.scalalogging.slf4j.Logging
import rx.lang.scala.Observer
import com.github.calumleslie.hugs.irc.messages.PING
import com.github.calumleslie.hugs.irc.messages.PONG
import com.github.calumleslie.hugs.irc.messages.PRIVMSG
import rx.lang.scala.Observable
import scala.concurrent.duration._

object HorrorTest extends App with Logging {
  val bot = new Bot()

  echo(bot.messages).subscribe(bot)
  pong(bot.messages).subscribe(bot)

  bot.maintainConnectionTo("localhost", 6667, Identity("hugbot"))

  def echo(messages: Observable[(ConnectionId, Message)]) = for {
    (cid, PRIVMSG(_, to :: message :: _)) <- messages
  } yield SendMessage(cid, PRIVMSG(to, message))

  def pong(messages: Observable[(ConnectionId, Message)]) = for {
    (cid, PING(_, args)) <- messages
  } yield SendMessage(cid, PONG(args: _*))
}