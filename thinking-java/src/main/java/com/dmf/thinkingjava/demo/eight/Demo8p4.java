package com.dmf.thinkingjava.demo.eight;

public class Demo8p4 {
    public static void main(String[] args) {
        Mill m = new Mill();
        Grain g = m.process();
        System.out.println(g.toString());
        m = new WheatMill();
        g = m.process();
        System.out.println(g.toString());
    }

}

class Grain {
    @Override
    public String toString() {
        return "Grain";
    }
}

class Wheat extends Grain {
    @Override
    public String toString() {
        return "Wheat";
    }
}

class Mill {
    public Grain process() {
        return new Grain();
    }
}

class WheatMill extends Mill {
    public Wheat process() {
        return new Wheat();
    }
}