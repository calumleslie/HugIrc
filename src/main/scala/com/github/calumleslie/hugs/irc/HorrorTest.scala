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
import com.github.calumleslie.hugs.irc.stacks.StacksInstance
import com.github.calumleslie.hugs.irc.stacks.HasEphemeralDictionary

object HorrorTest extends App with Logging {
  val bot = Bot.connect("localhost", 6667, Identity("hugbot"))
  bot.stayJoinedTo("#hello")
  
  val stacks = new StacksInstance with HasEphemeralDictionary
  stacks.attach(bot)

}