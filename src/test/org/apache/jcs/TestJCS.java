package org.apache.jcs;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jcs.JCS;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Random;

/**
 * Simple test for the JCS class.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id$
 */
public class TestJCS extends TestCase
{
    Random random = new Random();

    public TestJCS( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( TestJCS.class );
    }

    public static void main( String args[] )
    {
        String[] testCaseName = { TestJCS.class.getName() };
        junit.textui.TestRunner.main( testCaseName );
    }

    public void testJCS() throws Exception
    {
        JCS jcs = JCS.getInstance( "testCache1" );
        
        LinkedList list = buildList();
        
        jcs.put( "some:key", list );
        
        assertEquals( list, jcs.get( "some:key" ) );
    }
 
    private LinkedList buildList()
    {
        LinkedList list = new LinkedList();
        
        for ( int i = 0; i < 100; i++ )
        {
            list.add( buildMap() );
        }
        
        return list;
    }
    
    private HashMap buildMap()
    {       
        HashMap map = new HashMap();
        
        byte[] keyBytes = new byte[32];
        byte[] valBytes = new byte[128];
        
        for ( int i = 0; i < 10; i++ )
        {
            random.nextBytes( keyBytes );
            random.nextBytes( valBytes );
            
            map.put( new String( keyBytes ), new String( valBytes ) );
        }
        
        return map;
    }

}