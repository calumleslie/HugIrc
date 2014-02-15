package com.github.calumleslie.hugs.irc

case class Identity(nick: String, username: String, realname: String)

object Identity {
  def apply(nick: String): Identity = Identity(nick, nick, nick)
  def apply(nick: String, realname: String): Identity = Identity(nick, nick, realname)
}