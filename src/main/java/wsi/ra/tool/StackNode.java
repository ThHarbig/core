// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package wsi.ra.tool;


/**
 */
public class StackNode
{

    public StackNode(StackNode stacknode, Object obj, StackNode stacknode1)
    {
        key = obj;
        next = stacknode1;
        if(next != null)
            next.prev = this;
        prev = stacknode;
        if(prev != null)
            prev.next = this;
    }

    public StackNode(StackNode stacknode, Object obj)
    {
        this(stacknode, obj, null);
    }

    public StackNode(Object obj)
    {
        this(null, obj, null);
    }

    public Object key;
    public StackNode next;
    public StackNode prev;
}
