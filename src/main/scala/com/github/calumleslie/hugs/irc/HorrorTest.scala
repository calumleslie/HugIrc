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

  val what = for { MessageReceived(connectionId, message) <- bot } yield (connectionId, message)

  val echo = Reactor[(ConnectionId, Message), Command] { (message: (ConnectionId, Message), subject: Observer[Command]) =>
    message match {
      case (cid, PRIVMSG(_, to :: message :: _)) => subject.onNext(SendMessage(cid, PRIVMSG(to, message)))
      case _ => ()
    }
  }

  bot.messages.subscribe(echo)
  echo.subscribe(bot)
  
  val ponger = Reactor[(ConnectionId, Message), Command] { (message: (ConnectionId, Message), subject: Observer[Command]) =>
    message match {
      case (cid, PING(_, args)) => subject.onNext(SendMessage(cid, PONG(args: _*)))
      case _ => ()
    }
  }

  bot.messages.subscribe(ponger)
  ponger.subscribe(bot)
}