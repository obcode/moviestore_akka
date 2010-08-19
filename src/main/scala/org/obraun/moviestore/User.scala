/**
 * Copyright (c) 2010 Oliver Braun
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the author nor the names of his contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.obraun.moviestore

import java.util.Calendar

class User private (
    val name: String,
    val dateOfBirth: Calendar,
    val id: Int
  ) {
  override def toString = id+": "+name+" ("+age+")"
  def age = {
    val today =  Calendar.getInstance
    val age = today.get(Calendar.YEAR) -
              dateOfBirth.get(Calendar.YEAR)
    today.set(Calendar.YEAR,
              dateOfBirth.get(Calendar.YEAR))
    if (today before dateOfBirth)
      age-1
    else
      age
  }
}
object User {
  private[this] var id = 0
  def apply(name: String, dateOfBirth: Calendar) = {
    id += 1
    new User(name, dateOfBirth, id)
  }
  def unapply(x: Any) = {
    x match {
      case c: User => Some(c.name,c.dateOfBirth,c.id)
      case _ => None
    }
  }
}
// vim: set ts=2 sw=4 et:
