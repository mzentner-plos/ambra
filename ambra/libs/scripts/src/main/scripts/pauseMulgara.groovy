/* $HeadURL:: http://gandalf.topazproject.org/svn/branches/0.8.2.2/plos/libs/scripts/src/main/scr#$
 * $Id: alertsinfo.groovy 3478 2007-08-16 17:51:22Z ebrown $
 *
 * Copyright (c) 2006-2008 by Topaz, Inc.
 * http://topazproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.plos.util.ToolHelper

import org.topazproject.mulgara.itql.ItqlHelper;

log = LogFactory.getLog(this.getClass())

// Use ToolHelper to patch args
args = ToolHelper.fixArgs(args)

def cli = new CliBuilder(usage: 'lockMulgara [-c config-overrides.xml]')
cli.h(longOpt:'help', "help (this message)")
cli.c(args:1, 'config-overrides.xml - overrides /etc/topaz.xml')

// Display help if requested
def opt = cli.parse(args); if (opt.h) { cli.usage(); return }

// Load configuration
CONF = ToolHelper.loadConfiguration(opt.c)

def mulgaraUri = CONF.getString("ambra.topaz.tripleStore.mulgara.itql.uri")
def operation = '';

println "Mulgara URI: $mulgaraUri"

itql = new ItqlHelper(URI.create(mulgaraUri))
  
lockCmd = "set autocommit off"
println "Issuing lock cmd: $lockCmd ..."
itql.doUpdate(lockCmd, null)

println "Running script"
Process myProcess = Runtime.getRuntime().exec('/bin/false')

lockCmd = "set autocommit on"
println "Issuing lock cmd: $lockCmd ..."
itql.doUpdate(lockCmd, null)

println " completed!"
System.exit(0)