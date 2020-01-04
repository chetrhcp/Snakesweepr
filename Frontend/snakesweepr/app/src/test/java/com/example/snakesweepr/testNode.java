package com.example.snakesweepr;

import android.content.Context;
import android.graphics.Bitmap;

import org.junit.Test;

import game.node;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class testNode {
    Context context;
    Bitmap bit;
    GameView2 hold;
    CreateCharacter2 testing;
    @Test
    public void testNewSnake() {
        node test = new node();
        test.initList();

        assertTrue(test.isListEmpty(test.getHead()));
        assertEquals(0, test.getLength());

        test.addNode(test.getHead(),1, testing,1);
        assertFalse(test.isListEmpty(test.getHead()));
        assertEquals(1, test.getLength());

        test.addNode(test.getHead(),2, testing,1);
        assertEquals(2, test.getLength());
    }
}
