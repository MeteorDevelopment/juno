package org.meteordev.juno.api.utils;

public class ScissorStack {
    private final Entry[] scissors = new Entry[16];
    private int index = -1;

    public ScissorStack() {
        for (int i = 0; i < scissors.length; i++) {
            scissors[i] = new Entry();
        }
    }

    public Entry push(int x, int y, int width, int height) {
        index++;
        if (index >= scissors.length - 1) throw new RuntimeException("Maximum number of nested scissors " + scissors.length + " reached.");

        scissors[index].set(index > 0 ? scissors[index - 1] : null, x, y, width, height);
        return scissors[index];
    }

    public void pop() {
        index--;
        if (index < -1) throw new RuntimeException("Tried to pop a scissor entry when the stack was empty.");
    }

    public Entry peek() {
        if (index == -1) return null;
        return scissors[index];
    }

    public static class Entry {
        public int x, y, width, height;

        public void set(Entry parent, int x, int y, int width, int height) {
            if (parent != null) {
                if (x < parent.x) x = parent.x;
                if (x + width > parent.x + parent.width) width -= (x + width) - (parent.x + parent.width);

                if (y < parent.y) y = parent.y;
                if (y + height > parent.y + parent.height) height -= (y + height) - (parent.y + parent.height);
            }

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
