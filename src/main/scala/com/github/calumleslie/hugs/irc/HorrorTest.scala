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
import com.github.calumleslie.hugs.irc.context.InChannel
import com.github.calumleslie.hugs.irc.messages.JOIN

object HorrorTest extends App with Logging {
  val bot = Bot.connect("localhost", 6667, Identity("hugbot"))
  bot.stayJoinedTo("#hello")

  tellMeTopic(bot.channelMessages).subscribe(bot.commands)
  echo(bot.repliableMessages, "(yay!)").subscribe(bot.commands)
  def echo(messages: Observable[(ReplyableContext, Message)], suffix: String) = for {
    (ctx, PRIVMSG(_, _ :: message)) <- messages
  } yield {
    ctx.replyWithPrefix(s"${message.mkString(" ")} $suffix")
  }

  def tellMeTopic(messages: Observable[(InChannel, Message)]) = for {
    (ctx @ InChannel(_, _, channel, _), PRIVMSG(_, _ :: "!topic" :: Nil)) <- messages
  } yield ctx.replyWithPrefix(channel.topic.getOrElse("No topic set"))

}