package com.biu.dombelskizulti.imageserviceapp.ProgressBar;

public class ProgressBar {

    private int count;
    private int limit;

    public ProgressBar(int c, int lim) {
        count = c;
        limit = lim;
    }

    public ProgressBar() {
        this(0, 100);
    }

    public void increase() {
        count++;
    }

    public void decrease() {
        count--;
    }

    public int getCount() {
        return count;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isDone() {
        return (count >= limit);
    }

    public void setLimit(int lim) {
        limit = lim;
    }
}
