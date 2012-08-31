<#--
  $HeadURL::                                                                            $
  $Id$
  
  Copyright (c) 2007-2008 by Topaz, Inc.
  http://topazproject.org
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<html>
	<head>
		<title>Available replies</title>
	</head>

	<body>

    <fieldset>
      <#macro writeReplyDetails reply>
        <fieldset>
            <legend>${reply.id}</legend>
              root        =${reply.root}          <br/>
          <span style="background:burlywood">
              inReplyTo   =${reply.inReplyTo}</span>     <br/>
              title       =${reply.commentTitle}         <br/>
              body        =${reply.comment}          <br/>
              created     =${reply.created}       <br/>
              creator     =${reply.creator}       <br/>
              mediator    =${reply.mediator}      <br/>
              type        =${reply.type}          <br/>

              <@s.url id="createReplyURL" action="createReplySubmit" root="${reply.root}" inReplyTo="${reply.id}" namespace="/annotation/secure"/>
              <@s.a href="%{createReplyURL}">create reply</@s.a> <br/>

              <@s.url id="listReplyURL" action="listAllReplies" root="${reply.root}" inReplyTo="${reply.id}"/>
              <@s.a href="%{listReplyURL}">list all replies</@s.a> <br/>

              <@s.url id="listThreadedRepliesURL" action="listThreadedReplies" root="${reply.root}" inReplyTo="${reply.id}"/>
              <@s.a href="%{listThreadedRepliesURL}">list threaded replies</@s.a> <br/>

              <@s.url id="listFlagURL" action="listAllFlags" target="${reply.id}" />
              <@s.a href="%{listFlagURL}">list all flags</@s.a> <br/>
        </fieldset>
        <li>
          <ul>
            <#list reply.replies as subReply>
              <@writeReplyDetails subReply/>
            </#list>
          </ul>
        </li>
      </#macro>

      <ul>
        <#list replies as reply>
          <@writeReplyDetails reply/>
        </#list>
      </ul>
    </fieldset>

  </body>
</html>