package com.github.calumleslie.hugs.irc

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class ParserSpec extends FunSpec with ShouldMatchers {

  describe("An IRC parser") {
    val parser = new Parser

    it("should parse simple commands lines") {
      parser.parse("QUIT") shouldBe Message(None, "QUIT", List())
    }

    it("should parse a numeric command") {
      parser.parse("005 hugs CHANNELLEN=50") shouldBe Message(None, "005", List("hugs", "CHANNELLEN=50"))
    }

    it("should parse a full command without remaining section") {
      parser.parse(":prefix JOIN #wherever whatever") shouldBe Message(Some("prefix"), "JOIN", List("#wherever", "whatever"))
    }

    it("should parse a full command with a remaining section") {
      parser.parse(":prefix PRIVMSG #somewhere :hello pop pickers: how are  things?") shouldBe
        Message(Some("prefix"), "PRIVMSG", List("#somewhere", "hello pop pickers: how are  things?"))

      println(Message(Some("prefix"), "PRIVMSG", List("#somewhere", "hello pop pickers: how are  things?")).toLine)
    }

    it("should handle params with colons") {
      parser.parse(":irc.example.net 005 hugs CHANLIMIT=#&+:10 :are supported on this server") shouldBe
        Message(Some("irc.example.net"), "005", List("hugs", "CHANLIMIT=#&+:10", "are supported on this server"))
    }
  }
}
