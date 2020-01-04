package game;

import android.graphics.Bitmap;
//This class is used to create nodes by storing the position and the bitmap for the node
public class node {
    public CreateCharacter character;
    public int itemnum;
    public int direction;
    public node next;
    public node prev;
    public int length=0;
    public int x;
    public int y;
    node head;


    public void initList(){

    }

    public node makeNode(int number , CreateCharacter character, int direction){
        node newNode;
        newNode = new node();
        newNode.itemnum =number;
        newNode.direction = direction;
        newNode.character = character;
        newNode.x = x;
        newNode.y = y;
        if(head==null) {
            newNode.prev = null;
        }else{
            newNode.prev = findTail(head);
        }
        newNode.next = null;


        return newNode;
    }

    public boolean isListEmpty(node head){
        boolean empty;
        if (head == null){
            empty = true;
        }
        else {
            empty = false;
        }
        return empty;

    }

    public node findTail(node head) {
        node current;
        current = head;
        while(current.next != null){
            current = current.next;
        }
        return current;
    }

    public void addNode(node head ,int number, CreateCharacter character, int direction){
        node tail;
        if(isListEmpty(head)){
            this.head = makeNode(number, character, direction);
        }
        else {
            tail = findTail(head);
            tail.next = makeNode(number, character, direction);
        }
        length++;
    }

    public void showList(node head){//mainly for debugging
        node current;
        current = head;
        while ( current.next != null){
            System.out.print(current.itemnum);
            current = current.next;
        }
        System.out.println(current.itemnum);
    }
    public int getLength() {
       if(head == null){
           return 0;
       }
        return length;
    }
    public node getHead() {
        return head;
    }

}

