package com.github.calumleslie.hugs.irc

case class Message (
    val target: Option[String],
    val command: String,
    val arguments: Seq[String]
) {

}