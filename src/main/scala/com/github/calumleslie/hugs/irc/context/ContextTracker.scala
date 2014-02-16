package com.github.calumleslie.hugs.irc.context

import com.github.calumleslie.hugs.irc.Bot
import rx.lang.scala.Observable
import com.github.calumleslie.hugs.irc.Message
import com.github.calumleslie.hugs.irc.ConnectionId
import com.github.calumleslie.hugs.irc.Event
import scala.collection._
import scala.collection.mutable.HashMap
import com.github.calumleslie.hugs.irc.Disconnected
import com.github.calumleslie.hugs.irc.Connected
import com.github.calumleslie.hugs.irc.MessageReceived
import com.github.calumleslie.hugs.irc.messages.PRIVMSG
import com.github.calumleslie.hugs.irc.messages.Chan
import com.github.calumleslie.hugs.irc.messages.NAMES
import com.github.calumleslie.hugs.irc.messages.TOPIC
import com.github.calumleslie.hugs.irc.messages.TopicInformation
import com.github.calumleslie.hugs.irc.MessageReceived

class ContextTracker {

  private val connectionContexts: mutable.Map[ConnectionId, GlobalContext] = new HashMap().withDefaultValue(GlobalContext.empty)
  private val logLinesKept = 1000

  def updateContext(event: Event) {
    val connectionContext = connectionContexts(event.connectionId)

    val newGlobalContext = updateGlobalContext(connectionContext, event)

    connectionContexts(event.connectionId) = newGlobalContext
  }

  def contextFor(event: Event): Context = event match {
    case MessageReceived(_, message) => contextFor(event.connectionId, message, connectionContexts(event.connectionId))
    case _ => Other(event.connectionId, connectionContexts(event.connectionId))
  }

  def contextFor(connectionId: ConnectionId, message: Message, global: GlobalContext): Context = message match {
    case PRIVMSG(_, Chan(channel) :: message :: Nil) => InChannel(connectionId, global.channels(channel), global)
    case PRIVMSG(_, sender :: message :: Nil) => InPrivateMessage(connectionId, sender, global)
    case _ => Other(connectionId, global)
  }

  def updateGlobalContext(existing: GlobalContext, event: Event): GlobalContext = event match {
    case Disconnected(_) => GlobalContext.empty
    case MessageReceived(_, message) => updateGlobalContext(existing, message)
    case _ => existing
  }

  def updateGlobalContext(existing: GlobalContext, message: Message): GlobalContext = message match {
    case NAMES(from, Chan(channel) :: users) => existing.updatedChannel(channel, _.withMembers(immutable.Set(users: _*)))
    case TopicInformation(channel, topic) => existing.updatedChannel(channel, _.withTopic(topic))
    case _ => existing
  }

  def track(events: Observable[Event]) = for {
    event <- events.synchronize.doOnEach(updateContext(_))
  } yield (event, contextFor(event))

}