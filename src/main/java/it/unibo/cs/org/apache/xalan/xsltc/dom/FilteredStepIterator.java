/*
 * @(#)$Id: FilteredStepIterator.java 557 2006-09-01 08:31:40Z pradeep $
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 *
 */

package it.unibo.cs.org.apache.xalan.xsltc.dom;

import it.unibo.cs.org.apache.xalan.xsltc.NodeIterator;
import it.unibo.cs.org.apache.xalan.xsltc.runtime.BasisLibrary;

public final class FilteredStepIterator extends NodeIteratorBase {
    private NodeIterator _source;
    private NodeIterator _iterator;
    private final Filter _filter;
	
    public FilteredStepIterator(NodeIterator source,
				NodeIterator iterator,
				Filter filter) {
	_source = source;
	_iterator = iterator;
	_filter = filter;
    }

    public NodeIterator cloneIterator() {
	try {
	    final FilteredStepIterator clone =
		(FilteredStepIterator)super.clone();
	    clone._isRestartable = false;
	    clone._source = _source.cloneIterator();
	    clone._iterator = _iterator.cloneIterator()
		.setStartNode(_source.next());
	    return clone.resetPosition();
	}
	catch (CloneNotSupportedException e) {
	    BasisLibrary.runTimeError("Iterator clone not supported.");
	    return null;
	}
    }
    
    public NodeIterator setStartNode(int node) {
	if (_isRestartable) {
	    // iterator is not a clone
	    _source.setStartNode(_startNode = node);
	    _iterator.setStartNode(_source.next());
	    return resetPosition();
	}
	return this;
    }

    public NodeIterator reset() {
	_source.reset();
	_iterator.setStartNode(_source.next());
	return resetPosition();
    }
    
    public int next() {
	for (int node;;) {
	    while ((node = _iterator.next()) != END)
		if (_filter.test(node)) {
		    return returnNode(node);
		}
	    // local iterator ran out of nodes
	    // try to get new start node from source
	    if ((node = _source.next()) == END)
		return END;
	    else
		_iterator.setStartNode(node);
	}
    }
		
    public void setMark() {
	_source.setMark();
	_iterator.setMark();
    }

    public void gotoMark() {
	_source.gotoMark();
	_iterator.gotoMark();
    }
}
