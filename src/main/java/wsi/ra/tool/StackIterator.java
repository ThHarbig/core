// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package wsi.ra.tool;

import java.util.Iterator;

// Referenced classes of package wsi.ra.tool:
//            Stack, StackNode

/**
 */
@SuppressWarnings("unchecked")
public class StackIterator
    implements Iterator
{

    public StackIterator(Stack stack)
    {
        current = stack.head;
        l = stack;
    }

    public boolean hasNext()
    {
        return current != null;
    }

    public Object next()
    {
        Object obj = current.key;
        current = current.next;
        return obj;
    }

    public void remove()
    {
        if(current.prev == null)
        {
            return;
        } else
        {
            l.remove(current.prev);
            return;
        }
    }

    protected StackNode current;
    protected Stack l;
}
