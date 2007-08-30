/*
 * Copyright 2003-2007 the original author or authors.
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
package org.codehaus.groovy.ast;

import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.objectweb.asm.Opcodes;

/**
 * Represents a method declaration
 *
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
public class MethodNode extends AnnotatedNode implements Opcodes {

    private String name;
    private int modifiers;
    private ClassNode returnType;
    private Parameter[] parameters;
    private boolean hasDefaultValue = false;
    private Statement code;
    private boolean dynamicReturnType;
    private VariableScope variableScope;
    private ClassNode[] exceptions;
    
    // type spec for generics
    private GenericsType[] genericsTypes=null;

    public MethodNode(String name, int modifiers, ClassNode returnType, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        this.name = name;
        this.modifiers = modifiers;
        this.parameters = parameters;
        this.code = code;
        this.returnType = returnType;
        if (returnType==null) this.returnType = ClassHelper.OBJECT_TYPE; 

        variableScope = new VariableScope();
        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                Parameter para = parameters[i];
                if (para.hasInitialExpression()) {
                    this.hasDefaultValue = true;
                }
                para.setInStaticContext(isStatic());
                variableScope.getDeclaredVariables().put(para.getName(),para);
            }
        }
        variableScope.setInStaticContext(isStatic());
        
        this.exceptions = exceptions;
    }

    /**
     * The type descriptor for a method node is a string containing the name of the method, its return type,
     * and its parameter types in a canonical form. For simplicity, I'm using the format of a Java declaration
     * without parameter names, and with $dynamic as the type for any dynamically typed values.
     */
    // TODO: add test case for type descriptor
    public String getTypeDescriptor() {
        StringBuffer buf = new StringBuffer(name.length()+parameters.length*10);
        // buf.append(dynamicReturnType ? "$dynamic" : cleanupTypeName(returnType));
        //
        buf.append(returnType.getName()); // br  to replace the above. Dynamic type returns Object.
        //
        buf.append(' ');
        buf.append(name);
        buf.append('(');
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            Parameter param = parameters[i];
            buf.append(param.getType().getName());
        }
        buf.append(')');
        return buf.toString();
    }
 
    public boolean isVoidMethod() {
        return returnType==ClassHelper.VOID_TYPE;
    }

    public Statement getCode() {
        return code;
    }

    public void setCode(Statement code) {
        this.code = code;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public ClassNode getReturnType() {
        return returnType;
    }

    public VariableScope getVariableScope() {
        return variableScope;
    }

    public void setVariableScope(VariableScope variableScope) {
        this.variableScope = variableScope;
    }

    public boolean isDynamicReturnType() {
        return dynamicReturnType;
    }

    public boolean isAbstract() {
        return (modifiers & ACC_ABSTRACT) != 0;
    }

    public boolean isStatic() {
        return (modifiers & ACC_STATIC) != 0;
    }

    public boolean isPublic() {
        return (modifiers & ACC_PUBLIC) != 0;
    }

    public boolean isProtected() {
        return (modifiers & ACC_PROTECTED) != 0;
    }

    public boolean hasDefaultValue() {
        return this.hasDefaultValue;
    }

    public String toString() {
        return super.toString() + "[name: " + name + "]";
    }

    public void setReturnType(ClassNode returnType) {
        this.returnType = returnType;
    }

    public ClassNode[] getExceptions() {
        return exceptions;
    }
    
    public Statement getFirstStatement(){
        if (code == null) return null;
        if (code instanceof BlockStatement) {
            List list = ((BlockStatement) code).getStatements();
            if (!list.isEmpty()) return (Statement) list.get(0);
            return null;
        }
        return code;
    }
    
    public GenericsType[] getGenericsTypes() {
        return genericsTypes;
    }

    public void setGenericsTypes(GenericsType[] genericsTypes) {
        this.genericsTypes = genericsTypes;
    }
}
