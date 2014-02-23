package com.github.calumleslie.hugs.irc.stacks

import com.github.calumleslie.hugs.irc.context.ReplyableContext
import rx.lang.scala.Observable
import com.github.calumleslie.hugs.irc.Message
import com.github.calumleslie.hugs.irc.messages.PRIVMSG
import com.github.calumleslie.hugs.irc.matchers.BotRequest
import scala.collection.immutable.Map
import scala.collection.immutable.SortedMap
import com.github.calumleslie.hugs.stacks.Definition
import com.github.calumleslie.hugs.stacks.lib
import com.github.calumleslie.hugs.stacks.Parser
import com.github.calumleslie.hugs.stacks.State
import com.github.calumleslie.hugs.irc.Command
import com.github.calumleslie.hugs.irc.Bot
import com.typesafe.scalalogging.slf4j.Logging
import com.github.calumleslie.hugs.stacks.PureDefinition

abstract class StacksInstance extends HasDictionary with Logging {
  def attach(bot: Bot) = {
    evalRequest(bot.repliableMessages).subscribe(bot.commands)
    defineRequest(bot.repliableMessages).subscribe(bot.commands)
  }

  def defineRequest(messages: Observable[(ReplyableContext, Message)]) = for {
    (ctx, BotRequest("!define", Some(arguments))) <- messages
  } yield {
    val name = arguments.takeWhile(_ != ' ')
    val definition = arguments.dropWhile(_ != ' ')

    ctx.replyWithPrefix(define(name, definition))
  }

  def evalRequest(messages: Observable[(ReplyableContext, Message)]) = for {
    (ctx, BotRequest("!eval", Some(program))) <- messages
  } yield {
    ctx.replyWithPrefix(doOrErrorMessage {
      eval(program).stackStr
    })
  }

  private[this] def define(name: String, code: String): String = {
    if (dictionary.contains(name)) {
      return s"Name $name is already defined!"
    }

    if (name.isEmpty || code.isEmpty) {
      return "Usage: !define <word> <definition>, e.g. !define square dup *"
    }

    doOrErrorMessage {
      val definition = PureDefinition(code)

      addWord(name, definition)

      s"Word $name successfully defined!"
    }
  }

  private[this] def doOrErrorMessage(procedure: => String) = try {
    procedure
  } catch {
    case e: Exception => {
      logger.error("Error in definition", e)

      val displayException = if (e.getCause() == null) e else e.getCause()
      val error = s"Error was thrown: ${displayException.getMessage()}"

      error.take(255)
    }
  }

  private[this] def eval(code: String) = {
    val program = Parser.parse(code)
    val initial = State(program, dictionary)

    initial.eval
  }
}