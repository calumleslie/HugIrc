package com.github.calumleslie.hugs.irc

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class ExampleSpec extends FunSpec with ShouldMatchers {

  describe("An IRC parser") {
    val parser = new Parser

    it("should parse simple commands lines") {
      parser.parse("QUIT") shouldBe Message(None, "QUIT", Seq())
    }

    it("should parse a full command without remaining section") {
      parser.parse(":prefix JOIN #wherever whatever") shouldBe Message(Some("prefix"), "JOIN", Seq("#wherever", "whatever"))
    }

    it("should parse a full command with a remaining section") {
      parser.parse(":prefix PRIVMSG #somewhere :hello pop pickers: how are  things?") shouldBe
        Message(Some("prefix"), "PRIVMSG", Seq("#somewhere", "hello pop pickers: how are  things?"))
    }
  }
}
