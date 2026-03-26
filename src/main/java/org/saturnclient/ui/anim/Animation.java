package org.saturnclient.ui.anim;

import java.util.function.Function;

import org.saturnclient.config.AnimationConfig;
import org.saturnclient.ui.Element;

public abstract class Animation {
    public int duration;
    public int delay = 0;
    public Function<Double, Double> curve = Curve::easeInOutCubic;

    public static Function<Double, Double> getCurve(int curve) {
        switch (curve) {
            case 0:
                return Curve::easeInOutCubic;
            case 1:
                return Curve::easeOutCubic;
            case 2:
                return Curve::easeInOutBack;
            default:
                return Curve::easeOutCubic;
        }
    }

    Animation(AnimationConfig config) {
        this.duration = config.duration.value;
        this.curve = getCurve(config.curve.value);
    }

    Animation(int duration) {
        this.duration = duration;
    }

    public abstract void tick(double progress, Element element);

    public abstract void init(Element element);
}
