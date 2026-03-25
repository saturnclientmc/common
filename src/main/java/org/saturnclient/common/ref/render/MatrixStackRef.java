package org.saturnclient.common.ref.render;

public interface MatrixStackRef {
    public void push();

    public void pop();

    public void translate(float x, float y);

    public void scale(float x, float y);

    public void rotate(float angle, float originX, float originY, float originZ);

    public void rotate(float angle);
}
