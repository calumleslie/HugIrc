package com.github.calumleslie.hugs.irc

import com.typesafe.scalalogging.slf4j.Logging
import rx.lang.scala.Observer
import com.github.calumleslie.hugs.irc.messages.PING
import com.github.calumleslie.hugs.irc.messages.PONG
import com.github.calumleslie.hugs.irc.messages.PRIVMSG
import rx.lang.scala.Observable
import scala.concurrent.duration._
import com.github.calumleslie.hugs.irc.context.ContextTracker
import com.github.calumleslie.hugs.irc.messages.Chan
import com.github.calumleslie.hugs.irc.context.Context
import com.github.calumleslie.hugs.irc.context.ChannelContext
import com.github.calumleslie.hugs.irc.context.InChannel
import com.github.calumleslie.hugs.irc.context.ReplyableContext
import scala.util.Random
import rx.lang.scala.schedulers.IOScheduler
import rx.lang.scala.Subject

object HorrorTest extends App with Logging {
  val bot = new SimpleBot()

  val pongMessages = pong(bot.messages)
  pongMessages.subscribe(bot)
  pongMessages.subscribe(println(_))

  val contextTracker = new ContextTracker()
  val contextEvents = contextTracker.track(bot)

  val trackedRaw = for {
    (MessageReceived(_, message), context) <- contextEvents
  } yield (context, message)

  val trackedMessages = trackedRaw.observeOn(IOScheduler())

  tellMeTopic(trackedMessages).subscribe(bot)
  echo(trackedMessages, "(yay!)").subscribe(bot)

  val id = bot.maintainConnectionTo("localhost", 6667, Identity("hugbot"))

  def echo(messages: Observable[(Context, Message)], suffix: String) = for {
    (ReplyableContext(ctx), PRIVMSG(_, _ :: message)) <- messages
  } yield {
    ctx.replyWithPrefix(s"${message.mkString(" ")} $suffix")
  }

  def tellMeTopic(messages: Observable[(Context, Message)]) = for {
    (ctx @ InChannel(_, _, channel, _), PRIVMSG(_, _ :: "!topic" :: Nil)) <- messages
  } yield ctx.replyWithPrefix(channel.topic.getOrElse("No topic set"))

  def pong(messages: Observable[(ConnectionId, Message)]) = for {
    (cid, PING(_, args)) <- messages
  } yield SendMessage(cid, PONG(args: _*))
}