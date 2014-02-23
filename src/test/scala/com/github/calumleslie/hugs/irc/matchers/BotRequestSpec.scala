package com.github.calumleslie.hugs.irc.matchers

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.github.calumleslie.hugs.irc.messages.PRIVMSG

@RunWith(classOf[JUnitRunner])
class BotRequestSpec extends FunSpec with ShouldMatchers {

  describe("The bot request matcher") {
    it("should match commands without arguments") {
      PRIVMSG("#channel", "!hello") match {
        case BotRequest("!hello", None) => // Success!
        case _ => fail()
      }
    }
    it("should match commands with arguments") {
      PRIVMSG("#channel", "!hello world this is a test  ") match {
        case BotRequest("!hello", Some("world this is a test")) => // Success!
        case _ => fail()
      }
    }
  }
}