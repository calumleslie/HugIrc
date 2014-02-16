package com.github.calumleslie.hugs.irc

import com.github.calumleslie.hugs.irc.context.Context
import com.github.calumleslie.hugs.irc.context.ContextTracker
import com.github.calumleslie.hugs.irc.context.InChannel
import com.github.calumleslie.hugs.irc.context.ReplyableContext
import com.github.calumleslie.hugs.irc.messages.JOIN
import com.github.calumleslie.hugs.irc.messages.PING
import com.github.calumleslie.hugs.irc.messages.PONG
import rx.lang.scala.Observable
import rx.lang.scala.Observer
import scala.concurrent.duration._
import com.github.calumleslie.hugs.irc.messages.Responses

class Bot(val commands: Observer[Command], val connectionId: ConnectionId, val events: Observable[(Context, Event)]) {
  val messages = for {
    (context, MessageReceived(_, message)) <- events
  } yield (context, message)

  def repliableMessages = for {
    (ReplyableContext(context), message) <- messages
  } yield (context, message)

  def channelMessages = for {
    (context @ InChannel(_, _, _, _), message) <- repliableMessages
  } yield (context, message)

  def stayJoinedTo(channel: String) = joinIfNoChannelContext(channel).throttleFirst(10.seconds).subscribe(commands)

  private def joinIfNoChannelContext(channel: String) = for {
    (ctx, message) <- messages
    if !ctx.global.channels.contains(channel)
  } yield SendMessage(ctx.connectionId, JOIN(channel))
}

object Bot {
  def connect(host: String, port: Int, identity: Identity) = {
    val simpleBot = new SimpleBot()
    val tracker = new ContextTracker()

    val connectionId = simpleBot.maintainConnectionTo(host, port, identity)
    val bot = new Bot(simpleBot, connectionId, tracker.track(simpleBot))

    val pongs = for {
      (ctx, PING(_, args)) <- bot.messages
    } yield SendMessage(ctx.connectionId, PONG(args: _*))

    pongs.subscribe(bot.commands)

    bot
  }
}