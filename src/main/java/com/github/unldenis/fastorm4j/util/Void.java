package com.github.unldenis.fastorm4j.util;

public final class Void {

    public Void() {
        throw new UnsupportedOperationException();
    }

    private Void(VoidHolder dummy) {
    }

    public static Void getInstance() {
        return VoidHolder.INSTANCE;
    }

    private static final class VoidHolder {
        private static final Void INSTANCE = new Void(new VoidHolder());
    }

}
