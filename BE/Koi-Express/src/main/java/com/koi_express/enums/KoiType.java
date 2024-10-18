package com.koi_express.enums;

public enum KoiType {
    KOI_NHAT_BAN,
    KOI_VIET_NAM,
    KOI_CHAU_AU;

    public String getDisplayName() {
        switch (this) {
            case KOI_NHAT_BAN:
                return "Koi Nhật Bản";
            case KOI_VIET_NAM:
                return "Koi Việt Nam";
            case KOI_CHAU_AU:
                return "Koi Châu Âu";
            default:
                return this.name();
        }
    }

    public String toUpperCase() {
        return this.name().toUpperCase();
    }
}
