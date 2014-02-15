package com.github.calumleslie.hugs.irc

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.PackratParsers

class Parser {
  def parse(line: String) = {
    ParserImpl.parseAll(ParserImpl.message, line).get
  }

  private object ParserImpl extends RegexParsers {

    def message: Parser[Message] = prefix.? ~ command ~ params.? ^^ {
      case prefix ~ command ~ Some(params) => Message(prefix, command, params)
      case prefix ~ command ~ None => Message(prefix, command, Nil)
    }

    def params: Parser[List[String]] = rep(param) ~ trailing.? ^^ {
      case params ~ Some(trailing) => params :+ trailing
      case params ~ None => params
    } 
    
    def trailing = " :" ~ """.*""".r ^^ { case _ ~ content => content }
    
    def param = " " ~ """[^:][^ ]*""".r ^^ { case _ ~ param => param }
    
    // Not as strict as spec (sensible?)
    def prefix = ":" ~ """[^: ]+""".r ~ " " ^^ { case _ ~ prefix ~ _ => prefix }
    
    def command = """[A-Za-z]+""".r | """[0-9]{3}""".r

    def space: Parser[String] = " "
      
    override def skipWhitespace = false
  }
}