// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package wsi.ra.tool;


// Referenced classes of package wsi.ra.tool:
//            StackNode, StackIterator

/**
 */
public class Stack
{

    public Stack(StackNode stacknode)
    {
        size = 0;
        head = stacknode;
        tail = stacknode;
        if(tail != null)
            while(tail.next != null) 
            {
                tail = tail.next;
                size++;
            }
    }

    public Stack()
    {
        this(null);
    }

    public boolean isEmpty()
    {
        return head == null;
    }

    public StackNode pushFront(Object obj)
    {
        size++;
        StackNode stacknode = new StackNode(null, obj, head);
        if(head == null)
        {
            head = tail = stacknode;
            return head;
        } else
        {
            stacknode.next = head;
            head = stacknode;
            return head;
        }
    }

    public StackNode pushBack(Object obj)
    {
        size++;
        StackNode stacknode = new StackNode(tail, obj, null);
        if(tail == null)
        {
            head = tail = stacknode;
            return tail;
        } else
        {
            tail.next = stacknode;
            tail = stacknode;
            return tail;
        }
    }

    public StackNode insertAfter(StackNode stacknode, Object obj)
    {
        if(stacknode == tail)
        {
            return pushBack(obj);
        } else
        {
            size++;
            return new StackNode(stacknode, obj, stacknode.next);
        }
    }

    public StackNode insertBefore(StackNode stacknode, Object obj)
    {
        if(stacknode == head)
        {
            return pushFront(obj);
        } else
        {
            size++;
            return new StackNode(stacknode.prev, obj, stacknode);
        }
    }

    public Object popFront()
    {
        if(head != null)
        {
            size--;
            Object obj = head.key;
            head = head.next;
            if(head != null)
                head.prev = null;
            if(head == null)
                tail = null;
            return obj;
        } else
        {
            return null;
        }
    }

    public StackNode pushFront(Stack stack)
    {
        if(stack == null || stack.head == null)
        {
            return null;
        } else
        {
            size += stack.size;
            stack.tail.next = head;
            head.prev = stack.tail;
            head = stack.head;
            return head;
        }
    }

    public Object popBack()
    {
        if(tail != null)
        {
            size--;
            Object obj = tail.key;
            if(head == tail)
            {
                head = tail = null;
                return obj;
            } else
            {
                tail.prev.next = null;
                tail = tail.prev;
                return obj;
            }
        } else
        {
            return null;
        }
    }

    public void remove(StackNode stacknode)
    {
        if(stacknode == null)
            return;
        if(head == stacknode)
        {
            popFront();
            return;
        }
        if(tail == stacknode)
        {
            popBack();
            return;
        }
        if(head == null)
            System.err.println("Cannot remove from empty list");
        size--;
        stacknode.prev.next = stacknode.next;
        stacknode.next.prev = stacknode.prev;
    }

    public void removeAll()
    {
        head = tail = null;
        size = 0;
    }

    public int size()
    {
        return size;
    }

    public StackNode getFront()
    {
        return head;
    }

    public StackNode getBack()
    {
        return tail;
    }

    public StackIterator getStackIterator()
    {
        return new StackIterator(this);
    }

    private int size;
    public StackNode head;
    public StackNode tail;
}
