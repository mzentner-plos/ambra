/*
 * @(#)$Id: AbsolutePathPattern.java 557 2006-09-01 08:31:40Z pradeep $
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

package it.unibo.cs.org.apache.xalan.xsltc.compiler;

import it.unibo.cs.org.apache.xalan.xsltc.compiler.util.Type;

import it.unibo.cs.org.apache.xalan.xsltc.DOM;
import it.unibo.cs.org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.*;
import it.unibo.cs.org.apache.xalan.xsltc.compiler.util.*;

final class AbsolutePathPattern extends LocationPathPattern {
    private final RelativePathPattern _left; // may be null

    public AbsolutePathPattern(RelativePathPattern left) {
	_left = left;
	if (left != null) {
	    left.setParent(this);
	}
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	if (_left != null)
	    _left.setParser(parser);
    }
    
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	return _left == null ? Type.Root : _left.typeCheck(stable);
    }

    public boolean isWildcard() {
	return false;
    }
	
    public StepPattern getKernelPattern() {
	return _left != null ? _left.getKernelPattern() : null;
    }
	
    public void reduceKernelPattern() {
	_left.reduceKernelPattern();
    }
	
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final String DOM_CLASS = classGen.getDOMClass();

	if (_left != null) {
	    if (_left instanceof StepPattern) {
		final LocalVariableGen local = 
		    // absolute path pattern temporary
		    methodGen.addLocalVariable2("apptmp", 
						Util.getJCRefType(NODE_SIG),
						il.getEnd());
		il.append(DUP);
		il.append(new ISTORE(local.getIndex()));
		_left.translate(classGen, methodGen);
		il.append(methodGen.loadDOM());
		local.setEnd(il.append(new ILOAD(local.getIndex())));
		methodGen.removeLocalVariable(local);
	    }
	    else {
		_left.translate(classGen, methodGen);
	    }
	    _trueList.append(_left._trueList);
	    _falseList.append(_left._falseList);
	}
	il.append(methodGen.loadDOM());
	il.append(SWAP);
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(DOM_CLASS,
						     GET_PARENT,
						     GET_PARENT_SIG)));
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(DOM_CLASS,
						     "getType", "(I)I")));
	il.append(new PUSH(cpg, DOM.ROOT));
	_falseList.add(il.append(new IF_ICMPNE(null)));
    }
	
    public String toString() {
	return "absolutePathPattern(" + (_left!=null ? _left.toString() : ")");
    }
}
