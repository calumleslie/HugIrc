package com.github.calumleslie.hugs.irc

import scala.collection.mutable.StringBuilder

case class Message (
    val target: Option[String],
    val command: String,
    val arguments: List[String]
) {
  def toLine: String = {
    val builder = new StringBuilder()
    if( target.isDefined ) {
    	builder += ':'
 		builder ++= target.get
 		builder += ' '
    }
    
    builder ++= command
    
    for ( arg <- arguments ) {
      builder += ' '
      if( arg.contains(' ') || arg.contains(':') ) {
        builder += ':'
      }
      builder ++= arg
    }
    builder.toString()
  }
}

